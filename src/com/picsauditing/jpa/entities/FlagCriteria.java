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
import org.json.simple.JSONObject;

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
	private String comparison;
	private MultiYearScope multiYearScope;
	protected boolean validationRequired;
	private String defaultValue;
	private boolean allowCustomValue;
	private String dataType;

	public static final String BOOLEAN = "boolean";
	public static final String NUMBER = "number";
	public static final String DATE = "date";
	public static final String STRING = "string";

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

	public String getComparison() {
		return comparison;
	}

	public void setComparison(String comparison) {
		this.comparison = comparison;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("category", category);
		if (question != null) {
			json.put("question.id", question.getId());
		}
		if (auditType != null) {
			json.put("auditType.id", auditType.getId());
		}
		json.put("oshaType", oshaType);
		json.put("oshaRateType", oshaRateType);
		json.put("label", label);
		json.put("description", description);
		json.put("comparison", comparison);
		json.put("multiYearScope", multiYearScope);
		json.put("validationRequired", validationRequired);
		json.put("defaultValue", defaultValue);
		json.put("allowCustomValue", allowCustomValue);
		json.put("dataType", dataType);

		if (full) {
			json.putAll(super.toJSON(full));
		}

		return json;
	}
}
