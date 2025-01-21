/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.spi.rbac;

import java.util.List;

public interface CacheRBAC {

    void addUser(String loginId, List<String> actionIds) throws RBACException;

    boolean isActionPermitted(String loginId, String actionId) throws RBACException;

    void invalidateUser(String loginId) throws RBACException;
}
