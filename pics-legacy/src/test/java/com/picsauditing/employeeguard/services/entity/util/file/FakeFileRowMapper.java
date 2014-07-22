package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.util.Strings;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

public class FakeFileRowMapper implements FileRowMapper<FakePerson> {

	static final String[] HEADER = new String[]{"First Name", "Last Name", "Age"};

	@Override
	public boolean isHeader(String[] row) {
		return ArrayUtils.isEquals(HEADER, row);
	}

	@Override
	public boolean isValid(String[] row) {
		if (Strings.isEmpty(safeValueAtIndex(row, 0, null))) {
			return false;
		}

		if (Strings.isEmpty(safeValueAtIndex(row, 1, null))) {
			return false;
		}

		if (Strings.isEmpty(safeValueAtIndex(row, 2, null))) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isEmptyRow(String[] row) {
		if (ArrayUtils.isEmpty(row)) {
			return true;
		}

		for (String s : row) {
			if (Strings.isNotEmpty(s)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public FakePerson mapToEntity(String[] fileRow) {
		FakePerson fakePerson = new FakePerson();

		fakePerson.setFirstName(safeValueAtIndex(fileRow, 0, null));
		fakePerson.setLastName(safeValueAtIndex(fileRow, 1, null));
		fakePerson.setAge(NumberUtils.toInt(safeValueAtIndex(fileRow, 2, null)));

		return fakePerson;
	}

	private String safeValueAtIndex(final String[] array, final int index, final String defaultValue) {
		if (ArrayUtils.isEmpty(array) || index >= array.length) {
			return defaultValue;
		}

		String value = array[index];

		return Strings.isEmpty(value) ? defaultValue : value;
	}
}
