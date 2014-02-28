package com.picsauditing.employeeguard.entities.helper;

import com.picsauditing.employeeguard.entities.BaseEntity;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class EntityHelper {

    public static <E extends BaseEntity> E setCreateAuditFields(final E entity,
																final int createdBy,
																final Date createdDate) {
        entity.setCreatedBy(createdBy);
        entity.setCreatedDate(createdDate);
		return entity;
    }

    public static <E extends BaseEntity> Collection<E> setCreateAuditFields(final Collection<E> entities,
																			final int createdBy,
																			final Date createdDate) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        for (BaseEntity entity : entities) {
            setCreateAuditFields(entity, createdBy, createdDate);
        }

		return entities;
    }

    public static <E extends BaseEntity> E setUpdateAuditFields(final E entity,
																   final int createdBy,
																   final Date createdDate) {
        entity.setUpdatedBy(createdBy);
        entity.setUpdatedDate(createdDate);
		return entity;
    }

    public static <E extends BaseEntity> Collection<E> setUpdateAuditFields(final Collection<E> entities,
																   final int createdBy,
																   final Date createdDate) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        for (BaseEntity entity : entities) {
            setUpdateAuditFields(entity, createdBy, createdDate);
        }

		return entities;
    }

    public static <E extends BaseEntity> E softDelete(final E entity, final int deletedBy) {
        entity.setDeletedBy(deletedBy);
		return entity;
    }

    public static <E extends BaseEntity> void softDelete(final Collection<E> entities, final int deletedBy) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        for (BaseEntity entity : entities) {
            softDelete(entity, deletedBy);
        }
    }

    public static <E extends BaseEntity> List<Integer> getIdsForEntities(final List<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        List<Integer> ids = new ArrayList<>();
        for (E entity : entities) {
            ids.add(entity.getId());
        }

        return ids;
    }

    public static <E extends BaseEntity> Set<Integer> getUniqueIdsForEntities(final List<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptySet();
        }

        Set<Integer> ids = new HashSet<>();
        for (E entity : entities) {
            ids.add(entity.getId());
        }

        return ids;
    }

	public static <E extends BaseEntity> E setCreateAuditFields(final E entity, final EntityAuditInfo entityAuditInfo) {
		entity.setCreatedBy(entityAuditInfo.getAppUserId());
		entity.setCreatedDate(entityAuditInfo.getTimestamp());
		return entity;
	}

	public static <E extends BaseEntity> E setUpdateAuditFields(final E entity, final EntityAuditInfo entityAuditInfo) {
		entity.setUpdatedBy(entityAuditInfo.getAppUserId());
		entity.setUpdatedDate(entityAuditInfo.getTimestamp());
		return entity;
	}

	public static <E extends BaseEntity> Collection<E> setCreateAuditFields(final Collection<E> entities,
																			final EntityAuditInfo entityAuditInfo) {
		if (CollectionUtils.isEmpty(entities)) {
			return entities;
		}

		for (E entity : entities) {
			setCreateAuditFields(entity, entityAuditInfo);
		}

		return entities;
	}

	public static <E extends BaseEntity> Collection<E> setUpdateAuditFields(final Collection<E> entities,
																			final EntityAuditInfo entityAuditInfo) {
		if (CollectionUtils.isEmpty(entities)) {
			return entities;
		}

		for (E entity : entities) {
			setUpdateAuditFields(entity, entityAuditInfo);
		}

		return entities;
	}
}
