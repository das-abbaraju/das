package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.AuditType")
@Table(name = "audit_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditType extends BaseTable implements java.io.Serializable {

    public static final int PQF = 1;
    public static final int ANNUALADDENDUM = 11;

    protected AuditTypeClass classType = AuditTypeClass.Audit;
    protected boolean hasMultiple;
    protected Workflow workFlow;
    protected ScoreType scoreType;

    @Enumerated(EnumType.STRING)
    public AuditTypeClass getClassType() {
        return classType;
    }

    public void setClassType(AuditTypeClass classType) {
        this.classType = classType;
    }

    /**
     * More than one audit of this type can be active for a contractor at a time
     *
     * @return
     */
    public boolean isHasMultiple() {
        return hasMultiple;
    }

    public void setHasMultiple(boolean hasMultiple) {
        this.hasMultiple = hasMultiple;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowID")
    public Workflow getWorkFlow() {
        return workFlow;
    }

    public void setWorkFlow(Workflow workFlow) {
        this.workFlow = workFlow;
    }

    @Enumerated(EnumType.STRING)
    public ScoreType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }
}