package dev.galasa.framework.spi.rbac;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.galasa.framework.internal.rbac.RoleImpl;
import static dev.galasa.framework.spi.rbac.BuiltInAction.*;

public enum RBACRoles {

    RBAC_DEACTIVATED_ROLE(new RoleImpl("deactivated", "0", "User has no access", new ArrayList<>(), true)),
    RBAC_TESTER_ROLE(new RoleImpl("tester", "1", "Test developer and runner",
            List.of(USER_EDIT_OTHER.getAction().getId(), GENERAL_API_ACCESS.getAction().getId()), true)),
    RBAC_ADMIN_ROLE(new RoleImpl("admin", "2", "Administrator access", getAllActionIds(), true)),
    RBAC_OWNER_ROLE(new RoleImpl("owner", "3", "Galasa service owner", getAllActionIds(), false));

    private final Role role;

    RBACRoles(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return this.role;
    }

    private static class ActionIdsHolder {
        static final List<String> allActionIds = createActionIds();
    }

    private static List<String> createActionIds() {
        List<String> actionIds = new ArrayList<>();
        List<Action> allActionsUnsorted = BuiltInAction.getActions();

        List<Action> actionsSortedByName = new ArrayList<>(allActionsUnsorted);
        actionsSortedByName.sort(Comparator.comparing(Action::getName));

        for (Action action : actionsSortedByName) {
            actionIds.add(action.getId());
        }
        return actionIds;
    }

    private static List<String> getAllActionIds() {
        return ActionIdsHolder.allActionIds;
    }
}
