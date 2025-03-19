/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package dev.galasa.framework.api.streams.internal.routes;

import static dev.galasa.framework.api.common.ServletErrorMessage.GAL5418_INVALID_STREAM_NAME_QUERY_PARAM;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dev.galasa.framework.api.common.Environment;
import dev.galasa.framework.api.common.HttpRequestContext;
import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.QueryParameters;
import dev.galasa.framework.api.common.ResponseBuilder;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.api.common.SupportedQueryParameterNames;
import dev.galasa.framework.spi.FrameworkException;
import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.framework.spi.rbac.RBACService;
import dev.galasa.framework.spi.streams.IStreamsService;
import dev.galasa.framework.spi.streams.StreamsException;

public class StreamsRoute extends AbstractStreamsRoute {

    // Regex to match endpoint /streams and /streams/
    private static final String path = "\\/?";

    public static final String QUERY_PARAM_STREAM_NAME = "name";

    public static final SupportedQueryParameterNames SUPPORTED_QUERY_PARAMETER_NAMES = new SupportedQueryParameterNames(
            QUERY_PARAM_STREAM_NAME);

    public StreamsRoute(ResponseBuilder responseBuilder, Environment env,
            IStreamsService streamsService,RBACService rbacService, IConfigurationPropertyStoreService configurationPropertyStoreService)
            throws StreamsException {
        super(responseBuilder, path, rbacService, configurationPropertyStoreService);
    }

    @Override
    public HttpServletResponse handleGetRequest(String pathInfo, QueryParameters queryParams,
            HttpRequestContext requestContext, HttpServletResponse response)
            throws ServletException, IOException, FrameworkException {

        logger.info("StreamsRoute: handleGetRequest() entered.");
        HttpServletRequest request = requestContext.getRequest();

        Map<String, String> propertiesFromCps = new HashMap<String, String>();

        if (queryParams.isParameterPresent(QUERY_PARAM_STREAM_NAME)) {

            String streamNameValue = queryParams.getSingleString(QUERY_PARAM_STREAM_NAME, null);
            validateQueryParam(QUERY_PARAM_STREAM_NAME, streamNameValue);

        } else {
            propertiesFromCps = configurationPropertyStoreService.getPrefixedProperties("framework.test.stream.");
        }

        return getResponseBuilder().buildResponse(
                request, response, "application/json", "getStreamsAsJsonString(propertiesFromCps)",
                HttpServletResponse.SC_OK);
    }

    private void validateQueryParam(String paramName, String paramValue) throws InternalServletException {
        if (paramValue == null || paramValue.trim().length() == 0) {
            ServletError error = new ServletError(GAL5418_INVALID_STREAM_NAME_QUERY_PARAM);
            throw new InternalServletException(error, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getStreamsAsJsonString(Map<String, String> propertiesFromCps) {
        JsonArray streamsArray = new JsonArray();
    
        if(propertiesFromCps != null && propertiesFromCps.size() > 0) {
            for (Map.Entry<String, String> entry : propertiesFromCps.entrySet()) {
                JsonObject streamEntry = new JsonObject();
    
                streamsArray.add(streamEntry);
            }
        }
    
        JsonObject wrapper = new JsonObject();
        wrapper.add("streams", streamsArray);
    
        return gson.toJson(wrapper);
    }
    

    @Override
    public SupportedQueryParameterNames getSupportedQueryParameterNames() {
        return SUPPORTED_QUERY_PARAMETER_NAMES;
    }

}
