package com.picsauditing.employeeguard.services.entity;

import java.util.Date;

public interface EntityService<ENTITY, ID> {

	/**
	 * Return the entity with that id, or null if it does not exist.
	 *
	 * @throws NullPointerException if the id is null
	 * @param id
	 * @return
	 */
	ENTITY find(ID id);

	/**
	 * Sets the createdBy and createdDate of the entity and returns the new persisted entity.
	 *
	 * @param entity
	 * @param createdBy
	 * @param createdDate
	 * @return
	 */

	ENTITY save(ENTITY entity, int createdBy, Date createdDate);

	/**
	 * Sets the updatedBy and updatedDate of the entity and returns the updated version of previously persisted
	 * entity.
	 *
	 * @param entity
	 * @param updatedBy
	 * @param updatedDate
	 * @return
	 */
	ENTITY update(ENTITY entity, int updatedBy, Date updatedDate);

	/**
	 * Deletes the entity provided.
	 *
	 * @throws NullPointerException if the entity is null.
	 * @param entity
	 */
	void delete(ENTITY entity);

	/**
	 * Deletes the entity with the given id.
	 *
	 * @throws NullPointerException if the id is null
	 * @param id
	 */
	void deleteById(ID id);

}
