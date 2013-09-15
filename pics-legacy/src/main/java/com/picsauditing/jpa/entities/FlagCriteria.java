package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.builders.FlagCriteriaBuilder;
import com.picsauditing.model.i18n.TranslatableString;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_criteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteria extends BaseTableRequiringLanguages implements Comparable<FlagCriteria> {

	private FlagCriteriaCategory backwardCompatibleCategory;
	private FlagCriteriaCategory category;
	private int displayOrder = 999;
	private AuditQuestion question;
	private AuditType auditType;
	private OshaType oshaType;
	private OshaRateType oshaRateType;
	private String label;
	private String description;
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

	public static final List<Integer> EMR_IDS = new ArrayList<Integer>(Arrays.asList(505, 506, 507, 542));

    @Enumerated(EnumType.STRING)
	@ReportField(i18nKeyPrefix = "FlagCriteria.Category", type = FieldType.String, importance = FieldImportance.Required)
    @Column(name = "category")
	public FlagCriteriaCategory getCategory() {
		return category;
	}

	public void setCategory(FlagCriteriaCategory category) {
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
	@ReportField(type = FieldType.OshaType, importance = FieldImportance.Average)
	public OshaType getOshaType() {
		return oshaType;
	}

	public void setOshaType(OshaType oshaType) {
		this.oshaType = oshaType;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.OshaRateType") })
	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.OshaRateType, importance = FieldImportance.Average)
	public OshaRateType getOshaRateType() {
		return oshaRateType;
	}

	public void setOshaRateType(OshaRateType oshaRateType) {
		this.oshaRateType = oshaRateType;
	}

	@Transient
	public String getLabel() {
		if (label != null) {
			return label;
		}

		return new TranslatableString(getI18nKey("label")).toTranslatedString();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Transient
	public String getDescription() {
		if (description != null) {
			return description;
		}

		return new TranslatableString(getI18nKey("description")).toTranslatedString();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getDescriptionBeforeHurdle() {
        String desc = getDescription();
		try {
			return desc.substring(0, desc.indexOf("{HURDLE}"));
		} catch (Exception e) {
			return desc;
		}
	}

	@Transient
	public String getDescriptionAfterHurdle() {
        String desc = getDescription();
		try {
			if (desc.indexOf("{HURDLE}") < 0) {
				return null;
			}
			return desc.substring(desc.indexOf("{HURDLE}") + 8);
		} catch (Exception e) {
			return null;
		}
	}

	@ReportField(type = FieldType.String, importance = FieldImportance.Low)
	public String getComparison() {
		return comparison;
	}

	public void setComparison(String comparison) {
		this.comparison = comparison;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.MultiYearScope") })
	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.MultiYearScope, importance = FieldImportance.Average)
	public MultiYearScope getMultiYearScope() {
		return multiYearScope;
	}

	public void setMultiYearScope(MultiYearScope multiYearScope) {
		this.multiYearScope = multiYearScope;
	}

	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.AuditStatus, importance = FieldImportance.Average)
	public AuditStatus getRequiredStatus() {
		return requiredStatus;
	}

	public void setRequiredStatus(AuditStatus requiredStatus) {
		this.requiredStatus = requiredStatus;
	}

	@ReportField(type = FieldType.String, importance = FieldImportance.Low)
	public String getRequiredStatusComparison() {
		return requiredStatusComparison;
	}

	public void setRequiredStatusComparison(String requiredStatusComparison) {
		this.requiredStatusComparison = requiredStatusComparison;
	}

	@ReportField(type = FieldType.String, importance = FieldImportance.Low)
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@ReportField(type = FieldType.Boolean, importance = FieldImportance.Low)
	public boolean isAllowCustomValue() {
		return allowCustomValue;
	}

	public void setAllowCustomValue(boolean allowCustomValue) {
		this.allowCustomValue = allowCustomValue;
	}

	@ReportField(type = FieldType.String, importance = FieldImportance.Low)
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@ReportField(type = FieldType.Boolean, importance = FieldImportance.Low)
	public boolean isFlaggableWhenMissing() {
		return flaggableWhenMissing;
	}

	public void setFlaggableWhenMissing(boolean flaggableWhenMissing) {
		this.flaggableWhenMissing = flaggableWhenMissing;
	}

	@ReportField(type = FieldType.Boolean, importance = FieldImportance.Low)
	public boolean isInsurance() {
		return insurance;
	}

	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}

	@ReportField(type = FieldType.Integer, importance = FieldImportance.Low)
	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	@ReportField(type = FieldType.FlagCriteriaOptionCode, importance = FieldImportance.Low)
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
		if (!insurance || optionCode == null) {
			return null;
		}

		// We should consider putting this into the DB eventually
		if (optionCode == FlagCriteriaOptionCode.ExcessAggregate) {
			return AuditQuestion.EXCESS_AGGREGATE;
		}

		if (optionCode == FlagCriteriaOptionCode.ExcessEachOccurrence) {
			return AuditQuestion.EXCESS_EACH;
		}

		return null;
	}

	@Override
	public String toString() {
		return category + ": " + getDescription();
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
		json.put("label", getLabel());
		json.put("description", getDescription());
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

	public static FlagCriteriaBuilder builder() {
		return new FlagCriteriaBuilder();
	}
}