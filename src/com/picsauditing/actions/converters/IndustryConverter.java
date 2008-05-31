package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.Industry;

public class IndustryConverter extends EnumConverter {
	public IndustryConverter() {
		enumClass = Industry.class;
	}
}
