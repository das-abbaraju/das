package com.picsauditing.database;

public interface UpdateableListItem extends TableWithID {
    public void update(UpdateableListItem item);
}
