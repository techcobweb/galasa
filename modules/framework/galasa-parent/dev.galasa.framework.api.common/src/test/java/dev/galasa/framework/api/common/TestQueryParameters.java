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

        List<String> supportedParamNames = List.of("animal","colour");

        List<String> unusupportedParamNames = params.getUnsupportedQueryParameters(supportedParamNames); // size is not supported.

        assertThat(unusupportedParamNames).hasSize(1).contains("size");
    }

    @Test
    public void testQueryParametersCanCreateAQuotedList() throws Exception {

        Map<String,String[]> paramsToTest = new HashMap<String,String[]>();
        
        QueryParameters params = new QueryParameters(paramsToTest);

        String renderedList = params.listToString(List.of("cat","dog"));

        assertThat(renderedList).isEqualTo("'cat','dog'");
    }


    @Test
    public void testQueryParametersCanCreateASortedQuotedList() throws Exception {

        Map<String,String[]> paramsToTest = new HashMap<String,String[]>();
        
        QueryParameters params = new QueryParameters(paramsToTest);

        String renderedList = params.listToString(List.of("cat","dog"));
        assertThat(renderedList).isEqualTo("'cat','dog'");

        renderedList = params.listToString(List.of("dog","cat"));
        assertThat(renderedList).isEqualTo("'cat','dog'");
    }

    @Test
    public void testQueryParametersCanGiveAListOfUnusedQueryParametersWhenOneParamIsUsed() throws Exception {

        Map<String,String[]> paramsToTest = new HashMap<String,String[]>();
        
        paramsToTest.put("animal",new String[] { "cat" });
        paramsToTest.put("colour",new String[] { "blue" });
        paramsToTest.put("size",new String[] { "big" });
        QueryParameters params = new QueryParameters(paramsToTest);

        // When...
        InternalServletException ex = catchThrowableOfType( ()->params.checkForUnsupportedQueryParameters(List.of("animal")) , InternalServletException.class);

        assertThat(ex).hasMessageContaining("'animal'","'colour','size'");
    }

}