package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.BaseEntity;

public class BaseEntityDAO extends AbstractBaseEntityDAO<BaseEntity> {

    public BaseEntityDAO() {
        this.type = BaseEntity.class;
    }
}
