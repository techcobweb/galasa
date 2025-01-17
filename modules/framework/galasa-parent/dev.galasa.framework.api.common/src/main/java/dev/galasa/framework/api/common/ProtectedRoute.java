/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.common;

import javax.servlet.http.HttpServletRequest;

import dev.galasa.framework.spi.rbac.CacheRBAC;
import dev.galasa.framework.spi.rbac.RBACException;
import dev.galasa.framework.spi.rbac.RBACService;

public abstract class ProtectedRoute extends BaseRoute {

    protected RBACService rbacService;
    protected Environment env;

    public ProtectedRoute(
        ResponseBuilder responseBuilder,
        String path,
        RBACService rbacService,
        Environment env
    ) {
        super(responseBuilder, path);
        this.rbacService = rbacService;
        this.env = env;
    }

    @Override
    public boolean isActionPermitted(String actionId, HttpServletRequest request) throws InternalServletException {
        boolean isPermitted = false;
        String jwt = JwtWrapper.getBearerTokenFromAuthHeader(request);
        if (jwt != null) {
            try {
                String loginId = new JwtWrapper(jwt, env).getUsername();
                CacheRBAC cache = rbacService.getUsersActionsCache();
                isPermitted = cache.isActionPermitted(loginId, actionId);
            } catch (RBACException e) {
                // TODO throw error here
                e.printStackTrace();
            }
        }
        return isPermitted;
    }
}
