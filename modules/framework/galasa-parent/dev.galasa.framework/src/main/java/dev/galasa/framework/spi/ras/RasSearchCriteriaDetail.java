/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.spi.ras;

import javax.validation.constraints.NotNull;

import dev.galasa.framework.spi.teststructure.TestStructure;

public class RasSearchCriteriaDetail implements IRasSearchCriteria {

    private final String[] detail;

    public RasSearchCriteriaDetail(@NotNull String... detailCriteria) {
        this.detail = detailCriteria;
    }

    @Override
    public boolean criteriaMatched(@NotNull TestStructure structure) {
        
        if(structure == null) {
            return Boolean.FALSE;
        }

        if(detail != null) {
            for(String detailIn : detail) {
                if(detailIn.equals("methods")) {
                    return Boolean.TRUE;
                }
            }
        }
        
        return Boolean.FALSE;
        
    }

    public String[] getDetails() {
        return this.detail;
    }

}
