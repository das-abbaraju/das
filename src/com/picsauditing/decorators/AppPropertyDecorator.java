package com.picsauditing.decorators;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

import java.util.Arrays;

abstract class AppPropertyDecorator extends AppProperty {
	public AppPropertyDAO appPropertyDAO = SpringUtils.getBean(SpringUtils.APP_PROPERTY_DAO);

	public boolean isInCSV(String appProperty, String needle) {
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
}
