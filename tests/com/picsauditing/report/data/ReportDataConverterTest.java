package com.picsauditing.report.data;

import static org.junit.Assert.assertEquals;

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
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.Column;
import com.picsauditing.report.DynaBeanListBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AccountContractorModel;
import com.picsauditing.search.Database;

public class ReportDataConverterTest {
	@Mock
	Database databaseForTesting;

	ReportDataConverter converter;
	List<BasicDynaBean> queryResults;

	private List<Column> columns;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		AccountContractorModel model = new AccountContractorModel();
		Permissions permissions = EntityFactory.makePermission();
		EntityFactory.addUserPermission(permissions, OpPerms.Billing);
		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);
		columns = new ArrayList<Column>();
		columns.add(createColumn(availableFields.get("ACCOUNTID")));
		columns.add(createColumn(availableFields.get("ACCOUNTNAME")));
		columns.add(createColumn(availableFields.get("ACCOUNTCREATIONDATE")));
		columns.add(createColumn(availableFields.get("CONTRACTORMEMBERSHIPDATE")));
		Column membershipMonth = createColumn(availableFields.get("CONTRACTORMEMBERSHIPDATE"));
		membershipMonth.setFieldName("contractorMembershipDate__Month");
		columns.add(membershipMonth);
		columns.add(createColumn(availableFields.get("CONTRACTORLASTUPGRADEDATE")));
	}

	private Column createColumn(Field field) {
		Column column = new Column(field.getName());
		column.setField(field);
		return column;
	}

	@Test
	public void testConvertQueryResultsToJson_Empty() {
		queryResults = new ArrayList<BasicDynaBean>();
		JSONArray json = runConverter();
		assertEquals(0, json.size());
	}

	@Test
	public void testConvertQueryResultsToJson_Single() {
		queryResults = createAccountQueryList(1);
		JSONArray json = runConverter();

		assertEquals(1, json.size());
		String expected = "[{\"accountID\":1,\"accountName\":\"Test 1\",\"accountCreationDate\":1234567890,"
				+ "\"contractorMembershipDate__Month\":\"janvier\",\"contractorMembershipDate\":1234567890}]";
		assertEquals(expected, json.toString());
		System.out.println(json);
	}

	@Test
	public void testConvertQueryResultsToJson_Simple() {
		queryResults = createAccountQueryList(10);

		JSONArray json = runConverter();
		assertEquals(10, json.size());
	}

	private List<BasicDynaBean> createAccountQueryList(int count) {
		DynaBeanListBuilder builder = new DynaBeanListBuilder("account");
		builder.addProperty("accountID", Long.class);
		builder.addProperty("accountName", String.class);
		builder.addProperty("accountCreationDate", Timestamp.class);
		builder.addProperty("contractorMembershipDate", java.sql.Date.class);
		builder.addProperty("contractorMembershipDate__Month", Integer.class);
		builder.addProperty("contractorLastUpgradeDate", java.sql.Date.class);
		for (int i = 0; i < count; i++) {
			builder.addRow();
			long accountID = 1;
			long currentUnitTime = 1234567890;
			if (count > 1) {
				accountID = Math.round(Math.random() * 1000);
				currentUnitTime = new Date().getTime();
			}
			builder.setValue("accountID", accountID);
			builder.setValue("accountName", "Test " + accountID);
			builder.setValue("accountCreationDate", new Timestamp(currentUnitTime));
			builder.setValue("contractorMembershipDate", new java.sql.Date(currentUnitTime));
			builder.setValue("contractorMembershipDate__Month", 1);
			builder.setValue("contractorLastUpgradeDate", null);
		}
		return builder.getRows();
	}

	private JSONArray runConverter() {
		converter = new ReportDataConverter(columns, queryResults);
		converter.setLocale(Locale.FRENCH);
		converter.convertForExtJS();
		JSONArray json = converter.getReportResults().toJson();
		return json;
	}
}
