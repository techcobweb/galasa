/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.zos.internal.properties;

import javax.validation.constraints.NotNull;

import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.cps.CpsProperties;
import dev.galasa.zos.ZosManagerException;

/**
 * The VTAM logon string
 * 
 * @galasa.cps.property
 * 
 * @galasa.name zos.image.[tag].vtam.logon
 * 
 * @galasa.description The VTAM logon string for the specified tag
 * 
 * @galasa.required No
 * 
 * @galasa.default LOGON APPLID({0})
 * 
 * @galasa.valid_values 
 * 
 * @galasa.examples 
 * <code>zos.image.[tag].vtam.logon=LOGON APPLID {0}</code><br>
 *
 */
public class ImageVtamLogon extends CpsProperties {
    
    private static final String DEFAULT_VTAM_LOGON_STRING = "LOGON APPLID({0})";

    public static String get(@NotNull String tag) throws ZosManagerException {
        try {
            String vtamLogon = getStringNulled(ZosPropertiesSingleton.cps(), "image", "vtam.logon", tag);
            if (vtamLogon == null)  {
                return DEFAULT_VTAM_LOGON_STRING;
            }
            return vtamLogon;
        } catch (ConfigurationPropertyStoreException e) {
            throw new ZosManagerException("Problem asking the CPS for the VTAM logon string for z/OS image with tag '"  + tag + "'", e);
        }
    }

}
