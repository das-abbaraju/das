package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

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
public class OshaOrganizerTest {

	OshaOrganizer oshaOrganizer;
	private ContractorAccount contractor;

	@Before
	public void setUp() throws Exception {
		contractor = new ContractorAccount();
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
			
			List<ContractorAuditOperator> caos = new ArrayList<ContractorAuditOperator>();
			caos.add(EntityFactory.makeContractorAuditOperator(audit));
			audit.setOperators(caos);
		}
		oshaOrganizer = contractor.getOshaOrganizer();

	}

	private void setupAudits_TwoYearsOsha(ContractorAccount contractor) {
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		contractor.setAudits(audits);
		audits.add(EntityFactory.makeAnnualUpdate(11, contractor, "2010"));
		audits.add(EntityFactory.makeAnnualUpdate(11, contractor, "2008"));

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
			
			List<ContractorAuditOperator> caos = new ArrayList<ContractorAuditOperator>();
			caos.add(EntityFactory.makeContractorAuditOperator(audit));
			audit.setOperators(caos);
		}
		oshaOrganizer = contractor.getOshaOrganizer();
	}
	@Test
	public void testOshaOrganizerInitialization() {
		
		setupAudits_FourYearsOshaAndUk(contractor);
		assertTrue(oshaOrganizer.size() > 0);
	}

	@Test
	public void testTRIR_OneYearAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertTrue(2 == oshaOrganizer.getRate(OshaType.OSHA,
				MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute));
	}

	@Test	
	public void testTRIR_TwoYearsAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertTrue(3 == oshaOrganizer.getRate(OshaType.OSHA,
				MultiYearScope.TwoYearsAgo, OshaRateType.TrirAbsolute));
	}

	@Test		
	public void testTRIR_ThreeYearsAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertTrue(4 == oshaOrganizer.getRate(OshaType.OSHA,
				MultiYearScope.ThreeYearsAgo, OshaRateType.TrirAbsolute));
	}

	@Test		
	public void testTRIR_ThreeYearAvg() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertTrue(3 == oshaOrganizer.getRate(OshaType.OSHA,
				MultiYearScope.ThreeYearAverage, OshaRateType.TrirAbsolute));
	}

	@Test	
	public void testGetOshaAudit(){
		setupAudits_FourYearsOshaAndUk(contractor);
	
		assertNotNull(oshaOrganizer.getStatistic(OshaType.OSHA, MultiYearScope.LastYearOnly));
		assertNotNull(oshaOrganizer.getStatistic(OshaType.OSHA, MultiYearScope.TwoYearsAgo));
		assertNotNull(oshaOrganizer.getStatistic(OshaType.OSHA, MultiYearScope.ThreeYearsAgo));
		
		assertNotNull(oshaOrganizer.getStatistic(OshaType.UK_HSE, MultiYearScope.LastYearOnly));
		assertNotNull(oshaOrganizer.getStatistic(OshaType.UK_HSE, MultiYearScope.TwoYearsAgo));
		assertNotNull(oshaOrganizer.getStatistic(OshaType.UK_HSE, MultiYearScope.ThreeYearsAgo));
	}
	@Test
	public void testMostRecentThreeYears_FromFourYearsOfData() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		YearList years = oshaOrganizer.mostRecentThreeYears(OshaType.OSHA);
		assertEquals(3, years.size());
		
		assertEquals(2008,years.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
		assertEquals(2009,years.getYearForScope(MultiYearScope.TwoYearsAgo).intValue());
		assertEquals(2010,years.getYearForScope(MultiYearScope.LastYearOnly).intValue());
	}
	
	@Test
	public void testMostRecentThreeYears_FromTwoYearsOfData() throws Exception {
		setupAudits_TwoYearsOsha(contractor);
		YearList years = oshaOrganizer.mostRecentThreeYears(OshaType.OSHA);
		assertEquals(2, years.size());
		assertEquals(2008,years.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
		assertEquals(2010,years.getYearForScope(MultiYearScope.LastYearOnly).intValue());
	}
}