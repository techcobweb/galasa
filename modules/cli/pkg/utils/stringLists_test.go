/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package utils

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNoTagsInAreGatheredOk(t *testing.T) {

	inputTagParameters := []string{}

	cmdValues := RunsSubmitCmdValues{
		Tags: inputTagParameters,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 0)
}

func TestNilTagsInAreGatheredOk(t *testing.T) {

	cmdValues := RunsSubmitCmdValues{
		Tags: nil,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 0)
}

func TestOneTagInIsGatheredOk(t *testing.T) {

	inputTagParameters := []string{}
	inputTagParameters = append(inputTagParameters, "tag1")

	cmdValues := RunsSubmitCmdValues{
		Tags: inputTagParameters,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 1)
	assert.EqualValues(t, tagsOut[0], "tag1")
}

func TestOneTagWithSpacesInIsGatheredOk(t *testing.T) {

	inputTagParameters := []string{}
	inputTagParameters = append(inputTagParameters, "   tag1   ")

	cmdValues := RunsSubmitCmdValues{
		Tags: inputTagParameters,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 1)
	assert.EqualValues(t, tagsOut[0], "tag1")
}

func TestTwoTagsInIsGatheredOk(t *testing.T) {

	inputTagParameters := []string{}
	inputTagParameters = append(inputTagParameters, "tag1")
	inputTagParameters = append(inputTagParameters, "tag2")

	cmdValues := RunsSubmitCmdValues{
		Tags: inputTagParameters,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 2)
	assert.EqualValues(t, tagsOut[0], "tag1")
	assert.EqualValues(t, tagsOut[1], "tag2")
}

func TestTwoTagsInAListIsGatheredOk(t *testing.T) {

	inputTagParameters := []string{}
	inputTagParameters = append(inputTagParameters, "tag1,tag2")

	cmdValues := RunsSubmitCmdValues{
		Tags: inputTagParameters,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 2)
	assert.EqualValues(t, tagsOut[0], "tag1")
	assert.EqualValues(t, tagsOut[1], "tag2")
}

func TestTwoTagsInAListWithSpacesIsGatheredOk(t *testing.T) {

	inputTagParameters := []string{}
	inputTagParameters = append(inputTagParameters, "  tag1  , tag2     ")

	cmdValues := RunsSubmitCmdValues{
		Tags: inputTagParameters,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 2)
	assert.EqualValues(t, tagsOut[0], "tag1")
	assert.EqualValues(t, tagsOut[1], "tag2")
}

func TestTwoTagsInAListWithSpacesAndTabsIsGatheredOk(t *testing.T) {

	inputTagParameters := []string{}
	inputTagParameters = append(inputTagParameters, "  tag1 \t , tag2    \t ")

	cmdValues := RunsSubmitCmdValues{
		Tags: inputTagParameters,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 2)
	assert.EqualValues(t, tagsOut[0], "tag1")
	assert.EqualValues(t, tagsOut[1], "tag2")
}

func TestAMixOfTagListsAndSingleTagsAreGatheredOk(t *testing.T) {

	inputTagParameters := []string{}
	inputTagParameters = append(inputTagParameters, "tag1,tag2")
	inputTagParameters = append(inputTagParameters, "tag3")

	cmdValues := RunsSubmitCmdValues{
		Tags: inputTagParameters,
	}

	tagsOut, err := CombineAllCommaSeparatedLists(cmdValues.Tags)

	assert.Nil(t, err)
	assert.NotNil(t, tagsOut)
	assert.Len(t, tagsOut, 3)
	assert.EqualValues(t, tagsOut[0], "tag1")
	assert.EqualValues(t, tagsOut[1], "tag2")
	assert.EqualValues(t, tagsOut[2], "tag3")
}
