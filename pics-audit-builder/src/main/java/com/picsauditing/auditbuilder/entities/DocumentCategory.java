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
public class DocumentCategory extends BaseHistory {

	public static final int WORK_HISTORY = 6;
	public static final int FINANCIAL_HISTORY = 200;

	private AuditType auditType;
	private DocumentCategory parent;
    private int number;
    private int numRequired;
	private float scoreWeight = 0f;

	private List<DocumentCategory> subCategories = new ArrayList<>();
	private List<DocumentQuestion> questions = new ArrayList<>();

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
	public DocumentCategory getParent() {
		return parent;
	}

	public void setParent(DocumentCategory parent) {
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
	public List<DocumentCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<DocumentCategory> subCategories) {
		this.subCategories = subCategories;
	}

	@OneToMany(mappedBy = "category")
	@OrderBy("number")
	public List<DocumentQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<DocumentQuestion> questions) {
		this.questions = questions;
	}
}