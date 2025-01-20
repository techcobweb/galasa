/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.users.internal.routes;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import dev.galasa.framework.api.beans.generated.UserUpdateData;
import dev.galasa.framework.api.common.BaseServletTest;
import dev.galasa.framework.api.common.InternalServletException;

public class UserUpdateRequestValidatorTest extends BaseServletTest {

    @Test
    public void testCanCreateTheValidatorOk() {
        new UserUpdateRequestValidator();
    }

    @Test
    public void testUserUpdateWithNullRoleResultsInNoErrors() throws Exception {
        UserUpdateData dataToValidate = new UserUpdateData();
        dataToValidate.setrole(null);
        UserUpdateRequestValidator validator = new UserUpdateRequestValidator();
        validator.validateUpdateRequest(dataToValidate);
    }

    @Test
    public void testUserUpdateWithBlankRoleResultsInNoErrors() throws Exception {
        UserUpdateData dataToValidate = new UserUpdateData();
        dataToValidate.setrole("");
        UserUpdateRequestValidator validator = new UserUpdateRequestValidator();
        validator.validateUpdateRequest(dataToValidate);
    }

    @Test
    public void testUserUpdateWithSpacesRoleResultsInNoErrors() throws Exception {
        UserUpdateData dataToValidate = new UserUpdateData();
        dataToValidate.setrole("    ");
        UserUpdateRequestValidator validator = new UserUpdateRequestValidator();
        validator.validateUpdateRequest(dataToValidate);
    }

    @Test
    public void testUserUpdateWithWeirdCharactgerRoleResultsInErrors() throws Exception {
        UserUpdateData dataToValidate = new UserUpdateData();
        dataToValidate.setrole("%&3£");
        UserUpdateRequestValidator validator = new UserUpdateRequestValidator();
        InternalServletException ex = catchThrowableOfType( ()-> { validator.validateUpdateRequest(dataToValidate); },
            InternalServletException.class );
        assertThat(ex.getMessage()).contains("GAL5087");
    }

    @Test
    public void testUserUpdateWithSpaceInMiddleCharactgerRoleResultsInErrors() throws Exception {
        UserUpdateData dataToValidate = new UserUpdateData();
        dataToValidate.setrole("my id");
        UserUpdateRequestValidator validator = new UserUpdateRequestValidator();
        InternalServletException ex = catchThrowableOfType( ()-> { validator.validateUpdateRequest(dataToValidate); },
            InternalServletException.class );
        assertThat(ex.getMessage()).contains("GAL5087");
    }

    @Test
    public void testUserUpdateWithNoSpaceInMiddleIsOk() throws Exception {
        UserUpdateData dataToValidate = new UserUpdateData();
        dataToValidate.setrole("myid");
        UserUpdateRequestValidator validator = new UserUpdateRequestValidator();
        validator.validateUpdateRequest(dataToValidate);
    }

    @Test
    public void testUserUpdateWithDashSeparatorInMiddleIsOk() throws Exception {
        UserUpdateData dataToValidate = new UserUpdateData();
        dataToValidate.setrole("my-id");
        UserUpdateRequestValidator validator = new UserUpdateRequestValidator();
        validator.validateUpdateRequest(dataToValidate);
    }

    @Test
    public void testUserUpdateWithUnderscoreSeparatorInMiddleIsOk() throws Exception {
        UserUpdateData dataToValidate = new UserUpdateData();
        dataToValidate.setrole("my-id");
        UserUpdateRequestValidator validator = new UserUpdateRequestValidator();
        validator.validateUpdateRequest(dataToValidate);
    }
}
