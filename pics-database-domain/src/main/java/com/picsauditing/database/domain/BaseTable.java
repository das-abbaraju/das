package com.picsauditing.database.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import static javax.persistence.GenerationType.IDENTITY;

@MappedSuperclass
public abstract class BaseTable implements RowsIdentifiableByKey {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(nullable = false)
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
    }
}
