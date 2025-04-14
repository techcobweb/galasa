/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.resources.processors;

import static dev.galasa.framework.api.common.resources.ResourceAction.DELETE;

import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import com.google.gson.JsonObject;

import dev.galasa.framework.api.beans.generated.Stream;
import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.RBACValidator;
import dev.galasa.framework.api.common.resources.ResourceAction;
import dev.galasa.framework.api.common.resources.StreamBean;
import dev.galasa.framework.api.resources.validators.GalasaStreamValidator;
import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.framework.spi.rbac.BuiltInAction;

public class GalasaStreamProcessor extends AbstractGalasaResourceProcessor implements IGalasaResourceProcessor {

    private IConfigurationPropertyStoreService cps;
    private final Log logger = LogFactory.getLog(getClass());

    public GalasaStreamProcessor(IConfigurationPropertyStoreService cps, RBACValidator rbacValidator) {
        super(rbacValidator);
        this.cps = cps;
    }

    @Override
    public List<String> processResource(JsonObject resourceJson, ResourceAction action, String username)
            throws InternalServletException {

        logger.info("Processing GalasaStream resource");
        List<String> errors = checkGalasaStreamJsonStructure(resourceJson, action);
        
        if(errors.isEmpty()) {

            Stream galasaStream = gson.fromJson(resourceJson, Stream.class);
            StreamBean stream = new StreamBean(cps, galasaStream.getmetadata().getname());

            if (action == DELETE) {
                logger.info("Deleting stream from CPS store");
                stream.deleteStreamFromPropertyStore();
                logger.info("Deleted stream from CPS store OK");
            }

            logger.info("Processed GalasaStream resource OK");
        }

        return errors;
    }

    private List<String> checkGalasaStreamJsonStructure(JsonObject streamJson, ResourceAction action) throws InternalServletException {
        GalasaStreamValidator validator = new GalasaStreamValidator(action);
        return checkGalasaResourceJsonStructure(validator, streamJson);
    }

    @Override
    public void validateActionPermissions(ResourceAction action, String username) throws InternalServletException {
        BuiltInAction requestedAction = getResourceActionAsBuiltInAction(action, BuiltInAction.CPS_PROPERTIES_SET, BuiltInAction.CPS_PROPERTIES_DELETE);
        rbacValidator.validateActionPermitted(requestedAction, username);
    }
    
}
