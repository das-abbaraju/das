package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.OperatorAccount;

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
}
