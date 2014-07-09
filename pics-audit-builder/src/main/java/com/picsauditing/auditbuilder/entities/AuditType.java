package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditType extends BaseTable implements java.io.Serializable {

	public static final int PQF = 1;
	public static final int MANUAL_AUDIT = 2;
	public static final int IMPLEMENTATION_AUDIT = 3;
	public static final int FIELD = 5;
	public static final int WELCOME = 9;
	public static final int ANNUALADDENDUM = 11;
	public static final int INTEGRITYMANAGEMENT = 17;
	public static final int COR = 72;
	public static final int SHELL_COMPETENCY_REVIEW = 100;
	public static final int IMPORT_PQF = 232;
	public static final int IEC_AUDIT = 313;
	public static final int SSIP = 514;

	protected AuditTypeClass classType = AuditTypeClass.Audit;
	protected boolean hasMultiple;
	protected boolean canOperatorView;
	protected boolean renewable = true;
	protected Integer monthsToExpire;
	protected Workflow workFlow;
	protected ScoreType scoreType;
    protected AuditTypePeriod period = AuditTypePeriod.None;
    protected int anchorDay = 1;
    protected int anchorMonth = 1;
    protected int advanceDays = 0;
    protected int maximumActive = 1;

	protected List<AuditCategory> categories = new ArrayList<>();

	public static final Set<Integer> CANADIAN_PROVINCES = new HashSet<>(Arrays.asList(new Integer[]{145, 146,
        143, 170, 261, 168, 148, 147, 169, 166, 167, 144}));

    @Enumerated(EnumType.STRING)
	public AuditTypeClass getClassType() {
		return classType;
	}

	public void setClassType(AuditTypeClass classType) {
		this.classType = classType;
	}

	public boolean isHasMultiple() {
		return hasMultiple;
	}

	public void setHasMultiple(boolean hasMultiple) {
		this.hasMultiple = hasMultiple;
	}

	public boolean isCanOperatorView() {
		return canOperatorView;
	}

	public void setCanOperatorView(boolean canOperatorView) {
		this.canOperatorView = canOperatorView;
	}

	public Integer getMonthsToExpire() {
		return monthsToExpire;
	}

	public void setMonthsToExpire(Integer monthsToExpire) {
		this.monthsToExpire = monthsToExpire;
	}

	public boolean isRenewable() {
		return renewable;
	}

	public void setRenewable(boolean renewable) {
		this.renewable = renewable;
	}

	@OneToMany(mappedBy = "auditType")
	@OrderBy("number")
	public List<AuditCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<AuditCategory> categories) {
		this.categories = categories;
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

    public int getAdvanceDays() {
        return advanceDays;
    }

    public void setAdvanceDays(int advanceDays) {
        this.advanceDays = advanceDays;
    }

    public int getAnchorMonth() {
        return anchorMonth;
    }

    public void setAnchorMonth(int anchorMonth) {
        this.anchorMonth = anchorMonth;
    }

    public int getAnchorDay() {
        return anchorDay;
    }

    public void setAnchorDay(int anchorDay) {
        this.anchorDay = anchorDay;
    }

    @Enumerated(EnumType.STRING)
    public AuditTypePeriod getPeriod() {
        return period;
    }

    public void setPeriod(AuditTypePeriod period) {
        this.period = period;
    }

    public int getMaximumActive() {
        return maximumActive;
    }

    public void setMaximumActive(int maximumActive) {
        this.maximumActive = maximumActive;
    }
}