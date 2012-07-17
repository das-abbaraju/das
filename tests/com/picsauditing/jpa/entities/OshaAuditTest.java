package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;


public class OshaAuditTest extends PicsTest {
	
	private static final int CAT_ID_SOMETHING_OTHER_THAN_OSHA = 155;

	private static final int QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR = 8978;
	private static final int QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR = 8977;
	private static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 8812;
	
	ContractorAccount contractor;
	OshaAudit oshaAudit2010;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		contractor = EntityFactory.makeContractor();
		oshaAudit2010 = EntityFactory.makeOshaAudit(contractor, "2010");
	}

	private void setupAudit_USandUK() {
		oshaAudit2010.getCategories().add(EntityFactory.addCategories(oshaAudit2010.getContractorAudit(), OshaAudit.CAT_ID_OSHA));
		oshaAudit2010.getCategories().add(EntityFactory.addCategories(oshaAudit2010.getContractorAudit(), CAT_ID_SOMETHING_OTHER_THAN_OSHA));
		oshaAudit2010.getCategories().add(EntityFactory.addCategories(oshaAudit2010.getContractorAudit(), OshaAudit.CAT_ID_UK_HSE));
		
		AuditData trir2010Data = EntityFactory.makeAuditData("4.3",QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR);
		oshaAudit2010.getData().add(trir2010Data);
		
		AuditData lwcr2010Data = EntityFactory.makeAuditData("5",QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR);
		oshaAudit2010.getData().add(lwcr2010Data);
		
		AuditData fatalities2010Data = EntityFactory.makeAuditData("0",QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR);
		oshaAudit2010.getData().add(fatalities2010Data);
		
		AuditData hours2010Data = EntityFactory.makeAuditData("120000",8810);
		oshaAudit2010.getData().add(hours2010Data);
		
		AuditData daysAwayCases2010Data = EntityFactory.makeAuditData("1",8813);
		oshaAudit2010.getData().add(daysAwayCases2010Data);
		
		AuditData daysAway2010Data = EntityFactory.makeAuditData("10",8814);
		oshaAudit2010.getData().add(daysAway2010Data);
		
		AuditData jobTransfersCases2010Data = EntityFactory.makeAuditData("2",8815);
		oshaAudit2010.getData().add(jobTransfersCases2010Data);
		
		AuditData jobTransfersDays2010Data = EntityFactory.makeAuditData("20",8816);
		oshaAudit2010.getData().add(jobTransfersDays2010Data);		

		AuditData totalRecordables2010Data = EntityFactory.makeAuditData("7",8817);
		oshaAudit2010.getData().add(totalRecordables2010Data);
		
		AuditData ifr2010Data = EntityFactory.makeAuditData("103.7",9060);
		oshaAudit2010.getData().add(ifr2010Data);
		
		oshaAudit2010.initializeStatistics();
	}	

	private void setupAudit_Canada() {
		//oshaAudit2010.getCategories().add(EntityFactory.addCategories(oshaAudit2010, OshaAudit.CAT_ID_COHS));
		
		// TODO Need Canadian questions to be defined
		// AuditData ??? = EntityFactory.makeAuditData("4.3",QUESTION_ID_???);
		// oshaAudit2010.getData().add(???);
	}	

	@Test
	public void testIsEmpty() {
		setupAudit_USandUK();
		assertTrue(oshaAudit2010.isEmpty(OshaType.OSHA));
	}
	
	@Test
	public void testOshaAuditInitialization() {
		setupAudit_USandUK();
		assertEquals("2010", oshaAudit2010.getAuditFor());
	}
	
	@Test
	public void testStatisticsInitialization() {
		setupAudit_USandUK();
		// Should be 2, i.e. not counting the CAT_ID_SOMETHING_OTHER_THAN_OSHA
		assertEquals(2, oshaAudit2010.getStatistics().size());
	}
	
	@Test
	public void testGetTrir(){
		setupAudit_USandUK();
		assertEquals("4.3",oshaAudit2010.getSafetyStatistics(OshaType.OSHA).getStats(OshaRateType.TrirAbsolute));
	}
	
	@Test
	public void testOshaToDashboard(){
		//setupAudit_USandUK();
		//assertEquals("TRIR: 4.3, LWCR: 5, Fatalities: 0, Hours Worked: 120000",oshaAudit2010.getSafetyStatistics(OshaType.OSHA).toDashboard());
	}
	
	@Test
	public void testGetAllOshaValues(){
		setupAudit_USandUK();
		// @TODO Change these constants to use an ENUM (e.g. an expanded version of OshaRateType)
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
	public void testUKStatsInitilazation(){
		setupAudit_USandUK();
		assertNotNull(oshaAudit2010.getSafetyStatistics(OshaType.UK_HSE));
	}
	
	@Test
	public void testGetIFR(){
		setupAudit_USandUK();
		assertEquals("103.7",oshaAudit2010.getSafetyStatistics(OshaType.UK_HSE).getStats(OshaRateType.IFR));
	}

}
