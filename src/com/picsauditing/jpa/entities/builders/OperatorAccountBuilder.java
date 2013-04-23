package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.drools.reteoo.builder.ReteooComponentBuilder;

public class OperatorAccountBuilder {
    private OperatorAccount operator = new OperatorAccount();

    public OperatorAccountBuilder id(int id) {
        operator.setId(id);
       return this;
    }

    public OperatorAccountBuilder flagCriteria(FlagCriteria flag) {
        FlagCriteriaOperator joinTable = new FlagCriteriaOperator();
        joinTable.setOperator(operator);
        joinTable.setCriteria(flag);

        operator.getFlagCriteria().add(joinTable);

        return this;
    }

    public OperatorAccount build() {
        return operator;
    }
}
