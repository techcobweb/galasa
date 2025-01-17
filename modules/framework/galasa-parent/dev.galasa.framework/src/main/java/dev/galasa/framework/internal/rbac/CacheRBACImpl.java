/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.internal.rbac;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.galasa.framework.spi.rbac.CacheRBAC;
import dev.galasa.framework.spi.rbac.RBACException;

public class CacheRBACImpl implements CacheRBAC {

    Map<String, List<String>> usersToActionsMap = new HashMap<>();

    public CacheRBACImpl() {}

    @Override
    public void addUser(String loginId, List<String> actionIds) throws RBACException {
        usersToActionsMap.put(loginId, actionIds);
    }

    @Override
    public boolean isActionPermitted(String loginId, String actionId) throws RBACException {
        boolean isActionPermitted = false;
        List<String> userActionIds = usersToActionsMap.get(loginId);
        if (userActionIds != null) {
            // Check if the user is allowed to perform the given action
            isActionPermitted = userActionIds.contains(actionId);
        }
        return isActionPermitted;
    }

    @Override
    public void invalidateUser(String loginId) throws RBACException {
        usersToActionsMap.remove(loginId);
    }
}
