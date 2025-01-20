/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.users.internal.routes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import dev.galasa.framework.api.common.InternalServletException;
import dev.galasa.framework.api.common.ServletError;

import static dev.galasa.framework.api.common.ServletErrorMessage.*;

public class UsersUrlParameterExtractor {

    protected Pattern pathPattern;

    public UsersUrlParameterExtractor( Pattern pathPattern ) {
        this.pathPattern = pathPattern;
    }
    
    public String getUserNumber(String urlPathInfo) throws InternalServletException{

        try {

            Matcher matcher = pathPattern.matcher(urlPathInfo);
            matcher.matches();

            String userNumber = matcher.group(1);
            return userNumber;

        } catch(Exception ex){
            ServletError error = new ServletError(GAL5085_FAILED_TO_GET_LOGIN_ID_FROM_URL);
            throw new InternalServletException(error, HttpServletResponse.SC_NOT_FOUND, ex);
        }

    }
}
