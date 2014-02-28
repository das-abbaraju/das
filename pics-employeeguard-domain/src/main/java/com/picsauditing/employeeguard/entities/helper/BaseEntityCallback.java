package com.picsauditing.employeeguard.entities.helper;

import com.picsauditing.employeeguard.entities.BaseEntity;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BaseEntityCallback<E extends BaseEntity> implements IntersectionAndComplementProcess.EventCallbacks<E> {

	private final int user;
	private final Date timestamp;
	private final ArrayList<E> addedEntities;
	private final ArrayList<E> removedEntities;

	public BaseEntityCallback(int appUserId, Date timestamp) {
		this.user = appUserId;
		this.timestamp = timestamp;
		this.addedEntities = new ArrayList<>();
		this.removedEntities = new ArrayList<>();
	}

	@Override
	public E handleNewEntity(E entity) {
		EntityHelper.setCreateAuditFields(entity, user, timestamp);
		addedEntities.add(entity);
		return entity;
	}

	@Override
	public E handleDuplicate(E entity) {
		EntityHelper.setUpdateAuditFields(entity, user, timestamp);
		return entity;
	}

	@Override
	public void handleRemoval(E entity) {
		EntityHelper.softDelete(entity, user);
		entity.setDeletedDate(timestamp);
		removedEntities.add(entity);
	}

	public List<E> getAddedEntities() {
		return Collections.unmodifiableList(new ArrayList<>(addedEntities));
	}

	public List<E> getRemovedEntities() {
		return Collections.unmodifiableList(new ArrayList<>(removedEntities));
	}
}