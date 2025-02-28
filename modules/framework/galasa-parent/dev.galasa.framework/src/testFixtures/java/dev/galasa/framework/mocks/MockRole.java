/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.mocks;

import java.util.List;
import dev.galasa.framework.spi.rbac.Role;

public class MockRole implements Role{

    private String name ;
    private String id ;
    private String description;
    private List<String> actionIds;
    private boolean isAssignable;

    public MockRole( String name, String id , String description, List<String> actionIds, boolean isAssignable) {
        this(name,id,description);
        this.actionIds = actionIds;
        this.isAssignable = isAssignable;
    }

    private MockRole( String name, String id , String description) {
        this.name = name ;
        this.id = id ;
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
    public List<String> getActionIds() {
        return this.actionIds;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

	@Override
	public boolean isAssignable() {
		return this.isAssignable;
	}
    
}
