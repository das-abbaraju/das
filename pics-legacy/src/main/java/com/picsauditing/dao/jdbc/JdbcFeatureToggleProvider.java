package com.picsauditing.dao.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.toggle.FeatureToggleProvider;

public class JdbcFeatureToggleProvider extends JdbcAppPropertyProvider implements FeatureToggleProvider {

    private static final Logger logger = LoggerFactory.getLogger(JdbcFeatureToggleProvider.class);

    @Override
	public String findFeatureToggle(String toggleName) {
		if (!toggleName.startsWith("Toggle")) {
			toggleName = "Toggle." + toggleName;
		}
        String toggle = findAppProperty(toggleName);
        if (toggle == null) {
            logger.error("Error finding toggle {}", toggleName);
            return null;
        } else {
            return toggle;
        }
	}

}
