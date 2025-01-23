/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.resources.processors;

import static dev.galasa.framework.api.common.ServletErrorMessage.*;
import static dev.galasa.framework.api.common.resources.ResourceAction.*;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import dev.galasa.framework.api.beans.GalasaProperty;
import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ServletError;
import dev.galasa.framework.api.common.resources.CPSFacade;
import dev.galasa.framework.api.common.resources.CPSNamespace;
import dev.galasa.framework.api.common.resources.CPSProperty;
import dev.galasa.framework.api.common.resources.ResourceAction;
import dev.galasa.framework.api.resources.validators.GalasaPropertyValidator;
import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.rbac.Action;
import dev.galasa.framework.spi.rbac.BuiltInAction;
import dev.galasa.framework.spi.rbac.RBACException;
import dev.galasa.framework.spi.rbac.RBACService;

public class GalasaPropertyProcessor extends AbstractGalasaResourceProcessor implements IGalasaResourceProcessor {

    private CPSFacade cps;

    public GalasaPropertyProcessor(CPSFacade cps, RBACService rbacService) {
        super(rbacService);
        this.cps = cps;
    }

    @Override
    public List<String> processResource(JsonObject resource, ResourceAction action, String username) throws InternalServletException {
        List<String> errors = checkGalasaPropertyJsonStructure(resource, action);
        try {
            if (errors.isEmpty()) {
                GalasaProperty galasaProperty = gson.fromJson(resource, GalasaProperty.class);           
                CPSNamespace namespace = cps.getNamespace(galasaProperty.getNamespace());

                //getPropertyFromStore() will only return null if the property is in a hidden namespace
                CPSProperty property = namespace.getPropertyFromStore(galasaProperty.getName());

                if (action == DELETE) {
                    property.deletePropertyFromStore();
                } else {
                    /*
                    * The logic below is used to determine if the exclusive Not Or condition in property.setPropertyToStore 
                    * (i.e. "the property exists" must equal to "is this an update action") will action the request or error
                    *
                    * Logic Table to Determine actions
                    * If the action is equal to "update" (force update) the updateProperty is set to true (update property,
                    * will error if the property does not exist in CPS)
                    * If the action is either "update" or "apply" and the property exists in CPS the updateProperty is set to true (update property)
                    * If the action is equal to "apply" and the property does not exist in CPS the updateProperty is set to false (create property)
                    * If the action is equal to "create" (force create) the updateProperty is set to false (create property, will error if the property exists in CPS)
                    */
                    boolean updateProperty = false;
                    if ((updateActions.contains(action) && property.existsInStore()) || action == UPDATE) {
                        updateProperty = true;
                    }
                    property.setPropertyToStore(galasaProperty, updateProperty);
                }
            }
        } catch (ConfigurationPropertyStoreException e){
            ServletError error = new ServletError(GAL5000_GENERIC_API_ERROR, e.getMessage());
            throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
        return errors;
    }

    private List<String> checkGalasaPropertyJsonStructure(JsonObject propertyJson, ResourceAction action) throws InternalServletException {
        GalasaPropertyValidator validator = new GalasaPropertyValidator(action);
        return checkGalasaResourceJsonStructure(validator, propertyJson);
    }

    @Override
    public void validateActionPermissions(ResourceAction action, String loginId) throws InternalServletException {
        try {
            // TODO: This code is identical to the validateActionPermitted in the ProtectedRoute class - needs to be refactored
            Action requestedAction = null;
            switch (action) {
                case APPLY:
                case CREATE:
                case UPDATE:
                    requestedAction = BuiltInAction.CPS_PROPERTIES_SET.getAction();
                    break;
                case DELETE:
                    requestedAction = BuiltInAction.CPS_PROPERTIES_DELETE.getAction();
                    break;
            }
   
            if (!rbacService.isActionPermitted(loginId, requestedAction.getId())) {
                ServletError error = new ServletError(GAL5125_ACTION_NOT_PERMITTED, requestedAction.getId());
                throw new InternalServletException(error, HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (RBACException e) {
            ServletError error = new ServletError(GAL5126_INTERNAL_RBAC_ERROR);
            throw new InternalServletException(error, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
