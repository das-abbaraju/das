package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.services.models.EntityAuditInfo;

import java.util.Date;
import java.util.List;

public interface TestEntityService<ENTITY, ID> {

	/**
	 * Return the entity with that id, or null if it does not exist.
	 *
	 * @throws NullPointerException if the id is null
	 * @param id
	 * @return
	 */
	ENTITY find(ID id);

	/**
	 * Return a list of entities that match that search term, for that account.
	 *
	 * If the searchTerm is null or an Empty String, then an Empty List will be returned.
	 *
	 * @param searchTerm
	 * @param accountId
	 * @return
	 */
	List<ENTITY> search(String searchTerm, int accountId);

	/**
	 * Sets the createdBy and createdDate of the entity and returns the new persisted entity.
	 *
	 * @param entity
	 * @param entityAuditInfo
	 * @return
	 */
	ENTITY save(ENTITY entity, EntityAuditInfo entityAuditInfo);

	/**
	 * Sets the updatedBy and updatedDate of the entity and returns the updated version of previously persisted
	 * entity.
	 *
	 * @param entity
	 * @param entityAuditInfo
	 * @return
	 */
	ENTITY update(ENTITY entity, EntityAuditInfo entityAuditInfo);

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
