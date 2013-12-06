package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.*;

import java.util.ArrayList;
import java.util.HashSet;

public class OperatorAccountBuilder {
	private OperatorAccount operator = new OperatorAccount();

	public OperatorAccountBuilder() {
		operator.setVisibleAuditTypes(new HashSet<Integer>());
		operator.setAutoApproveRelationships(false);
	}

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

	public OperatorAccountBuilder visibleAuditType(int auditTypeId) {
		operator.getVisibleAuditTypes().add(auditTypeId);

		return this;
	}

	public OperatorAccountBuilder autoApproveRelationships(boolean b) {
		operator.setAutoApproveRelationships(b);
		return this;
	}

	public OperatorAccountBuilder corporate() {
		operator.setType(Account.CORPORATE_ACCOUNT_TYPE);
		return this;
	}

    public OperatorAccountBuilder operator() {
        operator.setType(Account.OPERATOR_ACCOUNT_TYPE);
        return this;
    }

	public OperatorAccountBuilder contractor(ContractorOperator op) {
		this.operator.getContractorOperators().add(op);
		return this;
	}

	public OperatorAccountBuilder child(OperatorAccount child) {
		operator.getChildOperators().add(child);
		child.setParent(operator);
		return this;
	}

	public OperatorAccountBuilder user(User user) {
		operator.getUsers().add(user);
		return this;
	}

	public OperatorAccountBuilder status(AccountStatus status) {
		operator.setStatus(status);
		return this;
	}

    public OperatorAccountBuilder name(String name) {
        operator.setName(name);
        return this;
    }

    public OperatorAccountBuilder parentAccount(OperatorAccount parentAccount) {
        if (operator.getCorporateFacilities() == null) {
            operator.setCorporateFacilities(new ArrayList<Facility>());
        }
        Facility facility = Facility.builder()
                .operator(operator)
                .corporate(parentAccount)
                .build();
       operator.getCorporateFacilities().add(facility);

        return this;
    }
}
