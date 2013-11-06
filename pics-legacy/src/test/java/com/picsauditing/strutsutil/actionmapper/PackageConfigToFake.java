package com.picsauditing.strutsutil.actionmapper;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.picsauditing.util.Strings;

import java.util.Map;

/**
 * Created inner class that extends the PackageConfig class in order to "fake" behaviors because
 * the class could not be mocked
 */
class PackageConfigToFake extends PackageConfig {

    public PackageConfigToFake() {
        super(Strings.EMPTY_STRING);
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setActionConfigs(Map<String, ActionConfig> actionConfigs) {
        this.actionConfigs = actionConfigs;
    }
}