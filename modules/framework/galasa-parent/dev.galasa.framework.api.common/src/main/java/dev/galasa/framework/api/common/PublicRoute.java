/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.common;

import javax.servlet.http.HttpServletRequest;

public abstract class PublicRoute extends BaseRoute {

    public PublicRoute(ResponseBuilder responseBuilder, String path) {
        super(responseBuilder, path);
    }

    @Override
    public boolean isActionPermitted(String actionId, HttpServletRequest request) throws InternalServletException {
        // Public routes don't require a JWT challenge, so are not restricted by RBAC
        return true;
    }
}
