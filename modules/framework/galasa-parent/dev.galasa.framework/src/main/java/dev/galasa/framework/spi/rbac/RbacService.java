package dev.galasa.framework.spi.rbac;

import java.util.Map;

public interface RbacService {

    /**
     * Gets all the roles available.
     * @return A map. The index is the role ID. The value is the role object.
     */
    Map<String,Role> getRolesMapById();

    /**
     * Gets all the actions available.
     * @return A map. The index is the action ID. The value is the action object.
     */
    Map<String,Action> getActionsMapById();

    /**
     * Gets all the actions available.
     * @return A map. The index is the action name. The value is the action object.
     */
    Map<String,Action> getActionsMapByName();

    Role getRoleById( String id );

    Action getActionById( String id );

    Action getActionByName( String name );

}
