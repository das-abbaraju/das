package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "flag_criteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteria extends BaseTable {

	protected String category;
	protected AuditQuestion question;
	protected AuditType auditType;
	protected OshaType oshaType;
	protected String label;
	protected String description;
	protected char comparision;
	protected boolean validationRequired;
	protected MultiYearScope multiYearScope;
	protected String defaultValue;
	protected boolean allowCustomValue;

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	@ManyToOne
	@JoinColumn(name = "questionID", nullable = true)
	public AuditQuestion getQuestion() {
		return question;
	}
	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}
	
	@ManyToOne
	@JoinColumn(name="auditTypeID", nullable=true)
	public AuditType getAuditType() {
		return auditType;
	}
	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}
	
	public OshaType getOshaType() {
		return oshaType;
	}
	public void setOshaType(OshaType oshaType) {
		this.oshaType = oshaType;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public char getComparision() {
		return comparision;
	}
	public void setComparision(char comparision) {
		this.comparision = comparision;
	}
	
	public boolean isValidationRequired() {
		return validationRequired;
	}
	public void setValidationRequired(boolean validationRequired) {
		this.validationRequired = validationRequired;
	}

	public MultiYearScope getMultiYearScope() {
		return multiYearScope;
	}
	public void setMultiYearScope(MultiYearScope multiYearScope) {
		this.multiYearScope = multiYearScope;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public boolean isAllowCustomValue() {
		return allowCustomValue;
	}
	public void setAllowCustomValue(boolean allowCustomValue) {
		this.allowCustomValue = allowCustomValue;
	}
}
