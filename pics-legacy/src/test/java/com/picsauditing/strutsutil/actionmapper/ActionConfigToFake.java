package com.picsauditing.strutsutil.actionmapper;

import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * Created inner class that extends the PackageConfig class in order to "fake" behaviors because
 * the class could not be mocked
 */
class ActionConfigToFake extends ActionConfig {

    public ActionConfigToFake(String packageName, String name, String className) {
        super(packageName, name, className);
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
