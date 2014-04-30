package com.picsauditing.flagcalculator.entities;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.FlagCriteriaOperator")
@Table(name = "flag_criteria_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaOperator extends BaseTable {
    private OperatorAccount operator;
    private FlagCriteria criteria;
    private FlagColor flag = FlagColor.Red;
    private String hurdle;
    private OperatorTag tag;
<<<<<<< HEAD
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

=======
>>>>>>> 7ae760b... US831 Deprecated old FDC

    @ManyToOne
    @JoinColumn(name = "opID", nullable = false)
    public OperatorAccount getOperator() {
        return operator;
    }

    public void setOperator(OperatorAccount operator) {
        this.operator = operator;
    }

    @ManyToOne
    @JoinColumn(name = "criteriaID", nullable = false)
    public FlagCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(FlagCriteria criteria) {
        this.criteria = criteria;
    }

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    public FlagColor getFlag() {
        return flag;
    }

    public void setFlag(FlagColor flag) {
        this.flag = flag;
    }

    public String getHurdle() {
        return hurdle;
    }

    public void setHurdle(String hurdle) {
        this.hurdle = hurdle;
    }

    @ManyToOne
    @JoinColumn(name = "tagID")
    public OperatorTag getTag() {
        return tag;
    }

    public void setTag(OperatorTag tag) {
        this.tag = tag;
    }
}