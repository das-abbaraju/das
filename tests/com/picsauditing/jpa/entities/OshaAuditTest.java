package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.picsauditing.EntityFactory;

public class OshaAuditTest {

	private static final int CAT_ID_SOMETHING_OTHER_THAN_OSHA = 155;

	private static final int QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR = 8978;
	private static final int QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR = 8977;
	private static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 8812;

	ContractorAudit contractorAudit;
	OshaAudit oshaAudit2010;

	@Before
	public void setUp() throws Exception {
		buildFakeContractorAudit();
		setupAudit_USandUK();
		oshaAudit2010 = new OshaAudit(contractorAudit);
	}

	private void buildFakeContractorAudit() {
		AuditType auditType = new AuditType();
		auditType.setId(AuditType.ANNUALADDENDUM);

		contractorAudit = new ContractorAudit();
		contractorAudit.setAuditType(auditType);
		contractorAudit.setAuditFor("2010");
	}

	private void setupAudit_USandUK() throws Exception {
		List<AuditCatData> auditCategories = new ArrayList<AuditCatData>();
		auditCategories.add(setupMockAuditCatData(contractorAudit, OshaAudit.CAT_ID_OSHA));
		auditCategories.add(setupMockAuditCatData(contractorAudit,  CAT_ID_SOMETHING_OTHER_THAN_OSHA));
		auditCategories.add(setupMockAuditCatData(contractorAudit, OshaAudit.CAT_ID_UK_HSE));
		auditCategories.add(setupMockAuditCatData(contractorAudit, OshaAudit.CAT_ID_OSHA_PARENT));
		auditCategories.add(setupMockAuditCatData(contractorAudit,  OshaAudit.CAT_ID_UK_HSE_PARENT));

		contractorAudit.setCategories(auditCategories);

		List<AuditData> auditDataList = new ArrayList<AuditData>();

		AuditData trir2010Data = EntityFactory.makeAuditData("4.3", QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR);
		auditDataList.add(trir2010Data);

		AuditData lwcr2010Data = EntityFactory.makeAuditData("5", QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR);
		auditDataList.add(lwcr2010Data);

		AuditData fatalities2010Data = EntityFactory.makeAuditData("0", QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR);
		auditDataList.add(fatalities2010Data);

		AuditData hours2010Data = EntityFactory.makeAuditData("120000", 8810);
		auditDataList.add(hours2010Data);

		AuditData daysAwayCases2010Data = EntityFactory.makeAuditData("1", 8813);
		auditDataList.add(daysAwayCases2010Data);

		AuditData daysAway2010Data = EntityFactory.makeAuditData("10", 8814);
		auditDataList.add(daysAway2010Data);

		AuditData jobTransfersCases2010Data = EntityFactory.makeAuditData("2", 8815);
		auditDataList.add(jobTransfersCases2010Data);

		AuditData jobTransfersDays2010Data = EntityFactory.makeAuditData("20", 8816);
		auditDataList.add(jobTransfersDays2010Data);

		AuditData totalRecordables2010Data = EntityFactory.makeAuditData("7", 8817);
		auditDataList.add(totalRecordables2010Data);

		AuditData ifr2010Data = EntityFactory.makeAuditData("103.7", 9060);
		auditDataList.add(ifr2010Data);

		contractorAudit.setData(auditDataList);
	}

	@Test
	public void testConvertCategoryToOshaType_OshaIsOsha() {
		OshaType type = OshaAudit.convertCategoryToOshaType(OshaAudit.CAT_ID_OSHA);

		assertEquals(OshaType.OSHA, type);
	}

	@Test
	public void testConvertCategoryToOshaType_OshaAdditionalIsOsha() {
		OshaType type = OshaAudit.convertCategoryToOshaType(OshaAudit.CAT_ID_OSHA_ADDITIONAL);

		assertEquals(OshaType.OSHA, type);
	}

	@Test
	public void testConvertCategoryToOshaType_OshaAdditionalDoesntReturnNull() {
		OshaType type = OshaAudit.convertCategoryToOshaType(OshaAudit.CAT_ID_OSHA_ADDITIONAL);

		assertNotNull(type);
	}

	@Test
	public void testConvertCategoryToOshaType_MshaIsMsha() {
		OshaType type = OshaAudit.convertCategoryToOshaType(OshaAudit.CAT_ID_MSHA);

		assertEquals(OshaType.MSHA, type);
	}

	@Test
	public void testConvertCategoryToOshaType_CohsIsCohs() {
		OshaType type = OshaAudit.convertCategoryToOshaType(OshaAudit.CAT_ID_COHS);

		assertEquals(OshaType.COHS, type);
	}

	@Test
	public void testConvertCategoryToOshaType_UkHseIsUkHse() {
		OshaType type = OshaAudit.convertCategoryToOshaType(OshaAudit.CAT_ID_UK_HSE);

		assertEquals(OshaType.UK_HSE, type);
	}

	@Test
	public void testConvertCategoryToOshaType_FranceNrisIsFranceNris() {
		OshaType type = OshaAudit.convertCategoryToOshaType(OshaAudit.CAT_ID_FRANCE_NRIS);

		assertEquals(OshaType.FRANCE_NRIS, type);
	}

	@Test
	public void testNotApplicableOshaIsVerified() throws Exception {
		OshaAudit emptyAudit;

		AuditType auditType = new AuditType();
		auditType.setId(AuditType.ANNUALADDENDUM);

		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setAuditType(auditType);
		contractorAudit.setAuditFor("2010");

		AuditCategory oshaParentCat = new AuditCategory();
		AuditCategory oshaDataCat = new AuditCategory();

		oshaParentCat.setId(OshaAudit.CAT_ID_OSHA_PARENT);
		oshaDataCat.setId(OshaAudit.CAT_ID_OSHA);

		List<AuditCatData> auditCategories = new ArrayList<AuditCatData>();
		AuditCatData mockAuditCategoryData = Mockito.mock(AuditCatData.class);
		when(mockAuditCategoryData.getCategory()).thenReturn(oshaParentCat);
		when(mockAuditCategoryData.getAudit()).thenReturn(contractorAudit);
		when(mockAuditCategoryData.getNumRequired()).thenReturn(4);
		when(mockAuditCategoryData.isApplies()).thenReturn(true);
		auditCategories.add(mockAuditCategoryData);

		mockAuditCategoryData = Mockito.mock(AuditCatData.class);
		when(mockAuditCategoryData.getCategory()).thenReturn(oshaDataCat);
		when(mockAuditCategoryData.getAudit()).thenReturn(contractorAudit);
		when(mockAuditCategoryData.getNumRequired()).thenReturn(4);
		when(mockAuditCategoryData.isApplies()).thenReturn(false);
		auditCategories.add(mockAuditCategoryData);
		contractorAudit.setCategories(auditCategories);

		List<AuditData> auditDataList = new ArrayList<AuditData>();
		AuditData keptOsha = EntityFactory.makeAuditData("No", 2064);
		auditDataList.add(keptOsha);
		contractorAudit.setData(auditDataList);

		ContractorAuditOperator cao = EntityFactory.addCao(contractorAudit, EntityFactory.makeOperator());
		cao.setStatus(AuditStatus.Complete);

		emptyAudit = new OshaAudit(contractorAudit);

		assertTrue(emptyAudit.isVerified());
	}

	@Test
	public void testOshaAuditInitialization() throws Exception {
		assertEquals("2010", oshaAudit2010.getAuditFor());
	}

	/**
	 * Should be 2, i.e. not counting the CAT_ID_SOMETHING_OTHER_THAN_OSHA
	 *
	 * @throws Exception
	 */
	@Test
	public void testStatisticsInitialization() throws Exception {
		assertEquals(2, oshaAudit2010.getStatistics().size());
	}

	@Test
	public void testGetTrir() throws Exception {
		assertEquals("4.3", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.TrirAbsolute));
	}

	@Test
	public void testGetAllOshaValues() throws Exception {
		// TODO Change these constants to use an ENUM (e.g. an expanded version of OshaRateType)
		assertEquals("1", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.DaysAwayCases));
		assertEquals("10", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.DaysAway));
		assertEquals("2", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.JobTransfersCases));
		assertEquals("20", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.JobTransferDays));
		assertEquals("7", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.OtherRecordables));
		assertEquals("4.3",oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.TrirAbsolute));
		assertEquals("5", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.LwcrAbsolute));
		assertEquals("0", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.Fatalities));
		assertEquals("120000", oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.Hours));
	}

	@Test
	public void testUKStatsInitilazation() throws Exception {
		assertNotNull(oshaAudit2010.getSafetyStatistics(OshaType.UK_HSE));
	}

	@Test
	public void testGetIFR() throws Exception {
		assertEquals("103.7", oshaAudit2010.getSafetyStatistics(OshaType.UK_HSE).getStats(OshaRateType.IFR));
	}

	private AuditCatData setupMockAuditCatData(ContractorAudit contractorAudit, int categoryId) {
		AuditCategory mockAuditCategory = Mockito.mock(AuditCategory.class);
		when(mockAuditCategory.getId()).thenReturn(categoryId);

		AuditCatData mockAuditCategoryData = Mockito.mock(AuditCatData.class);
		when(mockAuditCategoryData.getCategory()).thenReturn(mockAuditCategory);
		when(mockAuditCategoryData.getAudit()).thenReturn(contractorAudit);
		when(mockAuditCategoryData.getNumRequired()).thenReturn(4);
		when(mockAuditCategoryData.isApplies()).thenReturn(true);

		return mockAuditCategoryData;
	}

}