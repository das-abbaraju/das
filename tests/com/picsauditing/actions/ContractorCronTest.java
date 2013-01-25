package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Lists;
import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.LcCorPhase;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggle;

public class ContractorCronTest {
	
	@Mock private Database databaseForTesting;
	@Mock private FeatureToggle featureToggleChecker;
	@Mock private ContractorAccountDAO contractorDAO;
	@Mock private BasicDAO dao; 

	ContractorCron contractorCron;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		contractorCron = new ContractorCron();
		Whitebox.setInternalState(contractorCron, "featureToggleChecker", featureToggleChecker);
		Whitebox.setInternalState(contractorCron, "contractorDAO", contractorDAO);
		Whitebox.setInternalState(contractorCron, "dao", dao);
	}
	
	/**
	 * More Test Cases to be added.
	 * 
	 * Multiple WCB Types, one with overlapping where the overlapping one has a status of Pending,
	 * another where the overlapping one has a status of Approved, another where there isn't one
	 * for this year (none generated for the current year, so no need to add it to the list).
	 */
	
	/**
	 * Test that no WCBs are added to the list if all the WCBs are NOT about to
	 * expire and they all have a status of Submitted further in the workflow.
	 */
	@Test
	public void testContractorAccountOnlyWCBs() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountWithOnlyWCBs();		
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * The reason for this test is that getExpiringPolicies() only check for expiring
	 * WCBs or Policies.
	 */
	@Test
	public void testContractorAccountNoPoliciesOrWCBs() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountNoPoliciesOrWCBs();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Since the Audit (Policy) expired outside the range we are checking for, the list
	 * returned should be empty.
	 */
	@Test
	public void testContractorAccountPolicyExpiredTwoMonthsAgo() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountPolicyExpiredTwoMonthsAgo();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Since the Audit (WCB) expired outside the range we are checking for, the list
	 * returned should be empty.
	 */
	@Test
	public void testContractorAccountWCBExpiredTwoMonthsAgo() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountWCBExpiredTwoMonthsAgo();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Test to verify that the expired Audit returned in the list is the Policy.
	 */
	@Test
	public void testContractorAccountMultipleAuditsExpiredPolicy() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountMultipleAuditsExpiredPolicy();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);

		assertEquals(1, audits.size());
		for (Iterator<ContractorAudit> iterator = audits.iterator(); iterator.hasNext();) {
			ContractorAudit contractorAudit = iterator.next();
			assertEquals(AuditTypeClass.Policy, contractorAudit.getAuditType().getClassType());
		}
	}
	
	@Test
	public void testContractorAccountMultipleAuditsExpiredWCB() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountMultipleAuditsExpiredWCB();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);

		assertEquals(1, audits.size());
		for (Iterator<ContractorAudit> iterator = audits.iterator(); iterator.hasNext();) {
			ContractorAudit contractorAudit = iterator.next();
			assertTrue(contractorAudit.getAuditType().isWCB());
		}
	}
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Submitted or greater and is for the current year, no audit should be in the list.
	 */
	@Test
	public void testContractorAccountOverlappingValidWCBs() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountWCBsForDifferentYears();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Incomplete, then it should be in the expiring audit list.
	 */
	@Ignore("Not ready to run yet.")
	@Test
	public void testContractorAccountOverlappingWCBsIncomplete() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountWCBsForDifferentYearsIncomplete();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		
		assertEquals(1, audits.size());
		for (Iterator<ContractorAudit> iterator = audits.iterator(); iterator.hasNext();) {
			ContractorAudit contractorAudit = iterator.next();
			assertEquals(DateBean.addField(new Date(), Calendar.DATE, -2), contractorAudit.getExpiresDate());
		}
	}	
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Pending, then it should be in the expiring audit list.
	 */
	@Ignore("Not ready to run yet.")
	@Test
	public void testContractorAccountOverlappingWCBsPending() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountWCBsForDifferentYearsPending();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);

		assertEquals(2, audits.size());
		for (Iterator<ContractorAudit> iterator = audits.iterator(); iterator.hasNext();) {
			ContractorAudit contractorAudit = iterator.next();
			assertEquals(DateBean.addField(new Date(), Calendar.DAY_OF_YEAR, -2), contractorAudit.getExpiresDate());
		}
	}
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Pending, then it should be in the expiring audit list.
	 */
	@Test
	public void testContractorAccountOverlappingWCBsComplete() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountWCBsForDifferentYearsComplete();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		assertTrue(audits.isEmpty());
	}

	private ContractorAccount setupContractorAccountWCBExpiredTwoMonthsAgo() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(DateBean.addMonths(new Date(), -2))
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountPolicyExpiredTwoMonthsAgo() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(DateBean.addMonths(new Date(), -2))
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountNoPoliciesOrWCBs() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.auditTypeClass(AuditTypeClass.PQF)
									.auditStatus(AuditStatus.Pending)
									.expiresDate(DateBean.addMonths(new Date(), 3))
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWithOnlyWCBs() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		// WCB IDs = 143, 170, 261
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(DateBean.addMonths(new Date(), 3))
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(170)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addMonths(new Date(), 3))
					.build();
		audits.add(audit);
		
		audit = new MockAuditBuilder()
					.id(261)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Approved)
					.expiresDate(DateBean.addMonths(new Date(), 3))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountMultipleAuditsExpiredPolicy() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(DateBean.addMonths(new Date(), 3))
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addDays(new Date(), -2))
					.build();
		audits.add(audit);
		
		audit = new MockAuditBuilder()
					.id(261)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Approved)
					.expiresDate(DateBean.addMonths(new Date(), 3))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountMultipleAuditsExpiredWCB() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(DateBean.addDays(new Date(), -2))
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addMonths(new Date(), 3))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWCBsForDifferentYears() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = DateBean.addDays(new Date(), -2);
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(twoDaysAgo)
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(143)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWCBsForDifferentYearsIncomplete() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = DateBean.addDays(new Date(), -2);
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(twoDaysAgo)
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(143)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Incomplete)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWCBsForDifferentYearsPending() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = DateBean.addDays(new Date(), -2);
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(twoDaysAgo)
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(143)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Pending)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		audit = new MockAuditBuilder()
					.id(170)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Approved)
					.expiresDate(twoDaysAgo)
					.build();
		audits.add(audit);		

		audit = new MockAuditBuilder()
					.id(170)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Pending)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);

		contractorAccount.setAudits(audits);		
		
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWCBsForDifferentYearsComplete() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = DateBean.addDays(new Date(), -2);
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Complete)
									.expiresDate(twoDaysAgo)
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(143)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private static class MockAuditBuilder {
		
		private int id;
		private Date expiresDate;
		private AuditStatus auditStatus;
		private AuditTypeClass auditTypeClass;
		
		public MockAuditBuilder id(int id) {
			this.id = id;
			return this;
		}
		
		public MockAuditBuilder expiresDate(Date expiresDate) {
			this.expiresDate = expiresDate;
			return this;
		}
		
		public MockAuditBuilder auditStatus(AuditStatus auditStatus) {
			this.auditStatus = auditStatus;
			return this;
		}
		
		public MockAuditBuilder auditTypeClass(AuditTypeClass auditTypeClass) {
			this.auditTypeClass = auditTypeClass;
			return this;
		}
		
		@SuppressWarnings("deprecation")
		public ContractorAudit build() {
			ContractorAudit audit = new ContractorAudit();
		
			audit.setExpiresDate(expiresDate);
			ContractorAuditOperator cao = new ContractorAuditOperator();
			cao.setStatus(auditStatus);
			audit.setOperators(Lists.newArrayList(cao));
			
			AuditType auditType = new AuditType(id);
			auditType.setClassType(auditTypeClass);
			
			audit.setAuditType(auditType);
			
			return audit;
		}
	}

	@Test
	public void testCheckLcCor_NoCorAudit() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(anyString()))
				.thenReturn(true);
		ContractorAccount contractor = createContractorForLcCorTest(null, null,
				false, null);
		Whitebox.invokeMethod(contractorCron, "checkLcCor", contractor);
		assertTrue(contractor.getLcCorPhase() == null);
	}

	@Test
	public void testCheckLcCor_CorNoExpiration() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(anyString()))
				.thenReturn(true);
		ContractorAccount contractor = createContractorForLcCorTest(null, null,
				true, null);
		Whitebox.invokeMethod(contractorCron, "checkLcCor", contractor);
		assertTrue(contractor.getLcCorPhase() == null);
	}

	@Ignore
	@Test
	public void testCheckLcCor_CorEpirationBeyond4Months() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(anyString()))
				.thenReturn(true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 121);
		ContractorAccount contractor = createContractorForLcCorTest(null, null,
				true, cal.getTime());
		Whitebox.invokeMethod(contractorCron, "checkLcCor", contractor);
		assertTrue(contractor.getLcCorPhase() == null);
	}

	@Ignore
	@Test
	public void testCheckLcCor_CorWithin4MonthsNoPhase() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(anyString()))
				.thenReturn(true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 120);
		ContractorAccount contractor = createContractorForLcCorTest(null, null,
				true, cal.getTime());
		Whitebox.invokeMethod(contractorCron, "checkLcCor", contractor);
		assertTrue(contractor.getLcCorPhase() == LcCorPhase.RemindMeLaterAudit);
	}

	@Ignore
	@Test
	public void testCheckLcCor_CorWithin4MonthsNonAuditPhase() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(anyString()))
				.thenReturn(true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 120);
		ContractorAccount contractor = createContractorForLcCorTest(
				LcCorPhase.RemindMeLater, null, true, cal.getTime());
		Whitebox.invokeMethod(contractorCron, "checkLcCor", contractor);
		assertTrue(contractor.getLcCorPhase() == LcCorPhase.RemindMeLaterAudit);
	}

	@Test
	public void testCheckLcCor_CorWithin4MonthsAuditPhase() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(anyString()))
				.thenReturn(true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 120);
		ContractorAccount contractor = createContractorForLcCorTest(
				LcCorPhase.RemindMeLaterAudit, null, true, cal.getTime());
		Whitebox.invokeMethod(contractorCron, "checkLcCor", contractor);
		assertTrue(contractor.getLcCorPhase() == LcCorPhase.RemindMeLaterAudit);
	}

	@Ignore
	@Test
	public void testCheckLcCor_CorWithin4MonthsDoneBeforeCorExpires() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(anyString()))
				.thenReturn(true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 120);
		ContractorAccount contractor = createContractorForLcCorTest(
				LcCorPhase.Done, new Date(), true, cal.getTime());
		Whitebox.invokeMethod(contractorCron, "checkLcCor", contractor);
		assertTrue(contractor.getLcCorPhase() == LcCorPhase.RemindMeLaterAudit);
	}

	@Test
	public void testCheckLcCor_CorWithin4MonthsDoneAfterCorExpires() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(anyString()))
				.thenReturn(true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 120);
		Date corExpires = cal.getTime();
		cal.add(Calendar.DATE, 1);
		Date notificationDate = cal.getTime();
		ContractorAccount contractor = createContractorForLcCorTest(
				LcCorPhase.Done, notificationDate, true, corExpires);
		Whitebox.invokeMethod(contractorCron, "checkLcCor", contractor);
		assertTrue(contractor.getLcCorPhase() == LcCorPhase.Done);
	}

	private ContractorAccount createContractorForLcCorTest(LcCorPhase phase,
			Date notificationDate, boolean createCorAudit, Date corEpireDate) {
		ContractorAccount contractor = EntityFactory.makeContractor();
		contractor.setLcCorPhase(phase);
		contractor.setLcCorNotification(notificationDate);

		if (createCorAudit) {
			ContractorAudit audit = EntityFactory.makeContractorAudit(
					AuditType.COR, contractor);
			audit.setExpiresDate(corEpireDate);
			contractor.getAudits().add(audit);
		}

		return contractor;
	}

	@Test
	public void testCancelScheduledImplementationAudits() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.OFFICE, contractor);
		ContractorAuditOperator cao = EntityFactory.addCao(audit, EntityFactory.makeOperator());
		cao.setVisible(false);
		
		audit.setScheduledDate(new Date());
		contractor.getAudits().add(audit);
		
		Whitebox.invokeMethod(contractorCron, "cancelScheduledImplementationAudits", contractor);
		assertTrue(audit.getScheduledDate() == null);
	
	}
	
	@Test
	public void testContractorNotFound() throws Exception {
		Whitebox.invokeMethod(contractorCron, "run", 0, 0);
		assertTrue(contractorCron.getActionErrors().size() > 0);
	}
}
