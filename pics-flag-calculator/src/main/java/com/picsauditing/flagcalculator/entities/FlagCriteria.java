package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.FlagCriteria")
@Table(name = "flag_criteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteria extends BaseTable {

    private FlagCriteriaCategory category;
    private int displayOrder = 999;
    private AuditQuestion question;
    private AuditType auditType;
    private OshaType oshaType;
    private OshaRateType oshaRateType;
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

    @Enumerated(EnumType.STRING)
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

    @Type(type = "com.picsauditing.flagcalculator.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.flagcalculator.entities.OshaType") })
    @Enumerated(EnumType.STRING)
    public OshaType getOshaType() {
        return oshaType;
    }

    public void setOshaType(OshaType oshaType) {
        this.oshaType = oshaType;
    }

    @Type(type = "com.picsauditing.flagcalculator.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.flagcalculator.entities.OshaRateType") })
    @Enumerated(EnumType.STRING)
    public OshaRateType getOshaRateType() {
        return oshaRateType;
    }

    public void setOshaRateType(OshaRateType oshaRateType) {
        this.oshaRateType = oshaRateType;
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    @Type(type = "com.picsauditing.flagcalculator.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.flagcalculator.entities.MultiYearScope") })
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
}