package com.picsauditing.employeeguard.services.entity.employee;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.services.entity.util.file.FileRowMapper;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.ArrayUtils;

class EmployeeFileRowMapper implements FileRowMapper<Employee> {

	private final int contractorId;

	public EmployeeFileRowMapper(final int contractorId) {
		this.contractorId = contractorId;
	}

	@Override
	public boolean isHeader(String[] row) {
		return ArrayUtils.isEquals(EmployeeImportTemplate.IMPORT_FILE_HEADER, row);
	}

	@Override
	public boolean isValid(String[] row) {
		if (Strings.isEmpty(getFirstName(row))) {
			return false;
		}

		if (Strings.isEmpty(getLastName(row))) {
			return false;
		}

		if (Strings.isEmpty(getEmail(row))) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isEmptyRow(String[] row) {
		if (ArrayUtils.isEmpty(row)) {
			return true;
		}

		boolean result = true;
		for (String s : row) {
			if (Strings.isNotEmpty(s)) {
				return false;
			}
		}

		return result;
	}

	@Override
	public Employee mapToEntity(String[] fileRow) {
		return new EmployeeBuilder()
				.firstName(getFirstName(fileRow))
				.lastName(getLastName(fileRow))
				.positionName(safeGetValueAtIndex(fileRow, 2, null))
				.email(getEmail(fileRow))
				.phoneNumber(safeGetValueAtIndex(fileRow, 4, null))
				.slug(safeGetValueAtIndex(fileRow, 5, null))
				.accountId(contractorId)
				.build();
	}

	private String getEmail(String[] fileRow) {
		return safeGetValueAtIndex(fileRow, 3, null);
	}

	private String getLastName(String[] fileRow) {
		return safeGetValueAtIndex(fileRow, 1, null);
	}

	private String getFirstName(String[] fileRow) {
		return safeGetValueAtIndex(fileRow, 0, null);
	}

	private String safeGetValueAtIndex(final String[] fileRow, final int index, final String defaultValue) {
		if (ArrayUtils.isEmpty(fileRow) || index >= fileRow.length) {
			return defaultValue;
		}

		String valueAtIndex = fileRow[index];

		return Strings.isEmpty(valueAtIndex) ? defaultValue : valueAtIndex;
	}
}
