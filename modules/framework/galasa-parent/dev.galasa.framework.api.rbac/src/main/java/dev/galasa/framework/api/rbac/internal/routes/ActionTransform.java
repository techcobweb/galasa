package dev.galasa.framework.api.rbac.internal.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.galasa.framework.api.beans.generated.RBACAction;
import dev.galasa.framework.api.beans.generated.RBACActionMetadata;
import dev.galasa.framework.api.common.resources.GalasaResourceValidator;
import dev.galasa.framework.spi.rbac.Action;

/**
 * Transforms which convert the internal Action to the external ActionBean
 */
public class ActionTransform {

    public ActionTransform() {
    }

    public RBACAction createActionBean(Action action, String url) {
        RBACAction actionBean = new RBACAction();

        actionBean.setApiVersion(GalasaResourceValidator.DEFAULT_API_VERSION);
        actionBean.setkind("GalasaAction");

        RBACActionMetadata metadata = createActionMetadataBean(action, url);
        actionBean.setmetadata(metadata);
        return actionBean;
    }

    private RBACActionMetadata createActionMetadataBean(Action action, String url) {
        RBACActionMetadata metadata = new RBACActionMetadata();
        metadata.setdescription(action.getDescription());
        metadata.setid(action.getId());
        metadata.setname(action.getName());
        metadata.seturl(url);
        return metadata;
    }

    public List<RBACActionMetadata> creteaActionsSummary(Collection<Action> actions, String baseUrl ) {
        List<RBACActionMetadata> actionBeans = new ArrayList<RBACActionMetadata>();
        for( Action action : actions ) {
            String url = calculateActionUrl(baseUrl, action.getId());

            RBACActionMetadata actionBean = createActionMetadataBean(action, url);
            actionBeans.add(actionBean);
        }
        return actionBeans;
    }

    /**
     * Given a base URL and an action ID, construct the URL a user would have to use to address a single action resource.
     * Handling trailing slashes and all.
     * @param baseUrl
     * @param actionId
     * @return The URL a user would have to use to address a single action resource.
     */
    private String calculateActionUrl(String baseUrl, String actionId) {
        String url;
        if (baseUrl.endsWith("/")) {
            url = baseUrl+actionId;
        } else {
            url = baseUrl+"/"+actionId;
        }
        return url;
    }
    
}
