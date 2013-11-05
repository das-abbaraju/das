package com.picsauditing.report.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.FieldImportance;
import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Column;
import static org.mockito.Mockito.when;

public class ReportDataConverterTest extends PicsTranslationTest {

    TimeZone defaultTimezone = TimeZone.getTimeZone("America/Chicago");

	Permissions permissions;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);


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
		List<BasicDynaBean> queryResults = DynaBeanBuilder.createAccountQueryList(1);
        when(translationService.getText("Country.CA",Locale.FRENCH)).thenReturn("Canada");

		JSONArray json = runJsonConverter(queryResults);

		assertEquals(1, json.size());
		String expected = "[{\"ContractorTrades\":\"1, 2, 3, 4, 5\",\"AccountCountry\":\"Canada\",\"AccountID\":1,\"AccountName\":\"Test 1\",\"AccountCreationDate\":\"1970-01-14 @ 22:56\",\"ContractorMembershipDate__Month\":\"janvier\",\"ContractorLastUpgradeDate\":null,\"AccountZip\":\"92614\",\"ContractorMembershipDate\":\"1970-01-14\"}]";

        assertEquals(expected, json.toString());
	}

	@Test
	public void testConvertQueryResultsToJson_Simple() {
		List<BasicDynaBean> queryResults = DynaBeanBuilder.createAccountQueryList(10);
		JSONArray json = runJsonConverter(queryResults);
		assertEquals(10, json.size());
	}

    @Test
    public void testConvertQueryResultsToJson_TradesList() {
        List<BasicDynaBean> queryResults = DynaBeanBuilder.createAccountQueryList(1);
        when(translationService.getText("Country.CA",Locale.FRENCH)).thenReturn("Canada");
        when(translationService.getText("Trade.1.name",Locale.FRENCH)).thenReturn("a");
        when(translationService.getText("Trade.2.name",Locale.FRENCH)).thenReturn("b");
        when(translationService.getText("Trade.3.name",Locale.FRENCH)).thenReturn("c");
        when(translationService.getText("Trade.4.name",Locale.FRENCH)).thenReturn("d");
        when(translationService.getText("Trade.5.name",Locale.FRENCH)).thenReturn("e");

        List<Column> columns = DynaBeanBuilder.makeColumns(permissions);

        ReportResults reportResults = ReportResultsFromDynaBean.build(columns, queryResults);
        ReportDataConverter converter = new ReportDataConverterForExtJS(reportResults);
        converter.setLocale(Locale.FRENCH);
        converter.convert(defaultTimezone);
        JSONArray json = reportResults.toJson();

        assertEquals(1, json.size());
        String expected = "[{\"ContractorTrades\":\"a, b, c, d, e\",\"AccountCountry\":\"Canada\",\"AccountID\":1,\"AccountName\":\"Test 1\",\"AccountCreationDate\":\"1970-01-14 @ 22:56\",\"ContractorMembershipDate__Month\":\"janvier\",\"ContractorLastUpgradeDate\":null,\"AccountZip\":\"92614\",\"ContractorMembershipDate\":\"1970-01-14\"}]";
        String jsonString = json.toString();

        assertEquals(expected, jsonString);
    }

	@Test
	public void testConvertQueryResultsToPrinting_Single() {
		List<BasicDynaBean> queryResults = DynaBeanBuilder.createAccountQueryList(1);
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

	private JSONArray runJsonConverter(List<BasicDynaBean> queryResults) {
		List<Column> columns = DynaBeanBuilder.makeColumns(permissions);
        columns.get(7).getField().setSeparator(null);

        ReportResults reportResults = ReportResultsFromDynaBean.build(columns, queryResults);
        ReportDataConverter converter = new ReportDataConverterForExtJS(reportResults);
		converter.setLocale(Locale.FRENCH);
		converter.convert(defaultTimezone);
		JSONArray json = reportResults.toJson();

		return json;
	}

	private ReportResults runConverterForPrinting(List<BasicDynaBean> queryResults) {
		List<Column> columns = DynaBeanBuilder.makeColumns(permissions);

        ReportResults reportResults = ReportResultsFromDynaBean.build(columns, queryResults);
		ReportDataConverter converter = new ReportDataConverterForPrinting(reportResults);
		converter.setLocale(Locale.FRENCH);
        converter.convert(defaultTimezone);

		return reportResults;
	}
}
