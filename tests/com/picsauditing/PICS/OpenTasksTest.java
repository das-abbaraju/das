package com.picsauditing.PICS;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.jpa.entities.Currency;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsActionTest;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:OpenTasksTest-context.xml"})
public class OpenTasksTest extends PicsActionTest {
	private final String ImportAndSubmitPQF = "Please upload your prequalification form/questionnaire from your other registry";
	private final String RequiresTwoUsers = "PICS now requires contractors to have two or more users to help maintain their account.";
	private final String UpdatedAgreement = "Please review the terms of our updated Contractor User Agreement";
	private final String NeedsTradesUpdated = "Please review your provided services.";
	private final String NoTradesSelected = "You must have at least 1 Trade selected.";
	// "Updgrade" [SIC] - it is in the db this way
	private final String BidOnlyUpdgrade = "Your Account is a Bid-Only Account and will expire. Please upgrade your account to a full membership.";
	private final String GenerateInvoice = "Your Account has been upgraded. To continue working at your selected facilities please generate and pay the invoice";
	private final String OpenInvoiceReminder = "You have an invoice due ";
	private final String UpdatePaymentMethod = "Please update your payment method";
	private final String FixPolicyIssues = "Please fix issues with your Policy";
	private final String FixWcbIssues = "Please fix issues with your {1}{2,choice,0#|1# for {3}}";
	private final String UploadAndSubmitPolicy = "Please upload and submit your Policy";
	private final String ResubmitPolicy = "Please review and resubmit your Policy";
	private final String OpenRequirementsEmployeeGuard = "You have open requirements for employee";
	private final String OpenRequirements = "You have open requirements";

	private final BigDecimal OUTSTANDING_BALANCE = new BigDecimal(100.00);

	private OpenTasks openTasks;
	private LanguageModel injectedLanguageModel;

	@Mock
	private Account account;
	@Mock
	private AuditType auditType;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private ContractorAudit audit;
	@Mock
	protected ContractorAuditDAO contractorAuditDao;
	@Mock
	private OperatorTagDAO operatorTagDao;
	@Mock
	private ContractorAuditOperator cao;
	@Mock
	private User user;
	@Mock
	private OperatorAccount operator;
	@Mock
	private Workflow workFlow;
	@Mock
	private I18nCache i18nCache;
	@Mock
	private UserAccess userAccess;
	@Mock
	private Invoice invoice;
	@Mock
	private Database databaseForTesting;
	@Mock
	private FeatureToggle featureToggleChecker;
	@Mock
	private LanguageModel languageModel;

	private static final int ANTEA_SPECIFIC_AUDIT = 181;
	private static final int TALLRED_USER_ID = 941;
	private ArrayList<String> openTaskList;
	private Set<UserAccess> userPermissions;
	private AccountLevel accountLevel = AccountLevel.Full;
	private List<ContractorAudit> contractorAudits;
	private List<Invoice> invoices;
	private List<ContractorAuditOperator> caos;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		openTasks = new OpenTasks(); // class under test

		setUpCollections(); // MUST be before mocks
		setUpMocks();
		super.setupMocks();
		setUpI18nCacheText();

		Whitebox.setInternalState(openTasks, "supportedLanguages", languageModel);
		Whitebox.setInternalState(openTasks, "i18nCache", i18nCache);
		Whitebox.setInternalState(openTasks, "contractor", contractor);
		Whitebox.setInternalState(openTasks, "openTasks", openTaskList);
		Whitebox.setInternalState(openTasks, "operatorTagDao", operatorTagDao);
		Whitebox.setInternalState(openTasks, "featureToggleChecker", featureToggleChecker);
		when(contractor.getLocale()).thenReturn(Locale.ENGLISH);

		injectedLanguageModel = SpringUtils.getBean(SpringUtils.LANGUAGE_MODEL);
		reset(injectedLanguageModel);
	}

	private void setUpCollections() {
		openTaskList = new ArrayList<String>();
		userPermissions = new HashSet<UserAccess>();
		userPermissions.add(userAccess);
		contractorAudits = new ArrayList<ContractorAudit>();
		contractorAudits.add(audit);
		invoices = new ArrayList<Invoice>();
		invoices.add(invoice);
		caos = new ArrayList<ContractorAuditOperator>();
		caos.add(cao);
	}

	private void setUpMocks() {
		setUpMockUserPermissions();
		setUpMockContractor();
		setUpMockUser();
		setUpMockOperator();
		setUpMockInvoice();
		setUpMockAudit();
	}

	private void setUpMockUserPermissions() {
		when(userAccess.getViewFlag()).thenReturn(Boolean.TRUE);
	}

	private void setUpMockOperator() {
		when(operator.getType()).thenReturn("Operator");
	}

	private void setUpMockUser() {
		when(user.getId()).thenReturn(TALLRED_USER_ID);
		when(user.getLocale()).thenReturn(Locale.ENGLISH);
		when(user.getPermissions()).thenReturn(userPermissions);
		when(user.getAccount()).thenReturn(account);
	}

	private void setUpMockContractor() {
		when(contractor.getId()).thenReturn(12);
		when(contractor.getAccountLevel()).thenReturn(accountLevel);
		when(contractor.getBalance()).thenReturn(BigDecimal.ZERO);
		when(contractor.getAudits()).thenReturn(contractorAudits);
		when(contractor.getInvoices()).thenReturn(invoices);
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Current);
	}

	private void setUpMockInvoice() {
		when(invoice.getBalance()).thenReturn(OUTSTANDING_BALANCE);
		when(invoice.getDueDate()).thenReturn(new Date());
		when(invoice.getCurrency()).thenReturn(Currency.USD);
	}

	private void setUpMockAudit() {
		when(audit.getAuditType()).thenReturn(auditType);
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);
	}

	@Test
	public void testGetOpenTasks_NoTradesSelected() throws Exception {
		noTrades();

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(NoTradesSelected));
	}

	@Test
	public void testGetOpenTasks_TradesNeedUpdating() throws Exception {
		oneTrade();
		when(contractor.isNeedsTradesUpdated()).thenReturn(true);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(NeedsTradesUpdated));
	}

	@Test
	public void testGetOpenTasks_IfOperatorThenWillNotGatherRelationshipTasks() throws Exception {
		when(operator.getCanSeeInsurance()).thenReturn(YesNo.No);
		when(user.getAccount()).thenReturn(operator);
		when(permissions.isOperatorCorporate()).thenReturn(true);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		verify(contractor, never()).isAgreementInEffect();
	}

	@Test
	public void testGetOpenTasks_IfCorporateThenWillNotGatherRelationshipTasks() throws Exception {
		when(operator.getCanSeeInsurance()).thenReturn(YesNo.No);
		when(operator.getType()).thenReturn("Corporate");
		when(user.getAccount()).thenReturn(operator);
		when(permissions.isOperatorCorporate()).thenReturn(true);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		verify(contractor, never()).isAgreementInEffect();
	}

	@Test
	public void testGetOpenTasks_MustApproveUpdatedAgreement_ContractorBilling() throws Exception {
		when(permissions.hasPermission(OpPerms.ContractorBilling)).thenReturn(true);
		validateMustApproveUpdatedAgreement();
	}

	@Test
	public void testGetOpenTasks_MustApproveUpdatedAgreement_ContractorAdmin() throws Exception {
		when(permissions.hasPermission(OpPerms.ContractorAdmin)).thenReturn(true);
		validateMustApproveUpdatedAgreement();
	}

	@Test
	public void testGetOpenTasks_MustApproveUpdatedAgreement_ContractorSafety() throws Exception {
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);
		validateMustApproveUpdatedAgreement();
	}

	private void validateMustApproveUpdatedAgreement() {
		when(contractor.isAgreementInEffect()).thenReturn(false);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(UpdatedAgreement));
	}

	@Test
	public void testGetOpenTasks_NoNeedToApproveUpdatedAgreement_AgreementInEffect() throws Exception {
		when(contractor.isAgreementInEffect()).thenReturn(true);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, not(hasItem(UpdatedAgreement)));

	}

	@Test
	public void testGetOpenTasks_RequiresTwoUsers_IsContractorAdmin() throws Exception {
		when(permissions.hasPermission(OpPerms.ContractorAdmin)).thenReturn(true);

		validateRequiresTwoUsers();
	}

	@Test
	public void testGetOpenTasks_RequiresTwoUsers_UserIsAdmin() throws Exception {
		when(account.isAdmin()).thenReturn(true);

		validateRequiresTwoUsers();
	}

	private void validateRequiresTwoUsers() {
		List<User> users = new ArrayList<User>();
		users.add(user);
		when(contractor.isAgreementInEffect()).thenReturn(true);
		when(contractor.getUsers()).thenReturn(users);
		when(contractor.getSoleProprietor()).thenReturn(false);
		when(contractor.getCreationDate()).thenReturn(date179DaysAgo());

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(RequiresTwoUsers));
	}

	@Test
	public void testGetOpenTasks_BidOnlyUpgrade() throws Exception {
		when(permissions.hasPermission(OpPerms.ContractorAdmin)).thenReturn(true);
		when(contractor.isAgreementInEffect()).thenReturn(true);
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
		when(contractor.getPaymentExpires()).thenReturn(new Date());

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(BidOnlyUpdgrade));
	}

	@Test
	public void testGetOpenTasks_NoPqfTasksBecauseNoAudits() throws Exception {
		when(contractor.getAudits()).thenReturn(new ArrayList<ContractorAudit>());

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, not(hasItem(ImportAndSubmitPQF)));
		Boolean hasImportPQF = Whitebox.getInternalState(openTasks, "hasImportPQF");
		assertFalse(hasImportPQF);
		Boolean importPQFComplete = Whitebox.getInternalState(openTasks, "importPQFComplete");
		assertFalse(importPQFComplete);
	}

	@Test
	public void testGetOpenTasks_NoPqfTasksBecauseNoVisibleAudits() throws Exception {
		when(audit.isVisibleTo((Permissions) any())).thenReturn(false);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, not(hasItem(ImportAndSubmitPQF)));
		Boolean hasImportPQF = Whitebox.getInternalState(openTasks, "hasImportPQF");
		assertFalse(hasImportPQF);
		Boolean importPQFComplete = Whitebox.getInternalState(openTasks, "importPQFComplete");
		assertFalse(importPQFComplete);
	}

	@Test
	public void testGetOpenTasks_NoPqfTasksBecauseNoImportPQFAudit() throws Exception {
		when(audit.isVisibleTo((Permissions) any())).thenReturn(true);
		when(audit.getAuditType()).thenReturn(auditType);
		when(auditType.getId()).thenReturn(AuditType.IEC_AUDIT); // anything but
		// AuditType.IMPORT_PQF

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, not(hasItem(ImportAndSubmitPQF)));
		Boolean hasImportPQF = Whitebox.getInternalState(openTasks, "hasImportPQF");
		assertFalse(hasImportPQF);
		Boolean importPQFComplete = Whitebox.getInternalState(openTasks, "importPQFComplete");
		assertFalse(importPQFComplete);
	}

	@Test
	public void testGetOpenTasks_NoPqfTasksBecauseAuditIsExpired() throws Exception {
		when(audit.isVisibleTo((Permissions) any())).thenReturn(true);
		when(audit.getAuditType()).thenReturn(auditType);
		when(audit.isExpired()).thenReturn(true);
		when(auditType.getId()).thenReturn(AuditType.IMPORT_PQF);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, not(hasItem(ImportAndSubmitPQF)));
		Boolean hasImportPQF = Whitebox.getInternalState(openTasks, "hasImportPQF");
		assertFalse(hasImportPQF);
		Boolean importPQFComplete = Whitebox.getInternalState(openTasks, "importPQFComplete");
		assertFalse(importPQFComplete);
	}

	@Test
	public void testGetOpenTasks_NoPqfTasksBecauseNotBeforeSubmittedButInternalFlagsSetToTrue() throws Exception {
		when(audit.isVisibleTo((Permissions) any())).thenReturn(true);
		when(audit.getAuditType()).thenReturn(auditType);
		when(audit.isExpired()).thenReturn(false);
		when(audit.hasCaoStatusBefore(AuditStatus.Submitted)).thenReturn(false);
		when(audit.hasCaoStatus(AuditStatus.Complete)).thenReturn(true);
		when(auditType.getId()).thenReturn(AuditType.IMPORT_PQF);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, not(hasItem(ImportAndSubmitPQF)));
		Boolean hasImportPQF = Whitebox.getInternalState(openTasks, "hasImportPQF");
		assertTrue(hasImportPQF);
		Boolean importPQFComplete = Whitebox.getInternalState(openTasks, "importPQFComplete");
		assertTrue(importPQFComplete);
	}

	@Test
	public void testGetOpenTasks_HasPqfTask() throws Exception {
		when(audit.isVisibleTo((Permissions) any())).thenReturn(true);
		when(audit.getAuditType()).thenReturn(auditType);
		when(audit.isExpired()).thenReturn(false);
		when(audit.hasCaoStatusBefore(AuditStatus.Submitted)).thenReturn(true);
		when(audit.hasCaoStatus(AuditStatus.Complete)).thenReturn(true);
		when(auditType.getId()).thenReturn(AuditType.IMPORT_PQF);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(ImportAndSubmitPQF));
		Boolean hasImportPQF = Whitebox.getInternalState(openTasks, "hasImportPQF");
		assertTrue(hasImportPQF);
		Boolean importPQFComplete = Whitebox.getInternalState(openTasks, "importPQFComplete");
		assertTrue(importPQFComplete);
	}

	@Test
	public void testGetOpenTasks_NoBillingTasksBecauseNoPerm() throws Exception {
		openTasks.getOpenTasks(contractor, user);

		verify(contractor, never()).getBillingStatus();
	}

	@Test
	public void testGetOpenTasks_GenerateInvoiceBillingTasksBecauseBillingStatusIsUpgrade() throws Exception {
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Upgrade);

		verifyGenerateInvoice();
	}

	@Test
	public void testGetOpenTasks_GenerateInvoiceBillingTasksBecauseBillingStatusIsRenewal() throws Exception {
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Renewal);

		verifyGenerateInvoice();
	}

	private void verifyGenerateInvoice() {
		when(permissions.hasPermission(OpPerms.ContractorBilling)).thenReturn(true);
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(GenerateInvoice));
	}

	@Test
	public void testGetOpenTasks_OpenInvoiceReminderBillingTasksBecauseOutstandingBalanceAndUnpaidInvoice()
			throws Exception {
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Renewal);
		when(permissions.hasPermission(OpPerms.ContractorBilling)).thenReturn(true);
		when(contractor.getBalance()).thenReturn(OUTSTANDING_BALANCE);
		when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(OpenInvoiceReminder));
	}

	@Test
	public void testGetOpenTasks_UpdatePaymentMethod() throws Exception {
		when(permissions.hasPermission(OpPerms.ContractorBilling)).thenReturn(true);
		when(contractor.isPaymentMethodStatusValid()).thenReturn(false);
		when(contractor.isMustPayB()).thenReturn(true);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(UpdatePaymentMethod));

	}

	@Test
	public void testGetOpenTasks_FixPolicyIssues() throws Exception {
		when(audit.hasCaoStatus(AuditStatus.Incomplete)).thenReturn(true);
		setUpPolicyAuditTask();

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(FixPolicyIssues));
	}

	@Test
	public void testGetOpenTasks_UploadAndSubmitPolicy() throws Exception {
		when(audit.hasCaoStatus(AuditStatus.Incomplete)).thenReturn(false);
		setUpPolicyAuditTask();

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(UploadAndSubmitPolicy));
	}

	@Test
	public void testGetOpenTasks_ResubmitPolicy() throws Exception {
		setUpAuditTask();

		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit); // something
		// not
		// Policy
		when(auditType.isRenewable()).thenReturn(true);
		when(auditType.isCanContractorEdit()).thenReturn(true);
		when(audit.isAboutToExpire()).thenReturn(true);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(ResubmitPolicy));
	}

	@Test
	public void testGetOpenTasks_OpenRequirementsEmployeeGuard() throws Exception {
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Employee);
		setUpOpenRequirements();

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(startsWith(OpenRequirementsEmployeeGuard)));
	}

	@Test
	public void testGetOpenTasks_OpenRequirementsWcb() throws Exception {
		setUpAuditTask();
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);
		when(auditType.getId()).thenReturn(143);
		when(auditType.isWCB()).thenReturn(true);
		when(userAccess.getViewFlag()).thenReturn(Boolean.TRUE);
		when(permissions.hasPermission(OpPerms.ContractorInsurance)).thenReturn(true);

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(startsWith("Please fix issues")));
	}

	@Test
	public void testGetOpenTasks_OpenRequirementsNotEmployeeGuard() throws Exception {
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit); // something
		// not
		// Policy
		// and
		// not
		// Employee
		setUpOpenRequirements();

		List<String> openTaskList = openTasks.getOpenTasks(contractor, user);

		assertThat(openTaskList, hasItem(startsWith(OpenRequirements)));
	}

	private void setUpOpenRequirements() {
		setUpAuditTask();

		when(auditType.isRenewable()).thenReturn(false);
		Workflow workflow = mock(Workflow.class);
		when(workflow.isHasRequirements()).thenReturn(true);
		when(auditType.getWorkFlow()).thenReturn(workflow);
		when(auditType.getId()).thenReturn(AuditType.CAN_QUAL_PQF); // something
		// not
		// WA_STATE_VERIFICATION
		when(audit.hasCaoStatus(AuditStatus.Submitted)).thenReturn(true);
		when(audit.getEffectiveDateLabel()).thenReturn(new Date());
		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);
	}

	private void setUpAuditTask() {
		when(auditType.getI18nKey(anyString())).thenReturn("AuditName");
		when(audit.isVisibleTo((Permissions) any())).thenReturn(true);
		when(auditType.isCanContractorView()).thenReturn(true);
		when(audit.isExpired()).thenReturn(false);
		when(cao.getStatus()).thenReturn(AuditStatus.Incomplete);
		when(cao.isVisible()).thenReturn(true);
		when(audit.getOperators()).thenReturn(caos);

		Workflow wf = new Workflow();
		when(auditType.getWorkFlow()).thenReturn(wf);
	}

	private void setUpPolicyAuditTask() {
		setUpAuditTask();

		when(permissions.hasPermission(OpPerms.ContractorInsurance)).thenReturn(true);
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);
	}

	@Test(expected = NullPointerException.class)
	public void testIsOpenTaskNeeded() throws Exception {
		when(audit.getOperators()).thenReturn(null);
		Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);
	}

	@Test
	public void testIsOpenTaskNeeded_PendingManualAudit() throws Exception {
		when(cao.isVisible()).thenReturn(true);
		when(cao.getStatus()).thenReturn(AuditStatus.Pending);
		when(auditType.isCanContractorEdit()).thenReturn(true);
		when(auditType.getId()).thenReturn(AuditType.DESKTOP);
		when(audit.getOperators()).thenReturn(Arrays.asList(cao));
		when(audit.getAuditType()).thenReturn(auditType);
		when(workFlow.getId()).thenReturn(Workflow.MANUAL_AUDIT_WORKFLOW);
		when(account.isAdmin()).thenReturn(false);
		when(user.getAccount()).thenReturn(account);
		when(permissions.hasPermission((OpPerms) Matchers.argThat(instanceOf(OpPerms.class)))).then(
				buildAnswerForSpecificPermission(OpPerms.ContractorInsurance));

		Boolean result = Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);

		assertTrue(result);
	}

	@Test
	public void testGatherTasksAboutDeclaringTrades_tradesMissing() throws Exception {
		noTrades();

		Whitebox.invokeMethod(openTasks, "gatherTasksAboutDeclaringTrades");

		assertEquals("You must have at least 1 Trade selected.", openTaskList.get(0));
	}

	@Test
	public void testGatherTasksAboutDeclaringTrades_tradesSuppliedButNeedsUpdate() throws Exception {
		oneTrade();
		when(contractor.isNeedsTradesUpdated()).thenReturn(true);

		Whitebox.invokeMethod(openTasks, "gatherTasksAboutDeclaringTrades");

		assertEquals("Please review your provided services.", openTaskList.get(0));
	}

	@Test
	public void testGatherTasksAboutDeclaringTrades_tradesSuppliedAndUpToDate() throws Exception {
		oneTrade();
		when(contractor.isNeedsTradesUpdated()).thenReturn(false);

		Whitebox.invokeMethod(openTasks, "gatherTasksAboutDeclaringTrades");

		assertThat(openTaskList.isEmpty(), is(true));
	}

	/*
	 * PICS-4748 Antea-Specific Audit -- Contractor is seeing an outstanding
	 * task for Antea when there is actually nothing left to do.
	 */
	@Test
	public void testIsOpenTaskNeeded_SubmittedAnteaSpecificAudit() throws Exception {
		when(cao.isVisible()).thenReturn(true);
		when(cao.getStatus()).thenReturn(AuditStatus.Submitted);
		when(audit.getOperators()).thenReturn(Arrays.asList(cao));
		when(workFlow.getId()).thenReturn(Workflow.PQF_WORKFLOW);
		when(auditType.isCanContractorEdit()).thenReturn(true);
		when(auditType.getId()).thenReturn(ANTEA_SPECIFIC_AUDIT);
		when(audit.getAuditType()).thenReturn(auditType);
		when(account.isAdmin()).thenReturn(false);
		when(user.getAccount()).thenReturn(account);
		when(permissions.hasPermission((OpPerms) Matchers.argThat(instanceOf(OpPerms.class)))).then(
				buildAnswerForSpecificPermission(OpPerms.ContractorInsurance));

		Boolean result = Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);

		assertFalse(result);
	}

	@Test(expected = NullPointerException.class)
	public void testGatherTasksAboutDeclaringTrades_NullPointerException() throws Exception {
		when(contractor.getTrades()).thenReturn(null);
		Whitebox.setInternalState(openTasks, "contractor", contractor);
		Whitebox.invokeMethod(openTasks, "gatherTasksAboutDeclaringTrades");
	}

	private Answer<Boolean> buildAnswerForSpecificPermission(final OpPerms permission) {
		return new Answer<Boolean>() {

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if (invocation != null && ArrayUtils.isNotEmpty(invocation.getArguments())) {
					Object argument = invocation.getArguments()[0];
					if (argument instanceof OpPerms) {
						return (permission == (OpPerms) argument);
					}
				}

				return false;
			}

		};
	}

	private void setUpI18nCacheText() {
		when(i18nCache.hasKey(anyString(), eq(Locale.ENGLISH))).thenReturn(true);

		when(i18nCache.getText(eq("ContractorWidget.message.NoTradesSelected"), eq(Locale.ENGLISH), any())).thenReturn(
				NoTradesSelected);
		when(i18nCache.getText(eq("ContractorWidget.message.NeedsTradesUpdated"), eq(Locale.ENGLISH), any()))
				.thenReturn(NeedsTradesUpdated);
		when(i18nCache.getText(eq("ContractorWidget.message.UpdatedAgreement"), eq(Locale.ENGLISH), any())).thenReturn(
				UpdatedAgreement);
		when(i18nCache.getText(eq("ContractorWidget.message.RequiresTwoUsers"), eq(Locale.ENGLISH), any())).thenReturn(
				RequiresTwoUsers);
		when(i18nCache.getText(eq("ContractorWidget.message.ImportAndSubmitPQF"), eq(Locale.ENGLISH), any()))
				.thenReturn(ImportAndSubmitPQF);
		when(i18nCache.getText(eq("ContractorWidget.message.BidOnlyUpdgrade"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(BidOnlyUpdgrade);
		when(i18nCache.getText(eq("ContractorWidget.message.GenerateInvoice"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(GenerateInvoice);
		when(i18nCache.getText(eq("ContractorWidget.message.OpenInvoiceReminder"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(OpenInvoiceReminder);
		when(i18nCache.getText(eq("ContractorWidget.message.UpdatePaymentMethod"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(UpdatePaymentMethod);
		when(i18nCache.getText(eq("ContractorWidget.message.FixPolicyIssues"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(FixPolicyIssues);
		when(i18nCache.getText(eq("ContractorWidget.message.UploadAndSubmitPolicy"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(UploadAndSubmitPolicy);
		when(i18nCache.getText(eq("ContractorWidget.message.ResubmitPolicy"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(ResubmitPolicy);
		when(
				i18nCache.getText(eq("ContractorWidget.message.OpenRequirementsEmployeeGuard"), eq(Locale.ENGLISH),
						anyVararg())).thenReturn(OpenRequirementsEmployeeGuard);
		when(
				i18nCache.getText(eq("ContractorWidget.message.OpenRequirementsEmployeeGuard2"), eq(Locale.ENGLISH),
						anyVararg())).thenReturn(OpenRequirementsEmployeeGuard);
		when(i18nCache.getText(eq("ContractorWidget.message.OpenRequirements"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(OpenRequirements);
		when(i18nCache.getText(eq("ContractorWidget.message.UploadAndSubmitWCB"), eq(Locale.ENGLISH), anyVararg()))
				.thenReturn(FixWcbIssues);

		when(i18nCache.getText(eq("AuditName"), eq(Locale.ENGLISH), anyVararg())).thenReturn("Audit Name");

	}

	private void noTrades() {
		Set<ContractorTrade> noTrades = new HashSet<ContractorTrade>();
		when(contractor.getTrades()).thenReturn(noTrades);
	}

	private void oneTrade() {
		Set<ContractorTrade> trades = new HashSet<ContractorTrade>();
		ContractorTrade trade = mock(ContractorTrade.class);
		trades.add(trade);
		when(contractor.getTrades()).thenReturn(trades);
	}

	private Date date179DaysAgo() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, -179);
		return date.getTime();
	}

	@Test
	public void testIsLcCorTaskNeeded_NoPhase() throws Exception {
		setUpLcCorNotification(null, null);
		Boolean result = Whitebox.invokeMethod(openTasks, "isLcCorTaskNeeded");
		assertFalse(result);
	}

	@Test
	public void testIsLcCorTaskNeeded_Later() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		setUpLcCorNotification(LcCorPhase.RemindMeLater, cal.getTime());
		Boolean result = Whitebox.invokeMethod(openTasks, "isLcCorTaskNeeded");
		assertFalse(result);
	}

	@Test
	public void testIsLcCorTaskNeeded_Done() throws Exception {
		setUpLcCorNotification(LcCorPhase.Done, new Date());
		Boolean result = Whitebox.invokeMethod(openTasks, "isLcCorTaskNeeded");
		assertFalse(result);
	}

	@Test
	public void testIsLcCorTaskNeeded_Yes() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		setUpLcCorNotification(LcCorPhase.RemindMeLater, cal.getTime());
		Boolean result = Whitebox.invokeMethod(openTasks, "isLcCorTaskNeeded");
		assertTrue(result);
	}

	private void setUpLcCorNotification(LcCorPhase phase, Date date) {
		when(featureToggleChecker.isFeatureEnabled(anyString())).thenReturn(true);

		ContractorAccount contractor = EntityFactory.makeContractor();
		contractor.setLcCorPhase(phase);
		contractor.setLcCorNotification(date);

		Whitebox.setInternalState(openTasks, "contractor", contractor);
	}

	@Test
	public void testCorOpenTaskNeeded() throws Exception {
		ContractorAccount contractor = EntityFactory.makeContractor();
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.COR, contractor);
		contractor.getAudits().add(audit);
		audit.getAuditType().setCanContractorEdit(true);
		ContractorAuditOperator cao = EntityFactory.addCao(audit, EntityFactory.makeOperator());
//		audit.getOperators().add(cao);

		when(permissions.hasPermission(OpPerms.ContractorSafety)).thenReturn(true);

		cao.setStatus(AuditStatus.Pending);
		Boolean result = Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);
		assertTrue(result);

		cao.setStatus(AuditStatus.Submitted);
		result = Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);
		assertFalse(result);

		cao.setStatus(AuditStatus.Resubmitted);
		result = Whitebox.invokeMethod(openTasks, "isOpenTaskNeeded", audit, user, permissions);
		assertTrue(result);
	}

	@Test
	public void testEstablishPermissions_NullSessionInContext() throws Exception {
		Whitebox.invokeMethod(openTasks, "establishPermissions", (User) null);
		permissions = Whitebox.getInternalState(openTasks, "permissions");

		assertNotNull(permissions);
		assertNotNull(injectedLanguageModel);

		verify(languageModel).getClosestVisibleLocale(any(Locale.class));
	}

	@Test
	public void testEstablishPermissions_SessionInContext() throws Exception {
		ActionContext previousContext = ActionContext.getContext();
		ActionContext mockContext = mock(ActionContext.class);

		Map<String, Object> session = new HashMap<>();
		session.put(ActionContext.SESSION, permissions);

		ActionContext.setContext(mockContext);
		when(mockContext.getSession()).thenReturn(session);

		Whitebox.invokeMethod(openTasks, "establishPermissions", (User) null);

		Permissions permissionsFromOpenTasks = Whitebox.getInternalState(openTasks, "permissions");

		assertNotNull(permissionsFromOpenTasks);

		verify(languageModel).getClosestVisibleLocale(any(Locale.class));
		// Finally
		ActionContext.setContext(previousContext);
	}

	@Test
	public void testGatherTasksAboutEmployeeCompetencies_OperatorsDoNotHaveRequiredCompetencies() throws Exception {
		List<ContractorOperator> contractorOperators = new ArrayList<>();
		ContractorOperator contractorOperator = mock(ContractorOperator.class);
		contractorOperators.add(contractorOperator);

		when(contractor.getOperators()).thenReturn(contractorOperators);
		when(contractor.isRequiresCompetencyReview()).thenReturn(true);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		when(operator.getCompetencies()).thenReturn(Collections.<OperatorCompetency>emptyList());
		Whitebox.invokeMethod(openTasks, "gatherTasksAboutEmployeeCompetencies");

		verify(i18nCache, never()).getText(anyString());
	}

	@Test
	public void testGatherTasksAboutEmployeeCompetencies_OperatorHasRequiredCompetenciesAndNoContractorEmployees() throws Exception {
		List<ContractorOperator> contractorOperators = new ArrayList<>();
		ContractorOperator contractorOperator = mock(ContractorOperator.class);
		contractorOperators.add(contractorOperator);

		List<OperatorCompetency> competencies = new ArrayList<>();
		OperatorCompetency competency = mock(OperatorCompetency.class);
		competencies.add(competency);

		when(competency.isRequiresDocumentation()).thenReturn(true);
		when(contractor.getOperators()).thenReturn(contractorOperators);
		when(contractor.isRequiresCompetencyReview()).thenReturn(true);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		when(operator.getCompetencies()).thenReturn(competencies);
		Whitebox.invokeMethod(openTasks, "gatherTasksAboutEmployeeCompetencies");

		verify(i18nCache).getText(eq("ContractorWidget.message.EmployeesNeedToBeAdded"), any(Locale.class),
				anyObject());
	}

	@Test
	public void testGatherTasksAboutEmployeeCompetencies_OperatorHasRequiredCompetenciesAndContractorHasEmployees() throws Exception {
		List<ContractorOperator> contractorOperators = new ArrayList<>();
		ContractorOperator contractorOperator = mock(ContractorOperator.class);
		contractorOperators.add(contractorOperator);

		List<OperatorCompetency> competencies = new ArrayList<>();
		OperatorCompetency competency = mock(OperatorCompetency.class);
		competencies.add(competency);

		List<Employee> employees = new ArrayList<>();
		Employee employee = mock(Employee.class);
		employees.add(employee);

		when(competency.isRequiresDocumentation()).thenReturn(true);
		when(contractor.getEmployees()).thenReturn(employees);
		when(contractor.getOperators()).thenReturn(contractorOperators);
		when(contractor.isRequiresCompetencyReview()).thenReturn(true);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		when(operator.getCompetencies()).thenReturn(competencies);
		Whitebox.invokeMethod(openTasks, "gatherTasksAboutEmployeeCompetencies");

		verify(i18nCache, never()).getText(anyString());
	}
}
