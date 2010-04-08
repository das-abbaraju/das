package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.EmployeeClassification;

public class EmployeeClassificationConverter extends EnumConverter {
	public EmployeeClassificationConverter() {
		enumClass = EmployeeClassification.class;
	}
}
