package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
//@MappedSuperclass
public abstract class BaseTable implements /*JSONable,*/ Serializable/*, Autocompleteable, JSONAware, Translatable, RowsIdentifiableByKey*/ {

    protected int id;
    protected User createdBy;
    protected User updatedBy;
    protected Date creationDate;
    protected Date updateDate;
//
//    public BaseTable() {
//    }
//
//    public BaseTable(User user) {
//        setAuditColumns(user);
//    }
//
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    @Column(nullable = false)
//    @IndexableField(type = IndexValueType.STRINGTYPE, weight = 10)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    @Transient
//    public String getI18nKey() {
//        return getClass().getSimpleName() + "." + id;
//    }
//
//    /**
//     * Example AuditType.1.name or AuditQuestion.123.requirement
//     *
//     * @param property
//     * @return
//     */
//    @Transient
//    public String getI18nKey(String property) {
//        return getI18nKey() + "." + property;
//    }
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "createdBy", nullable = true)
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "updatedBy", nullable = true)
    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

//    @Transient
//    public User getUpdatedBy2() {
//        if (getUpdatedBy() == null) {
//            return getCreatedBy();
//        }
//        return getUpdatedBy();
//    }
//
//    @Transient
//    public Date getUpdateDate2() {
//        if (updateDate == null) {
//            return creationDate;
//        }
//        return updateDate;
//    }
//
//    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

//    @Temporal(TemporalType.TIMESTAMP)
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

//    public void setAuditColumns(Permissions permissions) {
//        if (permissions == null) {
//            setAuditColumns();
//            return;
//        }
//        int userID = permissions.getUserId();
//        if (permissions.getAdminID() > 0) {
//            userID = permissions.getAdminID();
//        }
//        setAuditColumns(new User(userID));
//    }
//
//    @Transient
//    public JSONObject toJSON() {
//        return toJSON(false);
//    }
//
//    @Transient
//    @SuppressWarnings("unchecked")
//    public JSONObject toJSON(boolean full) {
//        JSONObject obj = new JSONObject();
//        obj.put("id", id);
//        if (full) {
//            obj.put("createdBy", createdBy == null ? null : createdBy.toJSON());
//            obj.put("updatedBy", updatedBy == null ? null : updatedBy.toJSON());
//            obj.put("creationDate", creationDate == null ? null : creationDate.getTime());
//            obj.put("updateDate", updateDate == null ? null : updateDate.getTime());
//        }
//        return obj;
//    }
//
//    public String toJSONString() {
//        return toJSON(true).toJSONString();
//    }
//
//    public void fromJSON(JSONObject obj) {
//    }
//
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (id == 0) {
//            return false;
//        }
//
//        try {
//            BaseTable other = (BaseTable) obj;
//            if (other.getId() == 0) {
//                return false;
//            }
//
//            return id == other.getId();
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    public int hashCode() {
//        if (id == 0) {
//            return super.hashCode();
//        } else {
//            return ((getClass().getName().hashCode() % 1000) * 10000000) + id;
//        }
//    }
//
//    @Transient
//    public String getAutocompleteResult() {
//        return "" + id;
//    }
//
//    @Transient
//    public String getAutocompleteItem() {
//        return getAutocompleteResult();
//    }
//
//    @Transient
//    public String getAutocompleteValue() {
//        return getAutocompleteItem();
//    }
//
}
