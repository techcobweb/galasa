package dev.galasa.framework.api.ras.internal.common;
import static dev.galasa.framework.api.common.ServletErrorMessage.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.spi.FrameworkException;
import dev.galasa.framework.spi.IFramework;
import dev.galasa.framework.spi.Result;
import dev.galasa.framework.spi.utils.GalasaGson;

public class RunStatusUpdate {

   private IFramework framework;
   static final GalasaGson gson = new GalasaGson();

   public RunStatusUpdate (IFramework framework) {
      this.framework = framework;
   }

   public RunActionJson getUpdatedRunActionFromRequestBody(HttpServletRequest request) throws IOException {
      ServletInputStream body = request.getInputStream();
      String jsonString = new String(body.readAllBytes(), StandardCharsets.UTF_8);
      body.close();
      RunActionJson runAction = gson.fromJson(jsonString, RunActionJson.class);
      return runAction;
   }

   public void resetRun(String runName) throws InternalServletException {
      boolean isMarkedRequeued = false;
      try {
         // If a run is marked as requeued, the DSS record for the run will be given an interrupt reason.
         // This call would return false if the run could not be found in the DSS.
         isMarkedRequeued = framework.getFrameworkRuns().markRunInterrupted(runName, Result.REQUEUED);
      } catch (FrameworkException e){
         ServletError error = new ServletError(GAL5047_UNABLE_TO_RESET_RUN, runName);
         throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
      }

      if (!isMarkedRequeued) {
         ServletError error = new ServletError(GAL5049_UNABLE_TO_RESET_COMPLETED_RUN, runName);
         throw new InternalServletException(error, HttpServletResponse.SC_BAD_REQUEST);
      }
   }

   public void cancelRun(String runName, String result) throws InternalServletException {
      boolean isMarkedCancelled = false;
      if (!result.equalsIgnoreCase("cancelled")){
         ServletError error = new ServletError(GAL5046_UNABLE_TO_CANCEL_RUN_INVALID_RESULT, runName, result);
         throw new InternalServletException(error, HttpServletResponse.SC_BAD_REQUEST);
      }

      try {
         // If a run is marked as cancelled, the DSS record for the run will have been updated with an interrupt reason.
         // When a run could not be found in the DSS, the run may have already finished and its DSS record was cleared.
         isMarkedCancelled = framework.getFrameworkRuns().markRunInterrupted(runName, Result.CANCELLED);
      } catch (FrameworkException e) {
         ServletError error = new ServletError(GAL5048_UNABLE_TO_CANCEL_RUN, runName);
         throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
      }

      if (!isMarkedCancelled) {
         ServletError error = new ServletError(GAL5050_UNABLE_TO_CANCEL_COMPLETED_RUN, runName);
         throw new InternalServletException(error, HttpServletResponse.SC_BAD_REQUEST);
      }
   }

}  
