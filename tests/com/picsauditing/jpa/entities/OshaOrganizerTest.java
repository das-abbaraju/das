package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.util.YearList;

/**
 * The OshaOrganizer is a construct for dealing with multiple OshaAudits
 * together. So, this unit test class deals with test cases that span audits
 * (e.g. getting a 3-year average). See OshaAudirTest for test cases that deal
 * with only one OshaAudit.
 * 
 */
public class OshaOrganizerTest extends TestCase {

	OshaOrganizer oshaOrganizer;
	private ContractorAccount contractor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contractor = new ContractorAccount();
		

		// SafetyStatistics osha2010 =
		// oshaOrganizer.getStatitistics(OshaType.OSHA,
		// MultiYearScope.LastYearOnly);
		// SafetyStatistics osha2009 =
		// oshaOrganizer.getStatitistics(OshaType.OSHA,
		// MultiYearScope.TwoYearsAgo);
		// SafetyStatistics osha2008 =
		// oshaOrganizer.getStatitistics(OshaType.OSHA,
		// MultiYearScope.ThreeYearsAgo);

	}

	private void setupAudits_FourYearsOshaAndUk(ContractorAccount contractor) {
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		contractor.setAudits(audits);
		audits.add(EntityFactory.makeAnnualUpdate(11, contractor, "2010"));
		audits.add(EntityFactory.makeAnnualUpdate(11, contractor, "2009"));
		audits.add(EntityFactory.makeAnnualUpdate(11, contractor, "2008"));
		audits.add(EntityFactory.makeAnnualUpdate(11, contractor, "2007"));

		int trir = 2;
		for (ContractorAudit audit : audits) {
			audit.setCategories(new ArrayList<AuditCatData>());
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_OSHA));
			audit.getCategories()
					.add(
							EntityFactory.addCategories(audit,
									OshaAudit.CAT_ID_UK_HSE));

			AuditData trirData = EntityFactory.makeAuditData(String
					.valueOf(trir++),
					OshaStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR);
			audit.getData().add(trirData);

			AuditData hourData = EntityFactory.makeAuditData(String
					.valueOf(12000),
					OshaStatistics.QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR);
			audit.getData().add(hourData);
		}
		oshaOrganizer = contractor.getOshaOrganizer();

	}

	private void setupAudits_TwoYearsOsha(ContractorAccount contractor) {
		List<OshaAudit> oshaAudits = new ArrayList<OshaAudit>();
		contractor.setOshaAudits(oshaAudits);
		oshaAudits.add(EntityFactory.makeOshaAudit(contractor, "2010"));
		oshaAudits.add(EntityFactory.makeOshaAudit(contractor, "2008"));
		

		int trir = 2;
		for (OshaAudit audit : oshaAudits) {
			audit.setCategories(new ArrayList<AuditCatData>());
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_OSHA));

			AuditData trirData = EntityFactory.makeAuditData(String
					.valueOf(trir++),
					OshaStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR);
			audit.getData().add(trirData);

			AuditData hourData = EntityFactory.makeAuditData(String
					.valueOf(12000),
					OshaStatistics.QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR);
			audit.getData().add(hourData);
		}
		oshaOrganizer = contractor.getOshaOrganizer();

	}
	
	public void testOshaOrganizerInitialization() {
		setupAudits_FourYearsOshaAndUk(contractor);
		//assertTrue(oshaOrganizer.getOshaAudits().size() > 0);
	}
	
	public void testTRIR_OneYearAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertEquals(2.0f, oshaOrganizer.getRate(OshaType.OSHA,
				MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute));
	}
	
	public void testTRIR_TwoYearsAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertEquals(3.0f, oshaOrganizer.getRate(OshaType.OSHA,
				MultiYearScope.TwoYearsAgo, OshaRateType.TrirAbsolute));
	}
	
	public void testTRIR_ThreeYearsAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertEquals(4.0f, oshaOrganizer.getRate(OshaType.OSHA,
				MultiYearScope.ThreeYearsAgo, OshaRateType.TrirAbsolute));
	}
	
	public void testTRIR_ThreeYearAvg() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertEquals(3.0f, oshaOrganizer.getRate(OshaType.OSHA,
				MultiYearScope.ThreeYearAverage, OshaRateType.TrirAbsolute));
	}
	public void testGetOshaAudit(){
		setupAudits_FourYearsOshaAndUk(contractor);
		assertNotNull(oshaOrganizer.getStatistic(OshaType.UK_HSE, MultiYearScope.LastYearOnly));
		assertNotNull(oshaOrganizer.getStatistic(OshaType.UK_HSE, MultiYearScope.TwoYearsAgo));
		assertNotNull(oshaOrganizer.getStatistic(OshaType.UK_HSE, MultiYearScope.ThreeYearsAgo));
		
		assertNotNull(oshaOrganizer.getStatistic(OshaType.OSHA, MultiYearScope.LastYearOnly));
		assertNotNull(oshaOrganizer.getStatistic(OshaType.OSHA, MultiYearScope.TwoYearsAgo));
		assertNotNull(oshaOrganizer.getStatistic(OshaType.OSHA, MultiYearScope.ThreeYearsAgo));
	}
	public void testMostRecentThreeYears_FromFourYearsOfData() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		YearList years = oshaOrganizer.mostRecentThreeYears(OshaType.OSHA);
		assertEquals(3, years.size());
		assertEquals(2008,years.get(0).intValue());
		assertEquals(2009,years.get(1).intValue());
		assertEquals(2010,years.get(2).intValue());
	}
	
	public void testMostRecentThreeYears_FromTwoYearsOfData() throws Exception {
		setupAudits_TwoYearsOsha(contractor);
		YearList years = oshaOrganizer.mostRecentThreeYears(OshaType.OSHA);
		assertEquals(2, years.size());
		assertEquals(2008,years.get(0).intValue());
		assertEquals(2010,years.get(1).intValue());
	}
	
	public void testToDashboard() {
		
	}
}