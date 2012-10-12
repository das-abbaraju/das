package com.picsauditing.jpa.entities;

import java.util.List;

import javax.persistence.Column;
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
public class FlagCriteria extends BaseTableRequiringLanguages implements Comparable<FlagCriteria> {

	private String category;
	private int displayOrder = 999;
	private AuditQuestion question;
	private AuditType auditType;
	private OshaType oshaType;
	private OshaRateType oshaRateType;
	private TranslatableString label;
	private TranslatableString description;
	private String comparison;
	private MultiYearScope multiYearScope = null;
	protected AuditStatus requiredStatus = AuditStatus.Complete;
	private String requiredStatusComparison;
	private String defaultValue;
	private boolean allowCustomValue = false;
	private String dataType = "string";
	private boolean flaggableWhenMissing = false;
	private boolean insurance = false;
	private FlagCriteriaOptionCode optionCode;

	public static final String BOOLEAN = "boolean";
	public static final String NUMBER = "number";
	public static final String DATE = "date";
	public static final String STRING = "string";

	public static final int EMR_AVERAGE_ID = 506;
	public static final int ANNUAL_UPDATE_ID = 142;

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

	@Transient
	public TranslatableString getLabel() {
		return label;
	}

	public void setLabel(TranslatableString label) {
		this.label = label;
	}

	@Transient
	public TranslatableString getDescription() {
		return description;
	}

	public void setDescription(TranslatableString description) {
		this.description = description;
	}

	@Transient
	public String getDescriptionBeforeHurdle() {
		try {
			return description.toString().substring(0, description.toString().indexOf("{HURDLE}"));
		} catch (Exception e) {
			return description.toString();
		}
	}

	@Transient
	public String getDescriptionAfterHurdle() {
		try {
			if (description.toString().indexOf("{HURDLE}") < 0)
				return null;
			return description.toString().substring(description.toString().indexOf("{HURDLE}") + 8);
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

	@Enumerated(EnumType.STRING)
	public AuditStatus getRequiredStatus() {
		return requiredStatus;
	}

	public void setRequiredStatus(AuditStatus requiredStatus) {
		this.requiredStatus = requiredStatus;
	}

	public String getRequiredStatusComparison() {
		return requiredStatusComparison;
	}

	public void setRequiredStatusComparison(String requiredStatusComparison) {
		this.requiredStatusComparison = requiredStatusComparison;
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	public FlagCriteriaOptionCode getOptionCode() {
		return optionCode;
	}

	public void setOptionCode(FlagCriteriaOptionCode optionCode) {
		this.optionCode = optionCode;
	}

	/**
	 * @return the question ID if this criteria should include
	 */
	public Integer includeExcess() {
		if (!insurance || optionCode == null)
			return null;

		// We should consider putting this into the DB eventually
		if (optionCode == FlagCriteriaOptionCode.ExcessAggregate)
			return AuditQuestion.EXCESS_AGGREGATE;

		if (optionCode == FlagCriteriaOptionCode.ExcessEachOccurrence)
			return AuditQuestion.EXCESS_EACH;

		return null;
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
		json.put("label", label.toString());
		json.put("description", description.toString());
		json.put("comparison", comparison);
		json.put("multiYearScope", multiYearScope == null ? null : multiYearScope.toString());
		json.put("requiredStatus", requiredStatus);
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

	public void cascadeRequiredLanguages(List<String> add, List<String> remove) {
		return;
	}

	public boolean hasMissingChildRequiredLanguages() {
		return getLanguages().isEmpty();
	}
}