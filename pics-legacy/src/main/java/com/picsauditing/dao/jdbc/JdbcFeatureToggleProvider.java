package com.picsauditing.dao.jdbc;

import com.picsauditing.toggle.FeatureToggleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcFeatureToggleProvider extends JdbcAppPropertyProvider implements FeatureToggleProvider {

    private static final Logger logger = LoggerFactory.getLogger(JdbcFeatureToggleProvider.class);

    @Override
	public String findFeatureToggle(String toggleName) {
		if (!toggleName.startsWith("Toggle")) {
			toggleName = "Toggle." + toggleName;
		}
        String toggle = getPropertyString(toggleName);
        if (toggle == null) {
            logger.error("Error finding toggle {}", toggleName);
            return null;
        } else {
            return toggle;
        }
	}

}
