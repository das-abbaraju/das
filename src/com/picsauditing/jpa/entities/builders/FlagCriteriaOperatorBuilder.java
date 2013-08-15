package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class FlagCriteriaOperatorBuilder {
    FlagCriteriaOperator flagCriteriaOperator = new FlagCriteriaOperator();

    public FlagCriteriaOperatorBuilder operator(OperatorAccount operator) {
        flagCriteriaOperator.setOperator(operator);
        return this;
    }

    public FlagCriteriaOperatorBuilder criteria(FlagCriteria criteria) {
        flagCriteriaOperator.setCriteria(criteria);
        return this;
    }

    public FlagCriteriaOperator build() {
        return flagCriteriaOperator;
    }
}
