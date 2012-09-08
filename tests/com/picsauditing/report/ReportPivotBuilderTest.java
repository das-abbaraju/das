package com.picsauditing.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.PivotCellMethod;
import com.picsauditing.report.fields.PivotDimension;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;

public class ReportPivotBuilderTest {
	private List<Column> columns;
	private Map<String, Field> availableFields;
	private JSONArray jsonResults;

	private int scoreCounter = 0;
	private ReportPivotBuilder builder;

	@Before
	public void setup() {
		jsonResults = new JSONArray();
		addDataRow(AccountStatus.Active, "Irvine");
		addDataRow(AccountStatus.Active, "Irvine");
		addDataRow(AccountStatus.Active, "Houston");
		addDataRow(AccountStatus.Active, "Houston");
		addDataRow(AccountStatus.Pending, "Houston");

		columns = new ArrayList<Column>();
		AbstractModel table = ModelFactory.build(ModelType.Contractors);
		availableFields = ReportModel.buildAvailableFields(table.getRootTable(), EntityFactory.makePermission());
	}

	@Test
	public void testEmpty() throws Exception {
		addColumn("accountStatus");
		convertToPivot();
		Assert.assertEquals(1, builder.getColumns().size());
		Assert.assertEquals(5, jsonResults.size());
	}

	@Test
	public void testRowCountry_ColumnStatusRow_CountScore() throws Exception {
		addColumnsCountryStatusScore(PivotCellMethod.Count);

		Assert.assertEquals(3, builder.getColumns().size());
		Assert.assertEquals(1, jsonResults.size());
		Assert.assertEquals(4, getResultRow(0).get("Active"));
		Assert.assertEquals(1, getResultRow(0).get("Pending"));
		Assert.assertEquals("US", getResultRow(0).get("accountCountry"));
	}

	@Test
	public void testRowCountry_ColumnStatusRow_SumScore() throws Exception {
		addColumnsCountryStatusScore(PivotCellMethod.Sum);
		
		Assert.assertEquals(100.0, getResultRow(0).get("Active"));
		Assert.assertEquals(50.0, getResultRow(0).get("Pending"));
	}

	@Test
	public void testRowCountry_ColumnStatusRow_AvgScore() throws Exception {
		addColumnsCountryStatusScore(PivotCellMethod.Average);
		
		Assert.assertEquals(25.0, getResultRow(0).get("Active"));
		Assert.assertEquals(50.0, getResultRow(0).get("Pending"));
	}

	@Test
	public void testRowCountry_ColumnStatusRow_AvgMin() throws Exception {
		addColumnsCountryStatusScore(PivotCellMethod.Min);
		
		Assert.assertEquals(10, getResultRow(0).get("Active"));
		Assert.assertEquals(50, getResultRow(0).get("Pending"));
	}
	
	@Test
	@Ignore
	public void testRowCountry_ColumnStatusRow_AvgMax() throws Exception {
		addColumnsCountryStatusScore(PivotCellMethod.Max);
		
		Assert.assertEquals(40, getResultRow(0).get("Active"));
		Assert.assertEquals(50, getResultRow(0).get("Pending"));
	}

	private void addColumnsCountryStatusScore(PivotCellMethod method) {
		addColumn("accountCountry", PivotDimension.Row);
		addColumn("accountStatus", PivotDimension.Column);
		addColumn("contractorScore", method);
		
		convertToPivot();
	}

	private JSONObject getResultRow(int index) {
		return (JSONObject) jsonResults.get(index);
	}

	private Column addColumn(String fieldName, PivotCellMethod method) {
		Column column = addColumn(fieldName, PivotDimension.Cell);
		Whitebox.setInternalState(column, "pivotCellMethod", method);
		return column;
	}

	private Column addColumn(String fieldName, PivotDimension dimension) {
		Column column = addColumn(fieldName);
		Whitebox.setInternalState(column, "pivotDimension", dimension);
		return column;
	}

	private Column addColumn(String fieldName) {
		// It would be great to move this over to another class. It seems like a
		// standard utility like we have in EntityFactory
		Column column = new Column(fieldName);
		Field field = availableFields.get(fieldName.toUpperCase());
		column.setField(field);
		columns.add(column);
		return column;
	}

	private void convertToPivot() {
		builder = new ReportPivotBuilder(columns);
		jsonResults = builder.convertToPivot(jsonResults);
	}

	@SuppressWarnings("unchecked")
	private void addDataRow(AccountStatus status, String city) {
		JSONObject row = new JSONObject();
		row.put("accountStatus", status.toString());
		row.put("accountCity", city);
		row.put("accountCountry", "US");
		scoreCounter = scoreCounter + 10;
		row.put("contractorScore", scoreCounter);
		jsonResults.add(row);
	}

}
