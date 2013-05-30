package com.picsauditing.actions;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Lists;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggle;

public class ContractorCronTest extends PicsActionTest {

	ContractorCron contractorCron;

	@Mock
	private Database databaseForTesting;
	@Mock
	private FeatureToggle featureToggleChecker;
	@Mock
	private ContractorAccountDAO contractorDAO;
	@Mock
	private BasicDAO dao;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private ContractorAuditDAO contractorAuditDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		contractorCron = new ContractorCron();

		Whitebox.setInternalState(contractorCron, "featureToggleChecker", featureToggleChecker);
		Whitebox.setInternalState(contractorCron, "contractorDAO", contractorDAO);
		Whitebox.setInternalState(contractorCron, "dao", dao);
		Whitebox.setInternalState(contractorCron, "conAuditDAO", contractorAuditDAO);
		Whitebox.setInternalState(contractorCron, "database", databaseForTesting);
	}

	@Test
	public void testSafetyManualSla() throws Exception {
		contractor = EntityFactory.makeContractor();

		ContractorAudit pqf = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
		contractor.getAudits().add(pqf);
		ContractorAuditOperator pqfCao = EntityFactory.addCao(pqf, EntityFactory.makeOperator());
		AuditData data = EntityFactory.makeAuditData("", EntityFactory.makeAuditQuestion());
		data.getQuestion().setId(AuditQuestion.MANUAL_PQF);
		pqf.getData().add(data);

		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.DESKTOP, contractor);
		contractor.getAudits().add(audit);
		ContractorAuditOperator cao = EntityFactory.addCao(audit, EntityFactory.makeOperator());

		// pending balance
		audit.setSlaDate(null);
		contractor.setBalance(new BigDecimal(2));
		pqfCao.changeStatus(AuditStatus.Pending, null);
		cao.changeStatus(AuditStatus.Pending, null);
		data.setAnswer("");
		data.setDateVerified(null);
		Whitebox.invokeMethod(contractorCron, "checkSla", contractor);
		assertTrue(audit.getSlaDate() == null);

		// pqf pending
		audit.setSlaDate(null);
		contractor.setBalance(BigDecimal.ZERO);
		pqfCao.changeStatus(AuditStatus.Pending, null);
		Whitebox.invokeMethod(contractorCron, "checkSla", contractor);
		assertTrue(audit.getSlaDate() == null);

		audit.setSlaDate(null);
		pqfCao.changeStatus(AuditStatus.Complete, null);
		Whitebox.invokeMethod(contractorCron, "checkSla", contractor);
		assertTrue(audit.getSlaDate() == null);

		// safety manual not verified
		audit.setSlaDate(null);
		data.setAnswer("doc");
		Whitebox.invokeMethod(contractorCron, "checkSla", contractor);
		assertTrue(audit.getSlaDate() == null);

		// verified safety manual
		audit.setSlaDate(null);
		data.setDateVerified(new Date());
		Whitebox.invokeMethod(contractorCron, "checkSla", contractor);
		assertTrue(audit.getSlaDate() != null);

		Date targetSlaDate = audit.getSlaDate();
		ContractorAudit previousAudit = EntityFactory.makeContractorAudit(AuditType.DESKTOP, contractor);
		contractor.getAudits().add(previousAudit);
		ContractorAuditOperator previousCao = EntityFactory.addCao(previousAudit, EntityFactory.makeOperator());
		previousCao.changeStatus(AuditStatus.Complete, null);

		//expiring manual audit exist and is earlier
		previousAudit.setExpiresDate(targetSlaDate);
		Whitebox.invokeMethod(contractorCron, "checkSla", contractor);
		assertTrue(audit.getSlaDate() != null && audit.getSlaDate().equals(targetSlaDate));

		// expiring manual audit exists and is later
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(targetSlaDate);
		calendar.add(Calendar.DATE, 60);
		previousAudit.setExpiresDate(calendar.getTime());
		calendar.add(Calendar.DATE, -30);
		targetSlaDate = calendar.getTime();
		Whitebox.invokeMethod(contractorCron, "checkSla", contractor);
		assertTrue(audit.getSlaDate() != null && audit.getSlaDate().equals(targetSlaDate));
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
		when(contractorAuditDAO.findAuditsAboutToExpire(anyInt())).thenReturn(contractorAccount.getAudits());
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
		when(contractorAuditDAO.findAuditsAboutToExpire(anyInt())).thenReturn(contractorAccount.getAudits());
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
		when(contractorAuditDAO.findAuditsAboutToExpire(anyInt())).thenReturn(contractorAccount.getAudits());
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		assertTrue(audits.isEmpty());
	}

	/**
	 * Test to verify that the expired Audit returned in the list is the Policy.
	 */
	@Test
	public void testContractorAccountMultipleAuditsExpiredPolicy() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountMultipleAuditsExpiredPolicy();
		when(contractorAuditDAO.findAuditsAboutToExpire(anyInt())).thenReturn(contractorAccount.getAudits());
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);

		assertEquals(1, audits.size());
		for (ContractorAudit contractorAudit : audits) {
			assertEquals(AuditTypeClass.Policy, contractorAudit.getAuditType().getClassType());
		}
	}

	@Test
	public void testContractorAccountMultipleAuditsExpiredWCB() throws Exception {
		ContractorAccount contractorAccount = setupContractorAccountMultipleAuditsExpiredWCB();
		when(contractorAuditDAO.findAuditsAboutToExpire(anyInt())).thenReturn(contractorAccount.getAudits());
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);

		assertEquals(1, audits.size());
		for (ContractorAudit contractorAudit : audits) {
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
		when(contractorAuditDAO.findAuditsAboutToExpire(anyInt())).thenReturn(contractorAccount.getAudits());
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
		for (ContractorAudit contractorAudit : audits) {
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
		for (ContractorAudit contractorAudit : audits) {
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
		when(contractorAuditDAO.findAuditsAboutToExpire(anyInt())).thenReturn(contractorAccount.getAudits());
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
		contractorCron.setConID(0);
		ContractorCronStep[] steps = new ContractorCronStep[1];
		contractorCron.setSteps(steps);
		contractorCron.execute();

		assertTrue(contractorCron.getActionErrors().size() > 0);
	}

	@Test
	public void testContractorsThatShouldBeCleared() throws Exception {
		ContractorOperator co = EntityFactory.addContractorOperator(contractor, EntityFactory.makeOperator());
		List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
		operators.add(co);
		ContractorCronStep[] steps = new ContractorCronStep[1];
		steps[0] = ContractorCronStep.CorporateRollup;

		when(contractorDAO.find(anyInt())).thenReturn(contractor);
		Whitebox.setInternalState(contractorCron, "conID", 123);
		Whitebox.setInternalState(contractorCron, "steps", steps);

		when(contractor.getOperators()).thenReturn(operators);

		co.setFlagColor(FlagColor.Red);
		co.setBaselineFlag(FlagColor.Red);
		when(contractor.getStatus()).thenReturn(AccountStatus.Requested);
		contractorCron.execute();
		assertTrue(co.getFlagColor().equals(FlagColor.Clear));

		co.setFlagColor(FlagColor.Red);
		co.setBaselineFlag(FlagColor.Red);
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		contractorCron.execute();
		assertTrue(co.getFlagColor().equals(FlagColor.Clear));

		co.setFlagColor(FlagColor.Red);
		co.setBaselineFlag(FlagColor.Red);
		when(contractor.getStatus()).thenReturn(AccountStatus.Declined);
		contractorCron.execute();
		assertTrue(co.getFlagColor().equals(FlagColor.Clear));
	}

    @Test
    public void testRollUpCorporateFlags() throws Exception {
        Map<OperatorAccount, FlagColor> corporateRollupData = new HashMap<OperatorAccount, FlagColor>();
        Queue<OperatorAccount> corporateUpdateQueue = new LinkedList<OperatorAccount>();
        ContractorOperator coOperator = Mockito.mock(ContractorOperator.class);
        OperatorAccount childOperatorWithRedFlag = Mockito.mock(OperatorAccount.class);
        OperatorAccount corporateWithGreenFlag = Mockito.mock(OperatorAccount.class);

        Facility facility = Mockito.mock(Facility.class);
        when(facility.getCorporate()).thenReturn(corporateWithGreenFlag);
        when(facility.getOperator()).thenReturn(childOperatorWithRedFlag);

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);

        when(childOperatorWithRedFlag.getCorporateFacilities()).thenReturn(facilities);
        when(childOperatorWithRedFlag.getStatus()).thenReturn(AccountStatus.Active);

        when(coOperator.getFlagColor()).thenReturn(FlagColor.Red);
        corporateRollupData.put(corporateWithGreenFlag, FlagColor.Green);

        //rollUpCorporateFlags(corporateRollupData, corporateUpdateQueue, coOperator, operator);
        Whitebox.invokeMethod(contractorCron, "rollUpCorporateFlags",
                corporateRollupData, corporateUpdateQueue, coOperator,childOperatorWithRedFlag);

        assertEquals(FlagColor.Red, corporateRollupData.get(corporateWithGreenFlag));
    }

    @Test
    public void testRollUpCorporateFlags_WithDeletedChildAccount() throws Exception {
        Map<OperatorAccount, FlagColor> corporateRollupData = new HashMap<OperatorAccount, FlagColor>();
        Queue<OperatorAccount> corporateUpdateQueue = new LinkedList<OperatorAccount>();
        ContractorOperator coOperator = Mockito.mock(ContractorOperator.class);
        OperatorAccount deletedChildOperatorWithRedFlag = Mockito.mock(OperatorAccount.class);
        OperatorAccount corporateWithGreenFlag = Mockito.mock(OperatorAccount.class);

        Facility facility = Mockito.mock(Facility.class);
        when(facility.getCorporate()).thenReturn(corporateWithGreenFlag);
        when(facility.getOperator()).thenReturn(deletedChildOperatorWithRedFlag);

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);

        when(deletedChildOperatorWithRedFlag.getCorporateFacilities()).thenReturn(facilities);
        when(deletedChildOperatorWithRedFlag.getStatus()).thenReturn(AccountStatus.Deleted);

        when(coOperator.getFlagColor()).thenReturn(FlagColor.Red);
        corporateRollupData.put(corporateWithGreenFlag, FlagColor.Green);

        //rollUpCorporateFlags(corporateRollupData, corporateUpdateQueue, coOperator, operator);
        Whitebox.invokeMethod(contractorCron, "rollUpCorporateFlags",
                corporateRollupData, corporateUpdateQueue, coOperator,deletedChildOperatorWithRedFlag);

        assertEquals(FlagColor.Green, corporateRollupData.get(corporateWithGreenFlag));

	    corporateRollupData.put(corporateWithGreenFlag, FlagColor.Green);
	    when(deletedChildOperatorWithRedFlag.getStatus()).thenReturn(AccountStatus.Demo);
	    assertEquals(FlagColor.Green, corporateRollupData.get(corporateWithGreenFlag));
    }
}
