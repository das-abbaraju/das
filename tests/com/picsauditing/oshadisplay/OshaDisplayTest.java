package com.picsauditing.oshadisplay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.YearList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "OshaDisplayTest-context.xml" })
public class OshaDisplayTest extends PicsTranslationTest {
	private OshaDisplay oshaDisplay;

	@Autowired
	private NaicsDAO springNaicsDao;

	@Mock
	private OshaOrganizer oshaOrganizer;
	@Mock
	private NaicsDAO naicsDao;
	@Mock
	private YearList yearList;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private OperatorAccount operator;
	@Mock
	private OperatorAccount canadianOperator;
	@Mock
	private FlagCriteriaOperator fco;
	@Mock
	private FlagCriteriaOperator canadianFco;
	@Mock
	private FlagCriteria flagCriteria;
	@Mock
	private FlagCriteria canadianFlagCriteria;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
		ContractorOperator conOp = new ContractorOperator();
		conOp.setContractorAccount(contractor);
		conOp.setOperatorAccount(operator);
		contractorOperators.add(conOp);
		conOp = new ContractorOperator();
		conOp.setContractorAccount(contractor);
		conOp.setOperatorAccount(canadianOperator);
		contractorOperators.add(conOp);

		oshaDisplay = new OshaDisplay(oshaOrganizer, Locale.US, contractorOperators, contractor, naicsDao);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(oshaDisplay, this);
	}

	@Test
	public void testIsEquivalentRateTypes_NullFlagCriteriaRateTypeIsFalse() throws Exception {
		Boolean result = Whitebox.invokeMethod(oshaDisplay, "isEquivalentRateTypes", OshaType.OSHA,
				(OshaRateType) null, OshaRateType.EMR);
		assertFalse(result);
	}

	@Test
	public void testIsEquivalentRateTypes_EqualIsTrue() throws Exception {
		Boolean result = Whitebox.invokeMethod(oshaDisplay, "isEquivalentRateTypes", OshaType.EMR, OshaRateType.EMR,
				OshaRateType.EMR);
		assertTrue(result);
	}

	@Test
	public void testIsEquivalentRateTypes_NotEqualIsTrue() throws Exception {
		Boolean result = Whitebox.invokeMethod(oshaDisplay, "isEquivalentRateTypes", OshaType.OSHA,
				OshaRateType.Fatalities, OshaRateType.EMR);
		assertFalse(result);
	}

	@Test
	public void testIsEquivalentRateTypes_TrirTypesAreEquivalent() throws Exception {
		Boolean result = Whitebox.invokeMethod(oshaDisplay, "isEquivalentRateTypes", OshaType.OSHA,
				OshaRateType.TrirAbsolute, OshaRateType.TrirNaics);
		assertTrue(result);
	}

	@Test
	public void testIsEquivalentRateTypes_DartTypesAreEquivalent() throws Exception {
		Boolean result = Whitebox.invokeMethod(oshaDisplay, "isEquivalentRateTypes", OshaType.OSHA, OshaRateType.Dart,
				OshaRateType.DartNaics);
		assertTrue(result);
	}

	@Test
	public void testGetStats_TRIRIndustryAverage() throws Exception {
		Naics contractorNaics = new Naics();
		contractorNaics.setCode("81");
		Trade trade = new Trade();
		trade.setNaicsTRIR(1.2f);
		ContractorTrade topTrade = new ContractorTrade();
		topTrade.setTrade(trade);
		when(contractor.getTopTrade()).thenReturn(topTrade);
		when(contractor.getNaics()).thenReturn(contractorNaics);
		when(contractor.getWeightedIndustryAverage()).thenReturn(1.4f);
		when(operator.getInheritFlagCriteria()).thenReturn(operator);
		when(canadianOperator.getInheritFlagCriteria()).thenReturn(canadianOperator);
		when(operator.getOshaType()).thenReturn(OshaType.OSHA);
		when(canadianOperator.getOshaType()).thenReturn(OshaType.COHS);
		List<FlagCriteriaOperator> fcoList = new ArrayList<FlagCriteriaOperator>();
		fcoList.add(fco);
		when(operator.getFlagCriteriaInherited()).thenReturn(fcoList);
		List<FlagCriteriaOperator> canadianFcoList = new ArrayList<FlagCriteriaOperator>();
		canadianFcoList.add(canadianFco);
		when(canadianOperator.getFlagCriteriaInherited()).thenReturn(canadianFcoList);
		when(fco.getCriteria()).thenReturn(flagCriteria);
		when(flagCriteria.getOshaType()).thenReturn(OshaType.OSHA);
		when(flagCriteria.getOshaRateType()).thenReturn(OshaRateType.TrirAbsolute);
		when(flagCriteria.getMultiYearScope()).thenReturn(MultiYearScope.LastYearOnly);
		when(canadianFco.getCriteria()).thenReturn(canadianFlagCriteria);
		when(canadianFlagCriteria.getOshaType()).thenReturn(OshaType.COHS);
		when(canadianFlagCriteria.getOshaRateType()).thenReturn(OshaRateType.TrirAbsolute);
		when(canadianFlagCriteria.getMultiYearScope()).thenReturn(MultiYearScope.LastYearOnly);

		when(springNaicsDao.getIndustryAverage(eq(true), (Naics) any())).thenReturn(0.2f);
		when(springNaicsDao.getIndustryAverage(eq(false), (Naics) any())).thenReturn(1.2f);

		when(yearList.getYearForScope(MultiYearScope.LastYearOnly)).thenReturn(2011);
		when(yearList.getYearForScope(MultiYearScope.TwoYearsAgo)).thenReturn(2010);
		when(yearList.getYearForScope(MultiYearScope.ThreeYearsAgo)).thenReturn(2009);

		when(oshaOrganizer.getOutOfScopeYear(OshaType.OSHA)).thenReturn(null);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.LastYearOnly, OshaRateType.TrirNaics)).thenReturn(0.4);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.TwoYearsAgo, OshaRateType.TrirNaics)).thenReturn(0.3);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearsAgo, OshaRateType.TrirNaics))
				.thenReturn(0.2);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearAverage, OshaRateType.TrirNaics)).thenReturn(
				0.3);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearSum, OshaRateType.TrirNaics)).thenReturn(0.9);

		when(oshaOrganizer.getOutOfScopeYear(OshaType.COHS)).thenReturn(null);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute)).thenReturn(
				0.4);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.TwoYearsAgo, OshaRateType.TrirAbsolute)).thenReturn(
				0.3);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.ThreeYearsAgo, OshaRateType.TrirAbsolute)).thenReturn(
				0.2);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.ThreeYearAverage, OshaRateType.TrirAbsolute))
				.thenReturn(0.3);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.ThreeYearSum, OshaRateType.TrirAbsolute)).thenReturn(
				0.9);

		PicsTestUtil.forceSetPrivateField(oshaDisplay, "yearList", yearList);

		List<OshaDisplayRow> rows;

		rows = Whitebox.invokeMethod(oshaDisplay, "getData", OshaType.OSHA);
		for (OshaDisplayRow row : rows) {
			if ("TrirAbsolute".equals(row.getTitle())) {
				assertEquals("1.2", row.getCells().get(4));
			} else if ("LwcrAbsolute".equals(row.getTitle())) {
				assertEquals("4.0", row.getCells().get(4));
			}
		}

		rows = Whitebox.invokeMethod(oshaDisplay, "getData", OshaType.COHS);
		for (OshaDisplayRow row : rows) {
			if ("TrirAbsolute".equals(row.getTitle())) {
				assertTrue(row.getCells().get(4).startsWith("1.4"));
			}
		}
	}

	@Test
	public void testGetStats_DartIndustryAverage() throws Exception {
		Naics contractorNaics = new Naics();
		contractorNaics.setCode("81");
		when(contractor.getNaics()).thenReturn(contractorNaics);
		when(Utilities.getDartIndustryAverage(contractorNaics)).thenReturn(1.3f);
		when(operator.getInheritFlagCriteria()).thenReturn(operator);
		when(canadianOperator.getInheritFlagCriteria()).thenReturn(canadianOperator);
		when(operator.getOshaType()).thenReturn(OshaType.OSHA);
		when(canadianOperator.getOshaType()).thenReturn(OshaType.COHS);
		List<FlagCriteriaOperator> fcoList = new ArrayList<FlagCriteriaOperator>();
		fcoList.add(fco);
		when(operator.getFlagCriteriaInherited()).thenReturn(fcoList);
		List<FlagCriteriaOperator> canadianFcoList = new ArrayList<FlagCriteriaOperator>();
		canadianFcoList.add(canadianFco);
		when(canadianOperator.getFlagCriteriaInherited()).thenReturn(canadianFcoList);
		when(fco.getCriteria()).thenReturn(flagCriteria);
		when(flagCriteria.getOshaType()).thenReturn(OshaType.OSHA);
		when(flagCriteria.getOshaRateType()).thenReturn(OshaRateType.DartNaics);
		when(flagCriteria.getMultiYearScope()).thenReturn(MultiYearScope.LastYearOnly);
		when(canadianFco.getCriteria()).thenReturn(canadianFlagCriteria);
		when(canadianFlagCriteria.getOshaType()).thenReturn(OshaType.COHS);
		when(canadianFlagCriteria.getOshaRateType()).thenReturn(OshaRateType.DartNaics);
		when(canadianFlagCriteria.getMultiYearScope()).thenReturn(MultiYearScope.LastYearOnly);

		when(springNaicsDao.getDartIndustryAverage((Naics) any())).thenReturn(1.3f);

		when(yearList.getYearForScope(MultiYearScope.LastYearOnly)).thenReturn(2011);
		when(yearList.getYearForScope(MultiYearScope.TwoYearsAgo)).thenReturn(2010);
		when(yearList.getYearForScope(MultiYearScope.ThreeYearsAgo)).thenReturn(2009);

		when(oshaOrganizer.getOutOfScopeYear(OshaType.OSHA)).thenReturn(null);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.LastYearOnly, OshaRateType.DartNaics)).thenReturn(0.4);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.TwoYearsAgo, OshaRateType.DartNaics)).thenReturn(0.3);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearsAgo, OshaRateType.DartNaics))
				.thenReturn(0.2);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearAverage, OshaRateType.DartNaics)).thenReturn(
				0.3);
		when(oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearSum, OshaRateType.DartNaics)).thenReturn(0.9);

		when(oshaOrganizer.getOutOfScopeYear(OshaType.COHS)).thenReturn(null);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.LastYearOnly, OshaRateType.Dart)).thenReturn(0.4);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.TwoYearsAgo, OshaRateType.Dart)).thenReturn(0.3);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.ThreeYearsAgo, OshaRateType.Dart)).thenReturn(0.2);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.ThreeYearAverage, OshaRateType.Dart)).thenReturn(0.3);
		when(oshaOrganizer.getRate(OshaType.COHS, MultiYearScope.ThreeYearSum, OshaRateType.Dart)).thenReturn(0.9);

		PicsTestUtil.forceSetPrivateField(oshaDisplay, "yearList", yearList);

		List<OshaDisplayRow> rows;

		rows = Whitebox.invokeMethod(oshaDisplay, "getData", OshaType.OSHA);
		for (OshaDisplayRow row : rows) {
			if ("Dart".equals(row.getTitle())) {
				assertEquals("1.3", row.getCells().get(4));
			}
		}

		rows = Whitebox.invokeMethod(oshaDisplay, "getData", OshaType.COHS);
		for (OshaDisplayRow row : rows) {
			if ("Dart".equals(row.getTitle())) {
				assertTrue(row.getCells().get(4).startsWith("1.3"));
			}
		}
	}
}
