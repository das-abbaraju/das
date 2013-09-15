package com.picsauditing.actions;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.DBBean;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.util.SapAppPropertyUtil;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
public class CronTest extends PicsActionTest {
	private Cron cron;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private OperatorAccount anotherOperator;
	private List<ContractorOperator> operators;

	@Mock
	protected ContractorAccountDAO contractorAccountDAO;
	@Mock
	protected ContractorAuditDAO contractorAuditDAO;
	@Mock
	protected ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	@Mock
	private ContractorOperatorDAO contractorOperatorDAO;
	@Mock
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
	@Mock
	private EmailQueueDAO emailQueueDAO;
	@Mock
	private FlagDataOverrideDAO flagDataOverrideDAO;
	@Mock
	protected InvoiceDAO invoiceDAO;
	@Mock
	private InvoiceFeeDAO invoiceFeeDAO;
	@Mock
	private InvoiceItemDAO invoiceItemDAO;
	@Mock
	protected OperatorAccountDAO operatorDAO;
	@Mock
	protected NoteDAO noteDAO;
	@Mock
	protected UserDAO userDAO;
	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	private AccountStatusChanges accountStatusChanges;
	@Mock
	private EmailSender emailSender;
    @Mock
    protected BillingService billingService;
	@Mock
	protected Invoice mockInvoice;
	@Mock
	protected InvoiceItem mockInvoiceItem;
	@Mock
	protected ContractorAccount mockContractorAccount;
	@Mock
	protected InvoiceFee mockInvoiceFee;
	@Mock
	protected SapAppPropertyUtil mockSapAppPropertyUtil;

	@BeforeClass
	public static void classSetUp() {
		Whitebox.setInternalState(DBBean.class, "staticDataSource", Mockito.mock(DataSource.class));
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(DBBean.class, "staticDataSource", (DataSource) null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


		cron = new Cron();
		super.setUp(cron);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(cron, this);
		Whitebox.setInternalState(cron, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(cron, "emailSender", emailSender);
		Whitebox.setInternalState(cron, "accountStatusChanges", accountStatusChanges);
        Whitebox.setInternalState(cron, "billingService", billingService);

		operators = new ArrayList<ContractorOperator>();
		// certList = new ArrayList<Certificate>();
		// opIdsByCertIds = new HashMap<Integer, List<Integer>>();
		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		anotherOperator = EntityFactory.makeOperator();
		operators.add(EntityFactory.addContractorOperator(contractor, operator));
		operators.add(EntityFactory.addContractorOperator(contractor, anotherOperator));

		when(propertyDAO.find("admin_email_address")).thenReturn(
				new AppProperty("admin_email_address", "foo@example.com"));
	}

	@Test
	public void testExcecute_VerifyExtractMethodOfDecliningPendingAccounts() throws Exception {
		cron.setFlagsOnly(true);

		cron.execute();

		verify(contractorAccountDAO).findPendingAccountsToMoveToDeclinedStatus();
	}

	@Test
	public void testSendEmailPendingAccounts_SqlHasCountryRestrictionWhenEmailExclusionsExist() throws Exception {
		doTestSqlHasCountryRestriction();
	}

	@Test
	public void testSendEmailPendingAccounts_SqlHasCountryRestrictionWhenNoEmailExclusions() throws Exception {
		@SuppressWarnings("serial")
		List<String> emailExclusionList = new ArrayList<String>() {
			{
				add("123");
				add("124");
			}
		};
		Whitebox.setInternalState(cron, "emailExclusionList", emailExclusionList);
		doTestSqlHasCountryRestriction();
	}

	private void doTestSqlHasCountryRestriction() throws Exception {
		List<ContractorAccount> emptyList = new ArrayList<ContractorAccount>();
		when(contractorAccountDAO.findPendingAccounts(anyString())).thenReturn(emptyList);

		Whitebox.invokeMethod(cron, "sendEmailPendingAccounts");

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		verify(contractorAccountDAO, times(3)).findPendingAccounts(captor.capture());

		List<String> allSql = captor.getAllValues();

		for (String sql : allSql) {
			assertTrue(sql.contains("a.country IN ('US','CA')"));
		}
	}

	@Test
	public void testSumFlagChanges_EmptyBasicDynaBeanList() throws Exception {
		assertEquals(Integer.valueOf(0), Whitebox.invokeMethod(cron, "sumFlagChanges", (List<BasicDynaBean>) null));
		assertEquals(Integer.valueOf(0), Whitebox.invokeMethod(cron, "sumFlagChanges", new ArrayList<BasicDynaBean>()));
	}

	@Test
	public void testSumFlagChanges_ValidBasicDynaBeanList() throws Exception {
		BasicDynaBean mockDynaBean1 = Mockito.mock(BasicDynaBean.class);
		when(mockDynaBean1.get("changes")).thenReturn(1);
		BasicDynaBean mockDynaBean2 = Mockito.mock(BasicDynaBean.class);
		when(mockDynaBean2.get("changes")).thenReturn(2);

		List<BasicDynaBean> fakes = new ArrayList<BasicDynaBean>();
		fakes.add(mockDynaBean1);
		fakes.add(mockDynaBean2);
		assertEquals(Integer.valueOf(3), Whitebox.invokeMethod(cron, "sumFlagChanges", fakes));
	}

	@Test
	public void testSumFlagChanges_BasicDynaBeanListWithNonIntegerValue() throws Exception {
		BasicDynaBean mockDynaBean1 = Mockito.mock(BasicDynaBean.class);
		when(mockDynaBean1.get("changes")).thenReturn("Fail here!");
		BasicDynaBean mockDynaBean2 = Mockito.mock(BasicDynaBean.class);
		when(mockDynaBean2.get("changes")).thenReturn(2);
		BasicDynaBean mockDynaBean3 = Mockito.mock(BasicDynaBean.class);
		when(mockDynaBean3.get("changes")).thenReturn(null);

		List<BasicDynaBean> fakes = new ArrayList<BasicDynaBean>();
		fakes.add(mockDynaBean1);
		fakes.add(mockDynaBean2);
		fakes.add(mockDynaBean3);
		assertEquals(Integer.valueOf(2), Whitebox.invokeMethod(cron, "sumFlagChanges", fakes));
	}

	@Test
	public void testSumFlagChanges_BasicDynaBeanThrowsException() throws Exception {
		BasicDynaBean mockDynaBean1 = Mockito.mock(BasicDynaBean.class);
		when(mockDynaBean1.get("changes")).thenReturn(new IllegalArgumentException("Forcing an error"));
		BasicDynaBean mockDynaBean2 = Mockito.mock(BasicDynaBean.class);
		when(mockDynaBean2.get("changes")).thenReturn(5);
		List<BasicDynaBean> fakes = new ArrayList<BasicDynaBean>();
		fakes.add(mockDynaBean1);
		fakes.add(mockDynaBean2);
		assertEquals(Integer.valueOf(5), Whitebox.invokeMethod(cron, "sumFlagChanges", fakes));
	}

	@Test
	public void testSendEmailsTo_SingleContractorAccountGetsSingleEmail() throws Exception {
		ContractorAccount cAccount = new ContractorAccount();
		Map<ContractorAccount, Integer> fakeContractors = new TreeMap<ContractorAccount, Integer>();
		fakeContractors.put(cAccount, 48);

		EmailQueue email = new EmailQueue();
		email.setContractorAccount(new ContractorAccount(3));

		when(emailBuilder.build()).thenReturn(email);

		Whitebox.invokeMethod(cron, "sendEmailsTo", fakeContractors);

		verify(emailQueueDAO).save(any(EmailQueue.class));
		verify(noteDAO).save(any(Note.class));
	}

	@Test
	public void testSendInvalidEmailsToBilling_SingleContractorAccountGetsSingleEmail() throws Exception {
		ContractorAccount cAccount = new ContractorAccount();
		Map<ContractorAccount, Integer> fakeContractors = new TreeMap<ContractorAccount, Integer>();
		fakeContractors.put(cAccount, 48);

		EmailQueue email = new EmailQueue();
		email.setContractorAccount(new ContractorAccount(3));

		when(emailBuilder.build()).thenReturn(email);

		Whitebox.invokeMethod(cron, "sendEmailsTo", fakeContractors);

		verify(emailQueueDAO).save(any(EmailQueue.class));
		verify(noteDAO).save(any(Note.class));
	}

	@Test
	public void testRunCRREmailBlast() throws Exception {
		List<ContractorRegistrationRequest> list = new ArrayList<ContractorRegistrationRequest>();
		ContractorRegistrationRequest crr = new ContractorRegistrationRequest();
		crr.setName("test");
		list.add(crr);

		EmailQueue email = new EmailQueue();
		email.setContractorAccount(new ContractorAccount(3));
		// email.setEmailTemplate(any(EmailTemplate.class));
		// when(emailBuilder.build()).thenReturn(email);

		// Whitebox.invokeMethod(cron, "runCRREmailBlast", list, 1, "test");

		// verify(emailQueueDAO).save(any(EmailQueue.class));
		// verify(contractorRegistrationRequestDAO).save(any(ContractorRegistrationRequest.class));
	}

	@Test
	public void testRunAccountEmailBlast() throws Exception {
		List<ContractorAccount> list = new ArrayList<ContractorAccount>();
		ContractorAccount cAccount = new ContractorAccount(1);
		list.add(cAccount);

		EmailQueue email = new EmailQueue();
		email.setContractorAccount(new ContractorAccount(3));

		// when(emailBuilder.build()).thenReturn(email);

		// Whitebox.invokeMethod(cron, "runAccountEmailBlast", list, 1, "test");
		// verify(emailQueueDAO).save(any(EmailQueue.class));
		// verify(contractoAccountDAO).save(any(ContractorAccount.class));
	}

	@Test
	public void testDeactivatePendingAccounts() throws Exception {
		ContractorAccount cAccount = new ContractorAccount(1);
		ContractorAccount cAccount2 = new ContractorAccount(2);
		cAccount.setStatus(AccountStatus.Pending);
		cAccount2.setStatus(AccountStatus.Pending);
		List<ContractorAccount> cList = new ArrayList<>();
		cList.add(cAccount);
		cList.add(cAccount2);

		when(contractorAccountDAO.findPendingAccountsToMoveToDeclinedStatus()).thenReturn(cList);

		cron.move90DayPendingAccountsToDeclinedStatus();

		verify(accountStatusChanges).declineContractor(cAccount, permissions,
				AccountStatusChanges.DID_NOT_COMPLETE_PICS_PROCESS_REASON,
				AccountStatusChanges.NOTE_DID_NOT_COMPLETE_PICS_PROCESS_REASON);
		verify(accountStatusChanges).declineContractor(cAccount2, permissions,
				AccountStatusChanges.DID_NOT_COMPLETE_PICS_PROCESS_REASON,
				AccountStatusChanges.NOTE_DID_NOT_COMPLETE_PICS_PROCESS_REASON);
	}

	@Test
	public void testDeactivatePendingAccounts_emptyList() {
		List<ContractorAccount> cList = new ArrayList<>();
		when(contractorAccountDAO.findPendingAccountsToMoveToDeclinedStatus()).thenReturn(cList);
		verify(accountStatusChanges, never()).deactivateContractor(any(ContractorAccount.class),
				any(Permissions.class), anyString(), anyString());
	}

	@Test
	public void testDeactivateNonRenewalAccounts() throws Exception {
		String where = "a.status = 'Active' AND a.renew = 0 AND paymentExpires < NOW()";
		List<ContractorAccount> contractors = buildMockContractorList();
		when(contractorAccountDAO.findWhere(where)).thenReturn(contractors);

		Whitebox.invokeMethod(cron, "deactivateNonRenewalAccounts");

		for (ContractorAccount contractor : contractors) {
			verifyContractor(contractor, contractor.getAccountLevel().isBidOnly());
		}
	}

	private List<ContractorAccount> buildMockContractorList() {
		List<ContractorAccount> contractors = new ArrayList<ContractorAccount>();
		contractors.add(buildMockContractorAccount(1, false));
		contractors.add(buildMockContractorAccount(2, true));

		return contractors;
	}

	private ContractorAccount buildMockContractorAccount(int id, boolean isBidOnly) {
		ContractorAccount contractor = Mockito.mock(ContractorAccount.class);
		when(contractor.getId()).thenReturn(id);

		if (isBidOnly) {
			when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
		} else {
			when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
		}

		return contractor;
	}

	private void verifyContractor(ContractorAccount contractor, boolean isBidOnly) {
		String reason = isBidOnly ? AccountStatusChanges.BID_ONLY_ACCOUNT_REASON
				: AccountStatusChanges.DEACTIVATED_NON_RENEWAL_ACCOUNT_REASON;

		verify(billingService, times(1)).syncBalance(contractor);
		verify(contractor, times(1)).setAuditColumns(Cron.system);
		verify(accountStatusChanges, times(1)).deactivateContractor(contractor, permissions, reason,
				"Automatically inactivating account based on expired membership");
	}

	@Test
	public void testCalculateLateFeeFor() {
		BigDecimal invoiceFeeAmount = new BigDecimal(100);
		BigDecimal expectedLateFee = new BigDecimal(Cron.MINIMUM_LATE_FEE);
		when(mockInvoice.getTotalAmount()).thenReturn(invoiceFeeAmount);
		BigDecimal actualLateFee = cron.calculateLateFeeFor(mockInvoice);
		assertEquals(expectedLateFee,actualLateFee);
	}

	@Test
	public void createLateFeeInvoiceItem() {
		cron.setInvoiceFeeDAO(invoiceFeeDAO);
		when(invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.LateFee, 0)).thenReturn(mockInvoiceFee);
		when(mockInvoiceFee.getFeeClass()).thenReturn(FeeClass.LateFee);
		when(mockInvoice.getAccount()).thenReturn(mockContractorAccount);
		BigDecimal invoiceFeeAmount = new BigDecimal(100);
		when(mockInvoice.getTotalAmount()).thenReturn(invoiceFeeAmount);
		BigDecimal lateFee = cron.calculateLateFeeFor(mockInvoice);
		InvoiceItem lateFeeInvoiceItem = cron.createLateFeeInvoiceItem(mockInvoice, lateFee);
		assertEquals(FeeClass.LateFee,lateFeeInvoiceItem.getInvoiceFee().getFeeClass());
		assertEquals(lateFee,lateFeeInvoiceItem.getAmount());
	}

	@Test
	public void testGenerateLateFeeInvoice() {
		cron.setInvoiceFeeDAO(invoiceFeeDAO);
		when(invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.LateFee, 0)).thenReturn(mockInvoiceFee);
		when(mockInvoiceFee.getFeeClass()).thenReturn(FeeClass.LateFee);
		when(mockInvoice.getAccount()).thenReturn(mockContractorAccount);
		Date invoiceDueDate = new Date();
		when(mockInvoice.getDueDate()).thenReturn(invoiceDueDate);
		BigDecimal invoiceFeeAmount = new BigDecimal(100);
		when(mockInvoice.getTotalAmount()).thenReturn(invoiceFeeAmount);
		BigDecimal lateFee = cron.calculateLateFeeFor(mockInvoice);
		InvoiceItem lateFeeInvoiceItem = cron.createLateFeeInvoiceItem(mockInvoice, lateFee);
		Invoice lateInvoice = cron.generateLateFeeInvoice(mockInvoice, lateFeeInvoiceItem);

		assertEquals(mockInvoice.getAccount(),lateInvoice.getAccount());
		assertEquals(lateFeeInvoiceItem,lateInvoice.getItems().get(0));
		assertEquals(lateFee,lateInvoice.getTotalAmount());
		assertEquals(InvoiceType.LateFee,lateInvoice.getInvoiceType());
		assertEquals(FeeClass.LateFee,lateInvoice.getItems().get(0).getInvoiceFee().getFeeClass());
		assertEquals(invoiceDueDate,lateInvoice.getDueDate());
	}
}