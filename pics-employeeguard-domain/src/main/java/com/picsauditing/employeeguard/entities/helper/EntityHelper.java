package com.picsauditing.employeeguard.entities.helper;

import com.picsauditing.employeeguard.entities.BaseEntity;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class EntityHelper {

    public static <E extends BaseEntity> void setCreateAuditFields(E entity, int createdBy, Date createdDate) {
        entity.setCreatedBy(createdBy);
        entity.setCreatedDate(createdDate);
    }

    public static <E extends BaseEntity> void setCreateAuditFields(Collection<E> entities, int createdBy, Date createdDate) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        for (BaseEntity entity : entities) {
            setCreateAuditFields(entity, createdBy, createdDate);
        }
    }

    public static <E extends BaseEntity> void setUpdateAuditFields(E entity, int createdBy, Date createdDate) {
        entity.setUpdatedBy(createdBy);
        entity.setUpdatedDate(createdDate);
    }

    public static <E extends BaseEntity> void setUpdateAuditFields(Collection<E> entities, int createdBy, Date createdDate) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        for (BaseEntity entity : entities) {
            setUpdateAuditFields(entity, createdBy, createdDate);
        }
    }

    public static <E extends BaseEntity> void softDelete(E entity, int deletedBy, Date deletedDate) {
        entity.setDeletedBy(deletedBy);
        entity.setDeletedDate(deletedDate);
    }

    public static <E extends BaseEntity> void softDelete(Collection<E> entities, int deletedBy, Date deletedDate) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        for (BaseEntity entity : entities) {
            softDelete(entity, deletedBy, deletedDate);
        }
    }

    public static <E extends BaseEntity> List<Integer> getIdsForEntities(List<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        List<Integer> ids = new ArrayList<>();
        for (E entity : entities) {
            ids.add(entity.getId());
        }

        return ids;
    }
}
