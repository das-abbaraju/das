package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum Month implements Translatable {
	Jan("01"), Feb("02"), Mar("03"), Apr("04"), May("05"), Jun("06"), Jul("07"), Aug("08"), Sep("09"), Oct("10"), Nov(
			"11"), Dec("12");

	String number;

	private Month(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	@Transient
	@Override
	public String getI18nKey() {
		return getClass().getSimpleName() + "." + toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
