package com.picsauditing.dao;

import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.toggle.FeatureToggleProvider;

public class FeatureToggleDAO extends PicsDAO implements FeatureToggleProvider {

	public String findFeatureToggle(String toggleName) {
		if (!toggleName.startsWith("Toggle")) {
			toggleName = "Toggle." + toggleName;
		}
		AppProperty p = find(toggleName);
		if (p == null) {
			return null;
		}
		return p.getValue();
	}

	public AppProperty find(String property) {
		return em.find(AppProperty.class, property);
	}

}
