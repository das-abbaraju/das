package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.OperatorAccount;

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
		// TranslatableString translatedName = new TranslatableString();
		// translatedName.putTranslation("en", name, true);
		// type.setName(translatedName.toString());
		type.setName(name);
		return this;
	}

	public AuditTypeBuilder account(OperatorAccount operator) {
		type.setAccount(operator);
		return this;
	}
}
