/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.internal.rbac;

import java.util.List;

import org.junit.*;

import dev.galasa.framework.spi.rbac.CacheRBAC;

import static org.assertj.core.api.Assertions.*;

public class TestCacheRBACImpl {

    @Test
    public void testIsActionPermittedReturnsTrueForValidMappings() throws Exception {
        // Given...
        CacheRBAC cache = new CacheRBACImpl();
        String loginId = "bob";

        String apiAccessActionId = "GENERAL_API_ACCESS";
        String secretsGetActionId = "SECRETS_GET";
        List<String> actions = List.of(apiAccessActionId, secretsGetActionId);

        cache.addUser(loginId, actions);

        // When...
        boolean isApiAccessPermitted = cache.isActionPermitted(loginId, apiAccessActionId);
        boolean isSecretsAccessPermitted = cache.isActionPermitted(loginId, secretsGetActionId);

        // Then...
        assertThat(isApiAccessPermitted).isTrue();
        assertThat(isSecretsAccessPermitted).isTrue();
    }

    @Test
    public void testIsActionPermittedReturnsFalseForInvalidMappings() throws Exception {
        // Given...
        CacheRBAC cache = new CacheRBACImpl();
        String loginId = "bob";

        String apiAccessActionId = "GENERAL_API_ACCESS";
        String secretsGetActionId = "SECRETS_GET";
        List<String> actions = List.of(apiAccessActionId, secretsGetActionId);

        cache.addUser(loginId, actions);

        // Then...
        assertThat(cache.isActionPermitted(loginId, "not_a_permitted_action")).isFalse();
    }

    @Test
    public void testIsActionPermittedReturnsFalseForUnknownUsers() throws Exception {
        // Given...
        CacheRBAC cache = new CacheRBACImpl();
        String loginId = "unknown";
        String apiAccessActionId = "GENERAL_API_ACCESS";

        // Then...
        assertThat(cache.isActionPermitted(loginId, apiAccessActionId)).isFalse();
    }

    @Test
    public void testInvalidateRemovesUserFromCache() throws Exception {
        // Given...
        CacheRBAC cache = new CacheRBACImpl();
        String loginId = "bob";

        String apiAccessActionId = "GENERAL_API_ACCESS";
        String secretsGetActionId = "SECRETS_GET";
        List<String> actions = List.of(apiAccessActionId, secretsGetActionId);

        cache.addUser(loginId, actions);
        assertThat(cache.isActionPermitted(loginId, apiAccessActionId)).isTrue();

        // When...
        cache.invalidateUser(loginId);

        // Then...
        assertThat(cache.isActionPermitted(loginId, apiAccessActionId)).isFalse();
    }
}
