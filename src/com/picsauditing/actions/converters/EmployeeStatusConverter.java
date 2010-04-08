package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.EmployeeStatus;

public class EmployeeStatusConverter extends EnumConverter {
	public EmployeeStatusConverter() {
		enumClass = EmployeeStatus.class;
	}
}
