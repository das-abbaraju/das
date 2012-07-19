package com.picsauditing.model.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class AuditDataSaveEventListener implements ApplicationListener<AuditDataSaveEvent> {
	private final Logger logger = LoggerFactory.getLogger(AuditDataSaveEventListener.class);
	
	@Override
	public void onApplicationEvent(AuditDataSaveEvent event) {
		logger.debug("here");
	}

}
