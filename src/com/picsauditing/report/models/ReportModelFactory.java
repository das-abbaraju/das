package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class ReportModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(ReportModelFactory.class);

    public static AbstractModel build(ModelType type, Permissions permissions) {
        try {
            String className = "com.picsauditing.report.models." + type.toString() + "Model";
            return getModelObjectFromClassName(className, permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.warn("WARNING: ReportModelFactory failed to define Model for type = {}", type);

        return null;
    }

    private static AbstractModel getModelObjectFromClassName(String className, Permissions permissions) throws Exception {
        Class myClass = Class.forName(className);
        Class[] types = {permissions.getClass()};
        Constructor constructor = myClass.getConstructor(types);
        Object[] parameters = {permissions};
        return (AbstractModel) constructor.newInstance(parameters);
    }
}