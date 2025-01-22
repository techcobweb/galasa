/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.common;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestQueryParameters {

    @Test
    public void testQueryParametersCanGiveAListOfUnsupportedQueryParameters() throws Exception {

        Map<String,String[]> paramsToTest = new HashMap<String,String[]>();
        
        paramsToTest.put("animal",new String[] { "cat" });
        paramsToTest.put("colour",new String[] { "blue" });
        paramsToTest.put("size",new String[] { "big" });
        QueryParameters params = new QueryParameters(paramsToTest);

        SupportedQueryParameterNames supportedParamNames = new SupportedQueryParameterNames("animal","colour");

        List<String> unusupportedParamNames = params.getUnsupportedQueryParameters(supportedParamNames); // size is not supported.

        assertThat(unusupportedParamNames).hasSize(1).contains("size");
    }





    @Test
    public void testQueryParametersCanGiveAListOfUnusedQueryParametersWhenOneParamIsUsed() throws Exception {

        Map<String,String[]> paramsToTest = new HashMap<String,String[]>();
        
        paramsToTest.put("animal",new String[] { "cat" });
        paramsToTest.put("colour",new String[] { "blue" });
        paramsToTest.put("size",new String[] { "big" });
        QueryParameters params = new QueryParameters(paramsToTest);

        // When...
        InternalServletException ex = catchThrowableOfType( ()->params.checkForUnsupportedQueryParameters(new SupportedQueryParameterNames("animal")) , InternalServletException.class);

        assertThat(ex).hasMessageContaining("'animal'","'colour','size'");
    }

}