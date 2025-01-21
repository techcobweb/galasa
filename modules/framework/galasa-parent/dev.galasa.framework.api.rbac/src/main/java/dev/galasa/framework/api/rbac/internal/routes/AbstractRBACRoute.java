/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.rbac.internal.routes;

import dev.galasa.framework.api.common.Environment;
import dev.galasa.framework.api.common.ProtectedRoute;
import dev.galasa.framework.api.common.ResponseBuilder;
import dev.galasa.framework.spi.rbac.RBACService;
import dev.galasa.framework.spi.utils.ITimeService;

public abstract class AbstractRBACRoute extends ProtectedRoute {

    private ITimeService timeService;

    public AbstractRBACRoute(
        ResponseBuilder responseBuilder, String path, Environment env, 
        ITimeService timeService, RBACService rbacService
    ) {
        super(responseBuilder, path, rbacService, env);
        this.timeService = timeService;
    }

    protected RBACService getRBACService() {
        return this.rbacService;
    }

    protected ITimeService getTimeService() {
        return this.timeService;
    }

    protected Environment getEnv() {
        return this.env;
    }
}
