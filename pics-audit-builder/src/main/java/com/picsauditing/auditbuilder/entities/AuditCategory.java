package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditCategory")
@Table(name = "audit_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "audit_cache")
public class AuditCategory extends BaseHistory {

	public static final int WORK_HISTORY = 6;
	public static final int FINANCIAL_HISTORY = 200;

	private AuditType auditType;
	private AuditCategory parent;
    private int number;
    private int numRequired;
	private float scoreWeight = 0f;

	private List<AuditCategory> subCategories = new ArrayList<>();
	private List<AuditQuestion> questions = new ArrayList<>();

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

    @Column(nullable = false)
    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
	@Column(nullable = false)
	public int getNumRequired() {
		return numRequired;
	}

	public void setNumRequired(int numRequired) {
		this.numRequired = numRequired;
	}

	public float getScoreWeight() {
		return scoreWeight;
	}

	public void setScoreWeight(float scoreWeight) {
		this.scoreWeight = scoreWeight;
	}

	@OneToMany(mappedBy = "parent")
	@OrderBy("number")
	public List<AuditCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<AuditCategory> subCategories) {
		this.subCategories = subCategories;
	}

	@OneToMany(mappedBy = "category")
	@OrderBy("number")
	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}
}