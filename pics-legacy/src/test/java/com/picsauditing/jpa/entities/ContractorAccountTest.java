package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.validator.InputValidator;
import com.picsauditing.validator.VATValidator;

public class ContractorAccountTest {
	private ContractorAccount contractor;
	private ContractorAccount contractorUnderTest;
	private List<ContractorOperator> testOperators;
	private OperatorAccount testOperator1 = new OperatorAccount();
	private OperatorAccount testOperator2 = new OperatorAccount();
	private OperatorAccount testOperator3 = new OperatorAccount();
	private static final int TEST_OPERATOR_1_ID = 5;
	private static final int TEST_OPERATOR_2_ID = 55;
	private static final int TEST_OPERATOR_3_ID = 555;
	private ContractorOperator CO_1 = new ContractorOperator();
	private ContractorOperator CO_2 = new ContractorOperator();
	private ContractorOperator CO_3 = new ContractorOperator();
	private Map<FeeClass, ContractorFee> fees;
    private List<AccountUser> accountUsers;

	@Mock
	private ContractorFee bidOnlyFee;
	@Mock
	private Country country;
	@Mock
	private ContractorFee listOnlyFee;
	@Mock
	private InvoiceFee bidOnlyInvoiceFee;
	@Mock
	private InvoiceFee listOnlyinvoiceFee;
	@Mock
	private CountryDAO countryDAO;

	public ContractorAccountTest() {
		testOperator1.setId(TEST_OPERATOR_1_ID);
		testOperator2.setId(TEST_OPERATOR_2_ID);
		testOperator3.setId(TEST_OPERATOR_3_ID);
		testOperator1.setStatus(AccountStatus.Active);
		testOperator2.setStatus(AccountStatus.Active);
		testOperator3.setStatus(AccountStatus.Active);
		testOperator1.setName("Test Operator 1");
		testOperator2.setName("Test Operator 2");
		testOperator3.setName("Test Operator 3");
		CO_1.setContractorAccount(contractorUnderTest);
		CO_2.setContractorAccount(contractorUnderTest);
		CO_3.setContractorAccount(contractorUnderTest);
		CO_1.setOperatorAccount(testOperator1);
		CO_2.setOperatorAccount(testOperator2);
		CO_3.setOperatorAccount(testOperator3);
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		contractor = EntityFactory.makeContractor();
		contractorUnderTest = new ContractorAccount();
		testOperators = new ArrayList<ContractorOperator>();
        accountUsers = new ArrayList<>();

        contractorUnderTest.setAccountUsers(accountUsers);
		Whitebox.setInternalState(contractorUnderTest, "countryDAO", countryDAO);
		Whitebox.setInternalState(contractorUnderTest, "inputValidator", new InputValidator());
        Whitebox.setInternalState(contractorUnderTest, "vatValidator", new VATValidator());
	}

	@After
	public void cleanup() {
		testOperator2.setParent(null);
	}

	private void setupFees(boolean bidOnly, boolean listOnly) {
		fees = new HashMap<FeeClass, ContractorFee>();
		// all contractors have bid only and list only fees, they may just be
		// hidden and $0
		fees.put(FeeClass.BidOnly, bidOnlyFee);
		fees.put(FeeClass.ListOnly, listOnlyFee);
		when(bidOnlyFee.getCurrentLevel()).thenReturn(bidOnlyInvoiceFee);
		when(listOnlyFee.getCurrentLevel()).thenReturn(listOnlyinvoiceFee);
		when(bidOnlyInvoiceFee.isFree()).thenReturn(bidOnly);
		when(listOnlyinvoiceFee.isFree()).thenReturn(listOnly);
		contractorUnderTest.setFees(fees);
	}

    @Test
    public void testGetCurrentCsr_ReturnsCurrentCsr() throws Exception {
        User currentCsr = mock(User.class);
        AccountUser currentCsrAccountUser = currentCsr();
        when(currentCsrAccountUser.getUser()).thenReturn(currentCsr);
        accountUsers.add(oldCsr());
        accountUsers.add(currentCsrAccountUser);
        accountUsers.add(currentSalesRep());

        assertEquals(currentCsr, contractorUnderTest.getCurrentCsr());
    }

    @Test
    public void testGetCurrentCsr_ReturnsNullIfNoCurrentCsr() throws Exception {
        accountUsers.add(oldCsr());
        accountUsers.add(currentSalesRep());

        assertEquals(null, contractorUnderTest.getCurrentCsr());
    }

    @Test
    public void testSetCurrentCsr_ExpiresCurrentCsr() throws Exception {
        User newCsr = mock(User.class);
        AccountUser currentCsr = currentCsr();
        when(currentCsr.getUser()).thenReturn(newCsr);

        contractorUnderTest.setCurrentCsr(newCsr, 1);

        User newCurrentCsr = contractorUnderTest.getCurrentCsr();
        assertEquals(newCsr, newCurrentCsr);
    }

    @Test
    public void testSetCurrentCsr_setsNewCsr() throws Exception {
        User newCsr = mock(User.class);
        AccountUser currentCsr = currentCsr();
        when(currentCsr.getUser()).thenReturn(newCsr);
        AccountUser oldCsr = oldCsr();
        AccountUser salesRep = currentSalesRep();
        accountUsers.add(currentCsr);
        accountUsers.add(oldCsr);
        accountUsers.add(salesRep);

        contractorUnderTest.setCurrentCsr(newCsr, 1);

        verify(currentCsr).setEndDate(any(Date.class));
        verify(oldCsr, never()).setEndDate(any(Date.class));
        verify(salesRep, never()).setEndDate(any(Date.class));
    }


    private AccountUser currentSalesRep() {
        AccountUser currentSalesRep = mock(AccountUser.class);
        when(currentSalesRep.isCurrent()).thenReturn(true);
        when(currentSalesRep.getRole()).thenReturn(UserAccountRole.PICSSalesRep);
        return currentSalesRep;
    }

    private AccountUser currentCsr() {
        AccountUser currentCsr = mock(AccountUser.class);
        when(currentCsr.isCurrent()).thenReturn(true);
        when(currentCsr.getRole()).thenReturn(UserAccountRole.PICSCustomerServiceRep);
        return currentCsr;
    }

    private AccountUser oldCsr() {
        AccountUser oldCsr = mock(AccountUser.class);
        when(oldCsr.isCurrent()).thenReturn(false);
        when(oldCsr.getRole()).thenReturn(UserAccountRole.PICSCustomerServiceRep);
        return oldCsr;
    }

    @Test
	public void testGetBillingStatus_PastDueInvoicesIsPastDue() throws Exception {
		List<Invoice> invoices = new ArrayList<Invoice>();
		Invoice invoice = mock(Invoice.class);
		when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
		when(invoice.getDueDate()).thenReturn(DateBean.addDays(new Date(), -10));
		invoices.add(invoice);
		contractorUnderTest.setInvoices(invoices);

		setupFees(true, true);
		contractorUnderTest.setPaymentExpires(DateBean.addDays(new Date(), 46));

		// must have at least one paying facility or it'll be current
		contractorUnderTest.setPayingFacilities(1);

		contractorUnderTest.setStatus(AccountStatus.Active);
		contractorUnderTest.setRenew(true);
		contractorUnderTest.setAccountLevel(AccountLevel.Full);
		contractorUnderTest.setMembershipDate(DateBean.addDays(new Date(), -380));

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isPastDue());
	}

	@Test
	public void testGetBillingStatus_90DaysPastRenewalDateAndRenewFalseIsCancelled() throws Exception {
		contractorUnderTest.setRenew(false);
		setupFees(true, true);

		BillingStatus billingStatus = commonCancelledRenewed90DaysPastPaymentExpires();

		assertTrue(billingStatus.isCancelled());
	}

	@Test
	public void testGetBillingStatus_90DaysPastRenewalDateAndRenewTrueIsReactivation() throws Exception {
		contractorUnderTest.setRenew(true);
		setupFees(true, true);

		BillingStatus billingStatus = commonCancelledRenewed90DaysPastPaymentExpires();

		assertTrue(billingStatus.isReactivation());
	}

	private BillingStatus commonCancelledRenewed90DaysPastPaymentExpires() {
		contractorUnderTest.setPaymentExpires(DateBean.addDays(new Date(), -91));
		// must have at least one paying facility or it'll be current
		contractorUnderTest.setPayingFacilities(1);
		contractorUnderTest.setStatus(AccountStatus.Active);
		contractorUnderTest.setAccountLevel(AccountLevel.Full);
		contractorUnderTest.setMembershipDate(DateBean.addDays(new Date(), -380));

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();
		return billingStatus;
	}

	@Test
	public void testGetBillingStatus_DeactivatedRenewIsReactivation() throws Exception {
		// must have at least one paying facility or it'll be current
		contractorUnderTest.setPayingFacilities(1);

		contractorUnderTest.setStatus(AccountStatus.Deactivated);
		contractorUnderTest.setRenew(true);
		contractorUnderTest.setAccountLevel(AccountLevel.Full);
		contractorUnderTest.setMembershipDate(DateBean.addDays(new Date(), -380));

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isReactivation());
	}

	@Test
	public void testGetBillingStatus_DeactivatedNonRenewIsCancelled() throws Exception {
		// must have at least one paying facility or it'll be current
		contractorUnderTest.setPayingFacilities(1);

		contractorUnderTest.setStatus(AccountStatus.Deactivated);
		contractorUnderTest.setRenew(false);
		contractorUnderTest.setAccountLevel(AccountLevel.Full);
		contractorUnderTest.setMembershipDate(DateBean.addDays(new Date(), -380));

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isCancelled());
	}

	@Test
	public void testGetBillingStatus_ActiveFullNoMembershipDateIsActivation() throws Exception {
		// must have at least one paying facility or it'll be current
		contractorUnderTest.setPayingFacilities(1);

		contractorUnderTest.setStatus(AccountStatus.Active);
		contractorUnderTest.setAccountLevel(AccountLevel.Full);
		contractorUnderTest.setMembershipDate(null);

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isActivation());
	}

	@Test
	public void testGetBillingStatus_PendingFullNoMembershipDateIsActivation() throws Exception {
		// must have at least one paying facility or it'll be current
		contractorUnderTest.setPayingFacilities(1);

		contractorUnderTest.setStatus(AccountStatus.Pending);
		contractorUnderTest.setAccountLevel(AccountLevel.Full);
		contractorUnderTest.setMembershipDate(null);

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isActivation());
	}

	@Test
	public void testGetBillingStatus_NoMustPayIsCurrent() throws Exception {
		contractorUnderTest.setMustPay("No");
		contractorUnderTest.setPayingFacilities(1);
		contractorUnderTest.setStatus(AccountStatus.Active);

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isCurrent());
	}

	@Test
	public void testGetBillingStatus_NoPayingFacilitiesIsCurrent() throws Exception {
		contractorUnderTest.setMustPay("No");
		contractorUnderTest.setPayingFacilities(0);
		contractorUnderTest.setStatus(AccountStatus.Active);

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isCurrent());
	}

	@Test
	public void testGetBillingStatus_DemoAccountIsCurrent() throws Exception {
		contractorUnderTest.setMustPay("Yes");
		contractorUnderTest.setPayingFacilities(1);
		contractorUnderTest.setStatus(AccountStatus.Demo);

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isCurrent());
	}

	@Test
	public void testGetBillingStatus_DeletedAccountIsCurrent() throws Exception {
		contractorUnderTest.setMustPay("Yes");
		contractorUnderTest.setPayingFacilities(1);
		contractorUnderTest.setStatus(AccountStatus.Deleted);

		BillingStatus billingStatus = contractorUnderTest.getBillingStatus();

		assertTrue(billingStatus.isCurrent());
	}

	@Test
	public void testCreditCard_expiresToday() {
		contractor.setCcExpiration(new Date());
		contractor.setCcOnFile(true);
		assertTrue(contractor.isCcValid());
	}

	@Test
	public void testCreditCard_expiredTwoMonthsAgo() {
		contractor.setCcOnFile(true);
		contractor.setCcExpiration(DateBean.addMonths(new Date(), -2));
		assertFalse(contractor.isCcValid());
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenNoOperatorAssociationExistsReturnsFalse() {
		assertTrue(contractor.getOperators().size() == 0);
		assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenOneExpectedAssociationExistsReturnsTrue() {
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());

		assertTrue(contractor.getOperators().size() == 1);
		assertTrue(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenOneUnexpectedAssociationExistsReturnsFalse() {
		EntityFactory.addContractorOperator(contractor, new OperatorAccount());

		assertTrue(contractor.getOperators().size() == 1);
		assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenOnlyExclusiveAssociationsExistReturnsTrue() {
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());

		assertTrue(contractor.getOperators().size() > 1);
		assertTrue(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}

	@Test
	public void testIsOnlyAssociatedWith_WhenOneNonexclusiveAssociationOfManyExistsReturnsFalse() {
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeOperator());
		EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());

		assertTrue(contractor.getOperators().size() > 1);
		assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
	}

	@Ignore
	@Test
	public void testGetCompleteAnnualUpdates() {
		OperatorAccount operator = EntityFactory.makeOperator();
		ContractorAudit auditThreeYears = EntityFactory.makeContractorAudit(AuditType.ANNUALADDENDUM, contractor);
		ContractorAudit auditTwoYears = EntityFactory.makeContractorAudit(AuditType.ANNUALADDENDUM, contractor);
		ContractorAudit auditLastYear = EntityFactory.makeContractorAudit(AuditType.ANNUALADDENDUM, contractor);

		ContractorAuditOperator caoThreeYears = EntityFactory.addCao(auditThreeYears, operator);
		ContractorAuditOperator caoTwoYears = EntityFactory.addCao(auditTwoYears, operator);
		ContractorAuditOperator caoLastYear = EntityFactory.addCao(auditLastYear, operator);

		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);
		auditLastYear.setAuditFor("" + (currentYear - 1));
		auditTwoYears.setAuditFor("" + (currentYear - 2));
		auditThreeYears.setAuditFor("" + (currentYear - 3));
		cal.add(Calendar.YEAR, 3);
		auditThreeYears.setExpiresDate(cal.getTime());
		auditTwoYears.setExpiresDate(cal.getTime());
		auditLastYear.setExpiresDate(cal.getTime());

		contractor.getAudits().add(auditThreeYears);
		contractor.getAudits().add(auditTwoYears);
		contractor.getAudits().add(auditLastYear);

		caoThreeYears.setStatus(AuditStatus.Pending);
		caoTwoYears.setStatus(AuditStatus.Pending);
		caoLastYear.setStatus(AuditStatus.Pending);
		assertNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.LastYearOnly));
		assertNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.TwoYearsAgo));
		assertNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.ThreeYearsAgo));

		caoThreeYears.setStatus(AuditStatus.Pending);
		caoTwoYears.setStatus(AuditStatus.Complete);
		caoLastYear.setStatus(AuditStatus.Complete);
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.LastYearOnly));
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.TwoYearsAgo));
		assertNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.ThreeYearsAgo));

		caoThreeYears.setStatus(AuditStatus.Complete);
		caoTwoYears.setStatus(AuditStatus.Pending);
		caoLastYear.setStatus(AuditStatus.Complete);
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.LastYearOnly));
		assertNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.TwoYearsAgo));
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.ThreeYearsAgo));

		caoThreeYears.setStatus(AuditStatus.Complete);
		caoTwoYears.setStatus(AuditStatus.Complete);
		caoLastYear.setStatus(AuditStatus.Pending);
		assertNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.LastYearOnly));
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.TwoYearsAgo));
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.ThreeYearsAgo));

		caoThreeYears.setStatus(AuditStatus.Complete);
		caoTwoYears.setStatus(AuditStatus.Complete);
		caoLastYear.setStatus(AuditStatus.Complete);
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.LastYearOnly));
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.TwoYearsAgo));
		assertNotNull(contractor.getCompleteAnnualUpdates().get(MultiYearScope.ThreeYearsAgo));

	}

	@Test
	public void testHasVatId_null() {
		ContractorAccount testAccount = new ContractorAccount();
		testAccount.setVatId(null);
		assertFalse(testAccount.hasVatId());
	}

	@Test
	public void testHasVatId_emptyString() {
		ContractorAccount testAccount = new ContractorAccount();
		testAccount.setVatId("");
		assertFalse(testAccount.hasVatId());
	}

	@Test
	public void testHasVatId_pass() {
		ContractorAccount testaAccount = new ContractorAccount();
		testaAccount.setVatId("foo");
		assertTrue(testaAccount.hasVatId());
	}

	@Test
	public void testOnlyWorksFor_oneOperator_true() {
		testOperators.add(CO_1);
		contractorUnderTest.setOperators(testOperators);
		assertTrue(contractorUnderTest.onlyWorksFor(testOperator1));
	}

	@Test
	public void testOnlyWorksFor_oneOperator_false() {
		testOperators.add(CO_1);
		testOperators.add(CO_2);
		assertFalse(contractorUnderTest.onlyWorksFor(testOperator1));
	}

	@Test
	public void testOnlyWorksFor_OperatorList_true() {
		List<OperatorAccount> checkList = new ArrayList<OperatorAccount>();
		checkList.add(testOperator1);
		checkList.add(testOperator2);
		testOperators.add(CO_1);
		testOperators.add(CO_2);
		contractorUnderTest.setOperators(testOperators);
		assertTrue(contractorUnderTest.onlyWorksFor(checkList));
	}

	@Test
	public void testOnlyWorksFor_OperatorList_InclusiveTrue() {
		List<OperatorAccount> checkList = new ArrayList<OperatorAccount>();
		checkList.add(testOperator1);
		checkList.add(testOperator2);
		testOperators.add(CO_1);
		contractorUnderTest.setOperators(testOperators);
		assertTrue(contractorUnderTest.onlyWorksFor(checkList));
	}

	@Test
	public void testOnlyWorksFor_OperatorList_false() {
		List<OperatorAccount> checkList = new ArrayList<OperatorAccount>();
		checkList.add(testOperator1);
		testOperators.add(CO_1);
		testOperators.add(CO_2);
		contractorUnderTest.setOperators(testOperators);
		assertFalse(contractorUnderTest.onlyWorksFor(checkList));
	}

	@Test
	public void testOnlyWorksFor_OperatorList_InheritedTrue() {
		testOperator2.setParent(testOperator3);
		List<OperatorAccount> checkList = new ArrayList<OperatorAccount>();
		checkList.add(testOperator1);
		checkList.add(testOperator3);
		testOperators.add(CO_1);
		testOperators.add(CO_2);
		contractorUnderTest.setOperators(testOperators);
		assertTrue(contractorUnderTest.onlyWorksFor(checkList));
	}

	@Test
	public void testOnlyWorksFor_operatorID_true() {
		testOperators.add(CO_3);
		contractorUnderTest.setOperators(testOperators);
		assertTrue(contractorUnderTest.onlyWorksFor(TEST_OPERATOR_3_ID));
	}

	@Test
	public void testOnlyWorksFor_operatorID_false() {
		testOperators.add(CO_3);
		testOperators.add(CO_2);
		contractorUnderTest.setOperators(testOperators);
		assertFalse(contractorUnderTest.onlyWorksFor(TEST_OPERATOR_3_ID));
	}

	@Test
	public void testOnlyWorksFor_operatorParentIsTrue() {
		testOperators.add(CO_2);
		testOperators.add(CO_1);
		testOperator2.setParent(testOperator3);
		contractorUnderTest.setOperators(testOperators);
		assertTrue(contractorUnderTest.onlyWorksFor(new int[] { TEST_OPERATOR_3_ID, TEST_OPERATOR_1_ID }));
	}

	@Test
	public void testOnlyWorksFor_operatorParentIsTrue2() {
		testOperators.add(CO_2);
		testOperator2.setParent(testOperator3);
		contractorUnderTest.setOperators(testOperators);
		assertTrue(contractorUnderTest.onlyWorksFor(TEST_OPERATOR_2_ID));
	}

	@Test
	public void testIsListOnlyEligible_CEDA_CANADA() {
		// Set up CEDA Canada
		OperatorAccount CC = new OperatorAccount();
		CC.setId(OperatorAccount.CEDA_CANADA);
		CC.setName("CEDA_CANADA");
		ContractorOperator ConOpCC = new ContractorOperator();
		ConOpCC.setContractorAccount(contractorUnderTest);
		ConOpCC.setOperatorAccount(CC);
		testOperators.add(ConOpCC);

		// Set up test criteria
		setInternalState(contractorUnderTest, "materialSupplier", true);
		setInternalState(contractorUnderTest, "productRisk", LowMedHigh.Med);
		contractorUnderTest.setOperators(testOperators);

		assertTrue(contractorUnderTest.isListOnlyEligible());
	}

	@Test
	public void testIsListOnlyEligible_CEDA_USA() {
		// Set up CEDA USA
		OperatorAccount C_US = new OperatorAccount();
		C_US.setId(OperatorAccount.CEDA_USA);
		C_US.setName("CEDA_USA");
		ContractorOperator ConOpC_US = new ContractorOperator();
		ConOpC_US.setContractorAccount(contractorUnderTest);
		ConOpC_US.setOperatorAccount(C_US);
		testOperators.add(ConOpC_US);

		// Set up test criteria
		setInternalState(contractorUnderTest, "materialSupplier", true);
		setInternalState(contractorUnderTest, "productRisk", LowMedHigh.High);
		contractorUnderTest.setOperators(testOperators);

		assertTrue(contractorUnderTest.isListOnlyEligible());
	}

	@Test
	public void testIsListOnlyEligible_CEDA_Both() {
		// Set up CEDA Canada
		OperatorAccount CC = new OperatorAccount();
		CC.setId(OperatorAccount.CEDA_CANADA);
		CC.setName("CEDA_CANADA");
		ContractorOperator ConOpCC = new ContractorOperator();
		ConOpCC.setContractorAccount(contractorUnderTest);
		ConOpCC.setOperatorAccount(CC);
		testOperators.add(ConOpCC);

		// Set up CEDA USA
		OperatorAccount C_US = new OperatorAccount();
		C_US.setId(OperatorAccount.CEDA_USA);
		C_US.setName("CEDA_USA");
		ContractorOperator ConOpC_US = new ContractorOperator();
		ConOpC_US.setContractorAccount(contractorUnderTest);
		ConOpC_US.setOperatorAccount(C_US);
		testOperators.add(ConOpC_US);

		// Set up test criteria
		setInternalState(contractorUnderTest, "materialSupplier", true);
		setInternalState(contractorUnderTest, "productRisk", LowMedHigh.High);
		contractorUnderTest.setOperators(testOperators);

		assertTrue(contractorUnderTest.isListOnlyEligible());
	}

	@Test
	public void testIsListOnlyEligible_CEDA_None() {
		testOperators.add(CO_1);
		setInternalState(contractorUnderTest, "materialSupplier", true);
		setInternalState(contractorUnderTest, "productRisk", LowMedHigh.High);
		contractorUnderTest.setOperators(testOperators);

		assertFalse(contractorUnderTest.isListOnlyEligible());
	}

	@Test
	public void testValidVat_isUK() throws Exception {
		when(country.getIsoCode()).thenReturn(Country.UK_ISO_CODE);
		when(countryDAO.findbyISO(Country.UK_ISO_CODE)).thenReturn(country);
		contractorUnderTest.setCountry(country);

		boolean validationResult = contractorUnderTest.isValidVAT("145798");

		assertTrue(validationResult);
	}

    @Test
    public void testDoesNotHaveOperatorWithCompetencyRequiringDocumentation() throws Exception {
        assertFalse("contractorUnderTest has no operators",
                contractorUnderTest.hasOperatorWithCompetencyRequiringDocumentation());

        List<ContractorOperator> contractorOperators = new ArrayList<>();
        ContractorOperator contractorOperator = mock(ContractorOperator.class);
        contractorOperators.add(contractorOperator);
        OperatorAccount operatorAccount = mock(OperatorAccount.class);

        when(contractorOperator.getOperatorAccount()).thenReturn(operatorAccount);
        contractorUnderTest.setOperators(contractorOperators);

        assertFalse("contractorUnderTest has no operator with a competency requiring documentation",
                contractorUnderTest.hasOperatorWithCompetencyRequiringDocumentation());
    }

    @Test
    public void testHasOperatorWithCompetencyRequiringDocumentation() throws Exception {
        List<ContractorOperator> contractorOperators = new ArrayList<>();
        ContractorOperator contractorOperator = mock(ContractorOperator.class);
        contractorOperators.add(contractorOperator);
        OperatorAccount operatorAccount = mock(OperatorAccount.class);

        when(contractorOperator.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.hasCompetencyRequiringDocumentation()).thenReturn(true);
        contractorUnderTest.setOperators(contractorOperators);

        assertTrue("contractorUnderTest has at least one operator with a competency requiring documentation",
                contractorUnderTest.hasOperatorWithCompetencyRequiringDocumentation());
    }
}