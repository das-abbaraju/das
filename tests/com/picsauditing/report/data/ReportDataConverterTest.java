package com.picsauditing.report.data;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.DynaBeanListBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.AccountContractorModel;
import com.picsauditing.search.Database;

public class ReportDataConverterTest {

	@Mock
	Database databaseForTesting;

	Permissions permissions;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		permissions = EntityFactory.makePermission();
		EntityFactory.addUserPermission(permissions, OpPerms.Billing);
	}

	@Test
	public void testConvertQueryResultsToJson_Empty() {
		List<BasicDynaBean> queryResults = new ArrayList<BasicDynaBean>();
		JSONArray json = runJsonConverter(queryResults);
		assertEquals(0, json.size());
	}

	@Test
	public void testConvertQueryResultsToJson_Single() {
		List<BasicDynaBean> queryResults = createAccountQueryList(1);
		JSONArray json = runJsonConverter(queryResults);

		assertEquals(1, json.size());
		String expected = "\\[\\{\"AccountID\":1,\"AccountName\":\"Test 1\",\"AccountCreationDate\":\"1970-01-14 @ 2\\d:56\","
				+ "\"ContractorMembershipDate__Month\":\"janvier\",\"ContractorLastUpgradeDate\":null,\"AccountZip\":\"92614\","
				+ "\"ContractorMembershipDate\":\"1970-01-14\"\\}\\]";

		assertTrue(json.toString().matches(expected));
	}

	@Test
	public void testConvertQueryResultsToJson_Simple() {
		List<BasicDynaBean> queryResults = createAccountQueryList(10);
		JSONArray json = runJsonConverter(queryResults);
		assertEquals(10, json.size());
	}

	@Test
	public void testConvertQueryResultsToPrinting_Single() {
		List<BasicDynaBean> queryResults = createAccountQueryList(1);
		ReportResults results = runConverterForPrinting(queryResults);

		assertEquals(1, results.getRows().size());
		ReportRow row1 = results.getRows().get(0);
		for (ReportCell cell : row1.getCells()) {
			String fieldName = cell.getColumn().getName();
			if ("ContractorMembershipDate".equals(fieldName)) {
				assertEquals(new java.sql.Date(1234567890), cell.getValue());
			}
		}
	}

	public List<Column> makeColumns(Permissions permissions) {
		AccountContractorModel model = new AccountContractorModel(permissions);
		Map<String, Field> availableFields = model.getAvailableFields();

		List<Column> columns = new ArrayList<Column>();
		columns.add(createColumn(availableFields.get("ACCOUNTID")));
		columns.add(createColumn(availableFields.get("ACCOUNTNAME")));
		columns.add(createColumn(availableFields.get("ACCOUNTCREATIONDATE")));
		columns.add(createColumn(availableFields.get("CONTRACTORMEMBERSHIPDATE")));

		Column membershipMonth = createColumn(availableFields.get("CONTRACTORMEMBERSHIPDATE"));
		membershipMonth.setName("ContractorMembershipDate__Month");
		membershipMonth.setSqlFunction(SqlFunction.Month);
		membershipMonth.getField().setUrl("Test.action?id={AccountZip}");

		columns.add(membershipMonth);
		columns.add(createColumn(availableFields.get("CONTRACTORLASTUPGRADEDATE")));

		return columns;
	}

	private Column createColumn(Field field) {
		Column column = new Column(field.getName());
		column.setField(field);
		return column;
	}

	private List<BasicDynaBean> createAccountQueryList(int count) {
		DynaBeanListBuilder builder = new DynaBeanListBuilder("account");
		builder.addProperty("AccountID", Long.class);
		builder.addProperty("AccountName", String.class);
		builder.addProperty("AccountCreationDate", Timestamp.class);
		builder.addProperty("ContractorMembershipDate", java.sql.Date.class);
		builder.addProperty("ContractorMembershipDate__Month", Integer.class);
		builder.addProperty("ContractorLastUpgradeDate", java.sql.Date.class);
		builder.addProperty("AccountZip", String.class);

		for (int i = 0; i < count; i++) {
			builder.addRow();
			long accountID = 1;
			long currentUnitTime = 1234567890;
			if (count > 1) {
				accountID = Math.round(Math.random() * 1000);
				currentUnitTime = new Date().getTime();
			}

			builder.setValue("AccountID", accountID);
			builder.setValue("AccountName", "Test " + accountID);
			builder.setValue("AccountCreationDate", new Timestamp(currentUnitTime));
			builder.setValue("ContractorMembershipDate", new java.sql.Date(currentUnitTime));
			builder.setValue("ContractorMembershipDate__Month", 1);
			builder.setValue("ContractorLastUpgradeDate", null);
			builder.setValue("AccountZip", "92614");
		}

		return builder.getRows();
	}

	private JSONArray runJsonConverter(List<BasicDynaBean> queryResults) {
		List<Column> columns = makeColumns(permissions);

		ReportDataConverter converter = new ReportDataConverter(columns, queryResults);
		converter.setLocale(Locale.FRENCH);
		converter.convertForExtJS(null);
		JSONArray json = converter.getReportResults().toJson();

		return json;
	}

	private ReportResults runConverterForPrinting(List<BasicDynaBean> queryResults) {
		List<Column> columns = makeColumns(permissions);

		ReportDataConverter converter = new ReportDataConverter(columns, queryResults);
		converter.setLocale(Locale.FRENCH);
		converter.convertForPrinting();

		return converter.getReportResults();
	}
}
