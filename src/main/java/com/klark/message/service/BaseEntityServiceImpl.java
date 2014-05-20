// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.message.service;

import org.hibernate.exception.ConstraintViolationException;

import com.klark.common.AbstractEntity;
import com.klark.common.AbstractEntityDTO;
import com.klark.exception.NotFoundException;
import com.klark.message.dao.DaoBase;
import com.klark.util.ModelReflectUtil;
import com.klark.util.ModelUtil;

/**
 * Base service for entity-driven service that can operate on DTOs.
 * 
 * 
 * @author michaelc
 */
@SuppressWarnings("unchecked")
public abstract class BaseEntityServiceImpl<M extends AbstractEntity, T extends AbstractEntityDTO<M>> extends BaseServiceImpl implements BaseEntityService<M, T> {

    public final T getById(final Long id) throws NotFoundException {
        T dto = getFromCache(id);
        if (dto == null) {
            log.trace("get by id: '{}'", id);
            if (id == null) {
                return null;
            }
            M entity = null;// getDao().getById(id);
            if (entity == null) {
                // use checked exception
                throw new NotFoundException(" not found for id: " + id);
            }
            dto = toDTO(entity, true);
            decorateDTO(dto);
            // putInCache(dto);
        }
        return dto;
    }

    private DaoBase getDao() {
        // TODO Auto-generated method stub
        return null;
    }

    private T getFromCache(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public final T update(T dto) throws ConstraintViolationException {
        return null;
    }

    public final T insert(T dto) throws ConstraintViolationException {
        return dto;
    }

    protected T toDTO(final M entity, final boolean cascade) {
        final T dto = (T) ModelReflectUtil.instantiateDTO(entity, getClass());
        ModelUtil.setDTOFieldsFromEntity(dto, entity, getDao(), cascade);
        dto.prepareForDisplay(entity);
        return dto;
    }

    protected M fromDTO(final T dto) {
        boolean isUpdate = dto.getId() != null;
        M entity = null;
        if (isUpdate) {
            entity = null;// TODO getDao().getById(dto.getId());
            if (entity == null) {
                throw new NotFoundException(getDao().getEntityClass().getSimpleName() + " not found for id: " + dto.getId());
            }
        } else {
            entity = (M) ModelReflectUtil.instantiateEntity(dto);
        }
        ModelUtil.setEntityFieldsFromDTO(entity, dto, getDao());
        return entity;
    }

    protected void decorateDTO(T dto) {
        // override to do any decoration needed
    }

    protected boolean isDeleteSupported() {
        // override to enable delete
        return false;
    }

    protected boolean isInsertSupported() {
        // override to enable insert
        return false;
    }

    protected boolean isUpdateSupported() {
        // override to disable update
        return true;
    }

}
