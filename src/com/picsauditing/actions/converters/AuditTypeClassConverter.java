package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.AuditTypeClass;

public class AuditTypeClassConverter extends EnumConverter {
	public AuditTypeClassConverter() {
		enumClass = AuditTypeClass.class;
	}
}
