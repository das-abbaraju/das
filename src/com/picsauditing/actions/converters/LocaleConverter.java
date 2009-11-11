package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.Locale;

public class LocaleConverter extends EnumConverter {
	public LocaleConverter() {
		enumClass = Locale.class;
	}
}
