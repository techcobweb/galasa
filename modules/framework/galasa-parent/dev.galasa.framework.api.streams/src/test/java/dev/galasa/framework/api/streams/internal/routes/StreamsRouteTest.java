/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.streams.internal.routes;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.junit.Test;

import dev.galasa.framework.api.common.BaseServletTest;
import dev.galasa.framework.api.common.EnvironmentVariables;
import dev.galasa.framework.api.common.mocks.MockEnvironment;
import dev.galasa.framework.api.common.mocks.MockFramework;
import dev.galasa.framework.api.common.mocks.MockHttpServletRequest;
import dev.galasa.framework.api.common.mocks.MockHttpServletResponse;
import dev.galasa.framework.api.common.mocks.MockIConfigurationPropertyStoreService;
import dev.galasa.framework.api.streams.mocks.MockStreamsServlet;
import dev.galasa.framework.mocks.FilledMockRBACService;
import dev.galasa.framework.mocks.MockRBACService;

public class StreamsRouteTest extends BaseServletTest {

    public static final String QUERY_PARAM_STREAM_NAME = "name";
    public static final String TEST_CPS_NAMESPACE = "framework";

    @Test
    public void testGetStreamsRouteWhenInvalidNameQueryParamPovidedReturnsBadRequest() throws Exception {
        // Given...

        Map<String, String> headerMap = Map.of("Authorization", "Bearer " + BaseServletTest.DUMMY_JWT);

        String mockStreamName = null;
        Map<String, String[]> queryParams = new HashMap<>();
        queryParams.put(QUERY_PARAM_STREAM_NAME, new String[] { mockStreamName });

        MockIConfigurationPropertyStoreService mockIConfigurationPropertyStoreService = new MockIConfigurationPropertyStoreService();

        MockRBACService mockRBACService = FilledMockRBACService.createTestRBACServiceWithTestUser(JWT_USERNAME);
        MockFramework mockFramework = new MockFramework(mockRBACService);

        MockEnvironment env = new MockEnvironment();
        env.setenv(EnvironmentVariables.GALASA_USERNAME_CLAIMS, "preferred_username");

        MockStreamsServlet mockServlet = new MockStreamsServlet(mockFramework, env, mockIConfigurationPropertyStoreService);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest(queryParams, null, headerMap);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        ServletOutputStream outStream = servletResponse.getOutputStream();

        // When...
        mockServlet.init();
        mockServlet.doGet(mockRequest, servletResponse);

        assertThat(servletResponse.getStatus()).isEqualTo(400);
        checkErrorStructure(outStream.toString(), 5418, "GAL5418E");
    }

    @Test
    public void testGetStreamsRouteWhenUnsupportedQueryParamPovidedReturnsBadRequest() throws Exception {

        // Given...
        Map<String, String> headerMap = Map.of("Authorization", "Bearer " + BaseServletTest.DUMMY_JWT);

        String mockStreamName = "fakeParamValue";
        Map<String, String[]> queryParams = new HashMap<>();
        queryParams.put("unsupportedParam", new String[] { mockStreamName });

        MockRBACService mockRBACService = FilledMockRBACService.createTestRBACServiceWithTestUser(JWT_USERNAME);
        MockFramework mockFramework = new MockFramework(mockRBACService);
        MockIConfigurationPropertyStoreService mockIConfigurationPropertyStoreService = new MockIConfigurationPropertyStoreService();

        MockEnvironment env = new MockEnvironment();
        env.setenv(EnvironmentVariables.GALASA_USERNAME_CLAIMS, "preferred_username");

        MockStreamsServlet mockServlet = new MockStreamsServlet(mockFramework, env, mockIConfigurationPropertyStoreService);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest(queryParams, null, headerMap);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        ServletOutputStream outStream = servletResponse.getOutputStream();

        // When...
        mockServlet.init();
        mockServlet.doGet(mockRequest, servletResponse);

        assertThat(servletResponse.getStatus()).isEqualTo(400);
        assertThat(outStream.toString()).contains("'name'");
        checkErrorStructure(outStream.toString(), 5412, "GAL5412E");
    }

    // @Test
    // public void testGetStreamsRouteWhenValidNonMatchingNameQueryParamPovidedReturnsOK() throws Exception {

    //     // Given...
    //     Map<String, String> headerMap = Map.of("Authorization", "Bearer " + BaseServletTest.DUMMY_JWT);

    //     String mockStreamName = "myTestStream";
    //     Map<String, String[]> queryParams = new HashMap<>();
    //     queryParams.put(QUERY_PARAM_STREAM_NAME, new String[] { mockStreamName });

    //     MockRBACService mockRBACService = FilledMockRBACService.createTestRBACServiceWithTestUser(JWT_USERNAME);
    //     MockFramework mockFramework = new MockFramework(mockRBACService);
    //     MockIConfigurationPropertyStoreService mockIConfigurationPropertyStoreService = new MockIConfigurationPropertyStoreService(TEST_CPS_NAMESPACE);

    //     MockEnvironment env = new MockEnvironment();
    //     env.setenv(EnvironmentVariables.GALASA_USERNAME_CLAIMS, "preferred_username");

    //     MockStreamsServlet mockServlet = new MockStreamsServlet(mockFramework, env, mockIConfigurationPropertyStoreService);

    //     MockHttpServletRequest mockRequest = new MockHttpServletRequest(queryParams, null, headerMap);
    //     MockHttpServletResponse servletResponse = new MockHttpServletResponse();
    //     ServletOutputStream outStream = servletResponse.getOutputStream();

    //     // When...
    //     mockServlet.init();
    //     mockServlet.doGet(mockRequest, servletResponse);

    //     String output = outStream.toString();

    //     assertThat(servletResponse.getStatus()).isEqualTo(200);
    //     assertThat(servletResponse.getContentType()).isEqualTo("application/json");
    //     assertThat(getJsonArrayFromJson(output, "streams")).hasSize(0);
    // }

    // @Test
    // public void testGetStreamsRouteReturnsAllStreamsOK() throws Exception {

    //     // Given...
    //     Map<String, String> headerMap = Map.of("Authorization", "Bearer " + BaseServletTest.DUMMY_JWT);


    //     MockRBACService mockRBACService = FilledMockRBACService.createTestRBACServiceWithTestUser(JWT_USERNAME);
    //     MockFramework mockFramework = new MockFramework(mockRBACService);
    //     MockIConfigurationPropertyStoreService mockIConfigurationPropertyStoreService = new MockIConfigurationPropertyStoreService(TEST_CPS_NAMESPACE);

    //     MockEnvironment env = new MockEnvironment();
    //     env.setenv(EnvironmentVariables.GALASA_USERNAME_CLAIMS, "preferred_username");

    //     MockStreamsServlet mockServlet = new MockStreamsServlet(mockFramework, env, mockIConfigurationPropertyStoreService);

    //     MockHttpServletRequest mockRequest = new MockHttpServletRequest(null, headerMap);
    //     MockHttpServletResponse servletResponse = new MockHttpServletResponse();
    //     ServletOutputStream outStream = servletResponse.getOutputStream();

    //     // When...
    //     mockServlet.init();
    //     mockServlet.doGet(mockRequest, servletResponse);

    //     String output = outStream.toString();

    //     assertThat(servletResponse.getStatus()).isEqualTo(200);
    //     assertThat(servletResponse.getContentType()).isEqualTo("application/json");
    //     assertThat(getJsonArrayFromJson(output, "streams")).hasSize(0);
    // }

}
