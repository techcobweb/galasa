/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.ras.internal.routes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.galasa.api.ras.RasRunResult;
import dev.galasa.api.ras.RasTestStructure;
import dev.galasa.framework.api.common.mocks.MockFramework;
import dev.galasa.framework.api.common.mocks.MockHttpServletRequest;
import dev.galasa.framework.api.common.mocks.MockIFrameworkRuns;
import dev.galasa.framework.api.common.mocks.MockIRun;
import dev.galasa.framework.api.ras.internal.RasServlet;
import dev.galasa.framework.api.ras.internal.RasServletTest;
import dev.galasa.framework.api.ras.internal.mocks.MockArchiveStore;
import dev.galasa.framework.api.ras.internal.mocks.MockRasServletEnvironment;
import dev.galasa.framework.mocks.MockResultArchiveStoreDirectoryService;
import dev.galasa.framework.spi.DynamicStatusStoreException;
import dev.galasa.framework.spi.IFrameworkRuns;
import dev.galasa.framework.spi.IResultArchiveStoreDirectoryService;
import dev.galasa.framework.spi.IRun;
import dev.galasa.framework.spi.IRunResult;

import static org.assertj.core.api.Assertions.*;

public class TestGroupDetailsRoute extends RasServletTest {

	Pattern grouPattern = Pattern.compile(GroupDetailsRoute.path);
    
    public String generateExpectedJson (String runId, String runName, String groupId) {

		RasTestStructure testStructure = new RasTestStructure(runName, null, null, null, "galasa", null, "Passed", null, null, null, Collections.emptyList(), groupId, "some-submission-id", new HashSet<String>());
		RasRunResult rasRunResult = new RasRunResult(runId, Collections.emptyList(), testStructure);

		testStructure.setRunName(runName);
		testStructure.setRequestor("galasa");
		testStructure.setResult("Passed");
		testStructure.setGroup(groupId);
		testStructure.setMethods(Collections.emptyList());

		rasRunResult.setRunId(runId);
		rasRunResult.setArtifacts(Collections.emptyList());
		rasRunResult.setTestStructure(testStructure);

		return gson.toJson(rasRunResult);
    }

	public String generateStatusUpdateJson(String status, String result) {
		return
		"{\n" +
	    "  \"status\": \"" +  status + "\",\n" +
		"  \"result\": \"" + result + "\"\n" +
		"}";
	}

    /*
     * Regex Path
     */

    @Test
	public void testPathRegexExpectedLocalPathReturnsTrue(){
		//Given...
		String inputPath = "/groups/lcl-abcd-1234.run/";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isTrue();
	}

	@Test
	public void testPathRegexExpectedCouchDBPathReturnsTrue(){
		//Given...
		String inputPath = "/groups/cdb-efgh-5678.run/";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isTrue();
	}

	@Test
	public void testPathRegexExpectedNoTrailingForwardSlashReturnsTrue(){
		//Given...
		String inputPath = "/groups/cdb-efgh-5678.run";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isTrue();
	}

	@Test
	public void testPathRegexLowerCasePathReturnsTrue(){
		//Given...
		String inputPath = "/groups/cdbstoredrun/";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isTrue();
	}
	
	@Test
	public void testPathRegexExpectedPathWithCapitalLeadingLetterReturnsTrue(){
		//Given...
		String inputPath = "/groups/ABC-DEFG-5678.run/";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isTrue();
	}
	
	@Test
	public void testPathRegexUpperCasePathReturnsFalse(){
		//Given...
		String inputPath = "/RUNS/cdb-EFGH-5678.run/";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isFalse();
	}
 
	@Test
	public void testPathRegexExpectedPathWithLeadingNumberReturnsFalse(){
		//Given...
		String inputPath = "/1runs/cdb-EFGH-5678.run/";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isFalse();
	}
 
	@Test
	public void testPathRegexExpectedPathWithTrailingForwardSlashReturnsFalse(){
		//Given...
		String inputPath = "/groups/cdb-EFGH-5678.run//";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isFalse();
	}
 
	@Test
	public void testPathRegexNumberPathReturnsFalse(){
		//Given...
		
		String inputPath = "/groups/cdb-EFGH-5678.run/1";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isFalse();
	}

	@Test
	public void testPathRegexUnexpectedPathReturnsFalse(){
		//Given...
		String inputPath = "/groups/cdb-EFGH-5678.run/randompath";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isFalse();
	}

	@Test
	public void testPathRegexEmptyPathReturnsFalse(){
		//Given...
		String inputPath = "";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isFalse();
	}

	@Test
	public void testPathRegexSpecialCharacterPathReturnsFalse(){
		//Given...
		String inputPath = "/groups/cdb-EFGH-5678.run/?";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isFalse();
	}

	@Test
	public void testPathRegexMultipleForwardSlashPathReturnsFalse(){
		//Given...
		String inputPath = "/groups/cdb-EFGH-5678.run///////";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).isFalse();
	} 


	@Test
	public void testRegexWithMaliciousHtmlInsideFailsRegexMatching(){
		//Given...
		String inputPath = "/groups/ABC-<href>/";

		//When...
		boolean matches = grouPattern.matcher(inputPath).matches();

		//Then...
		assertThat(matches).as("malicious regex containing html was not treated as invalid input.").isFalse();
	}

	@Test
	public void testRequestToResetRunByGroupIdWithNullStatusReturnsBadRequest() throws Exception {
		// Given...
		String runId = "xx12345xx";
		String runName = "U123";
		String groupId = "group-one";

		List<IRunResult> mockInputRunResults = generateTestData(runId, runName, null,"galasa", groupId);

		String content = generateStatusUpdateJson(null, ""); //reset run with null status
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("/groups/" + groupId, content, "PUT");
	
		IFrameworkRuns frameworkRuns = new MockIFrameworkRuns(new ArrayList<>());
		MockResultArchiveStoreDirectoryService mockrasService = new MockResultArchiveStoreDirectoryService(mockInputRunResults);
		List<IResultArchiveStoreDirectoryService> directoryServices = new ArrayList<IResultArchiveStoreDirectoryService>();
		directoryServices.add(mockrasService);
		MockFramework mockFramework = new MockFramework(new MockArchiveStore(directoryServices), frameworkRuns);
		MockRasServletEnvironment mockServletEnvironment = new MockRasServletEnvironment(mockFramework, mockInputRunResults, mockRequest);

		RasServlet servlet = mockServletEnvironment.getRasServlet();
		HttpServletRequest req = mockServletEnvironment.getRequest();
		HttpServletResponse resp = mockServletEnvironment.getResponse();
		ServletOutputStream outStream = resp.getOutputStream();

		// When...
		servlet.init();
		servlet.doPut(req, resp);

		// Then...
		assertThat(resp.getStatus()).isEqualTo(400);
		checkErrorStructure(outStream.toString(), 5045, "E: Error occurred. The field 'status' in the request body is invalid.");
		assertThat(resp.getContentType()).isEqualTo("application/json");
	}

	@Test
	public void testRequestToCancelRunByGroupIdWithBadResultReturnsBadRequest() throws Exception {
		// Given...
		String runId = "xx12345xx";
		String runName = "U123";
		String groupId = "group-one";

		List<IRunResult> mockInputRunResults = generateTestData(runId, runName, null,"galasa", groupId);

		String content = generateStatusUpdateJson("finished", "some-invalid-result"); //reset run with null status
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("/groups/" + groupId, content, "PUT");
	
		IFrameworkRuns frameworkRuns = new MockIFrameworkRuns(new ArrayList<>());
		MockResultArchiveStoreDirectoryService mockrasService = new MockResultArchiveStoreDirectoryService(mockInputRunResults);
		List<IResultArchiveStoreDirectoryService> directoryServices = new ArrayList<IResultArchiveStoreDirectoryService>();
		directoryServices.add(mockrasService);
		MockFramework mockFramework = new MockFramework(new MockArchiveStore(directoryServices), frameworkRuns);
		MockRasServletEnvironment mockServletEnvironment = new MockRasServletEnvironment(mockFramework, mockInputRunResults, mockRequest);

		RasServlet servlet = mockServletEnvironment.getRasServlet();
		HttpServletRequest req = mockServletEnvironment.getRequest();
		HttpServletResponse resp = mockServletEnvironment.getResponse();
		ServletOutputStream outStream = resp.getOutputStream();

		// When...
		servlet.init();
		servlet.doPut(req, resp);

		// Then...
		assertThat(resp.getStatus()).isEqualTo(400);
		checkErrorStructure(outStream.toString(), 5046, "E: Error occurred when trying to cancel the run ");
		assertThat(resp.getContentType()).isEqualTo("application/json");
	}

	@Test
	public void testRequestToResetRunNoLongerProcessingReturnsError() throws Exception {
		// Given...
		String runId = "xx12345xx";
		String runName = "U123";
		String groupId = "group-one";

		List<IRunResult> mockInputRunResults = generateTestData(runId, runName, null, "galasa", groupId);

		String content = generateStatusUpdateJson("queued", runName);
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("/groups/" + groupId, content, "PUT");
		
		List<IRun> runs = new ArrayList<IRun>();
		Set<String> tags = new HashSet<>();
		runs.add(new MockIRun(runName, "type1", "requestor1", "test1", "BUILDING", "bundle1", "testClass1", "group1", "submission1",tags));
		IFrameworkRuns frameworkRuns = new MockIFrameworkRuns(runs) {
			@Override
    		public boolean markRunInterrupted(String runname, String interruptReason) throws DynamicStatusStoreException {
        		return false;
			}
		};
		MockResultArchiveStoreDirectoryService mockrasService = new MockResultArchiveStoreDirectoryService(mockInputRunResults);
		List<IResultArchiveStoreDirectoryService> directoryServices = new ArrayList<IResultArchiveStoreDirectoryService>();
		directoryServices.add(mockrasService);
		MockFramework mockFramework = new MockFramework(new MockArchiveStore(directoryServices), frameworkRuns);
		MockRasServletEnvironment mockServletEnvironment = new MockRasServletEnvironment(mockFramework, mockInputRunResults, mockRequest);

		RasServlet servlet = mockServletEnvironment.getRasServlet();
		HttpServletRequest req = mockServletEnvironment.getRequest();
		HttpServletResponse resp = mockServletEnvironment.getResponse();
		ServletOutputStream outStream = resp.getOutputStream();

		// When...
		servlet.init();
		servlet.doPut(req, resp);

		// Then...
		assertThat(resp.getStatus()).isEqualTo(400);
		checkErrorStructure(outStream.toString(), 
			5049, 
			"E: Error occurred when trying to reset the run 'U123'. The run has already completed.");
	}

	@Test
	public void testRequestToCancelRunNoLongerProcessingReturnsError() throws Exception {
		// Given...
		String runId = "xx12345xx";
		String runName = "U123";
		String groupId = "group-one";

		List<IRunResult> mockInputRunResults = generateTestData(runId, runName, null, "galasa", groupId);

		String content = generateStatusUpdateJson("finished", "cancelled");
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("/runs/" + runId, content, "PUT");
		
		List<IRun> runs = new ArrayList<IRun>();
		Set<String> tags = new HashSet<>();
		runs.add(new MockIRun(runName, "type1", "requestor1", "test1", "BUILDING", "bundle1", "testClass1", "group1", "submission1",tags));
		IFrameworkRuns frameworkRuns = new MockIFrameworkRuns(runs) {
			@Override
			public boolean markRunInterrupted(String runname, String interruptReason) throws DynamicStatusStoreException {
        		return false;
			}
		};
		MockResultArchiveStoreDirectoryService mockrasService = new MockResultArchiveStoreDirectoryService(mockInputRunResults);
		List<IResultArchiveStoreDirectoryService> directoryServices = new ArrayList<IResultArchiveStoreDirectoryService>();
		directoryServices.add(mockrasService);
		MockFramework mockFramework = new MockFramework(new MockArchiveStore(directoryServices), frameworkRuns);
		MockRasServletEnvironment mockServletEnvironment = new MockRasServletEnvironment(mockFramework, mockInputRunResults, mockRequest);

		RasServlet servlet = mockServletEnvironment.getRasServlet();
		HttpServletRequest req = mockServletEnvironment.getRequest();
		HttpServletResponse resp = mockServletEnvironment.getResponse();
		ServletOutputStream outStream = resp.getOutputStream();

		// When...
		servlet.init();
		servlet.doPut(req, resp);

		// Then...
		assertThat(resp.getStatus()).isEqualTo(400);
		checkErrorStructure(outStream.toString(), 
			5050, 
			"E: Error occurred when trying to cancel the run 'U123'. The run has already completed.");
	}

    @Test
	public void testRequestToResetRunByGroupIdReturnsOK() throws Exception {
		// Given...
		String runId = "xx12345xx";
		String runName = "U123";
		String groupId = "group-one";

		List<IRunResult> mockInputRunResults = generateTestData(runId, runName, null,"galasa", groupId);

		String content = generateStatusUpdateJson("queued", ""); //reset run
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("/groups/" + groupId, content, "PUT");
		
		List<IRun> runs = new ArrayList<IRun>();
		Set<String> tags = new HashSet<>();
		runs.add(new MockIRun(runName, "type1", "requestor1", "test1", "BUILDING", "bundle1", "testClass1", "group-two", "submission1", tags));
		
		IFrameworkRuns frameworkRuns = new MockIFrameworkRuns(runs);
		MockResultArchiveStoreDirectoryService mockrasService = new MockResultArchiveStoreDirectoryService(mockInputRunResults);
		List<IResultArchiveStoreDirectoryService> directoryServices = new ArrayList<IResultArchiveStoreDirectoryService>();
		directoryServices.add(mockrasService);
		MockFramework mockFramework = new MockFramework(new MockArchiveStore(directoryServices), frameworkRuns);
		MockRasServletEnvironment mockServletEnvironment = new MockRasServletEnvironment(mockFramework, mockInputRunResults, mockRequest);

		RasServlet servlet = mockServletEnvironment.getRasServlet();
		HttpServletRequest req = mockServletEnvironment.getRequest();
		HttpServletResponse resp = mockServletEnvironment.getResponse();
		ServletOutputStream outStream = resp.getOutputStream();

		// When...
		servlet.init();
		servlet.doPut(req, resp);

		// Then...
		assertThat(resp.getStatus()).isEqualTo(202);
		assertThat(outStream.toString()).isEqualTo("The request to reset run with group id " + groupId + " has been received.");
		assertThat(resp.getContentType()).isEqualTo("text/plain");
	}

	@Test
	public void testRequestToCancelRunByGroupIdReturnsOK() throws Exception {
		// Given...
		String runId = "xx12345xx";
		String runName = "U123";
		String groupId = "group-one";

		List<IRunResult> mockInputRunResults = generateTestData(runId, runName, null,"galasa", groupId);

		String content = generateStatusUpdateJson("finished", "cancelled"); //cancel run
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("/groups/" + groupId, content, "PUT");
		
		List<IRun> runs = new ArrayList<IRun>();
		Set<String> tags = new HashSet<>();
		runs.add(new MockIRun(runName, "type1", "requestor1", "test1", "BUILDING", "bundle1", "testClass1", groupId, "submission1",tags));
		IFrameworkRuns frameworkRuns = new MockIFrameworkRuns(runs);
		MockResultArchiveStoreDirectoryService mockrasService = new MockResultArchiveStoreDirectoryService(mockInputRunResults);
		List<IResultArchiveStoreDirectoryService> directoryServices = new ArrayList<IResultArchiveStoreDirectoryService>();
		directoryServices.add(mockrasService);
		MockFramework mockFramework = new MockFramework(new MockArchiveStore(directoryServices), frameworkRuns);
		MockRasServletEnvironment mockServletEnvironment = new MockRasServletEnvironment(mockFramework, mockInputRunResults, mockRequest);

		RasServlet servlet = mockServletEnvironment.getRasServlet();
		HttpServletRequest req = mockServletEnvironment.getRequest();
		HttpServletResponse resp = mockServletEnvironment.getResponse();
		ServletOutputStream outStream = resp.getOutputStream();

		// When...
		servlet.init();
		servlet.doPut(req, resp);

		// Then...
		assertThat(resp.getStatus()).isEqualTo(202);
		assertThat(outStream.toString()).isEqualTo("The request to cancel run with group id " + groupId + " has been received.");
		assertThat(resp.getContentType()).isEqualTo("text/plain");
	}

	@Test
	public void testRequestToResetRunFailsReturnsError() throws Exception {
		// Given...
		String runId = "xx12345xx";
		String runName = "U123";
		String groupId = "group-one";

		List<IRunResult> mockInputRunResults = generateTestData(runId, runName, null,"galasa", groupId);

		String content = generateStatusUpdateJson("queued", "");
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("/groups/" + groupId, content, "PUT");
		
		List<IRun> runs = new ArrayList<IRun>();
		Set<String> tags = new HashSet<>();
		runs.add(new MockIRun(runName, "type1", "requestor1", "test1", "BUILDING", "bundle1", "testClass1", groupId, "submission1",tags));
		IFrameworkRuns frameworkRuns = new MockIFrameworkRuns(runs) {
			@Override
    		public boolean markRunInterrupted(String runname, String interruptReason) throws DynamicStatusStoreException {
        		throw new DynamicStatusStoreException();
			}
		};
		MockResultArchiveStoreDirectoryService mockrasService = new MockResultArchiveStoreDirectoryService(mockInputRunResults);
		List<IResultArchiveStoreDirectoryService> directoryServices = new ArrayList<IResultArchiveStoreDirectoryService>();
		directoryServices.add(mockrasService);
		MockFramework mockFramework = new MockFramework(new MockArchiveStore(directoryServices), frameworkRuns);
		MockRasServletEnvironment mockServletEnvironment = new MockRasServletEnvironment(mockFramework, mockInputRunResults, mockRequest);

		RasServlet servlet = mockServletEnvironment.getRasServlet();
		HttpServletRequest req = mockServletEnvironment.getRequest();
		HttpServletResponse resp = mockServletEnvironment.getResponse();
		ServletOutputStream outStream = resp.getOutputStream();

		// When...
		servlet.init();
		servlet.doPut(req, resp);

		// Then...
		assertThat(resp.getStatus()).isEqualTo(500);
		checkErrorStructure(outStream.toString(), 
			5047, 
			"E: Error occurred when trying to reset the run 'U123'. Report the problem to your Galasa Ecosystem owner.");
	}

	@Test
	public void testRequestToCancelRunFailsReturnsError() throws Exception {
		// Given...
		String runId = "xx12345xx";
		String runName = "U123";
		String groupId = "group-one";

		List<IRunResult> mockInputRunResults = generateTestData(runId, runName, null,"galasa", groupId);

		String content = generateStatusUpdateJson("finished", "cancelled");
		MockHttpServletRequest mockRequest = new MockHttpServletRequest("/groups/" + groupId, content, "PUT");
		
		List<IRun> runs = new ArrayList<IRun>();
		Set<String> tags = new HashSet<>();
		runs.add(new MockIRun(runName, "type1", "requestor1", "test1", "BUILDING", "bundle1", "testClass1", groupId, "submission1",tags));
		IFrameworkRuns frameworkRuns = new MockIFrameworkRuns(runs) {
			@Override
    		public boolean markRunInterrupted(String runname, String interruptReason) throws DynamicStatusStoreException {
        		throw new DynamicStatusStoreException();
			}
		};
		MockResultArchiveStoreDirectoryService mockrasService = new MockResultArchiveStoreDirectoryService(mockInputRunResults);
		List<IResultArchiveStoreDirectoryService> directoryServices = new ArrayList<IResultArchiveStoreDirectoryService>();
		directoryServices.add(mockrasService);
		MockFramework mockFramework = new MockFramework(new MockArchiveStore(directoryServices), frameworkRuns);
		MockRasServletEnvironment mockServletEnvironment = new MockRasServletEnvironment(mockFramework, mockInputRunResults, mockRequest);

		RasServlet servlet = mockServletEnvironment.getRasServlet();
		HttpServletRequest req = mockServletEnvironment.getRequest();
		HttpServletResponse resp = mockServletEnvironment.getResponse();
		ServletOutputStream outStream = resp.getOutputStream();

		// When...
		servlet.init();
		servlet.doPut(req, resp);

		// Then...
		assertThat(resp.getStatus()).isEqualTo(500);
		checkErrorStructure(outStream.toString(), 
			5048, 
			"Error occurred when trying to cancel the run 'U123'");
	}

}
