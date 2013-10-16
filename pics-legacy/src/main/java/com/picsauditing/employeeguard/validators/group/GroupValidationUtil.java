package com.picsauditing.employeeguard.validators.group;

import com.picsauditing.util.Strings;

public class GroupValidationUtil {

	public enum RoleField {
		NAME
	}

	public static boolean valid(String value, RoleField field) {
		switch (field) {
			case NAME:
				return validateName(value);
			default:
				throw new IllegalArgumentException("You have not set up validation for that field: " + field);
		}
	}

	private static boolean validateName(String value) {
		return Strings.isNotEmpty(value);
	}

}
