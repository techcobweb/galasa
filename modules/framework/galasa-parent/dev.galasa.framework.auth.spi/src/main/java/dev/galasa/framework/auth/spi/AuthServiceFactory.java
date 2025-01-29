/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.auth.spi;

import java.net.http.HttpResponse;

import javax.servlet.ServletException;

import dev.galasa.framework.api.common.Environment;
import dev.galasa.framework.api.common.EnvironmentVariables;
import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.api.common.ServletErrorMessage;
import dev.galasa.framework.auth.spi.internal.AuthService;
import dev.galasa.framework.auth.spi.internal.DexGrpcClient;
import dev.galasa.framework.spi.IFramework;
import dev.galasa.framework.spi.rbac.RBACException;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;

public class AuthServiceFactory implements IAuthServiceFactory {

    private IFramework framework;
    private Environment env;
    private IAuthService authService;

    public AuthServiceFactory(IFramework framework, Environment env) {
        this.framework = framework;
        this.env = env;
    }

    @Override
    public IAuthService getAuthService() throws InternalServletException {
        if (authService == null) {
            String dexIssuerHostname = getRequiredEnvVariable(EnvironmentVariables.GALASA_DEX_GRPC_HOSTNAME);
            String externalApiServerUrl = getRequiredEnvVariable(EnvironmentVariables.GALASA_EXTERNAL_API_URL);
            String externalWebUiUrl = externalApiServerUrl.replace("/api", "");

            IDexGrpcClient dexGrpcClient = new DexGrpcClient(dexIssuerHostname, externalWebUiUrl);

            try {
                this.authService = new AuthService(framework.getAuthStoreService(), dexGrpcClient, framework.getRBACService());
            } catch(RBACException rbacEx) {
                ServletError error = new ServletError(ServletErrorMessage.GAL5126_INTERNAL_RBAC_ERROR);
                throw new InternalServletException(error, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), rbacEx);
            }
        }
        return authService;
    }

    private String getRequiredEnvVariable(String envName) throws InternalServletException {
        String envValue = env.getenv(envName);

        if (envValue == null) {
            ServletError error = new ServletError(ServletErrorMessage.GAL5126_INTERNAL_RBAC_ERROR);
            throw new InternalServletException( "Required environment variable '" + envName + "' has not been set.");
        }
        return envValue;
    }
}
