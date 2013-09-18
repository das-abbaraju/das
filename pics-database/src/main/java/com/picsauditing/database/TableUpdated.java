package com.picsauditing.database;

import java.util.Date;

public interface TableUpdated extends TableCreated {

    public int getUpdatedById();

    public void setUpdatedById(int userID);

    public Date getUpdatedDate();

    public void setUpdatedDate(Date updatedDate);
}
