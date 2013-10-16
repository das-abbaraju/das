package com.picsauditing.database.domain;

import java.util.Date;

public interface CreatableRows {

    public int getCreatedById();

    public void setCreatedById(int userID);

    public Date getCreatedDate();

    public void setCreatedDate(Date createdDate);
}
