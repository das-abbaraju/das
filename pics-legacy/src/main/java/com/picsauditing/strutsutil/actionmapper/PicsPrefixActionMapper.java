package com.picsauditing.strutsutil.actionmapper;

import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.PrefixBasedActionMapper;

public final class PicsPrefixActionMapper extends PrefixBasedActionMapper {

    public static final String DEFAULT_NAMESPACE_IF_NAMESPACE_IS_NULL = "/";

    @Override
    public String getUriFromActionMapping(ActionMapping mapping) {
        String namespace = getNamespace(mapping);
        for (int lastIndex = namespace.length(); lastIndex > (-1); lastIndex = namespace.lastIndexOf('/', lastIndex-1)) {
            ActionMapper actionMapper = actionMappers.get(namespace.substring(0,lastIndex));
            if (actionMapper != null) {
                String uri = actionMapper.getUriFromActionMapping(mapping);
                if (log.isDebugEnabled()) {
                    log.debug("Using ActionMapper "+ actionMapper);
                }
                if (uri != null) {
                    return uri;
                } else if (log.isDebugEnabled()) {
                    log.debug("ActionMapper " + actionMapper + " failed to return an ActionMapping (null)");
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("ActionMapper failed to return a uri");
        }
        return null;
    }

    private String getNamespace(ActionMapping mapping) {
        String namespace = mapping.getNamespace();
        if (namespace == null) {
            return DEFAULT_NAMESPACE_IF_NAMESPACE_IS_NULL;
        }

        return namespace;
    }
}
