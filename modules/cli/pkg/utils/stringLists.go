/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package utils

import "strings"

// The input is a slice containing strings, each of which could contain a single tag, or multiple tags in a
// comma-separated list.
// Here we mutate them into a single slice of tag strings, so they are all separate.
// We can also remove duplicates.
func CombineAllCommaSeparatedLists(stringListInputs []string) ([]string, error) {
	var err error

	collectedStrings := []string{}

	for _, commaSeparatedList := range stringListInputs {
		parts := strings.Split(commaSeparatedList, ",")

		for _, part := range parts {
			collectedStrings = append(collectedStrings, strings.TrimSpace(part))
		}
	}

	return collectedStrings, err
}
