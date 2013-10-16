package com.picsauditing.database.domain;

import java.util.Date;

public interface UpdatableRows extends CreatableRows {

    public int getUpdatedById();

    public void setUpdatedById(int userID);

    public Date getUpdatedDate();

    public void setUpdatedDate(Date updatedDate);
}
