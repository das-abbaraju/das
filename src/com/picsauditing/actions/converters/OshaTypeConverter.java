package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.OshaType;

public class OshaTypeConverter extends EnumConverter {
	public OshaTypeConverter() {
		enumClass = OshaType.class;
	}
}
