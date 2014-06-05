package com.picsauditing.employeeguard.services.entity.util.file;

public class FakeFileRowMapper implements FileRowMapper<FakePerson> {
	@Override
	public boolean isHeader(String[] row) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isValid(String[] row) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isEmptyRow(String[] row) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public FakePerson mapToEntity(String[] fileRow) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
