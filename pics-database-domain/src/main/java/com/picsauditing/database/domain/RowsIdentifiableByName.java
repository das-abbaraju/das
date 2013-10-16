package com.picsauditing.database.domain;

public interface RowsIdentifiableByName extends RowsIdentifiableByKey {

    public String getName();

    public void setName(String name);
}
