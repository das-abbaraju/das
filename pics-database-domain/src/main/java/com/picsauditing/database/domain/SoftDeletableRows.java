package com.picsauditing.database.domain;

import java.util.Date;

public interface SoftDeletableRows {
    public int getDeletedById();

    public void setDeletedById(int userID);

    public Date getDeletedDate();

    public void setDeletedDate(Date deletedDate);
}
