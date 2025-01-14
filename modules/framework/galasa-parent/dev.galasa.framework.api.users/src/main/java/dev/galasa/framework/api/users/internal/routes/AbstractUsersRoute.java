/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.users.internal.routes;

import dev.galasa.framework.api.common.BaseRoute;
import dev.galasa.framework.api.common.Environment;
import dev.galasa.framework.api.common.EnvironmentVariables;
import dev.galasa.framework.api.common.ResponseBuilder;
import dev.galasa.framework.auth.spi.IAuthService;
import dev.galasa.framework.spi.auth.IAuthStoreService;
import dev.galasa.framework.spi.rbac.RBACService;

public abstract class AbstractUsersRoute extends BaseRoute {

    protected IAuthService authService ;
    protected IAuthStoreService authStoreService;
    protected Environment env;
    protected String baseServletUrl;
    protected RBACService rbacService;

    public AbstractUsersRoute(ResponseBuilder responseBuilder, String path, IAuthService authService, Environment env, RBACService rbacService) {
        super(responseBuilder,path);
        this.authService = authService;
        this.authStoreService = authService.getAuthStoreService();
        this.env = env ;
        this.rbacService = rbacService;

        baseServletUrl = env.getenv(EnvironmentVariables.GALASA_EXTERNAL_API_URL);
    }


}
