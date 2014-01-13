package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.*;

import java.util.ArrayList;

public class AuditTypeBuilder {
	private AuditType type = new AuditType();

	public AuditTypeBuilder id(int id) {
		type.setId(id);
		return this;
	}

	public AuditTypeBuilder auditClass(AuditTypeClass clazz) {
		type.setClassType(clazz);
		return this;
	}

	public AuditType build() {
		return type;
	}

	public AuditTypeBuilder name(String name) {
		type.setName(name);
		return this;
	}

	public AuditTypeBuilder account(OperatorAccount operator) {
		type.setAccount(operator);
		return this;
	}

    public AuditTypeBuilder categories(AuditCategory category) {
        if (type.getCategories() == null) {
            type.setCategories(new ArrayList<AuditCategory>());
        }

        type.getCategories().add(category);
        return this;
    }

    public AuditTypeBuilder period(AuditTypePeriod period) {
        type.setPeriod(period);
        return this;
    }

    public AuditTypeBuilder advanceDays(int advanceDays) {
        type.setAdvanceDays(advanceDays);
        return this;
    }

    public AuditTypeBuilder maximumActive(int maxActive) {
        type.setMaximumActive(maxActive);
        return this;
    }

    public AuditTypeBuilder anchorDay(int anchorDay) {
        type.setAnchorDay(anchorDay);
        return this;
    }

    public AuditTypeBuilder anchorMonth(int anchorMonth) {
        type.setAnchorMonth(anchorMonth);
        return this;
    }

    public AuditTypeBuilder contractorCanView() {
        type.setCanContractorView(true);
        return this;
    }

    public AuditTypeBuilder contractorCanEdit() {
        type.setCanContractorEdit(true);
        return this;
    }

    public AuditTypeBuilder workflow(Workflow workflow) {
        type.setWorkFlow(workflow);
        return this;
    }

    public AuditTypeBuilder classType(AuditTypeClass classType) {
        type.setClassType(classType);
        return this;
    }
}
