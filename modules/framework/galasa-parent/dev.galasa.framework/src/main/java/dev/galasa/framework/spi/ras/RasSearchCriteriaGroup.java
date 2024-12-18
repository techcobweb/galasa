/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.spi.ras;

import javax.validation.constraints.NotNull;

import dev.galasa.framework.spi.teststructure.TestStructure;

public class RasSearchCriteriaGroup implements IRasSearchCriteria {
	
	private final String[] group;

	public RasSearchCriteriaGroup(@NotNull String... testNameCriteria) {
		this.group = testNameCriteria;
	}

	@Override
	public boolean criteriaMatched(@NotNull TestStructure structure) {

		if(structure == null) {
			return Boolean.FALSE;	
		}

		if(group != null) {
			for(String groupIn : group) {
				if(groupIn.equals(structure.getGroup())) {
					return Boolean.TRUE;
				}
			}
		}

		return Boolean.FALSE;
	}


    public String[] getGroups() {
        return this.group;
    }
}
	
