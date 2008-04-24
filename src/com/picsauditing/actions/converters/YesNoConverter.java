package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.YesNo;

public class YesNoConverter extends EnumConverter
{
	public YesNoConverter() {
		enumClass = YesNo.class;
	}
}
