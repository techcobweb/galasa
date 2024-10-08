/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.authentication.internal.routes;

import static dev.galasa.framework.api.common.ServletErrorMessage.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.coreos.dex.api.DexOuterClass.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.galasa.framework.api.authentication.IOidcProvider;
import dev.galasa.framework.api.authentication.internal.DexGrpcClient;
import dev.galasa.framework.api.authentication.internal.TokenPayloadValidator;
import dev.galasa.framework.api.beans.AuthToken;
import dev.galasa.framework.api.beans.TokenPayload;
import dev.galasa.framework.api.beans.User;
import dev.galasa.framework.api.common.BaseRoute;
import dev.galasa.framework.api.common.Environment;
import dev.galasa.framework.api.common.IBeanValidator;
import dev.galasa.framework.api.common.JwtWrapper;
import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.InternalUser;
import dev.galasa.framework.api.common.QueryParameters;
import dev.galasa.framework.api.common.ResponseBuilder;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.spi.FrameworkException;
import dev.galasa.framework.spi.auth.IAuthStoreService;
import dev.galasa.framework.spi.auth.IInternalAuthToken;
import dev.galasa.framework.spi.auth.IInternalUser;
import dev.galasa.framework.spi.auth.AuthStoreException;

public class AuthTokensRoute extends BaseRoute {

    private IAuthStoreService authStoreService;
    private IOidcProvider oidcProvider;
    private DexGrpcClient dexGrpcClient;
    private Environment env;

    private static final String ID_TOKEN_KEY      = "id_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    // Regex to match /auth/tokens and /auth/tokens/ only
    private static final String PATH_PATTERN = "\\/tokens\\/?";

    private static final IBeanValidator<TokenPayload> validator = new TokenPayloadValidator();

    public AuthTokensRoute(
        ResponseBuilder responseBuilder,
        IOidcProvider oidcProvider,
        DexGrpcClient dexGrpcClient,
        IAuthStoreService authStoreService,
        Environment env
    ) {
        super(responseBuilder, PATH_PATTERN);
        this.oidcProvider = oidcProvider;
        this.dexGrpcClient = dexGrpcClient;
        this.authStoreService = authStoreService;
        this.env = env;
    }

    /**
     * GET requests to /auth/tokens return all the tokens stored in the tokens
     * database, sorted by creation date order by default.
     */
    @Override
    public HttpServletResponse handleGetRequest(String pathInfo, QueryParameters queryParams,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FrameworkException {

        logger.info("handleGetRequest() entered");

        List<AuthToken> tokensToReturn = new ArrayList<>();
        try {
            // Retrieve all the tokens and put them into a mutable list before sorting them based on their creation time
            List<IInternalAuthToken> tokens = new ArrayList<>(authStoreService.getTokens());
            Collections.sort(tokens, Comparator.comparing(IInternalAuthToken::getCreationTime));

            // Convert the token received from the auth store into the token bean that will be returned as JSON
            for (IInternalAuthToken token : tokens) {
                User user = new User(token.getOwner().getLoginId());
                tokensToReturn.add(new AuthToken(
                    token.getTokenId(),
                    token.getDescription(),
                    token.getCreationTime(),
                    user)
                );
            }
        } catch (AuthStoreException e) {
            ServletError error = new ServletError(GAL5053_FAILED_TO_RETRIEVE_TOKENS);
            throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }

        return getResponseBuilder().buildResponse(request, response, "application/json", getTokensAsJsonString(tokensToReturn), HttpServletResponse.SC_OK);
    }

    /**
     * Sending a POST request to /auth/tokens issues a new bearer token using a
     * client ID, client secret, and refresh token.
     */
    @Override
    public HttpServletResponse handlePostRequest(String pathInfo, QueryParameters queryParameters,
            HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, FrameworkException {

        logger.info("AuthRoute: handlePostRequest() entered.");

        // Check that the request body contains the required payload
        TokenPayload requestPayload = parseRequestBody(request, TokenPayload.class);
        validator.validate(requestPayload);

        JsonObject responseJson = new JsonObject();
        try {
            // Send a POST request to Dex's /token endpoint
            JsonObject tokenResponseBodyJson = sendTokenPost(requestPayload);

            // Return the JWT and refresh token as the servlet's response
            if (tokenResponseBodyJson != null && tokenResponseBodyJson.has(ID_TOKEN_KEY) && tokenResponseBodyJson.has(REFRESH_TOKEN_KEY)) {
                logger.info("Bearer and refresh tokens successfully received from issuer.");

                String jwt = tokenResponseBodyJson.get(ID_TOKEN_KEY).getAsString();
                responseJson.addProperty("jwt", jwt);
                responseJson.addProperty(REFRESH_TOKEN_KEY, tokenResponseBodyJson.get(REFRESH_TOKEN_KEY).getAsString());

                // If we're refreshing an existing token, then we don't want to create a new entry in the tokens database.
                // We only want to store tokens in the tokens database when they are created.
                String tokenDescription = requestPayload.getDescription();
                if (requestPayload.getRefreshToken() == null && tokenDescription != null) {
                    addTokenToAuthStore(requestPayload.getClientId(), jwt, tokenDescription);
                }

            } else {
                logger.info("Unable to get new bearer and refresh tokens from issuer.");

                ServletError error = new ServletError(GAL5055_FAILED_TO_GET_TOKENS_FROM_ISSUER);
                throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (InterruptedException e) {
            logger.error("POST request to the OpenID Connect provider's token endpoint was interrupted.", e);

            ServletError error = new ServletError(GAL5000_GENERIC_API_ERROR);
            throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }

        return getResponseBuilder().buildResponse(request, response, "application/json", gson.toJson(responseJson), HttpServletResponse.SC_OK);
    }

    /**
     * Converts a list of authentication tokens into a JSON string
     *
     * @param tokens the tokens to convert
     * @return a JSON representation of the tokens within a "tokens" JSON array
     */
    private String getTokensAsJsonString(List<AuthToken> tokens) {
        JsonArray tokensArray = new JsonArray();
        for (AuthToken token : tokens) {
            String tokenJson = gson.toJson(token);
            tokensArray.add(JsonParser.parseString(tokenJson));
        }

        JsonObject tokensObj = new JsonObject();
        tokensObj.add("tokens", tokensArray);

        return gson.toJson(tokensObj);
    }

    /**
     * Sends a POST request to the JWT issuer's /token endpoint and returns the
     * response's body as a JSON object.
     *
     * @param requestBodyJson the request payload containing the required parameters
     *                        for the /token endpoint
     */
    private JsonObject sendTokenPost(TokenPayload requestBodyJson)
            throws IOException, InterruptedException, InternalServletException {
        String refreshToken = requestBodyJson.getRefreshToken();
        String clientId = requestBodyJson.getClientId();
        Client dexClient = dexGrpcClient.getClient(clientId);

        JsonObject response = null;
        if (dexClient != null) {
            String clientSecret = dexClient.getSecret();

            // Refresh tokens and authorization codes can be used in exchange for JWTs.
            // At this point, we either have a refresh token or an authorization code,
            // so perform the relevant POST request
            HttpResponse<String> tokenResponse = null;
            if (refreshToken != null) {
                tokenResponse = oidcProvider.sendTokenPost(clientId, clientSecret, refreshToken);
            } else {
                tokenResponse = oidcProvider.sendTokenPost(clientId, clientSecret, requestBodyJson.getCode(), AuthCallbackRoute.getExternalAuthCallbackUrl());
            }

            if (tokenResponse != null) {
                response = gson.fromJson(tokenResponse.body(), JsonObject.class);
            }
        }
        return response;
    }

    /**
     * Records a new Galasa token in the auth store.
     *
     * @param clientId the ID of the client that a user has authenticated with
     * @param jwt the JWT that was returned after authenticating with the client, identifying the user
     * @param description the description of the Galasa token provided by the user
     * @throws InternalServletException
     */
    private void addTokenToAuthStore(String clientId, String jwt, String description) throws InternalServletException {
        logger.info("Storing new token record in the auth store");
        JwtWrapper jwtWrapper = new JwtWrapper(jwt, env);
        IInternalUser user = new InternalUser(jwtWrapper.getUsername(), jwtWrapper.getSubject());

        try {
            authStoreService.storeToken(clientId, description, user);
        } catch (AuthStoreException e) {
            ServletError error = new ServletError(GAL5056_FAILED_TO_STORE_TOKEN_IN_AUTH_STORE, description);
            throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
        logger.info("Stored token record in the auth store OK");
    }
}
