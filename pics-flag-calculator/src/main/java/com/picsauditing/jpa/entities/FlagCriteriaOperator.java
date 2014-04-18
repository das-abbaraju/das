package com.picsauditing.jpa.entities;

@SuppressWarnings("serial")
//@Entity
//@Table(name = "flag_criteria_operator")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaOperator extends BaseTable {
//    private OperatorAccount operator;
//    private FlagCriteria criteria;
    private FlagColor flag = FlagColor.Red;
//    private String hurdle;
//    private int affected = 0;
//    private Date lastCalculated;
//    private OperatorTag tag;
//
//    public static final Comparator<FlagCriteriaOperator> COMPARATOR = new Comparator<FlagCriteriaOperator>() {
//        public int compare(FlagCriteriaOperator o1, FlagCriteriaOperator o2) {
//            FlagCriteria f1 = o1.getCriteria();
//            FlagCriteria f2 = o2.getCriteria();
//
//            // Display order matches, sort by category
//            if (f1.getDisplayOrder() == f2.getDisplayOrder()) {
//                // If category matches, sort by label
//                if (f1.getCategory().equals(f2.getCategory())) {
//                    return f1.getLabel().compareTo(f2.getLabel());
//                } else
//                    return f1.getCategory().compareTo(f2.getCategory());
//            } else
//                return f1.getDisplayOrder() - f2.getDisplayOrder();
//        }
//    };
//
//    @ManyToOne
//    @JoinColumn(name = "opID", nullable = false)
//    public OperatorAccount getOperator() {
//        return operator;
//    }
//
//    public void setOperator(OperatorAccount operator) {
//        this.operator = operator;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "criteriaID", nullable = false)
//    public FlagCriteria getCriteria() {
//        return criteria;
//    }
//
//    public void setCriteria(FlagCriteria criteria) {
//        this.criteria = criteria;
//    }
//
//    @Enumerated(EnumType.STRING)
//    @JoinColumn(nullable = false)
//    @ReportField(type = FieldType.FlagColor, i18nKeyPrefix = "FlagColor", importance = FieldImportance.Required)
    public FlagColor getFlag() {
        return flag;
    }
//
//    public void setFlag(FlagColor flag) {
//        this.flag = flag;
//    }
//
//    @ReportField(importance = FieldImportance.Required)
//    public String getHurdle() {
//        return hurdle;
//    }
//
//    public void setHurdle(String hurdle) {
//        this.hurdle = hurdle;
//    }
//
//    public int getAffected() {
//        return affected;
//    }
//
//    public void setAffected(int affected) {
//        this.affected = affected;
//    }
//
//    public Date getLastCalculated() {
//        return lastCalculated;
//    }
//
//    public void setLastCalculated(Date lastCalculated) {
//        this.lastCalculated = lastCalculated;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "tagID")
//    public OperatorTag getTag() {
//        return tag;
//    }
//
//    public void setTag(OperatorTag tag) {
//        this.tag = tag;
//    }
//
//    @Transient
//    public boolean isNeedsRecalc() {
//        if (lastCalculated != null) {
//            Date now = new Date();
//            Long diff = now.getTime() - lastCalculated.getTime();
//
//            // Difference is a day?
//            if (diff > (long) 60 * 60 * 24 * 1000)
//                return true;
//            else
//                return false;
//        }
//
//        return true;
//    }
//
//    @Transient
//    public String getReplaceHurdle() {
//        String value = criteriaValue();
//        if (criteria.getDataType().equals(FlagCriteria.NUMBER))
//            value = Strings.formatDecimalComma(value);
//
//        return criteria.getDescription().toString().replaceAll("\\{HURDLE\\}", value);
//    }
//
//    @Transient
//    public String criteriaValue() {
//        if (criteria.isAllowCustomValue() && hurdle != null)
//            return hurdle;
//
//        return criteria.getDefaultValue();
//    }
//
//    @Override
//    public String toString() {
//        return getReplaceHurdle() + " for " + operator.toString();
//    }
//
//    @Transient
//    public String getShortDescription() {
//        String desc = criteria.getComparison() + " ";
//
//        String value = criteriaValue();
//        if (OshaRateType.Fatalities.equals(criteria.getOshaRateType()))
//            value = Strings.trimTrailingZeros(value);
//
//        if (criteria.getDataType().equals(FlagCriteria.NUMBER))
//            desc += Strings.formatDecimalComma(value);
//        else
//            desc += value;
//
//        return desc;
//    }
//
//    public static FlagCriteriaOperatorBuilder builder() {
//        return new FlagCriteriaOperatorBuilder();
//    }
}
