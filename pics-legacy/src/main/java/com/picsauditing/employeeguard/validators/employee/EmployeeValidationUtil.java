package com.picsauditing.employeeguard.validators.employee;

import com.picsauditing.access.Permissions;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;

public class EmployeeValidationUtil {

	public enum EmployeeField {
		FIRST_NAME, LAST_NAME, EMAIL, EMPLOYEE_ID
	}

	public static boolean valid(String toValidate, EmployeeField field) {
		switch (field) {
			case FIRST_NAME:
				return validateFirstName(toValidate);
			case LAST_NAME:
				return validateLastName(toValidate);
			case EMAIL:
				return validateEmail(toValidate);
			case EMPLOYEE_ID:
				return validateEmployeeId(toValidate);
			default:
				throw new IllegalArgumentException("You have not set up validation for that field: " + field);
		}
	}

	private static boolean validateFirstName(String firstName) {
		return Strings.isNotEmpty(firstName);
	}

	private static boolean validateLastName(String lastName) {
		return Strings.isNotEmpty(lastName);
	}

	private static boolean validateEmail(String email) {
		if (Strings.isEmpty(email)) {
			return false;
		}

		Permissions permissions = (Permissions) ServletActionContext.getContext().getSession().get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);
		String id = ServletActionContext.getActionMapping().getParams().get("id").toString();

		Employee existing = employeeDAO().findEmployeeByAccountIdAndEmail(permissions.getAccountId(), email);
		if (existing == null || NumberUtils.toInt(id) == existing.getId()) {
			return true;
		}

		return false;
	}

	private static boolean validateEmployeeId(String employeeId) {
		if (Strings.isNotEmpty(employeeId)) {
			Permissions permissions = (Permissions) ServletActionContext.getContext().getSession().get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);
			String id = ServletActionContext.getActionMapping().getParams().get("id").toString();

			Employee existing = employeeDAO().findEmployeeByAccountIdAndSlug(permissions.getAccountId(), employeeId);
			if (existing != null && NumberUtils.toInt(id) != existing.getId()) {
				return false;
			}
		}

		return true;
	}

	private static EmployeeDAO employeeDAO() {
		return SpringUtils.getBean(SpringUtils.EMPLOYEE_DAO);
	}
}
