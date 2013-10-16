package com.picsauditing.database.domain;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractUser extends BaseTable implements RowsIdentifiableByName {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
