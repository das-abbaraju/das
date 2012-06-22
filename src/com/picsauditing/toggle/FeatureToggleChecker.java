package com.picsauditing.toggle;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppPropertyValueParseException;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.messaging.PublisherRabbitMq;

public class FeatureToggleChecker {
	@Autowired
	private AppPropertyDAO appPropertyDAO;

	private final Logger logger = LoggerFactory.getLogger(PublisherRabbitMq.class);

	/**
	 * If App Property does not exist or cannot be parsed, logs error and
	 * returns false. Feature App Property value must be true or false currently
	 * for feature checking.
	 * 
	 * @param appPropertyFeatureName
	 * @return
	 */
	public boolean isFeatureEnabled(String appPropertyFeatureName) {
		try {
			AppProperty appPropertyFeature = appPropertyDAO.find(appPropertyFeatureName);

			if (appPropertyFeature == null)
				throw new EntityNotFoundException("AppProperty " + appPropertyFeatureName + " not found");

			return appPropertyFeature.valueEquals(true);
		} catch (EntityNotFoundException appPropNotExists) {
			logger.error(appPropNotExists.getMessage(), appPropNotExists);
		} catch (AppPropertyValueParseException appPropParseException) {
			logger.error(appPropParseException.getMessage(), appPropParseException);
		}

		return false;
	}

}
