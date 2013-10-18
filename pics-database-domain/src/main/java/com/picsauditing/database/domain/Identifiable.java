package com.picsauditing.database.domain;

public interface Identifiable {
	public static int SYSTEM = 1;

	String getFirstName();

	String getLastName();

	String getEmail();

	String getPhone();
}
