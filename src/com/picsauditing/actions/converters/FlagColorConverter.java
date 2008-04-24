package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.FlagColor;

public class FlagColorConverter extends EnumConverter
{
	public FlagColorConverter() {
		enumClass = FlagColor.class;
	}
}
