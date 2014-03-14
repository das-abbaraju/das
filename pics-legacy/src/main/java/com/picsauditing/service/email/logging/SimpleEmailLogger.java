package com.picsauditing.service.email.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEmailLogger implements EmailLog {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void error(String s, Object[] objects) {
        logger.error(s, objects);
    }
}
