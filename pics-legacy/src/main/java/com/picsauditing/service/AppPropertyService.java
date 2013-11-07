package com.picsauditing.service;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AppPropertyService {

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

    public int getPropertyInt(String property, int defaultInteger) {
        AppProperty appProperty = appPropertyDao.find(property);
        try {
            return Integer.valueOf(appProperty.getValue());
        } catch (Exception e) {
            return defaultInteger;
        }
    }
}
