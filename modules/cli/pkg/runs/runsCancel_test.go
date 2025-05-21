/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package runs

import (
	"fmt"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/galasa-dev/cli/pkg/api"
	"github.com/galasa-dev/cli/pkg/utils"
	"github.com/stretchr/testify/assert"
)

func WriteMockRasRunsPutStatusFinishedResponse(
	t *testing.T,
	writer http.ResponseWriter,
	req *http.Request,
	runName string) {

	var statusCode int
	var response string

	if runName == "U123" {
		statusCode = 202
		response = fmt.Sprintf("The request to cancel run %s has been received.", runName)
		writer.Header().Set("Content-Type", "text/plain")
	} else if runName == "U120" {
		statusCode = 400
		response = `{
			 "error_code": 5049,
			 "error_message": "GAL5049E: Error occured when trying to cancel the run 'U120'. The run has already completed."
		 }`
		writer.Header().Set("Content-Type", "application/json")
	} else if runName == "U121" {
		statusCode = 400
		response = `{{
			 not for parsing
		}`
	}

	writer.WriteHeader(statusCode)
	writer.Write([]byte(response))
}

func WriteMockGroupRunsPutStatusFinishedResponse(
	t *testing.T,
	writer http.ResponseWriter,
	req *http.Request,
	groupName string) {

	var statusCode int
	var response string

	if groupName == "valid-group-name" {
		statusCode = 202
		response = fmt.Sprintf("The request to cancel run with group id %s has been received.", groupName)
		writer.Header().Set("Content-Type", "text/plain")
	} else if groupName == "finished-runs-group" {
		statusCode = 200
		response = fmt.Sprintf("Info: When trying to cancel the run group '%s', no recent active (unfinished) test runs were found which are part of that group. Archived test runs may be part of that group, which can be queried separately from the Result Archive Store.", groupName)
		writer.Header().Set("Content-Type", "text/plain")
	}

	writer.WriteHeader(statusCode)
	writer.Write([]byte(response))
}

func NewRunsCancelServletMock(
	t *testing.T,
	runName string,
	runId string,
	runResultStrings []string,
) *httptest.Server {

	server := httptest.NewServer(http.HandlerFunc(func(writer http.ResponseWriter, req *http.Request) {

		assert.NotEmpty(t, req.Header.Get("ClientApiVersion"))
		acceptHeader := req.Header.Get("Accept")
		if req.URL.Path == "/ras/runs" {
			assert.Equal(t, "application/json", acceptHeader, "Expected Accept: application/json header, got: %s", acceptHeader)
			WriteMockRasRunsResponse(t, writer, req, runName, runResultStrings)
		} else if req.URL.Path == "/ras/runs/"+runId {
			assert.Equal(t, "application/json", acceptHeader, "Expected Accept: application/json header, got: %s", acceptHeader)
			WriteMockRasRunsPutStatusFinishedResponse(t, writer, req, runName)
		}
	}))

	return server
}

func NewGroupRunsCancelServletMock(
	t *testing.T,
	groupName string,
	runResultStrings []string,
) *httptest.Server {

	server := httptest.NewServer(http.HandlerFunc(func(writer http.ResponseWriter, req *http.Request) {

		assert.NotEmpty(t, req.Header.Get("ClientApiVersion"))
		acceptHeader := req.Header.Get("Accept")

		assert.Equal(t, "application/json", acceptHeader, "Expected Accept: application/json header, got: %s", acceptHeader)
		WriteMockGroupRunsPutStatusFinishedResponse(t, writer, req, groupName)

	}))

	return server
}

//------------------------------------------------------------------
// Test methods
//------------------------------------------------------------------

func TestRunsCancelWithOneActiveRunReturnsOK(t *testing.T) {
	// Given ...
	runName := "U123"
	runId := "xxx123xxx"
	group := ""

	runResultStrings := []string{RUN_U123_RE_RUN}

	server := NewRunsCancelServletMock(t, runName, runId, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Nil(t, err)
	textGotBack := mockConsole.ReadText()
	assert.Contains(t, textGotBack, "GAL2504I")
	assert.Contains(t, textGotBack, runName)
}

func TestRunsCancelWithMultipleActiveRunsReturnsOK(t *testing.T) {
	// Given ...
	runName := "U123"
	runId := "xxx122xxx"
	group := ""

	runResultStrings := []string{RUN_U123_FIRST_RUN, RUN_U123_RE_RUN, RUN_U123_RE_RUN_2}

	server := NewRunsCancelServletMock(t, runName, runId, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Nil(t, err)
	textGotBack := mockConsole.ReadText()
	assert.Contains(t, textGotBack, "GAL2504I")
	assert.Contains(t, textGotBack, runName)
}

func TestRunsCancelWithNoActiveRunReturnsError(t *testing.T) {
	// Given ...
	runName := "U123"
	runId := "xxx123xxx"
	group := ""

	runResultStrings := []string{}

	server := NewRunsCancelServletMock(t, runName, runId, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Contains(t, err.Error(), "GAL1132")
	assert.Contains(t, err.Error(), runName)
}

func TestRunsCancelWithInvalidRunNameReturnsError(t *testing.T) {
	// Given ...
	runName := "garbage"
	runId := "xxx123xxx"
	group := ""

	runResultStrings := []string{RUN_U123_RE_RUN}

	server := NewRunsCancelServletMock(t, runName, runId, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Contains(t, err.Error(), "GAL1075")
	assert.Contains(t, err.Error(), runName)
}

func TestRunsCancelWhereOperationFailedServerSideReturnsError(t *testing.T) {
	// Given ...
	runName := "U120"
	runId := "xxx120xxx"
	group := ""

	runResultStrings := []string{RUN_U120}

	server := NewRunsCancelServletMock(t, runName, runId, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Error(t, err)
	assert.ErrorContains(t, err, "GAL1135")
}

func TestRunsCancelWhereServerSideResponseCannotBeParsedReturnsError(t *testing.T) {
	// Given ...
	runName := "U121"
	runId := "xxx121xxx"
	group := ""

	runResultStrings := []string{RUN_U121}

	server := NewRunsCancelServletMock(t, runName, runId, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Error(t, err)
	assert.ErrorContains(t, err, "GAL1136")
}

func TestGroupRunsCancelWithInvalidGroupNameReturnsError(t *testing.T) {
	// Given ...
	runName := ""
	group := "bad$group.name"

	runResultStrings := []string{RUN_U123_RE_RUN}

	server := NewGroupRunsCancelServletMock(t, group, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Contains(t, err.Error(), "GAL1105")
}

func TestGroupRunsCancelWithValidGroupNameReturnsAccepted(t *testing.T) {
	// Given ...
	runName := ""
	group := "valid-group-name"

	runResultStrings := []string{RUN_U123_RE_RUN}

	server := NewGroupRunsCancelServletMock(t, group, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Nil(t, err)
	textGotBack := mockConsole.ReadText()
	assert.Contains(t, textGotBack, "GAL2505I")
	assert.Contains(t, textGotBack, group)
}

func TestGroupRunsCancelWithRunsAlreadyFInishedReturnsOK(t *testing.T) {
	// Given ...
	runName := ""
	group := "finished-runs-group"

	runResultStrings := []string{RUN_U120}

	server := NewGroupRunsCancelServletMock(t, group, runResultStrings)
	defer server.Close()

	mockConsole := utils.NewMockConsole()

	apiServerUrl := server.URL
	mockTimeService := utils.NewMockTimeService()
	commsClient := api.NewMockAPICommsClient(apiServerUrl)

	// When...
	err := CancelRun(runName, mockTimeService, mockConsole, commsClient, group)

	// Then...
	assert.Nil(t, err)
	textGotBack := mockConsole.ReadText()
	assert.Contains(t, textGotBack, "GAL2506I")
	assert.Contains(t, textGotBack, group)
}
