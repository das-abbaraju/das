package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.LowMedHigh;

public class LowMedHighConverter extends EnumConverter {
	public LowMedHighConverter() {
		enumClass = LowMedHigh.class;
	}
}
