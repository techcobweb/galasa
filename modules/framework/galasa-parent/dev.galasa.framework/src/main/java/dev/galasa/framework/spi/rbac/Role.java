package dev.galasa.framework.spi.rbac;

import java.util.Map;

public interface Role {
    
    String getName();

    String getId();

    Map<String,Action> getActionsMapById();

    String getDescription();

}
