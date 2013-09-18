package com.picsauditing.database;

import java.util.Date;

public interface TableDeleted {
    public int getDeletedById();

    public void setDeletedById(int userID);

    public Date getDeletedDate();

    public void setDeletedDate(Date deletedDate);
}
