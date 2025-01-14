/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.users.internal.routes;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static dev.galasa.framework.api.common.ServletErrorMessage.*;

import dev.galasa.framework.api.beans.generated.UserUpdateData;
import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ServletError;


public class UserUpdateRequestValidator {
    
    /**
     * The most characters in the role id string field which are allowable.
     * Beyond this length and the role id string field is deemed to be invalid.
     */
    public static final int MAX_ROLE_FIELD_LENGTH = 128;

    private Log logger = LogFactory.getLog(this.getClass());

    public void validateUpdateRequest(UserUpdateData userUpdateData) throws InternalServletException {
        String roleId = userUpdateData.getrole();
        if (roleId==null || roleId.trim().isBlank()) {
            // Role is blank, so the user isn't trying to update the role field in the user record. Ok.
        } else {

            boolean isRoleFieldOK = true ;

            if (roleId.length() > MAX_ROLE_FIELD_LENGTH) {
                logger.info("validateUpdateRequest() ; role field from user is too long.");
                isRoleFieldOK = false ;
            }

            // Loop through all the characters in the input validating each in turn.
            char[] characters = roleId.toCharArray();
            for(char c: characters) {

                if (!Character.isLetter(c)) {
                    if (!Character.isDigit(c)) {
                        if(c != '_') {
                            if( c != '-' ) {
                                isRoleFieldOK = false;
                                break;
                            }
                        }
                    }
                }

            }

            if (!isRoleFieldOK) {
                ServletError error = new ServletError(GAL5087_BAD_USER_UPDATE_FIELD_ROLE);
                throw new InternalServletException(error, HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
    
}