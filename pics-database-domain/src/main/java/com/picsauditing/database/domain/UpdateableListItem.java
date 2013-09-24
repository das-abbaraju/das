package com.picsauditing.database.domain;

public interface UpdateableListItem extends TableWithID {
    public void update(UpdateableListItem item);
}
