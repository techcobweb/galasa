/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.rbac.internal.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dev.galasa.framework.api.beans.generated.RBACActionMetadata;
import dev.galasa.framework.api.common.Environment;
import dev.galasa.framework.api.common.QueryParameters;
import dev.galasa.framework.api.common.ResponseBuilder;
import dev.galasa.framework.spi.FrameworkException;
import dev.galasa.framework.spi.rbac.Action;
import dev.galasa.framework.spi.rbac.RBACService;
import dev.galasa.framework.spi.utils.ITimeService;

public class ActionsRoute extends AbstractRBACRoute {

    // Regex to match /rbac/actions} and /rbac/actions/ only
    private static final String PATH_PATTERN = "\\/actions\\/?";

    private ActionTransform actionsTransform;

    private Log logger = LogFactory.getLog(getClass());

    private static final List<String> supportedQueryParameters = new ArrayList<String>();

    public ActionsRoute(
        ResponseBuilder responseBuilder,
        RBACService rbacService,
        Environment env,
        ITimeService timeService) {
        super(responseBuilder, PATH_PATTERN, env, timeService, rbacService);

        actionsTransform = new ActionTransform();
    }

    @Override
    public HttpServletResponse handleGetRequest(
        String pathInfo,
        QueryParameters queryParams,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws FrameworkException {
        String baseUrl = request.getRequestURL().toString();

        logger.info("handleGetRequest() entered. Getting actions. "+baseUrl);

        // TODO: This check should really be in the servlet, checking all routes, but that's a big change, so just leaving it here for now.
        queryParams.checkForUnsupportedQueryParameters(supportedQueryParameters);

        Collection<Action> actions = getRBACService().getActionsSortedByName();

        List<RBACActionMetadata> actionBeans = actionsTransform.creteaActionsSummary(actions, baseUrl);

        logger.info("handleGetRequest() exiting");
        return getResponseBuilder().buildResponse(request, response, "application/json",
            gson.toJson(actionBeans), HttpServletResponse.SC_OK);
    }

}
