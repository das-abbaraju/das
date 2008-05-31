package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.AuditStatus;

public class AuditStatusConverter extends EnumConverter {
	public AuditStatusConverter() {
		enumClass = AuditStatus.class;
	}
}
