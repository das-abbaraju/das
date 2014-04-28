package com.picsauditing.actions;

import com.google.common.collect.Lists;
import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.messaging.MessagePublisherService;
import com.picsauditing.messaging.Publisher;
import com.picsauditing.search.Database;
import com.picsauditing.service.employeeGuard.EmployeeGuardRulesService;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.togglz.junit.TogglzRule;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private ContractorAudit contractorAudit;
	@Mock
	private ContractorAuditDAO contractorAuditDAO;
	@Mock
	private ContractorOperator contractorOperator;
	@Mock
	private OperatorAccount operator;
	@Mock
	private FlagDataOverride flagOverride;
	@Mock
	private FlagCriteria criteria;
	@Mock
	private OshaAudit oshaAudit;
    @Mock
    private FlagDataCalculator flagDataCalculator;
    @Mock
    private Publisher flagChangePublisher;
    @Mock
    private UserAssignmentDAO userAssignmentDAO;
    @Mock
    private MessagePublisherService messageService;
    @Mock
    private EmployeeGuardRulesService employeeGuardRulesService;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allEnabled(Features.class);

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		contractorCron = new ContractorCron();

		Whitebox.setInternalState(contractorCron, "featureToggleChecker", featureToggleChecker);
		Whitebox.setInternalState(contractorCron, "contractorDAO", contractorDAO);
		Whitebox.setInternalState(contractorCron, "dao", dao);
		Whitebox.setInternalState(contractorCron, "conAuditDAO", contractorAuditDAO);
		Whitebox.setInternalState(contractorCron, "database", databaseForTesting);
        Whitebox.setInternalState(contractorCron, "userAssignmentDAO", userAssignmentDAO);
        Whitebox.setInternalState(contractorCron, "messageService", messageService);
        Whitebox.setInternalState(contractorCron, "employeeGuardRulesService", employeeGuardRulesService);

        when(messageService.getFlagChangePublisher()).thenReturn(flagChangePublisher);
	}

    @Test
    public void testRunWaitingOn_Operator_No() throws Exception {
        setupStep(ContractorCronStep.WaitingOn);
        Whitebox.setInternalState(contractorCron, "flagDataCalculator", flagDataCalculator);
        ContractorAccount contractor = EntityFactory.makeContractor();
        OperatorAccount operator = EntityFactory.makeOperator();
        ContractorOperator co = EntityFactory.addContractorOperator(contractor, operator);

        co.setWorkStatus(ApprovalStatus.N);
        co.setWaitingOn(WaitingOn.Contractor);

        when(flagDataCalculator.calculateWaitingOn(co)).thenReturn(WaitingOn.Operator);

        Whitebox.invokeMethod(contractorCron, "runWaitingOn", co);
        assertTrue(co.getWorkStatus().isNo());
        assertTrue(co.getWaitingOn().equals(WaitingOn.Contractor));

    }

    @Test
    public void testRunWaitingOn_Contractor_Yes() throws Exception {
        setupStep(ContractorCronStep.WaitingOn);
        Whitebox.setInternalState(contractorCron, "flagDataCalculator", flagDataCalculator);
        ContractorAccount contractor = EntityFactory.makeContractor();
        OperatorAccount operator = EntityFactory.makeOperator();
        ContractorOperator co = EntityFactory.addContractorOperator(contractor, operator);

        co.setWorkStatus(ApprovalStatus.Y);
        co.setWaitingOn(WaitingOn.Contractor);

        when(flagDataCalculator.calculateWaitingOn(co)).thenReturn(WaitingOn.Contractor);

        Whitebox.invokeMethod(contractorCron, "runWaitingOn", co);
        assertTrue(co.getWorkStatus().isYes());
        assertTrue(co.getWaitingOn().equals(WaitingOn.Contractor));

    }

    private void setupStep(ContractorCronStep step) {
        ContractorCronStep[] steps = new ContractorCronStep[1];
        steps[0] = step;
        Whitebox.setInternalState(contractorCron, "steps", steps);

    }

    @Test
    public void testRunAssignAudit_ManualAudit() throws Exception {
        ContractorCronStep[] steps = new ContractorCronStep[1];
        steps[0] = ContractorCronStep.AssignAudit;
        Whitebox.setInternalState(contractorCron, "steps", steps);

        List<ContractorAudit> audits = new ArrayList<>();
        when(contractor.getAudits()).thenReturn(audits);
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);
        User closingAuditor = EntityFactory.makeUser();
        audit.setClosingAuditor(closingAuditor);

        User auditor = EntityFactory.makeUser();
        User origAuditor = EntityFactory.makeUser();
        UserAssignment ua = new UserAssignment();
        ua.setUser(auditor);
        when(userAssignmentDAO.findByContractor(contractor, audit.getAuditType())).thenReturn(ua);

        // bi sla
        Whitebox.invokeMethod(contractorCron, "runAssignAudit", contractor);
        assertNull(audit.getAuditor());

        // sla exists, assign auditor
        audit.setSlaDate(new Date());
        Whitebox.invokeMethod(contractorCron, "runAssignAudit", contractor);
        assertNotNull(audit.getAuditor());
        assertTrue(audit.getAuditor() == auditor);

        // don't re-assign auditor
        audit.setAuditor(origAuditor);
        Whitebox.invokeMethod(contractorCron, "runAssignAudit", contractor);
        assertNotNull(audit.getAuditor());
        assertTrue(audit.getAuditor() == origAuditor);
    }

    @Test
    public void testNoInsuranceCriteriaInFlagDifferences() throws Exception {
        ContractorCronStep[] steps = new ContractorCronStep[1];
        steps[0] = ContractorCronStep.Flag;

        ContractorOperator conOp = new ContractorOperator();
        conOp.setContractorAccount(contractor);
        conOp.setOperatorAccount(operator);

        FlagCriteria criteria = new FlagCriteria();
        criteria.setInsurance(true);
        FlagData flagData = new FlagData();
        flagData.setCriteria(criteria);
        List<FlagData> changes = new ArrayList<>();
        changes.add(flagData);

        Whitebox.setInternalState(contractorCron, "steps", steps);
        Whitebox.setInternalState(contractorCron, "flagDataCalculator", flagDataCalculator);
        when(flagDataCalculator.calculate()).thenReturn(changes);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(contractor.getStatus()).thenReturn(AccountStatus.Active);

        Whitebox.invokeMethod(contractorCron, "runFlag", conOp);
        assertEquals("{}", conOp.getFlagDetail());
    }

    @Test
    public void testFlagCalculator_declinedAccount() throws Exception {
        ContractorCronStep[] steps = new ContractorCronStep[1];
        steps[0] = ContractorCronStep.Flag;

        ContractorOperator conOp = new ContractorOperator();
        conOp.setContractorAccount(contractor);
        conOp.setOperatorAccount(operator);

        FlagCriteria criteria = new FlagCriteria();
        criteria.setInsurance(true);
        FlagData flagData = new FlagData();
        flagData.setCriteria(criteria);
        List<FlagData> changes = new ArrayList<>();
        changes.add(flagData);

        Whitebox.setInternalState(contractorCron, "steps", steps);
        Whitebox.setInternalState(contractorCron, "flagDataCalculator", flagDataCalculator);
        when(flagDataCalculator.calculate()).thenReturn(changes);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(contractor.getStatus()).thenReturn(AccountStatus.Declined);

        Whitebox.invokeMethod(contractorCron, "runFlag", conOp);
        assertEquals("{}", conOp.getFlagDetail());
        assertEquals(FlagColor.Clear, conOp.getFlagColor());
    }

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
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.IMPLEMENTATION_AUDIT, contractor);
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

    @Test
    public void testRunEmployeeGuardRules_FeatureEnabled() throws Exception {
        setupStep(ContractorCronStep.EmployeeGuardRules);
        Whitebox.invokeMethod(contractorCron, "runEmployeeGuardRules", contractor);
        verify(employeeGuardRulesService).runEmployeeGuardRules(contractor);
    }

    @Test
    public void testRunEmployeeGuardRules_FeatureDisabled() throws Exception {
        togglzRule.disable(Features.USE_NEW_EMPLOYEE_GUARD_RULES);
        setupStep(ContractorCronStep.EmployeeGuardRules);
        Whitebox.invokeMethod(contractorCron, "runEmployeeGuardRules", contractor);
        verify(employeeGuardRulesService, never()).runEmployeeGuardRules(contractor);
    }


    @Ignore
	public void testOverrideStillApplicable() throws Exception {
		Boolean result;

		List<FlagCriteriaOperator> criteriaList = new ArrayList<>();
		criteriaList.add(createFlagCriteriaOperator(operator, 1));

		FlagCriteria unused = new FlagCriteria();
		unused.setId(2);

		List<OshaAudit> oshaAuditList = new ArrayList<>();
		oshaAuditList.add(oshaAudit);

		when(flagOverride.getContractor()).thenReturn(contractor);
		when(flagOverride.getOperator()).thenReturn(operator);
		when(flagOverride.getYear()).thenReturn("2009");
		when(operator.getFlagCriteriaInherited()).thenReturn(criteriaList);
		when(contractor.getOshaAudits()).thenReturn(oshaAuditList);

		// valid
		when(flagOverride.getCriteria()).thenReturn(criteriaList.get(0).getCriteria());
		when(oshaAudit.getAuditFor()).thenReturn("2009");
		result = Whitebox.invokeMethod(contractorCron, "isOverrideApplicableToOperator", flagOverride);
		assertTrue(result);

		// invalid audit is expired
		when(flagOverride.getCriteria()).thenReturn(criteriaList.get(0).getCriteria());
		when(oshaAudit.getAuditFor()).thenReturn("2010");
		result = Whitebox.invokeMethod(contractorCron, "isOverrideApplicableToOperator", flagOverride);
		assertFalse(result);

		// invalid no longer used
		when(flagOverride.getCriteria()).thenReturn(unused);
		when(oshaAudit.getAuditFor()).thenReturn("2009");
		result = Whitebox.invokeMethod(contractorCron, "isOverrideApplicableToOperator", flagOverride);
		assertFalse(result);

	}

	private FlagCriteriaOperator createFlagCriteriaOperator(OperatorAccount operator, int criteriaId) {
		FlagCriteria criteria = new FlagCriteria();
		criteria.setId(criteriaId);

		FlagCriteriaOperator fco = new FlagCriteriaOperator();
		fco.setCriteria(criteria);
		fco.setOperator(operator);
		return fco;  //To change body of created methods use File | Settings | File Templates.
	}
}
