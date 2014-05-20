// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.message.dao;

import javax.persistence.EntityManager;

import com.klark.common.AbstractEntity;

/**
 * 
 * 
 * @param <T>
 */
public interface DaoBase<T extends AbstractEntity> {

    /**
     * 
     * 
     * @return entity class
     */
    Class<T> getEntityClass();

    /**
     * 
     * 
     * @param entityManager
     */
    void setEntityManager(EntityManager entityManager);

    /**
     * 
     * 
     * @return entity manager
     */
    EntityManager getEntityManager();

    /**
     * 
     * 
     * @param entity
     */
    void create(T entity);

    /**
     * 
     * 
     * @param entity
     * @return attached entity
     */
    T read(T entity);

    /**
     * 
     * 
     * @param entity
     * @return updated entity
     */
    T update(T entity);

    /**
     * 
     * 
     * @param entity
     */
    void delete(T entity);

    /**
     * 
     * 
     * @param id
     * @return entity
     */
    T getById(Long id);

    /**
     * 
     * 
     * @param id
     */
    void deleteById(Long id);

    /**
     * 
     * @return total count
     */
    Long getTotalCount();

    /**
     * Hydrate an entity of clazz by id
     * 
     * @param clazz
     * @param id
     * @return entity or null
     */
    @SuppressWarnings({ "rawtypes" })
    AbstractEntity hydrate(Class clazz, Long id);

}