package com.picsauditing.jpa.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_criteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteria extends BaseTable implements Comparable<FlagCriteria> {

	private String category;
	private int displayOrder = 999;
	private AuditQuestion question;
	private AuditType auditType;
	private OshaType oshaType;
	private OshaRateType oshaRateType;
	private String label;
	private String description;
	private String comparison;
	private MultiYearScope multiYearScope = null;
	protected boolean validationRequired;
	private String defaultValue;
	private boolean allowCustomValue = false;
	private String dataType = "string";
	private boolean flaggableWhenMissing = false;
	private boolean insurance = false;

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

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.OshaRateType") })
	@Enumerated(EnumType.STRING)
	public OshaRateType getOshaRateType() {
		return oshaRateType;
	}

	public void setOshaRateType(OshaRateType oshaRateType) {
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

	@Transient
	public String getDescriptionBeforeHurdle() {
		try {
			return description.substring(0, description.indexOf("{HURDLE}"));
		} catch (Exception e) {
			return description;
		}
	}

	@Transient
	public String getDescriptionAfterHurdle() {
		try {
			if (description.indexOf("{HURDLE}") < 0)
				return null;
			return description.substring(description.indexOf("{HURDLE}") + 8);
		} catch (Exception e) {
			return null;
		}
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

	public boolean isFlaggableWhenMissing() {
		return flaggableWhenMissing;
	}

	public void setFlaggableWhenMissing(boolean flaggableWhenMissing) {
		this.flaggableWhenMissing = flaggableWhenMissing;
	}

	public boolean isInsurance() {
		return insurance;
	}

	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
	public String toString() {
		return category + ": " + description;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("category", category);
		json.put("displayOrder", displayOrder);
		json.put("question", question == null ? null : question.toJSON(full));
		json.put("auditType", auditType == null ? null : auditType.toJSON(full));
		json.put("oshaType", oshaType == null ? null : oshaType.toString());
		json.put("oshaRateType", oshaRateType == null ? null : oshaRateType.toString());
		json.put("label", label);
		json.put("description", description);
		json.put("comparison", comparison);
		json.put("multiYearScope", multiYearScope == null ? null : multiYearScope.toString());
		json.put("validationRequired", validationRequired);
		json.put("defaultValue", defaultValue);
		json.put("allowCustomValue", allowCustomValue);
		json.put("dataType", dataType);
		json.put("insurance", insurance);

		return json;
	}

	@Override
	public int compareTo(FlagCriteria o) {

		return ((Integer) displayOrder).compareTo(o.displayOrder);
	}

	@Transient
	public Collection<String> getAmbestRatings(){
		return new TreeMap<Integer, String>(AmBest.ratingMap).values();
	}
	
	@Transient
	public Collection<String> getAmbestClasses(){
		TreeMap<Integer, String> reverseClasses = new TreeMap<Integer, String>(Collections.reverseOrder());
		reverseClasses.putAll(AmBest.financialMap);
		return reverseClasses.values();
	}
}
