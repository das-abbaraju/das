package com.picsauditing.dao.jdbc;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.dao.mapper.AppPropertyRowMapper;
import com.picsauditing.dao.mapper.GenericQueryMapper;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggleProvider;

public class JdbcFeatureToggleProvider implements FeatureToggleProvider {

	private static final String FIND_TOGGLE_BY_NAME = "SELECT * FROM app_properties WHERE property = ?";

	private static final Logger logger = LoggerFactory.getLogger(JdbcFeatureToggleProvider.class);

	@Override
	public String findFeatureToggle(String toggleName) {
		if (!toggleName.startsWith("Toggle")) {
			toggleName = "Toggle." + toggleName;
		}

		try {
			List<AppProperty> appProperties = Database.select(FIND_TOGGLE_BY_NAME, toggleName,
					new GenericQueryMapper<String>(), new AppPropertyRowMapper());
            if (appProperties != null && appProperties.size() > 0) {
			    return appProperties.get(0).getValue();
            }
		} catch (Exception e) {
			logger.error("Error finding toggle {}: {}", toggleName, e);
		}
        logger.error("Error finding toggle {}", toggleName);
		return null;
	}

}
