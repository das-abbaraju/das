package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.FlagCriteriaOperator")
@Table(name = "flag_criteria_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaOperator extends BaseTable {
    private OperatorAccount operator;
    private FlagCriteria criteria;
    private FlagColor flag = FlagColor.Red;
    private String hurdle;
    private OperatorTag tag;

    @ManyToOne
    @JoinColumn(name = "opID", nullable = false)
    public OperatorAccount getOperator() {
        return operator;
    }

    public void setOperator(OperatorAccount operator) {
        this.operator = operator;
    }

    @ManyToOne
    @JoinColumn(name = "criteriaID", nullable = false)
    public FlagCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(FlagCriteria criteria) {
        this.criteria = criteria;
    }

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    public FlagColor getFlag() {
        return flag;
    }

    public void setFlag(FlagColor flag) {
        this.flag = flag;
    }

    public String getHurdle() {
        return hurdle;
    }

    public void setHurdle(String hurdle) {
        this.hurdle = hurdle;
    }

    @ManyToOne
    @JoinColumn(name = "tagID")
    public OperatorTag getTag() {
        return tag;
    }

    public void setTag(OperatorTag tag) {
        this.tag = tag;
    }
}