/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.resources.validators;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static dev.galasa.framework.api.common.ServletErrorMessage.*;

import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.api.common.resources.GalasaResourceValidator;
import dev.galasa.framework.api.common.resources.ResourceAction;

public class GalasaStreamValidator extends GalasaResourceValidator<JsonObject> {

    public GalasaStreamValidator(ResourceAction action) {
        super(action);
    }

    @Override
    public void validate(JsonObject streamJson) throws InternalServletException {

        checkResourceHasRequiredFields(streamJson, DEFAULT_API_VERSION);
        validateStreamMetadata(streamJson);

    }

    private void validateStreamMetadata(JsonObject streamJson) {

        JsonObject metadata = streamJson.get("metadata").getAsJsonObject();

        // Check for name as we will delete the stream by name
        if (metadata.has("name")) {

            JsonElement name = metadata.get("name");
            boolean isStreamNameValid = isAlphanumWithDashes(name.getAsString());

            if(!isStreamNameValid) {
                validationErrors.add(GAL5418_INVALID_STREAM_NAME.toString());
            }

        } else {
            ServletError error = new ServletError(GAL5427_MISSING_STREAM_NAME);
            validationErrors.add(new InternalServletException(error, HttpServletResponse.SC_BAD_REQUEST).getMessage());
        }

    }

}
