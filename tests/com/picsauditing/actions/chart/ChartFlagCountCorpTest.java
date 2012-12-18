package com.picsauditing.actions.chart;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.chart.DataRow;

public class ChartFlagCountCorpTest extends PicsActionTest {
	private ChartFlagCountCorp chartFlagCountCorp;

	@Mock
	private OperatorAccountDAO operatorAccountDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		chartFlagCountCorp = new ChartFlagCountCorp();
		super.setUp(chartFlagCountCorp);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(chartFlagCountCorp, this);
	}

	@Test
	public void testExtractOperatorIdFromLink() throws Exception {
		String link = "ReportContractorOperatorFlag.action?button=Search%26filter.flagStatus=Red%26filter.operator=37813";

		Integer opId = Whitebox.invokeMethod(chartFlagCountCorp, "extractOperatorIdFromLink", link);

		assertThat(37813, is(equalTo(opId.intValue())));
	}

	@Test
	public void testReplaceLinksExpandCorporateToOperatorChildren_DoesNotNeedExpansion() throws Exception {
		List<DataRow> data = new ArrayList<DataRow>();
		String link = "ReportContractorOperatorFlag.action?button=Search%26filter.flagStatus=Red%26filter.operator=123";
		DataRow row = new DataRow();
		row.setLink(link);
		data.add(row);
		OperatorAccount operator = setupNotCorporateOperator(123);
		when(operatorAccountDAO.find(123)).thenReturn(operator);

		Whitebox.invokeMethod(chartFlagCountCorp, "replaceLinksExpandCorporateToOperatorChildren", data);

		String newLink = row.getLink();

		assertEquals(link, newLink);
	}

	@Test
	public void testReplaceLinksExpandCorporateToOperatorChildren_NeedsExpansion() throws Exception {
		List<DataRow> data = new ArrayList<DataRow>();
		String link = "ReportContractorOperatorFlag.action?button=Search%26filter.flagStatus=Red%26filter.operator=37813";
		DataRow row = new DataRow();
		row.setLink(link);
		data.add(row);
		OperatorAccount operator = setupEmeaCorporate();
		when(operatorAccountDAO.find(37813)).thenReturn(operator);

		Whitebox.invokeMethod(chartFlagCountCorp, "replaceLinksExpandCorporateToOperatorChildren", data);

		String newLink = row.getLink();

		assertEquals(
				"ReportContractorOperatorFlag.action?button=Search%26filter.flagStatus=Red%26filter.operator=37822%26filter.operator=37823%26filter.operator=37824%26filter.operator=37825",
				newLink);
	}

	@Test
	public void testChildrenOperatorsFromCorporate_NotCorporate() throws Exception {
		OperatorAccount operator = setupNotCorporateOperator(123);

		List<Integer> opIds = new ArrayList<Integer>();
		Whitebox.invokeMethod(chartFlagCountCorp, "childrenOperatorsFromCorporate", operator, opIds);

		assertTrue(opIds.contains(123));
	}

	@Test
	public void testChildrenOperatorsFromCorporate_OneLevel() throws Exception {
		OperatorAccount holcimUkCorporate = setupUKCorporate();

		List<Integer> opIds = new ArrayList<Integer>();
		Whitebox.invokeMethod(chartFlagCountCorp, "childrenOperatorsFromCorporate", holcimUkCorporate, opIds);

		assertFalse(opIds.contains(37816));
		assertTrue(opIds.contains(37822));
		assertTrue(opIds.contains(37823));
	}

	@Test
	public void testChildrenOperatorsFromCorporate_TwoLevels() throws Exception {
		OperatorAccount holcimEmeaCorporate = setupEmeaCorporate();

		List<Integer> opIds = new ArrayList<Integer>();
		Whitebox.invokeMethod(chartFlagCountCorp, "childrenOperatorsFromCorporate", holcimEmeaCorporate, opIds);

		assertFalse(opIds.contains(37813));
		assertFalse(opIds.contains(37816));
		assertTrue(opIds.contains(37822));
		assertTrue(opIds.contains(37823));
		assertTrue(opIds.contains(37824));
		assertTrue(opIds.contains(37825));
	}

	private OperatorAccount setupEmeaCorporate() {
		OperatorAccount holcimUkCorporate = setupUKCorporate();
		OperatorAccount holcimGermanyCorporate = setupGermanyCorporate();

		OperatorAccount holcimEmeaCorporate = mock(OperatorAccount.class);
		List<OperatorAccount> childOperators = new ArrayList<OperatorAccount>();
		childOperators.add(holcimUkCorporate);
		childOperators.add(holcimGermanyCorporate);
		when(holcimEmeaCorporate.getChildOperators()).thenReturn(childOperators);
		when(holcimEmeaCorporate.getId()).thenReturn(37813);

		return holcimEmeaCorporate;
	}

	private OperatorAccount setupUKCorporate() {
		OperatorAccount holcimUkSite1 = mock(OperatorAccount.class);
		when(holcimUkSite1.getId()).thenReturn(37822);
		when(holcimUkSite1.getChildOperators()).thenReturn(new ArrayList<OperatorAccount>());
		OperatorAccount holcimUkSite2 = mock(OperatorAccount.class);
		when(holcimUkSite2.getId()).thenReturn(37823);
		when(holcimUkSite2.getChildOperators()).thenReturn(new ArrayList<OperatorAccount>());

		OperatorAccount holcimUkCorporate = mock(OperatorAccount.class);
		List<OperatorAccount> childOperators = new ArrayList<OperatorAccount>();
		childOperators.add(holcimUkSite1);
		childOperators.add(holcimUkSite2);
		when(holcimUkCorporate.getChildOperators()).thenReturn(childOperators);
		when(holcimUkCorporate.getId()).thenReturn(37816);

		return holcimUkCorporate;
	}

	private OperatorAccount setupGermanyCorporate() {
		OperatorAccount holcimGermanySite1 = mock(OperatorAccount.class);
		when(holcimGermanySite1.getId()).thenReturn(37824);
		when(holcimGermanySite1.getChildOperators()).thenReturn(new ArrayList<OperatorAccount>());
		OperatorAccount holcimGermanySite2 = mock(OperatorAccount.class);
		when(holcimGermanySite2.getId()).thenReturn(37825);
		when(holcimGermanySite2.getChildOperators()).thenReturn(new ArrayList<OperatorAccount>());

		OperatorAccount holcimUkCorporate = mock(OperatorAccount.class);
		List<OperatorAccount> childOperators = new ArrayList<OperatorAccount>();
		childOperators.add(holcimGermanySite1);
		childOperators.add(holcimGermanySite2);
		when(holcimUkCorporate.getChildOperators()).thenReturn(childOperators);
		when(holcimUkCorporate.getId()).thenReturn(37817);

		return holcimUkCorporate;
	}

	private OperatorAccount setupNotCorporateOperator(int id) {
		OperatorAccount operator = mock(OperatorAccount.class);
		List<OperatorAccount> childOperators = new ArrayList<OperatorAccount>();
		when(operator.getChildOperators()).thenReturn(childOperators);
		when(operator.getId()).thenReturn(id);

		return operator;
	}

	/*
	id,type,name,parentID
	32670,Corporate,"Holcim Group Support",NULL
	37790,Operator,"Holcim Site 1",NULL
	37791,Operator,"Holcim Site 2",NULL
	37792,Operator,"Holcim Site 3",NULL
	37810,Corporate,"Holcim Group Support Corporate",NULL
	37811,Corporate,"Holcim Americas Corporate",37810
	37813,Corporate,"Holcim EMEA Corporate",37810
	37814,Corporate,"Holcim AsiaPac Corporate",37810
	37815,Corporate,"Holcim US Corporate",37811
	37816,Corporate,"Holcim UK Corporate",37813
	37817,Corporate,"Holcim Germany Corporate",37813
	37818,Corporate,"Holcim India Corporate",37814
	37819,Corporate,"Holcim China Corporate",37814
	37820,Operator,"Holcim US Site 1",37815
	37821,Operator,"Holcim US Site 2",37815
	37822,Operator,"Holcim UK Site 1",37816
	37823,Operator,"Holcim UK Site 2",37816
	37824,Operator,"Holcim Germany Site 1",37817
	37825,Operator,"Holcim Germany Site 2",37817
	37826,Operator,"Holcim India Site 1",37818
	37827,Operator,"Holcim India Site 2",37818
	37828,Operator,"Holcim China Site 1",37819
	37829,Operator,"Holcim China Site 2",37819
	38101,Corporate,"Holcim France Corporate",37813
	38102,Operator,"Holcim France Site 1",38101
	38103,Operator,"Holcim France Site 2",38101

	 */
}
