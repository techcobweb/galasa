package dev.galasa.framework.internal.rbac;

import java.util.HashMap;
import java.util.Map;

import dev.galasa.framework.spi.rbac.Action;
import dev.galasa.framework.spi.rbac.RbacService;
import dev.galasa.framework.spi.rbac.Role;

public class RbacServiceImpl implements RbacService {

    private static final Action actionUserRoleUpdateAny = new ActionImpl("0","USER_ROLE_UPDATE_ANY", "Able to update the role of any user");
    private static final Action actionSecretsGet = new ActionImpl("1","SECRETS_GET", "Able to get secret values" );
    private static final Action actionGeneralApiAccess = new ActionImpl("2","GENERAL_API_ACCESS", "Able to access the REST API" );

    private static final Map<String,Action> adminActionsMapById = new HashMap<String,Action>();
    {
        adminActionsMapById.put( actionUserRoleUpdateAny.getId(), actionUserRoleUpdateAny );
        adminActionsMapById.put( actionSecretsGet.getId(), actionSecretsGet );
        adminActionsMapById.put( actionGeneralApiAccess.getId(), actionGeneralApiAccess );
    }

    private static final Role roleAdmin = new RoleImpl("admin","2","Administrator access",adminActionsMapById);

    private static final Map<String,Action> defaultActionsMapById = new HashMap<String,Action>();
    {
        defaultActionsMapById.put( actionUserRoleUpdateAny.getId(), actionUserRoleUpdateAny );
        defaultActionsMapById.put( actionGeneralApiAccess.getId(), actionGeneralApiAccess );
    }

    private static final Role roleDefault = new RoleImpl("default", "1", "Test developer and runner", defaultActionsMapById);


    private static final Map<String,Action> deactivatedActionsMayById = new HashMap<String,Action>();
    private static final Role roleDeactivated = new RoleImpl("deactivated", "0", "User has no access", deactivatedActionsMayById);


    private static final Map<String,Role> rolesMapById = new HashMap<String,Role>();
    {
        rolesMapById.put( roleAdmin.getId(), roleAdmin);
        rolesMapById.put( roleDefault.getId(), roleDefault);
        rolesMapById.put( roleDeactivated.getId(), roleDeactivated);
    }

    private static final Map<String,Action> actionsMapByName = new HashMap<String,Action>();
    {
        for( Action action: adminActionsMapById.values() ) {
            actionsMapByName.put( action.getName(), action);
        }
    }

    @Override
    public Map<String,Role> getRolesMapById() {
        return rolesMapById;
    }

    @Override
    public Map<String,Action> getActionsMapById() {
        return adminActionsMapById;
    }

    @Override
    public Role getRoleById(String id) {
        return getRolesMapById().get(id);
    }

    @Override
    public Action getActionById(String id) {
        return getActionsMapById().get(id);
    }

    @Override
    public Action getActionByName(String name) {
        return getActionsMapByName().get(name);
    }

    @Override
    public Map<String, Action> getActionsMapByName() {
        return actionsMapByName;
    }
    
}
