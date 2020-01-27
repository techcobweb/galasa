/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2019.
 */
package dev.galasa.zosmf.internal.properties;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.zosmf.ZosmfManagerException;

@Component(service=ZosmfPropertiesSingleton.class, immediate=true)
public class ZosmfPropertiesSingleton {
    
    private static ZosmfPropertiesSingleton singletonInstance;
    private static void setInstance(ZosmfPropertiesSingleton instance) {
        singletonInstance = instance;
    }
    
    private IConfigurationPropertyStoreService cps;
    
    @Activate
    public void activate() {
        setInstance(this);
    }
    
    @Deactivate
    public void deacivate() {
        setInstance(null);
    }
    
    public static IConfigurationPropertyStoreService cps() throws ZosmfManagerException {
        if (singletonInstance != null) {
            return singletonInstance.cps;
        }
        
        throw new ZosmfManagerException("Attempt to access manager CPS before it has been initialised");
    }
    
    public static void setCps(IConfigurationPropertyStoreService cps) throws ZosmfManagerException {
        if (singletonInstance != null) {
            singletonInstance.cps = cps;
            return;
        }
        
        throw new ZosmfManagerException("Attempt to set manager CPS before instance created");
    }
}
