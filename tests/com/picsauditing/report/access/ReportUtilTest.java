package com.picsauditing.report.access;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.Column;
import com.picsauditing.report.models.ModelType;

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

	@Test(expected = ReportValidationException.class)
	public void testValidate_NullReport() throws ReportValidationException {
		Report report = null;

		ReportUtil.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_NullModelType() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(null);

		ReportUtil.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_InvalidReportParameters() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(ModelType.Accounts);
		report.setParameters("NOT_A_REPORT");

		ReportUtil.validate(report);
	}

	@Test
	public void testValidate_ValidReportParameters() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(ModelType.Accounts);
		report.setParameters("{}");

		ReportUtil.validate(report);
	}
}
