package com.picsauditing.dao.jdbc;

import com.picsauditing.dao.mapper.AppPropertyRowMapper;
import com.picsauditing.dao.mapper.GenericQueryMapper;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.search.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JdbcAppPropertyProvider implements AppPropertyProvider {
    private static final Logger logger = LoggerFactory.getLogger(JdbcAppPropertyProvider.class);
    private static final String FIND_APP_PROPERTY_BY_NAME = "SELECT * FROM app_properties WHERE property = ?";

    public JdbcAppPropertyProvider() {
    }

    public String findAppProperty(String appPropertyName) {
        try {
            List<AppProperty> appProperties
                    = Database.select(FIND_APP_PROPERTY_BY_NAME,
                                      appPropertyName,
                                      new GenericQueryMapper<String>(),
                                      new AppPropertyRowMapper());
            if (appProperties != null && appProperties.size() > 0) {
                return appProperties.get(0).getValue();
            }
        } catch (Exception e) {
            logger.error("Error finding appProperty {}: {}", appPropertyName, e);
        }
        return null;
    }
}