package com.picsauditing.flagcalculator.entities;

import com.picsauditing.flagcalculator.EntityFactory;
import com.picsauditing.flagcalculator.service.FlagService;
import com.picsauditing.flagcalculator.util.YearList;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * The OshaOrganizer is a construct for dealing with multiple OshaAudits
 * together. So, this unit test class deals with test cases that span audits
 * (e.g. getting a 3-year average). See OshaAudirTest for test cases that deal
 * with only one OshaAudit.
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
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		for (int i = 0; i < 4; i++) {
			now.add(Calendar.YEAR, -1);
			audits.add(EntityFactory.makeAnnualUpdate(11, contractor, format.format(now.getTime())));
		}

		int trir = 2;
		for (ContractorAudit audit : audits) {
			audit.setCategories(new ArrayList<AuditCatData>());
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_OSHA_PARENT));
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_UK_HSE_PARENT));
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

		oshaOrganizer = FlagService.getOshaOrganizer(contractor);

	}

	private void setupAudits_TwoYearsOsha(ContractorAccount contractor) {
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		contractor.setAudits(audits);
		Calendar date = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		date.add(Calendar.YEAR, -1);
		audits.add(EntityFactory.makeAnnualUpdate(11, contractor, format.format(date.getTime())));
		date.add(Calendar.YEAR, -2);
		audits.add(EntityFactory.makeAnnualUpdate(11, contractor, format.format(date.getTime())));

		int trir = 2;
		for (ContractorAudit audit : audits) {
			audit.setCategories(new ArrayList<AuditCatData>());
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_OSHA_PARENT));
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_UK_HSE_PARENT));
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

		oshaOrganizer = FlagService.getOshaOrganizer(contractor);
	}

	
	
	private void setupAudits_InvalidNumericTrirAnswers(ContractorAccount contractor) {
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		contractor.setAudits(audits);
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		for (int i = 0; i < 4; i++) {
			now.add(Calendar.YEAR, -1);
			audits.add(EntityFactory.makeAnnualUpdate(11, contractor, format.format(now.getTime())));
		}

		int trir = 2;
		for (ContractorAudit audit : audits) {
			audit.setCategories(new ArrayList<AuditCatData>());
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_OSHA_PARENT));
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_UK_HSE_PARENT));
			audit.getCategories().add(
					EntityFactory.addCategories(audit, OshaAudit.CAT_ID_OSHA));
			audit.getCategories()
					.add(
							EntityFactory.addCategories(audit,
									OshaAudit.CAT_ID_UK_HSE));

			AuditData trirData = EntityFactory.makeAuditData("blah",
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

		oshaOrganizer = FlagService.getOshaOrganizer(contractor);

	}

	
	
	
	
	@Test
	public void testOshaOrganizerInitialization() {
		setupAudits_FourYearsOshaAndUk(contractor);
		assertTrue(oshaOrganizer.size() > 0);
	}

	@Test
	public void testTRIR_OneYearAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		double rate = oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute);
		assertThat(rate, is(2d));
	}

	@Test
	public void testTRIR_TwoYearsAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		double rate = oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.TwoYearsAgo, OshaRateType.TrirAbsolute);
		assertThat(rate, is(3d)); 
	}

	@Test
	public void testTRIR_ThreeYearsAgo() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		double rate = oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearsAgo, OshaRateType.TrirAbsolute);
		assertThat(rate, is(4d));
	}

	@Test
	public void testTRIR_ThreeYearAvg() throws Exception {
		setupAudits_FourYearsOshaAndUk(contractor);
		double rate = oshaOrganizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearAverage, OshaRateType.TrirAbsolute);
		assertThat(rate, is(3d));
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

		assertEquals(lastYear(),years.getYearForScope(MultiYearScope.LastYearOnly).intValue());
		assertEquals(twoYearsAgo(),years.getYearForScope(MultiYearScope.TwoYearsAgo).intValue());
		assertEquals(threeYearsAgo(),years.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
	}

	@Test
	public void testMostRecentThreeYears_FromTwoYearsOfData() throws Exception {
		setupAudits_TwoYearsOsha(contractor);
		YearList years = oshaOrganizer.mostRecentThreeYears(OshaType.OSHA);
		assertEquals(2, years.size());
		assertEquals(lastYear(),years.getYearForScope(MultiYearScope.LastYearOnly).intValue());
		assertEquals(threeYearsAgo(),years.getYearForScope(MultiYearScope.ThreeYearsAgo).intValue());
	}

	@Test
	public void testIsVerified_ThreeYearAverage_False() {
		setupAudits_TwoYearsOsha(contractor);
		
		oshaOrganizer.safetyStatisticsData.get(OshaType.OSHA).get(lastYear()).setVerified(true);
		oshaOrganizer.safetyStatisticsData.get(OshaType.OSHA).get(threeYearsAgo()).setVerified(false);
		assertFalse(oshaOrganizer.isVerified(OshaType.OSHA, MultiYearScope.ThreeYearAverage));
	}

	private int lastYear() {
		Calendar date = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		date.add(Calendar.YEAR, -1);
		int lastYear = new Integer(format.format(date.getTime())).intValue();
		return lastYear;
	}
	
	private int twoYearsAgo() {
		Calendar date = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		date.add(Calendar.YEAR, -2);
		int lastYear = new Integer(format.format(date.getTime())).intValue();
		return lastYear;
	}

	private int threeYearsAgo() {
		Calendar date = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		date.add(Calendar.YEAR, -3);
		int lastYear = new Integer(format.format(date.getTime())).intValue();
		return lastYear;
	}

	@Test
	public void testIsVerified_ThreeYearAverage_True() {
		setupAudits_TwoYearsOsha(contractor);
		oshaOrganizer.safetyStatisticsData.get(OshaType.OSHA).get(lastYear()).setVerified(true);
		oshaOrganizer.safetyStatisticsData.get(OshaType.OSHA).get(threeYearsAgo()).setVerified(true);
		assertTrue(oshaOrganizer.isVerified(OshaType.OSHA, MultiYearScope.ThreeYearAverage));
	}

	@Test
	public void testIsVerified_SpecificYear_False() {
		setupAudits_TwoYearsOsha(contractor);
		oshaOrganizer.safetyStatisticsData.get(OshaType.OSHA).get(threeYearsAgo()).setVerified(false);
		assertFalse(oshaOrganizer.isVerified(OshaType.OSHA, MultiYearScope.ThreeYearsAgo));
	}

	@Test
	public void testIsVerified_SpecificYear_True() {
		setupAudits_TwoYearsOsha(contractor);
		oshaOrganizer.safetyStatisticsData.get(OshaType.OSHA).get(lastYear()).setVerified(true);
		assertTrue(oshaOrganizer.isVerified(OshaType.OSHA, MultiYearScope.LastYearOnly));
	}
	@Test
	public void testGetRateForSpecificYear_ValidNumber() throws Exception {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.YEAR, -1);
		int lastYear = now.get(Calendar.YEAR);

		setupAudits_FourYearsOshaAndUk(contractor);
        BigDecimal rate = Whitebox.invokeMethod(oshaOrganizer, "getRateForSpecificYear", OshaType.OSHA, lastYear, OshaRateType.TrirAbsolute);
		assertThat(rate.doubleValue(), is(2d));
	}
	@Test
	public void testGetRateForSpecificYear_InvalidNumber() throws Exception {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.YEAR, -1);
		int lastYear = now.get(Calendar.YEAR);

		setupAudits_InvalidNumericTrirAnswers(contractor);
		
		BigDecimal rate = Whitebox.invokeMethod(oshaOrganizer, "getRateForSpecificYear", OshaType.OSHA, lastYear, OshaRateType.TrirAbsolute);
		assertNull(rate);
	}
}