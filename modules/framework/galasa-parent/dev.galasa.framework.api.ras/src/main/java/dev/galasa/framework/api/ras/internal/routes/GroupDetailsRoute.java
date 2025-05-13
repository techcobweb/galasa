/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.ras.internal.routes;

import static dev.galasa.framework.api.common.ServletErrorMessage.*;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.galasa.framework.api.common.HttpRequestContext;
import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ResponseBuilder;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.api.ras.internal.common.RunActionJson;
import dev.galasa.framework.api.ras.internal.common.RunActionStatus;
import dev.galasa.framework.api.ras.internal.common.RunStatusUpdate;
import dev.galasa.framework.spi.FrameworkException;
import dev.galasa.framework.spi.IFramework;
import dev.galasa.framework.spi.IRunResult;
import dev.galasa.framework.spi.ResultArchiveStoreException;
import dev.galasa.framework.spi.rbac.RBACException;
import dev.galasa.framework.spi.utils.GalasaGson;

public class GroupDetailsRoute extends RunsRoute {

   private IFramework framework;
   private RunStatusUpdate runStatusUpdate;
   static final GalasaGson gson = new GalasaGson();

   // Regex to match endpoint: /ras/groups/{groupid}
   protected static final String path = "\\/groups\\/([A-Za-z0-9.\\-=]+)\\/?";

   public GroupDetailsRoute(ResponseBuilder responseBuilder, IFramework framework) throws RBACException {
      super(responseBuilder, path, framework);
      this.framework = framework;
      this.runStatusUpdate = new RunStatusUpdate(framework);
   }

   @Override
   public HttpServletResponse handlePutRequest(String pathInfo, HttpRequestContext requestContext,
         HttpServletResponse res) throws ServletException, IOException, FrameworkException {

      HttpServletRequest request = requestContext.getRequest();
      String groupId = getGroupIdFromPath(pathInfo);

      RunActionJson runAction = runStatusUpdate.getUpdatedRunActionFromRequestBody(request);
      String responseBody = updateRunStatus(groupId, runAction);

      return getResponseBuilder().buildResponse(request, res, "text/plain", responseBody, HttpServletResponse.SC_ACCEPTED);
   }

   private String getGroupIdFromPath(String pathInfo) throws InternalServletException {
      Matcher matcher = this.getPathRegex().matcher(pathInfo);
      matcher.matches();
      String groupId = matcher.group(1);
      return groupId;
   }

   private String updateRunStatus(String groupId, RunActionJson runAction) throws InternalServletException, ResultArchiveStoreException {

      String responseBody = "";
      List<IRunResult> groupedRuns = getRunsByGroupId(groupId);

      if (groupedRuns != null && !groupedRuns.isEmpty()) {

         for (IRunResult run : groupedRuns) {

            RunActionStatus status = RunActionStatus.getfromString(runAction.getStatus());
            String result = runAction.getResult();
            String runName = run.getTestStructure().getRunName();
            
            if (status == null) {
               ServletError error = new ServletError(GAL5045_INVALID_STATUS_UPDATE_REQUEST, runAction.getStatus());
               throw new InternalServletException(error, HttpServletResponse.SC_BAD_REQUEST);
            } else if (status == RunActionStatus.QUEUED) {
               runStatusUpdate.resetRun(runName);
               logger.info("Run reset by external source.");
               responseBody = String.format("The request to reset run with group id %s has been received.", groupId);
            } else if (status == RunActionStatus.FINISHED) {
               runStatusUpdate.cancelRun(runName, result);
               logger.info("Run cancelled by external source.");
               responseBody = String.format("The request to cancel run with group id %s has been received.", groupId);
            }
            
         }

      }

      return responseBody;
   }

}
