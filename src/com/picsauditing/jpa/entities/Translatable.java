package com.picsauditing.jpa.entities;

public interface Translatable {
	String getI18nKey();

	String getI18nKey(String property);
}