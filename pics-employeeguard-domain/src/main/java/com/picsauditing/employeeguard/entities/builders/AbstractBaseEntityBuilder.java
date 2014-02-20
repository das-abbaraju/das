package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.BaseEntity;

import java.util.Date;

public abstract class AbstractBaseEntityBuilder<E extends BaseEntity, T extends AbstractBaseEntityBuilder<E, T>> {

	protected E entity;
	protected T that;

	public T id(int id) {
		entity.setId(id);
		return that;
	}

	public T createdBy(int createdBy) {
		entity.setCreatedBy(createdBy);
		return that;
	}

	public T updatedBy(int updatedBy) {
		entity.setUpdatedBy(updatedBy);
		return that;
	}

	public T deletedBy(int deletedBy) {
		entity.setDeletedBy(deletedBy);
		return that;
	}

	public T createdDate(Date createdDate) {
		entity.setCreatedDate(createdDate);
		return that;
	}

	public T updatedDate(Date updatedDate) {
		entity.setUpdatedDate(updatedDate);
		return that;
	}

	public T deletedDate(Date deletedDate) {
		entity.setDeletedDate(deletedDate);
		return that;
	}

	public abstract E build();
}
