package dev.galasa.framework.internal.rbac;

import java.util.Map;

import org.junit.*;

import dev.galasa.framework.spi.rbac.Action;
import dev.galasa.framework.spi.rbac.RbacService;
import dev.galasa.framework.spi.rbac.Role;

import static org.assertj.core.api.Assertions.*;

public class TestRbacServiceImpl {
    
    @Test
    public void testRolesMapByIdContainsAdminRole() {
        RbacService service = new RbacServiceImpl();
        Map<String,Role> roleMap = service.getRolesMapById();

        Role roleGot = roleMap.get("2");
        assertThat(roleGot).isNotNull();
        assertThat(roleGot.getName()).isEqualTo("admin");
        assertThat(roleGot.getDescription()).contains("Administrator access");

        assertThat(roleGot.getActionsMapById().size()).isEqualTo(3);
        assertThat(roleGot.getActionsMapById().get("0")).isNotNull();
        assertThat(roleGot.getActionsMapById().get("1")).isNotNull();
        assertThat(roleGot.getActionsMapById().get("2")).isNotNull();
    }

    @Test
    public void testRolesMapByIdContainsDefaultRole() {
        RbacService service = new RbacServiceImpl();
        Map<String,Role> roleMap = service.getRolesMapById();

        Role roleGot = roleMap.get("1");
        assertThat(roleGot).isNotNull();
        assertThat(roleGot.getName()).isEqualTo("default");

        assertThat(roleGot.getActionsMapById().size()).isEqualTo(2);
        assertThat(roleGot.getActionsMapById().get("0")).isNotNull();
        assertThat(roleGot.getActionsMapById().get("2")).isNotNull();
    }

    @Test
    public void testRolesMapByIdContainsDeactivateddRole() {
        RbacService service = new RbacServiceImpl();
        Map<String,Role> roleMap = service.getRolesMapById();

        Role roleGot = roleMap.get("0");
        assertThat(roleGot).isNotNull();
        assertThat(roleGot.getName()).isEqualTo("deactivated");

        assertThat(roleGot.getActionsMapById().size()).isEqualTo(0);
    }

    @Test 
    public void testActionsMapByIdContainsActionUserRoleUpdateAny() {
        RbacService service = new RbacServiceImpl();
        Map<String,Action> actionMap = service.getActionsMapById();

        Action action = actionMap.get("0");
        assertThat(action.getName()).isEqualTo("USER_ROLE_UPDATE_ANY");
    }


    @Test 
    public void testActionsMapByIdContainsActionSecretsGet() {
        RbacService service = new RbacServiceImpl();
        Map<String,Action> actionMap = service.getActionsMapById();

        Action action = actionMap.get("1");
        assertThat(action.getName()).isEqualTo("SECRETS_GET");
    }

    @Test 
    public void testActionsMapByIdContainsActionGeneralApiAccess() {
        RbacService service = new RbacServiceImpl();
        Map<String,Action> actionMap = service.getActionsMapById();

        Action action = actionMap.get("2");
        assertThat(action.getName()).isEqualTo("GENERAL_API_ACCESS");
    }

    @Test
    public void testActionsMapByNameContainsSecretsGet() {
        RbacService service = new RbacServiceImpl();
        Map<String,Action> actionMapByName = service.getActionsMapByName();

        Action action = actionMapByName.get("SECRETS_GET");

        assertThat(action.getName()).isEqualTo("SECRETS_GET");
    }

    @Test
    public void testServiceCanLookupAdminRoleById() {
        RbacService service = new RbacServiceImpl();
        Role roleGotBack = service.getRoleById("2");
        assertThat(roleGotBack.getName()).isEqualTo("admin");
    }

    @Test
    public void testServiceCanLookupGetSecretsActionById() {
        RbacService service = new RbacServiceImpl();
        Action actionGotBack = service.getActionById("1");
        assertThat(actionGotBack.getName()).isEqualTo("SECRETS_GET");
    }

    @Test
    public void testServiceCanLookupGetSecretsActionByName() {
        RbacService service = new RbacServiceImpl();
        Action actionGotBack = service.getActionByName("SECRETS_GET");
        assertThat(actionGotBack.getName()).isEqualTo("SECRETS_GET");
    }

    @Test
    public void testGetSecretsActionHasDescription() {
        RbacService service = new RbacServiceImpl();
        Action actionGotBack = service.getActionByName("SECRETS_GET");
        assertThat(actionGotBack.getDescription()).contains("Able to get secret values");
    }
}
