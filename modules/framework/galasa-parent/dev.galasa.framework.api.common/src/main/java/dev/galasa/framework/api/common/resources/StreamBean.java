/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.common.resources;

import javax.servlet.http.HttpServletResponse;

import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import static dev.galasa.framework.api.common.ServletErrorMessage.*;

public class StreamBean {
   
    private String streamName;
    private IConfigurationPropertyStoreService cpsService;

    public StreamBean(IConfigurationPropertyStoreService cpsService, String streamName) {
        this.streamName = streamName;
        this.cpsService = cpsService;
    }

    public void deleteStreamFromPropertyStore() throws InternalServletException {

        try {

            String fullStreamPrefix = streamName + ".";
            cpsService.deletePrefixedProperties(fullStreamPrefix);

        } catch (ConfigurationPropertyStoreException e) {
            ServletError error = new ServletError(GAL5426_FAILED_TO_DELETE_STREAM);
            throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
    
}
