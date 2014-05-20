// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.message.service;

import com.klark.exception.NotFoundException;

/**
 * Base service interface
 */
public interface BaseService {

    <T> T getById(Long id) throws NotFoundException;

}