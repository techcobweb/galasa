/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.zos.internal.properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.zos.ZosManagerException;

@RunWith(MockitoJUnitRunner.class)
public class TestImageVtamLogon {
    
    @Mock
    private static IConfigurationPropertyStoreService cps;
    private static ZosPropertiesSingleton zps = new ZosPropertiesSingleton();
    
    private static final String DEFAULT_VTAM_LOGON_STRING = "LOGON APPLID({0})";
    private static final String TAG_VTAM_LOGON_STRING = "TESTING({0})";
    private static final String TAG = "tag";
 
    @Before
    public void setup() throws ZosManagerException, ConfigurationPropertyStoreException {
        zps.activate();
        ZosPropertiesSingleton.setCps(cps);
        Mockito.when(cps.getProperty(Mockito.eq("image"), Mockito.eq("vtam.logon"),Mockito.any())).thenReturn(null);
        Mockito.when(cps.getProperty("image", "vtam.logon",TAG)).thenReturn(TAG_VTAM_LOGON_STRING);
        Mockito.when(cps.getProperty("image", "vtam.logon", "EXC")).thenThrow(new ConfigurationPropertyStoreException());
}

    @Test
    public void testNull() throws Exception {        
        Assert.assertEquals("Unexpected value returned from ImageVtamLogon.get()", DEFAULT_VTAM_LOGON_STRING, ImageVtamLogon.get(null));
    }
    
    @Test
    public void testUndefined() throws Exception {        
        Assert.assertEquals("Unexpected value returned from ImageVtamLogon.get()", DEFAULT_VTAM_LOGON_STRING, ImageVtamLogon.get("ANY"));
    }
    
    @Test
    public void testValid() throws Exception {
        Assert.assertEquals("Unexpected value returned from ImageVtamLogon.get()", TAG_VTAM_LOGON_STRING, ImageVtamLogon.get(TAG));
    }
    
    @Test
    public void testException() throws Exception {
        String expectedMessage = "Problem asking the CPS for the VTAM logon string for z/OS image with tag 'EXC'";
        ZosManagerException expectedException = Assert.assertThrows("expected exception should be thrown", ZosManagerException.class, ()->{
        	ImageVtamLogon.get("EXC");
        });
    	Assert.assertEquals("Exception should contain expected message", expectedMessage, expectedException.getMessage());
    }
}
