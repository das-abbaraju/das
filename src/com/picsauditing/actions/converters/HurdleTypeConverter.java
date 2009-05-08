package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.HurdleType;

public class HurdleTypeConverter extends EnumConverter {
	public HurdleTypeConverter() {
		enumClass = HurdleType.class;
	}
}
