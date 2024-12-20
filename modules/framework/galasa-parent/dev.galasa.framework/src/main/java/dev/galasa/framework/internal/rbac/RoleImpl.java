package dev.galasa.framework.internal.rbac;

import java.util.Map;

import dev.galasa.framework.spi.rbac.Action;
import dev.galasa.framework.spi.rbac.Role;

public class RoleImpl implements Role {

    private String name ;
    private String id ;
    private String description;
    private Map<String,Action> actions;

    public RoleImpl( String name, String id , String description, Map<String,Action> actions) {
        this.name = name ;
        this.id = id ;
        this.actions = actions;
        this.description = description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Map<String,Action> getActionsMapById() {
        return this.actions;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
    
}
