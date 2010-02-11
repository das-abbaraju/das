package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.OshaRateType;

public class OshaRateTypeConverter extends EnumConverter {
	public OshaRateTypeConverter() {
		enumClass = OshaRateType.class;
	}
}
