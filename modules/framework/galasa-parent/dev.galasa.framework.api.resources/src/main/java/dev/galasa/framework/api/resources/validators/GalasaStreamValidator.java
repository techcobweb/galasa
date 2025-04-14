package dev.galasa.framework.api.resources.validators;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static dev.galasa.framework.api.common.ServletErrorMessage.*;

import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.api.common.resources.GalasaResourceValidator;
import dev.galasa.framework.api.common.resources.ResourceAction;
import dev.galasa.framework.api.common.resources.ResourceNameValidator;

public class GalasaStreamValidator extends GalasaResourceValidator<JsonObject> {

    private ResourceNameValidator nameValidator = new ResourceNameValidator();
    private final String TEST_STREAM_PREFIX = "framework.test.stream.";

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
            String prefixedName = TEST_STREAM_PREFIX + name.getAsString();

            try{
                nameValidator.assertPropertyNameCharPatternIsValid(prefixedName);
            } catch (InternalServletException e) {
                // All ResourceNameValidator error should be added to the list of reasons why the property action has failed
                validationErrors.add(e.getMessage());
            }

        } else {
            ServletError error = new ServletError(GAL5415_INVALID_GALASASTREAM_EMPTY_METADATA);
            validationErrors.add(new InternalServletException(error, HttpServletResponse.SC_BAD_REQUEST).getMessage());
        }

    }

}
