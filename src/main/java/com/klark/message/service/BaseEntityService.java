// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.message.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.klark.common.AbstractEntity;
import com.klark.common.AbstractEntityDTO;
import com.klark.exception.NotFoundException;

/**
 * Interface for base entity service.
 * 
 * 
 * @author michaelc
 */
public interface BaseEntityService<M extends AbstractEntity, T extends AbstractEntityDTO<M>> extends BaseService {

    @Transactional
    T getById(Long id) throws NotFoundException;

    @Transactional
    T insert(T dto) throws ConstraintViolationException;

    @Transactional
    T update(T dto) throws ConstraintViolationException;

    @Transactional
    void deleteById(Long id) throws NotFoundException;

    void clearDtoCache();

}
