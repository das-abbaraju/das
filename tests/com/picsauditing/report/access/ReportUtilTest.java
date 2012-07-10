package com.picsauditing.report.access;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.picsauditing.report.Column;

public class ReportUtilTest {

	@Test
	public void testGetColumnFromFieldName_NullFieldName() {
		String fieldName = null;
		List<Column> columns = new ArrayList<Column>();

		Column column = ReportUtil.getColumnFromFieldName(fieldName, columns);

		assertNull(column);
	}

	@Test
	public void testGetColumnFromFieldName_FieldNameNotFound() {
		String fieldName = "fieldName";
		List<Column> columns = new ArrayList<Column>();
		columns.add(new Column("differentFieldName"));

		Column column = ReportUtil.getColumnFromFieldName(fieldName, columns);

		assertNull(column);
	}

	@Test
	public void testGetColumnFromFieldName_FieldNameFound() {
		String fieldName = "fieldName";
		List<Column> columns = new ArrayList<Column>();
		columns.add(new Column(fieldName));

		Column column = ReportUtil.getColumnFromFieldName(fieldName, columns);

		assertNotNull(column);
		assertEquals(fieldName, column.getFieldName());
	}
}
