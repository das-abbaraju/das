package com.picsauditing.database.domain;

public interface UpdatableListItem extends RowsIdentifiableByKey {
    public void update(UpdatableListItem item);
}
