package com.picsauditing.util;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;

import java.util.Arrays;

abstract class AppPropertyUtil extends AppProperty {
	private AppPropertyDAO appPropertyDAO;

	public void instantiateDAOs() {
		if (appPropertyDAO == null) {
			appPropertyDAO = SpringUtils.getBean(SpringUtils.APP_PROPERTY_DAO);
		}
	}

	public boolean isInCSV(String appProperty, String needle) {
		instantiateDAOs();
		boolean found = false;
		AppProperty appProperty1 = appPropertyDAO.find(appProperty);
		if (appProperty1 != null) {
			String value = appProperty1.getValue();
			if (!Strings.isEmpty(value)) {
				if (Arrays.asList(value.split(",")).contains(needle.trim())) {
					found = true;
				}
			}
		}
		return found;
	}

	public boolean isInCSV(String appProperty, int needle) {
		return isInCSV(appProperty,needle+"");
	}

	public AppPropertyDAO getAppPropertyDAO() {
		return appPropertyDAO;
	}

	public void setAppPropertyDAO(AppPropertyDAO appPropertyDAO) {
		this.appPropertyDAO = appPropertyDAO;
	}
}
