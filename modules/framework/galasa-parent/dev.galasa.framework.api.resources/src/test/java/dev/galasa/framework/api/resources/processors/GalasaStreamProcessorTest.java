/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.resources.processors;

import static dev.galasa.framework.api.common.resources.ResourceAction.DELETE;
import static dev.galasa.framework.spi.rbac.BuiltInAction.GENERAL_API_ACCESS;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonObject;

import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.RBACValidator;
import dev.galasa.framework.api.common.mocks.MockFramework;
import dev.galasa.framework.api.resources.ResourcesServletTest;
import dev.galasa.framework.mocks.FilledMockRBACService;
import dev.galasa.framework.mocks.MockRBACService;
import dev.galasa.framework.mocks.MockStreamsService;
import dev.galasa.framework.spi.rbac.Action;

public class GalasaStreamProcessorTest extends ResourcesServletTest {

    private JsonObject generateStreamJson(String streamName, String description, String streamUrl, String mavenUrl, String catalogUrl) {
        return generateStreamJson(streamName, description, streamUrl, mavenUrl, catalogUrl, true);
    }

    private JsonObject generateStreamJson(
        String streamName,
        String description,
        String streamUrl,
        String mavenUrl,
        String catalogUrl,
        boolean isEnabled
    ) {
        JsonObject streamJson = new JsonObject();
        streamJson.addProperty("apiVersion", "galasa-dev/v1alpha1");
        streamJson.addProperty("kind", "GalasaStream");

        JsonObject streamMetadata = new JsonObject();
        if(streamName != null){
            streamMetadata.addProperty("name", streamName);
        }

        if (description != null) {
            streamMetadata.addProperty("description", description);
        }

        if (streamUrl != null) {
            streamMetadata.addProperty("url", streamUrl);
        }

        JsonObject streamData = new JsonObject();
        streamData.addProperty("isEnabled", isEnabled);

        if (catalogUrl != null) {
            JsonObject streamCatalog = new JsonObject();
            streamCatalog.addProperty("url", catalogUrl);
            streamData.add("testCatalog", streamCatalog);
        }

        if (mavenUrl != null) {
            JsonObject streamMavenRepo = new JsonObject();
            streamMavenRepo.addProperty("url", mavenUrl);
            streamData.add("repository", streamMavenRepo);
        }

        streamJson.add("metadata", streamMetadata);
        streamJson.add("data", streamData);

        // Expecting a JSON structure in the form:
        // {
        //     "apiVersion": "galasa-dev/v1alpha1",
        //     "kind": "GalasaStream",
        //     "metadata": {
        //         "name": "mystream",
        //         "description": "This is a test stream",
        //         "url": "http://localhost:8080/streams/mystream"
        //     },
        //     "data": {
        //         "isEnabled": true,
        //         "testCatalog": {
        //              "url": "http://points-to-my-test-catalog.example.org"
        //         },
        //         "repository" : {
        //              "url": "http://points-to-my-maven-repo.example.org"
        //         },
        //     }
        // }
        return streamJson;
    }

    @Test
    public void testValidateDeletePermissionsWithMissingPropertiesDeleteReturnsForbidden() throws Exception {
        // Given...
        List<Action> permittedActions = List.of(GENERAL_API_ACCESS.getAction());
        MockRBACService mockRbacService = FilledMockRBACService.createTestRBACServiceWithTestUser(JWT_USERNAME, permittedActions);

        MockStreamsService streamService = new MockStreamsService(new ArrayList<>());
        MockFramework mockFramework = new MockFramework();
        mockFramework.setRBACService(mockRbacService);

        RBACValidator rbacValidator = new RBACValidator(mockFramework.getRBACService());
        GalasaStreamProcessor streamProcessor = new GalasaStreamProcessor(streamService, rbacValidator);

        // When...
        InternalServletException thrown = catchThrowableOfType(() -> {
            streamProcessor.validateActionPermissions(DELETE, JWT_USERNAME);
        }, InternalServletException.class);

        // Then...
        assertThat(thrown).isNotNull();
        checkErrorStructure(thrown.getMessage(), 5125, "GAL5125E", "CPS_PROPERTIES_DELETE");
    }

    @Test
    public void testDeleteStreamWithMissingNamePropertyReturnsBadRequest() throws Exception {
        // Given...
        List<Action> permittedActions = List.of(GENERAL_API_ACCESS.getAction());
        MockRBACService mockRbacService = FilledMockRBACService.createTestRBACServiceWithTestUser(JWT_USERNAME, permittedActions);

        MockStreamsService streamService = new MockStreamsService(new ArrayList<>());
        MockFramework mockFramework = new MockFramework();
        mockFramework.setRBACService(mockRbacService);

        RBACValidator rbacValidator = new RBACValidator(mockFramework.getRBACService());
        GalasaStreamProcessor streamProcessor = new GalasaStreamProcessor(streamService, rbacValidator);

        String description = "This is a test stream";
        String streamUrl = "http://localhost:8080/streams/mystream";
        String requestUsername = "myuser";

        JsonObject streamJson = generateStreamJson(null, description, streamUrl, null, null);
        
        List<String> errors = streamProcessor.processResource(streamJson, DELETE, requestUsername);
        String errorMessage = errors.get(0);

        // Then...
        assertThat(errors).isNotEmpty();
        errorMessage.contains("GAL5427E");
        errorMessage.contains("E: Error occurred because the Galasa Stream is invalid. The 'metadata' field cannot be empty. The field 'name' is mandatory for the type GalasaStream.");
    }

    @Test
    public void testDeleteStreamWithInvalidNameEndingWithPeriodReturnsError() throws Exception {
        // Given...
        List<Action> permittedActions = List.of(GENERAL_API_ACCESS.getAction());
        MockRBACService mockRbacService = FilledMockRBACService.createTestRBACServiceWithTestUser(JWT_USERNAME, permittedActions);

        MockStreamsService streamService = new MockStreamsService(new ArrayList<>());
        MockFramework mockFramework = new MockFramework();
        mockFramework.setRBACService(mockRbacService);

        RBACValidator rbacValidator = new RBACValidator(mockFramework.getRBACService());
        GalasaStreamProcessor streamProcessor = new GalasaStreamProcessor(streamService, rbacValidator);

        String streamName = "mystream.";
        String description = "This is a test stream";
        String streamUrl = "http://localhost:8080/streams/mystream";
        String requestUsername = "myuser";

        JsonObject streamJson = generateStreamJson(streamName, description, streamUrl, null, null);
        
        List<String> errors = streamProcessor.processResource(streamJson, DELETE, requestUsername);
        String errorMessage = errors.get(0);

        // Then...
        assertThat(errors).isNotEmpty();
        errorMessage.contains("GAL5418");
        errorMessage.contains("E: Invalid 'name' provided. A valid stream name should always start with 'a'-'z' or 'A'-'Z' and end with 'a'-'z', 'A'-'Z' or 0-9.");
    }

    @Test
    public void testDeleteStreamDeletesStreamsOk() throws Exception {
        // Given...
        List<Action> permittedActions = List.of(GENERAL_API_ACCESS.getAction());
        MockRBACService mockRbacService = FilledMockRBACService.createTestRBACServiceWithTestUser(JWT_USERNAME, permittedActions);

        MockStreamsService streamService = new MockStreamsService(new ArrayList<>());
        MockFramework mockFramework = new MockFramework();
        mockFramework.setRBACService(mockRbacService);

        RBACValidator rbacValidator = new RBACValidator(mockFramework.getRBACService());
        GalasaStreamProcessor streamProcessor = new GalasaStreamProcessor(streamService, rbacValidator);

        String streamName = "mystream";
        String description = "This is a test stream";
        String streamUrl = "http://localhost:8080/streams/mystream";
        String requestUsername = "myuser";

        JsonObject streamJson = generateStreamJson(streamName, description, streamUrl, null, null);
        
        List<String> errors = streamProcessor.processResource(streamJson, DELETE, requestUsername);

        // Then...
        assertThat(errors).isEmpty();
    }

}
