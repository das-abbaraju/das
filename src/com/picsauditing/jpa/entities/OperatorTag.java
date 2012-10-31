package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "operator_tag")
public class OperatorTag extends BaseTable {
	public static final int SHELL_COMPETENCY_REVIEW = 142;
	public static final String HSE_COMPETENCY = "HSE Competency";

	private OperatorAccount operator;
	private String tag;
	private boolean active = true;
	private boolean visibleToContractor = false;
	private boolean inheritable = true;

	private List<AuditTypeRule> auditTypeRules = new ArrayList<AuditTypeRule>();
	private List<AuditCategoryRule> auditCategoryRules = new ArrayList<AuditCategoryRule>();
	private List<ContractorTag> contractorTags = new ArrayList<ContractorTag>();
	private List<FlagCriteriaOperator> operatorFlagCriteria = new ArrayList<FlagCriteriaOperator>();

	@Column(nullable = false, length = 50)
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Column(nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "opID", nullable = false, updatable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public boolean isVisibleToContractor() {
		return visibleToContractor;
	}

	public void setVisibleToContractor(boolean visibleToContractor) {
		this.visibleToContractor = visibleToContractor;
	}

	public boolean isInheritable() {
		return inheritable;
	}

	public void setInheritable(boolean inheritable) {
		this.inheritable = inheritable;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transient
	public JSONObject toJSON(boolean full) {
		JSONObject o = new JSONObject();
		o.put("id", this.getId());
		o.put("operator", this.getOperator().getName());
		o.put("tag", this.getTag());

		return o;
	}

	@Override
	@Transient
	public String getAutocompleteValue() {
		return "(" + getAutocompleteResult() + ") " + this.getTag();
	}

	@OneToMany(mappedBy = "tag")
	public List<ContractorTag> getContractorTags() {
		return contractorTags;
	}

	public void setContractorTags(List<ContractorTag> contractorTags) {
		this.contractorTags = contractorTags;
	}

	@OneToMany(mappedBy = "tag")
	public List<AuditTypeRule> getAuditTypeRules() {
		return auditTypeRules;
	}

	@Transient
	@OneToMany(mappedBy = "tag")
	@Where(clause = "expirationDate > NOW()")
	public List<AuditTypeRule> getNonExpiredAuditTypeRules() {
		return auditTypeRules;
	}

	public void setAuditTypeRules(List<AuditTypeRule> auditTypeRules) {
		this.auditTypeRules = auditTypeRules;
	}

	@OneToMany(mappedBy = "tag")
	@Where(clause = "expirationDate > NOW()")
	public List<AuditCategoryRule> getAuditCategoryRules() {
		return auditCategoryRules;
	}

	public void setAuditCategoryRules(List<AuditCategoryRule> auditCategoryRules) {
		this.auditCategoryRules = auditCategoryRules;
	}

	@OneToMany(mappedBy = "tag")
	public List<FlagCriteriaOperator> getOperatorFlagCriteria() {
		return operatorFlagCriteria;
	}

	public void setOperatorFlagCriteria(List<FlagCriteriaOperator> operatorFlagCriteria) {
		this.operatorFlagCriteria = operatorFlagCriteria;
	}
}
