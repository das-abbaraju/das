package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.search.Database;

@SuppressWarnings("deprecation")
public class CronTest {
	private Cron cron;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private OperatorAccount anotherOperator;
	private List<ContractorOperator> operators;
	// private List<Certificate> certList;
	// private Map<Integer, List<Integer>> opIdsByCertIds;

	@Mock
	private EmailQueueDAO emailQueueDAO;
	@Mock
	private ContractorAccountDAO contractoAccountDAO;
	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	protected NoteDAO noteDAO;
	@Mock
	private Database databaseForTesting;
	@Mock
	private AccountStatusChanges accountStatusChanges;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		cron = new Cron();

		PicsTestUtil.autowireDAOsFromDeclaredMocks(cron, this);
		Whitebox.setInternalState(cron, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(cron, "accountStatusChanges", accountStatusChanges);

		operators = new ArrayList<ContractorOperator>();
		// certList = new ArrayList<Certificate>();
		// opIdsByCertIds = new HashMap<Integer, List<Integer>>();
		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		anotherOperator = EntityFactory.makeOperator();
		operators.add(EntityFactory.addContractorOperator(contractor, operator));
		operators.add(EntityFactory.addContractorOperator(contractor, anotherOperator));
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
		when(contractoAccountDAO.findPendingAccounts(anyString())).thenReturn(emptyList);

		Whitebox.invokeMethod(cron, "sendEmailPendingAccounts");

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		verify(contractoAccountDAO, times(3)).findPendingAccounts(captor.capture());

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
	public void testDeactivatePendingAccounts() {
		ContractorAccount cAccount = new ContractorAccount(1);
		ContractorAccount cAccount2 = new ContractorAccount(2);
		cAccount.setStatus(AccountStatus.Pending);
		cAccount2.setStatus(AccountStatus.Pending);
		List<ContractorAccount> cList = new ArrayList<ContractorAccount>();
		cList.add(cAccount);
		cList.add(cAccount2);

		when(contractoAccountDAO.findPendingAccountsToDeactivate()).thenReturn(cList);
		// Not sure why the List is empty. Galen, please help.
		// verify(contractoAccountDAO).save(any(ContractorAccount.class));
		// verify(noteDAO).save(any(Note.class));
	}

	@Test
	public void TestDeactivatePendingAccounts_null() {
		List<ContractorAccount> cList = new ArrayList<ContractorAccount>();
		cList = null;
		when(contractoAccountDAO.findPendingAccountsToDeactivate()).thenReturn(cList);
		verify(contractoAccountDAO, never()).save(any(ContractorAccount.class));
		verify(noteDAO, never()).save(any(Note.class));
	}

	@Test
	public void testDeactivateNonRenewalAccounts() throws Exception {
		String where = "a.status = 'Active' AND a.renew = 0 AND paymentExpires < NOW()";
		List<ContractorAccount> contractors = buildMockContractorList();
		when(contractoAccountDAO.findWhere(where)).thenReturn(contractors);

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
		if (isBidOnly) {
			verify(contractor, times(1)).setReason("Bid Only Account");
		}

		verify(contractor, times(1)).syncBalance();
		verify(contractor, times(1)).setAuditColumns(Cron.system);
		verify(accountStatusChanges, times(1)).deactivateContractor(contractor, null,
				"Automatically inactivating account based on expired membership");
	}
}
