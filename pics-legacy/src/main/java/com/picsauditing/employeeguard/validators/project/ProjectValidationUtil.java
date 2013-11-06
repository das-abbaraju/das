package com.picsauditing.employeeguard.validators.project;

import com.picsauditing.employeeguard.forms.operator.ProjectNameLocationForm;
import com.picsauditing.util.Strings;

public class ProjectValidationUtil {
	public enum ProjectField {
		NAME, LOCATION
	}

	public static boolean valid(final ProjectNameLocationForm projectNameLocationForm, final ProjectField field) {
		switch (field) {
			case NAME:
				return validateName(projectNameLocationForm);
			case LOCATION:
				return validateLocation(projectNameLocationForm);
			default:
				throw new IllegalArgumentException("You have not set up validation for that field: " + field);
		}
	}

	private static boolean validateName(ProjectNameLocationForm projectNameLocationForm) {
		return Strings.isNotEmpty(projectNameLocationForm.getName());
	}

	private static boolean validateLocation(ProjectNameLocationForm projectNameLocationForm) {
		return Strings.isNotEmpty(projectNameLocationForm.getLocation());
	}
}
