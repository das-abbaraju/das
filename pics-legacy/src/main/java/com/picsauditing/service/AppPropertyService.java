package com.picsauditing.service;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AppPropertyService {

	private static final Logger logger = LoggerFactory.getLogger(AppPropertyService.class);

	@Autowired
	private AppPropertyDAO appPropertyDao;

	public boolean emailSubscriptionsAreEnabled() {
		AppProperty enableSubscriptions = appPropertyDao.find("subscription.enable");
		return Boolean.parseBoolean(enableSubscriptions.getValue());
	}
}
