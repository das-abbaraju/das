package com.picsauditing.service;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.model.general.AppPropertyProvider;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AppPropertyService implements AppPropertyProvider {
    private static final String DEFAULT_STRING = "";
    private static final Logger logger = LoggerFactory.getLogger(AppPropertyService.class);

    @Autowired
    AppPropertyDAO appPropertyDao;

    public boolean isEnabled(String property, boolean defaultBoolean) {
        AppProperty appProperty = appPropertyDao.find(property);
        try {
            if (appProperty.getValue().equals("1")) {
                return true;
            }
            return Boolean.parseBoolean(appProperty.getValue());
        } catch (Exception e) {
            return defaultBoolean;
        }
    }

    public String getPropertyString(String property, String defaultString) {
        AppProperty appProperty = appPropertyDao.find(property);
        if (appProperty == null || appProperty.getValue() == null) {
            return defaultString;
        }

        return appProperty.getValue();
    }

    public String getPropertyString(String property) {
        return getPropertyString(property, DEFAULT_STRING);
    }

    public int getPropertyInt(String property, int defaultInteger) {
		return NumberUtils.toInt(getPropertyString(property), defaultInteger);
    }
}
