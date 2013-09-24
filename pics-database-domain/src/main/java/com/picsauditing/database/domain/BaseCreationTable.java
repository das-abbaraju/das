package com.picsauditing.database.domain;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class BaseCreationTable extends BaseTable {
    protected int createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date creationDate;

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
