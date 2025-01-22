/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.common;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class TestSupportedQueryParameterNames {
    
    @Test
    public void testQueryParametersCanCreateASortedQuotedList() throws Exception {

        String renderedList = new SupportedQueryParameterNames("cat","dog").toString();
        assertThat(renderedList).isEqualTo("'cat','dog'");

        renderedList = new SupportedQueryParameterNames("dog","cat").toString();
        assertThat(renderedList).isEqualTo("'cat','dog'");
    }

    @Test
    public void testQueryParametersAreConvertedToLowerCase() throws Exception {
        String renderedList = new SupportedQueryParameterNames("CAT","dOg").toString();
        assertThat(renderedList).isEqualTo("'cat','dog'");
    }

    @Test
    public void testQueryParametersAreTrimmed() throws Exception {
        String renderedList = new SupportedQueryParameterNames("CAT  ","  dOg").toString();
        assertThat(renderedList).isEqualTo("'cat','dog'");
    }

}
