package com.picsauditing.strutsutil.actionmapper.delegate;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.picsauditing.util.Strings;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

/**
 * Rather than being dependent on inherited classes from the struts framework, inverting the dependency on a
 * delegate class.
 */
public class ActionMapperDelegate {

    /**
     * From the Struts RestActionMapper
     *
     * @param uri
     * @param mapping
     * @param configManager
     */
    public void parseNameAndNamespace(String uri, ActionMapping mapping, ConfigurationManager configManager) {
        String namespace, name;
        int lastSlash = uri.lastIndexOf("/");
        if (lastSlash == -1) {
            namespace = Strings.EMPTY_STRING;
            name = uri;
        } else if (lastSlash == 0) {
            namespace = "/";
            name = uri.substring(lastSlash + 1);
        } else {
            // Try to find the namespace in those defined, defaulting to ""
            Configuration config = configManager.getConfiguration();
            String prefix = uri.substring(0, lastSlash);
            namespace = Strings.EMPTY_STRING;

            // Find the longest matching namespace, defaulting to the default
            for (Object o : config.getPackageConfigs().values()) {
                String ns = ((PackageConfig) o).getNamespace();
                if (ns != null && prefix.startsWith(ns) && (prefix.length() == ns.length() || prefix.charAt(ns.length()) == '/')) {
                    if (ns.length() > namespace.length()) {
                        namespace = ns;
                    }
                }
            }

            name = uri.substring(namespace.length() + 1);
        }

        mapping.setNamespace(namespace);
        mapping.setName(name);
    }

}
