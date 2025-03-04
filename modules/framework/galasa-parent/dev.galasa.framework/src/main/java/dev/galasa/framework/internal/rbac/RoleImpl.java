/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.internal.rbac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.galasa.framework.spi.rbac.Role;

public class RoleImpl implements Role {

    private String name ;
    private String id ;
    private String description;
    private List<String> actionIdsSorted;
    private boolean isAssignable;

    public RoleImpl( String name, String id , String description, List<String> actionIds, boolean isAssignable ) {
        this.name = name ;
        this.id = id ;
        this.isAssignable = isAssignable;

        // Take a copy of the action Ids and sort it.
        this.actionIdsSorted = new ArrayList<String>(actionIds);
        Collections.sort(this.actionIdsSorted, (a,b)-> a.compareTo(b) );

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
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<String> getActionIds() {
        return this.actionIdsSorted;
    }

	@Override
	public boolean isAssignable() {
		return this.isAssignable;
	}
    
}
