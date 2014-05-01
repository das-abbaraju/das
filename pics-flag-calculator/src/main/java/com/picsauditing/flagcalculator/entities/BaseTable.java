package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class BaseTable implements Serializable {

    protected int id;
    protected User createdBy;
    protected User updatedBy;
    protected Date creationDate;
    protected Date updateDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", nullable = true)
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updatedBy", nullable = true)
    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setAuditColumns() {
        updateDate = new Date();

        if (createdBy == null) {
            createdBy = updatedBy;
        }
        if (creationDate == null) {
            creationDate = updateDate;
        }
    }

    public void setAuditColumns(User user) {
        if (user != null) {
            updatedBy = user;
        }

        setAuditColumns();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (id == 0) {
            return false;
        }

        try {
            BaseTable other = (BaseTable) obj;
            if (other.getId() == 0) {
                return false;
            }

            return id == other.getId();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (id == 0) {
            return super.hashCode();
        } else {
            return ((getClass().getName().hashCode() % 1000) * 10000000) + id;
        }
    }

}