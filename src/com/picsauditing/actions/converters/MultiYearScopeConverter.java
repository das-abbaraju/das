package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.MultiYearScope;

@SuppressWarnings("unchecked")
public class MultiYearScopeConverter extends EnumConverter {
	public MultiYearScopeConverter() {
		enumClass = MultiYearScope.class;
	}
}
