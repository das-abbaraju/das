package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.ListType;

public class ListTypeConverter extends EnumConverter {
	public ListTypeConverter() {
		enumClass = ListType.class;
	}
}
