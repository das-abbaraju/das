package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.AuditCategory")
@Table(name = "audit_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "audit_cache")
public class AuditCategory extends BaseTable {

    private AuditType auditType;
    private AuditCategory parent;

    public AuditCategory() {

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditTypeID")
    public AuditType getAuditType() {
        return this.auditType;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

    @ManyToOne
    @JoinColumn(name = "parentID")
    public AuditCategory getParent() {
        return parent;
    }

    public void setParent(AuditCategory parent) {
        this.parent = parent;
    }
}