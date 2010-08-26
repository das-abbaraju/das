package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditCategory extends BaseTable implements java.io.Serializable, Comparable<AuditCategory> {

	public static final int WORK_HISTORY = 6;
	public static final int OSHA_AUDIT = 151;
	public static final int GENERAL_INFORMATION = 155;
	public static final int EMR = 152;
	public static final int MSHA = 157;
	public static final int CANADIAN_STATISTICS = 158;
	public static final int LOSS_RUN = 159;
	public static final int TRAINING = 25;
	public static final int SAFETY_POLICIES = 7;
	public static final int SERVICES_PERFORMED = 28;
	public static final int WCB = 210;
	public static final int CITATIONS = 278;

	private AuditType auditType;
	private AuditCategory parent;
	private String name;
	private int number;
	private int numRequired;
	private int numQuestions;
	private String helpText;
	private boolean pageBreak = false;

	private List<AuditCategory> subCategories = new ArrayList<AuditCategory>();
	private List<AuditQuestion> questions = new ArrayList<AuditQuestion>();

	public AuditCategory() {

	}

	public AuditCategory(AuditCategory a, AuditType at) {
		this.number = a.getNumber();
		this.numQuestions = a.getNumQuestions();
		this.numRequired = a.getNumRequired();
		this.helpText = a.getHelpText();
		this.auditType = at;
	}

	public AuditCategory(AuditCategory a) {
		this.auditType = a.auditType;
		this.parent = a.parent;
		this.name = a.name;
		this.number = a.getNumber();
		this.numRequired = a.numRequired;
		this.numQuestions = a.numQuestions;
		this.helpText = a.getHelpText();
		this.pageBreak = a.pageBreak;
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

	@Transient
	public AuditType getParentAuditType() {
		if (auditType == null)
			return parent.getParentAuditType();

		return auditType;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(nullable = false)
	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Transient
	public String getFullNumber() {
		if (parent == null)
			return "" + number;

		return parent.getFullNumber() + "." + number;
	}
	
	@Transient
	public String getFullyQualifiedName(){
		if(parent == null)
			return getAuditType().getAuditName()+" - "+name;
		
		return parent.getFullyQualifiedName() +" : "+name;
	}
	

	@Column(nullable = false)
	public int getNumRequired() {
		return this.numRequired;
	}

	public void setNumRequired(int numRequired) {
		this.numRequired = numRequired;
	}

	@Column(nullable = false)
	public int getNumQuestions() {
		return this.numQuestions;
	}

	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}

	@Transient
	public void recalculateQuestions() {
		numQuestions = 0;
		numRequired = 0;
		for (AuditQuestion question : questions) {
			if (question.isCurrent()) {
				numQuestions++;
				if (question.isRequired())
					numRequired++;
			}
		}
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	@Column(nullable = false)
	public boolean isPageBreak() {
		return pageBreak;
	}

	public void setPageBreak(boolean pageBreak) {
		this.pageBreak = pageBreak;
	}

	@OneToMany(mappedBy = "parent")
	@OrderBy("number")
	public List<AuditCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<AuditCategory> subCategories) {
		this.subCategories = subCategories;
	}

	@Transient
	public List<AuditCategory> getSiblings() {
		if (auditType != null)
			return auditType.getCategories();
		else
			return parent.getSubCategories();
	}

	@OneToMany(mappedBy = "category")
	@OrderBy("number")
	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}

	@Transient
	public boolean isSha() {
		if (id == OSHA_AUDIT)
			return true;
		if (id == MSHA)
			return true;
		if (id == CANADIAN_STATISTICS)
			return true;
		return false;
	}

	@Transient
	public List<AuditCategory> getAncestors() {
		List<AuditCategory> ancestors = new ArrayList<AuditCategory>();
		addAncestors(ancestors, this);

		return ancestors;
	}

	@Transient
	private void addAncestors(List<AuditCategory> ancestors, AuditCategory category) {
		if (category.getParent() != null)
			addAncestors(ancestors, category.getParent());

		ancestors.add(category);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject j = super.toJSON(full);
		j.put("auditType", auditType == null ? null : auditType.toJSON());
		j.put("parent", parent == null ? null : parent.toJSON());
		j.put("name", name);

		if (full) {
			JSONArray qArray = new JSONArray();
			for (AuditQuestion q : questions) {
				qArray.add(q.toJSON());
			}
			j.put("questions", qArray);

			JSONArray subArray = new JSONArray();
			for (AuditCategory sub : subCategories) {
				subArray.add(sub.toJSON());
			}

			j.put("subCategories", subArray);
		}

		return j;
	}

	@Override
	public int compareTo(AuditCategory other) {
		if (other == null) {
			return 1;
		}

		int cmp = getAuditType().compareTo(other.getAuditType());

		if (cmp != 0)
			return cmp;

		return new Integer(getNumber()).compareTo(new Integer(other.getNumber()));
	}

	@Override
	public String toString() {
		return getNumber() + " " + name;
	}
}
