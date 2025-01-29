/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.common;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class TestServletErrorMessage {

    @Test
    public void TestCanGetAMessageOutOfTheList() throws Exception {
        // Given...
        ServletErrorMessage msg = ServletErrorMessage.GAL5002_INVALID_RUN_ID;
    }

    @Test
    public void TestAllErrorMessageNumbersAreUnique() throws Exception {

        Map<Integer,ServletErrorMessage> messagesLookedAtSoFar = new HashMap<>();

        StringBuilder buff = new StringBuilder();

        for( ServletErrorMessage msg : EnumSet.allOf(ServletErrorMessage.class)) {

            int msgNumber = msg.getTemplateNumber();
            ServletErrorMessage clashingErrorMessage = messagesLookedAtSoFar.get(msgNumber);
            if( clashingErrorMessage != null) {
                buff.append("Error message number clashes: Template number "+msgNumber+" is used more than once!\n");
                buff.append("  Template1: "+msg.toString()+"\n");
                buff.append("  Template2: "+clashingErrorMessage.toString()+"\n");
            } else {
                messagesLookedAtSoFar.put(msgNumber,msg);
            }
        }

        String errorMsg = buff.toString();
        assertThat(errorMsg).isBlank();
    }

}