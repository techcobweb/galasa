/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.ras.internal.common;

public class RasDetailsQueryParams {
    
    public static final String DETAILS_METHOD_QUERY_PARAM_VALUE = "methods";

    public static final String[] SUPPORTED_DETAIL_QUERY_PARAMS = {DETAILS_METHOD_QUERY_PARAM_VALUE};

    public RasDetailsQueryParams() {
        
    }

    public boolean isMethodDetailsExcluded(String detailParam) {

        boolean isMethodDetailsExcluded = true;
        if(detailParam != null && detailParam.equals(DETAILS_METHOD_QUERY_PARAM_VALUE)) {
            isMethodDetailsExcluded = false;
        }

        return isMethodDetailsExcluded;

    }

    public boolean isParamSupported(String queryParam) {

        boolean isParamSupported = false;

        for(String type: SUPPORTED_DETAIL_QUERY_PARAMS) {
            if(type.equals(queryParam))
            isParamSupported = true;
        }

        return isParamSupported;

    }

}
