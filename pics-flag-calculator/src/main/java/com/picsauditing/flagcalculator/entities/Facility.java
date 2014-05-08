package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.Facility")
@Table(name = "facilities")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class Facility extends BaseTable {

    private OperatorAccount operator;
    private OperatorAccount corporate;

    @ManyToOne
    @JoinColumn(name = "opID", nullable = false, updatable = false)
    public OperatorAccount getOperator() {
        return operator;
    }

    public void setOperator(OperatorAccount operator) {
        this.operator = operator;
    }

    @ManyToOne
    @JoinColumn(name = "corporateID", nullable = false, updatable = false)
    public OperatorAccount getCorporate() {
        return corporate;
    }

    public void setCorporate(OperatorAccount corporate) {
        this.corporate = corporate;
    }
}