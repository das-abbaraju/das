package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "flag_criteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteria extends BaseTable {

	private String category;
	private AuditQuestion question;
	private AuditType auditType;
	private OshaType oshaType;
	private String oshaRateType;
	private String label;
	private String description;
	private char comparision;
	private MultiYearScope multiYearScope;
	protected boolean validationRequired;
	private String defaultValue;
	private boolean allowCustomValue;

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
	@JoinColumn(name = "auditTypeID", nullable = true)
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.OshaType") })
	@Enumerated(EnumType.STRING)
	public OshaType getOshaType() {
		return oshaType;
	}

	public void setOshaType(OshaType oshaType) {
		this.oshaType = oshaType;
	}

	public String getOshaRateType() {
		return oshaRateType;
	}

	public void setOshaRateType(String oshaRateType) {
		this.oshaRateType = oshaRateType;
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

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.MultiYearScope") })
	@Enumerated(EnumType.STRING)
	public MultiYearScope getMultiYearScope() {
		return multiYearScope;
	}

	public void setMultiYearScope(MultiYearScope multiYearScope) {
		this.multiYearScope = multiYearScope;
	}

	public boolean isValidationRequired() {
		return validationRequired;
	}

	public void setValidationRequired(boolean validationRequired) {
		this.validationRequired = validationRequired;
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
