// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.message.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

/**
 * Base service impl.
 */
public abstract class BaseServiceImpl implements BaseService {

    /**
     * Service logger.
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected void fire(final ApplicationEvent event) {
    }

}
