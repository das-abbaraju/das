package com.picsauditing.flagcalculator.entities;

import com.picsauditing.flagcalculator.EntityFactory;
import com.picsauditing.flagcalculator.service.AuditService;
import com.picsauditing.flagcalculator.service.TradeService;
import com.picsauditing.flagcalculator.util.YearList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContractorAccountTest {
    private ContractorAccount contractor;
//    private ContractorAccount contractorUnderTest;
//    private List<ContractorOperator> testOperators;
//    private OperatorAccount testOperator1 = new OperatorAccount();
//    private OperatorAccount testOperator2 = new OperatorAccount();
//    private OperatorAccount testOperator3 = new OperatorAccount();
//    private static final int TEST_OPERATOR_1_ID = 5;
//    private static final int TEST_OPERATOR_2_ID = 55;
//    private static final int TEST_OPERATOR_3_ID = 555;
//    private ContractorOperator CO_1 = new ContractorOperator();
//    private ContractorOperator CO_2 = new ContractorOperator();
//    private ContractorOperator CO_3 = new ContractorOperator();
//    private Map<FeeClass, ContractorFee> fees;
//    private List<AccountUser> accountUsers;
//
//    @Mock
//    private ContractorFee bidOnlyFee;
//    @Mock
//    private Country country;
//    @Mock
//    private ContractorFee listOnlyFee;
//    @Mock
//    private ContractorFee upgradeFee;
//    @Mock
//    private InvoiceFee bidOnlyInvoiceFee;
//    @Mock
//    private InvoiceFee listOnlyinvoiceFee;
//    @Mock
//    private InvoiceFee upgradeinvoiceFee;
//
//    @Mock
//    private CountryDAO countryDAO;
//
//    public ContractorAccountTest() {
//        testOperator1.setId(TEST_OPERATOR_1_ID);
//        testOperator2.setId(TEST_OPERATOR_2_ID);
//        testOperator3.setId(TEST_OPERATOR_3_ID);
//        testOperator1.setStatus(AccountStatus.Active);
//        testOperator2.setStatus(AccountStatus.Active);
//        testOperator3.setStatus(AccountStatus.Active);
//        testOperator1.setName("Test Operator 1");
//        testOperator2.setName("Test Operator 2");
//        testOperator3.setName("Test Operator 3");
//        CO_1.setContractorAccount(contractorUnderTest);
//        CO_2.setContractorAccount(contractorUnderTest);
//        CO_3.setContractorAccount(contractorUnderTest);
//        CO_1.setOperatorAccount(testOperator1);
//        CO_2.setOperatorAccount(testOperator2);
//        CO_3.setOperatorAccount(testOperator3);
//    }
//
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        contractor = EntityFactory.makeContractor();
//        contractorUnderTest = new ContractorAccount();
//        testOperators = new ArrayList<>();
//        accountUsers = new ArrayList<>();
//
//        when(upgradeFee.isUpgrade()).thenReturn(true);
//        contractorUnderTest.setAccountUsers(accountUsers);
//        Whitebox.setInternalState(contractorUnderTest, "countryDAO", countryDAO);
//        Whitebox.setInternalState(contractorUnderTest, "inputValidator", new InputValidator());
//        Whitebox.setInternalState(contractorUnderTest, "vatValidator", new VATValidator());
    }

//    @After
//    public void cleanup() {
//        testOperator2.setParent(null);
//    }
//
//    private void setupFees(boolean bidOnly, boolean listOnly) {
//        // all contractors have bid only and list only fees, they may just be
//        // hidden and $0
//        fees = new HashMap<>();
//        contractorUnderTest.setFees(fees);
//
//        fees.put(FeeClass.BidOnly, bidOnlyFee);
//        fees.put(FeeClass.ListOnly, listOnlyFee);
//        when(bidOnlyFee.getCurrentLevel()).thenReturn(bidOnlyInvoiceFee);
//        when(listOnlyFee.getCurrentLevel()).thenReturn(listOnlyinvoiceFee);
//        when(upgradeFee.getCurrentLevel()).thenReturn(upgradeinvoiceFee);
//        when(bidOnlyInvoiceFee.isFree()).thenReturn(bidOnly);
//        when(bidOnlyInvoiceFee.isBidonly()).thenReturn(true);
//        when(listOnlyinvoiceFee.isFree()).thenReturn(listOnly);
//    }
//
@Test
public void testGetWeightedIndustryAverage_NoSelfPerformedTrades() {
    ContractorAccount contractor = new ContractorAccount();
    contractor.setTrades(new HashSet<ContractorTrade>());
    contractor.getTrades().add(makeContractorTrade(makeTrade(1, null, 2.0f), 5, false));
    contractor.getTrades().add(makeContractorTrade(makeTrade(2, null, 8.0f), 5, false));

    assertEquals(5, TradeService.getWeightedIndustryAverage(contractor), 1);
}

    @Test
    public void testGetWeightedIndustryAverage_SelfPerformedTrades() {
        ContractorAccount contractor = new ContractorAccount();
        contractor.setTrades(new HashSet<ContractorTrade>());
        contractor.getTrades().add(makeContractorTrade(makeTrade(1, null, 2.0f), 5, true));
        contractor.getTrades().add(makeContractorTrade(makeTrade(2, null, 8.0f), 5, false));

        assertEquals(2, TradeService.getWeightedIndustryAverage(contractor), 1);
    }

    @Test
    public void testGetTopTrade_NoTrade() {
        ContractorAccount contractor = new ContractorAccount();
        assertNull(contractor.getTopTrade());
    }

    @Test
    public void testGetTopTrade_OnlySelfPerformed() {
        ContractorAccount contractor = new ContractorAccount();
        contractor.setTrades(new HashSet<ContractorTrade>());

        Trade subcontractedTrir = makeTrade(1, null, 4.0f);
        ContractorTrade subcontractorTrade = makeContractorTrade(subcontractedTrir, 5, false);
        Trade selfTrir = makeTrade(1, null, 4.0f);
        ContractorTrade selfTrade = makeContractorTrade(selfTrir, 5, true);

        contractor.getTrades().add(subcontractorTrade);
        contractor.getTrades().add(selfTrade);

        assertEquals(selfTrade, contractor.getTopTrade());
    }

    @Test
    public void testGetTopTrade_MultipleSelfTrades_HighestActivity() {
        ContractorAccount contractor = new ContractorAccount();
        contractor.setTrades(new HashSet<ContractorTrade>());

        Trade lowTrir = makeTrade(1, null, 2.0f);
        ContractorTrade lowTrade = makeContractorTrade(lowTrir, 2, true);
        ContractorTrade highTrade = makeContractorTrade(lowTrir, 5, true);

        contractor.getTrades().add(lowTrade);
        contractor.getTrades().add(highTrade);

        assertEquals(highTrade, contractor.getTopTrade());
    }

    @Test
    public void testGetTopTrade_MultipleSelfTrades_HighestTrir() {
        ContractorAccount contractor = new ContractorAccount();
        contractor.setTrades(new HashSet<ContractorTrade>());

        Trade lowTrir = makeTrade(1, null, 2.0f);
        ContractorTrade lowTrade = makeContractorTrade(lowTrir, 5, true);
        Trade highTrir = makeTrade(1, null, 4.0f);
        ContractorTrade highTrade = makeContractorTrade(highTrir, 5, true);

        contractor.getTrades().add(lowTrade);
        contractor.getTrades().add(highTrade);

        assertEquals(highTrade, contractor.getTopTrade());
    }

    @Test
    public void testGetTopTrade_MultipleSelfTrades_HighestTrirParent() {
        ContractorAccount contractor = new ContractorAccount();
        contractor.setTrades(new HashSet<ContractorTrade>());

        Trade lowTrir = makeTrade(1, null, 2.0f);
        ContractorTrade lowTrade = makeContractorTrade(lowTrir, 5, true);
        Trade highTrir = makeTrade(1, null, 4.0f);
        Trade noTrir = makeTrade(1, highTrir, null);
        ContractorTrade highTrade = makeContractorTrade(noTrir, 5, true);

        contractor.getTrades().add(lowTrade);
        contractor.getTrades().add(highTrade);

        assertEquals(highTrade, contractor.getTopTrade());
    }

    private ContractorTrade makeContractorTrade(Trade trade, int activity, boolean selfPerformed) {
        ContractorTrade cTrade = new ContractorTrade();
        cTrade.setTrade(trade);
        cTrade.setActivityPercent(activity);
        cTrade.setSelfPerformed(selfPerformed);

        return cTrade;
    }

    private Trade makeTrade(int tradeId, Trade parentTrade, Float trir) {
        Trade trade = new Trade();
        trade.setNaicsTRIR(trir);
        trade.setId(tradeId);
        trade.setParent(parentTrade);
        if (parentTrade != null) {
            parentTrade.getChildren().add(trade);
        }

        return trade;
    }

    @Test
    public void testGetAfterPendingAnnualUpdates_3Pending() {
        ContractorAccount contractor = EntityFactory.makeContractor();

        ContractorAudit lastYear = createAnnualUpdate(-1, contractor, AuditStatus.Pending);
        ContractorAudit twoYears = createAnnualUpdate(-2, contractor, AuditStatus.Pending);
        ContractorAudit threeYears = createAnnualUpdate(-3, contractor, AuditStatus.Pending);

        assertEquals(null, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.LastYearOnly));
        assertEquals(null, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.TwoYearsAgo));
        assertEquals(null, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.ThreeYearsAgo));
    }

    @Test
    public void testGetAfterPendingAnnualUpdates_MiddlePending() {
        ContractorAccount contractor = EntityFactory.makeContractor();

        ContractorAudit lastYear = createAnnualUpdate(-1, contractor, AuditStatus.Complete);
        ContractorAudit twoYears = createAnnualUpdate(-2, contractor, AuditStatus.Pending);
        ContractorAudit threeYears = createAnnualUpdate(-3, contractor, AuditStatus.Complete);

        assertEquals(lastYear, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.LastYearOnly));
        assertEquals(null, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.TwoYearsAgo));
        assertEquals(threeYears, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.ThreeYearsAgo));
    }


    @Test
    public void testGetAfterPendingAnnualUpdates_RecentPending() {
        ContractorAccount contractor = EntityFactory.makeContractor();

        YearList yearList = createTestYearList();

        ContractorAudit lastYear = createAnnualUpdate(-1, contractor, AuditStatus.Pending);
        ContractorAudit twoYears = createAnnualUpdate(-2, contractor, AuditStatus.Complete);
        ContractorAudit threeYears = createAnnualUpdate(-3, contractor, AuditStatus.Complete);

        Map<MultiYearScope, ContractorAudit> annualUpdates = AuditService.getAfterPendingAnnualUpdates(contractor);

        assertEquals(twoYears, annualUpdates.get(MultiYearScope.LastYearOnly));
        assertEquals(threeYears, annualUpdates.get(MultiYearScope.TwoYearsAgo));
        assertEquals(null, annualUpdates.get(MultiYearScope.ThreeYearsAgo));
    }

    @Test
    public void testGetAfterPendingAnnualUpdates_RecentPendingFourYears() {
        ContractorAccount contractor = EntityFactory.makeContractor();

        YearList yearList = createTestYearList();

        ContractorAudit lastYear = createAnnualUpdate(-1, contractor, AuditStatus.Pending);
        ContractorAudit twoYears = createAnnualUpdate(-2, contractor, AuditStatus.Complete);
        ContractorAudit threeYears = createAnnualUpdate(-3, contractor, AuditStatus.Complete);
        ContractorAudit fourYears = createAnnualUpdate(-4, contractor, AuditStatus.Complete);

        Map<MultiYearScope, ContractorAudit> annualUpdates = AuditService.getAfterPendingAnnualUpdates(contractor);

        assertEquals(twoYears, annualUpdates.get(MultiYearScope.LastYearOnly));
        assertEquals(threeYears, annualUpdates.get(MultiYearScope.TwoYearsAgo));
        assertEquals(fourYears, annualUpdates.get(MultiYearScope.ThreeYearsAgo));
    }

    private YearList createTestYearList() {
        YearList yearlist = new YearList();
        Calendar d = Calendar.getInstance();
        d.set(d.get(Calendar.YEAR), 0, 1);
        yearlist.setToday(d.getTime());

        return yearlist;
    }

    @Test
    public void testGetAfterPendingAnnualUpdates_FourYears() {
        ContractorAccount contractor = EntityFactory.makeContractor();

        ContractorAudit lastYear = createAnnualUpdate(-1, contractor, AuditStatus.Complete);
        ContractorAudit twoYears = createAnnualUpdate(-2, contractor, AuditStatus.Complete);
        ContractorAudit threeYears = createAnnualUpdate(-3, contractor, AuditStatus.Complete);
        ContractorAudit fourYears = createAnnualUpdate(-4, contractor, AuditStatus.Complete);

        assertEquals(lastYear, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.LastYearOnly));
        assertEquals(twoYears, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.TwoYearsAgo));
        assertEquals(threeYears, AuditService.getAfterPendingAnnualUpdates(contractor).get(MultiYearScope.ThreeYearsAgo));
    }

    private ContractorAudit createAnnualUpdate(int yearsAgo, ContractorAccount contractor, AuditStatus status) {
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.ANNUALADDENDUM, contractor);

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.YEAR, 3);

        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.YEAR, yearsAgo);
        int year = currentDate.get(Calendar.YEAR);

        audit.setAuditFor("" + year);
        audit.setExpiresDate(expirationDate.getTime());
        addCao(audit, EntityFactory.makeOperator(), status);

        return audit;
    }

    private void addCao(ContractorAudit audit, OperatorAccount operator, AuditStatus status) {
        ContractorAuditOperator cao = new ContractorAuditOperator();
        cao.setAudit(audit);
//        cao.changeStatus(status, null);
        cao.setOperator(operator);
        audit.getOperators().add(cao);

        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
//        caop.setCao(cao);
        caop.setOperator(operator);
        cao.getCaoPermissions().add(caop);
    }

//    @Test
//    public void testGetCurrentCsr_ReturnsCurrentCsr() throws Exception {
//        User currentCsr = mock(User.class);
//        AccountUser currentCsrAccountUser = currentCsr();
//        when(currentCsrAccountUser.getUser()).thenReturn(currentCsr);
//        accountUsers.add(oldCsr());
//        accountUsers.add(currentCsrAccountUser);
//        accountUsers.add(currentSalesRep());
//
//        assertEquals(currentCsr, contractorUnderTest.getCurrentCsr());
//    }
//
//    @Test
//    public void testGetCurrentCsr_ReturnsNullIfNoCurrentCsr() throws Exception {
//        accountUsers.add(oldCsr());
//        accountUsers.add(currentSalesRep());
//
//        assertEquals(null, contractorUnderTest.getCurrentCsr());
//    }
//
//    @Test
//    public void testSetCurrentCsr_ExpiresCurrentCsr() throws Exception {
//        User newCsr = mock(User.class);
//        AccountUser currentCsr = currentCsr();
//        when(currentCsr.getUser()).thenReturn(newCsr);
//
//        contractorUnderTest.setCurrentCsr(newCsr, 1);
//
//        User newCurrentCsr = contractorUnderTest.getCurrentCsr();
//        assertEquals(newCsr, newCurrentCsr);
//    }
//
//    @Test
//    public void testSetCurrentCsr_setsNewCsr() throws Exception {
//        User newCsr = mock(User.class);
//        AccountUser currentCsr = currentCsr();
//        when(currentCsr.getUser()).thenReturn(newCsr);
//        AccountUser oldCsr = oldCsr();
//        AccountUser salesRep = currentSalesRep();
//        accountUsers.add(currentCsr);
//        accountUsers.add(oldCsr);
//        accountUsers.add(salesRep);
//
//        contractorUnderTest.setCurrentCsr(newCsr, 1);
//
//        verify(currentCsr).setEndDate(any(Date.class));
//        verify(oldCsr, never()).setEndDate(any(Date.class));
//        verify(salesRep, never()).setEndDate(any(Date.class));
//    }
//
//
//    private AccountUser currentSalesRep() {
//        AccountUser currentSalesRep = mock(AccountUser.class);
//        when(currentSalesRep.isCurrent()).thenReturn(true);
//        when(currentSalesRep.getRole()).thenReturn(UserAccountRole.PICSSalesRep);
//        return currentSalesRep;
//    }
//
//    private AccountUser currentCsr() {
//        AccountUser currentCsr = mock(AccountUser.class);
//        when(currentCsr.isCurrent()).thenReturn(true);
//        when(currentCsr.getRole()).thenReturn(UserAccountRole.PICSCustomerServiceRep);
//        return currentCsr;
//    }
//
//    private AccountUser oldCsr() {
//        AccountUser oldCsr = mock(AccountUser.class);
//        when(oldCsr.isCurrent()).thenReturn(false);
//        when(oldCsr.getRole()).thenReturn(UserAccountRole.PICSCustomerServiceRep);
//        return oldCsr;
//    }
//
//    @Test
//    public void testCreditCard_expiresToday() {
//        contractor.setCcExpiration(new Date());
//        contractor.setCcOnFile(true);
//        assertTrue(contractor.isCcValid());
//    }
//
//    @Test
//    public void testCreditCard_expiredTwoMonthsAgo() {
//        contractor.setCcOnFile(true);
//        contractor.setCcExpiration(DateBean.addMonths(new Date(), -2));
//        assertFalse(contractor.isCcValid());
//    }
//
//    @Test
//    public void testIsOnlyAssociatedWith_WhenNoOperatorAssociationExistsReturnsFalse() {
//        assertTrue(contractor.getOperators().size() == 0);
//        assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
//    }
//
//    @Test
//    public void testIsOnlyAssociatedWith_WhenOneExpectedAssociationExistsReturnsTrue() {
//        EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
//
//        assertTrue(contractor.getOperators().size() == 1);
//        assertTrue(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
//    }
//
//    @Test
//    public void testIsOnlyAssociatedWith_WhenOneUnexpectedAssociationExistsReturnsFalse() {
//        EntityFactory.addContractorOperator(contractor, new OperatorAccount());
//
//        assertTrue(contractor.getOperators().size() == 1);
//        assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
//    }
//
//    @Test
//    public void testIsOnlyAssociatedWith_WhenOnlyExclusiveAssociationsExistReturnsTrue() {
//        EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
//        EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
//        EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
//
//        assertTrue(contractor.getOperators().size() > 1);
//        assertTrue(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
//    }
//
//    @Test
//    public void testIsOnlyAssociatedWith_WhenOneNonexclusiveAssociationOfManyExistsReturnsFalse() {
//        EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
//        EntityFactory.addContractorOperator(contractor, EntityFactory.makeOperator());
//        EntityFactory.addContractorOperator(contractor, EntityFactory.makeSuncorOperator());
//
//        assertTrue(contractor.getOperators().size() > 1);
//        assertFalse(contractor.isOnlyAssociatedWith(OperatorAccount.SUNCOR));
//    }
//
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
        assertNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.LastYearOnly));
        assertNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.TwoYearsAgo));
        assertNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.ThreeYearsAgo));

        caoThreeYears.setStatus(AuditStatus.Pending);
        caoTwoYears.setStatus(AuditStatus.Complete);
        caoLastYear.setStatus(AuditStatus.Complete);
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.LastYearOnly));
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.TwoYearsAgo));
        assertNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.ThreeYearsAgo));

        caoThreeYears.setStatus(AuditStatus.Complete);
        caoTwoYears.setStatus(AuditStatus.Pending);
        caoLastYear.setStatus(AuditStatus.Complete);
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.LastYearOnly));
        assertNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.TwoYearsAgo));
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.ThreeYearsAgo));

        caoThreeYears.setStatus(AuditStatus.Complete);
        caoTwoYears.setStatus(AuditStatus.Complete);
        caoLastYear.setStatus(AuditStatus.Pending);
        assertNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.LastYearOnly));
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.TwoYearsAgo));
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.ThreeYearsAgo));

        caoThreeYears.setStatus(AuditStatus.Complete);
        caoTwoYears.setStatus(AuditStatus.Complete);
        caoLastYear.setStatus(AuditStatus.Complete);
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.LastYearOnly));
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.TwoYearsAgo));
        assertNotNull(AuditService.getCompleteAnnualUpdates(contractor).get(MultiYearScope.ThreeYearsAgo));

    }

//    @Test
//    public void testHasVatId_null() {
//        ContractorAccount testAccount = new ContractorAccount();
//        testAccount.setVatId(null);
//        assertFalse(testAccount.hasVatId());
//    }
//
//    @Test
//    public void testHasVatId_emptyString() {
//        ContractorAccount testAccount = new ContractorAccount();
//        testAccount.setVatId("");
//        assertFalse(testAccount.hasVatId());
//    }
//
//    @Test
//    public void testHasVatId_pass() {
//        ContractorAccount testaAccount = new ContractorAccount();
//        testaAccount.setVatId("foo");
//        assertTrue(testaAccount.hasVatId());
//    }
//
//    @Test
//    public void testOnlyWorksFor_oneOperator_true() {
//        testOperators.add(CO_1);
//        contractorUnderTest.setOperators(testOperators);
//        assertTrue(contractorUnderTest.onlyWorksFor(testOperator1));
//    }
//
//    @Test
//    public void testOnlyWorksFor_oneOperator_false() {
//        testOperators.add(CO_1);
//        testOperators.add(CO_2);
//        assertFalse(contractorUnderTest.onlyWorksFor(testOperator1));
//    }
//
//    @Test
//    public void testOnlyWorksFor_OperatorList_true() {
//        List<OperatorAccount> checkList = new ArrayList<OperatorAccount>();
//        checkList.add(testOperator1);
//        checkList.add(testOperator2);
//        testOperators.add(CO_1);
//        testOperators.add(CO_2);
//        contractorUnderTest.setOperators(testOperators);
//        assertTrue(contractorUnderTest.onlyWorksFor(checkList));
//    }
//
//    @Test
//    public void testOnlyWorksFor_OperatorList_InclusiveTrue() {
//        List<OperatorAccount> checkList = new ArrayList<OperatorAccount>();
//        checkList.add(testOperator1);
//        checkList.add(testOperator2);
//        testOperators.add(CO_1);
//        contractorUnderTest.setOperators(testOperators);
//        assertTrue(contractorUnderTest.onlyWorksFor(checkList));
//    }
//
//    @Test
//    public void testOnlyWorksFor_OperatorList_false() {
//        List<OperatorAccount> checkList = new ArrayList<OperatorAccount>();
//        checkList.add(testOperator1);
//        testOperators.add(CO_1);
//        testOperators.add(CO_2);
//        contractorUnderTest.setOperators(testOperators);
//        assertFalse(contractorUnderTest.onlyWorksFor(checkList));
//    }
//
//    @Test
//    public void testOnlyWorksFor_OperatorList_InheritedTrue() {
//        testOperator2.setParent(testOperator3);
//        List<OperatorAccount> checkList = new ArrayList<OperatorAccount>();
//        checkList.add(testOperator1);
//        checkList.add(testOperator3);
//        testOperators.add(CO_1);
//        testOperators.add(CO_2);
//        contractorUnderTest.setOperators(testOperators);
//        assertTrue(contractorUnderTest.onlyWorksFor(checkList));
//    }
//
//    @Test
//    public void testOnlyWorksFor_operatorID_true() {
//        testOperators.add(CO_3);
//        contractorUnderTest.setOperators(testOperators);
//        assertTrue(contractorUnderTest.onlyWorksFor(TEST_OPERATOR_3_ID));
//    }
//
//    @Test
//    public void testOnlyWorksFor_operatorID_false() {
//        testOperators.add(CO_3);
//        testOperators.add(CO_2);
//        contractorUnderTest.setOperators(testOperators);
//        assertFalse(contractorUnderTest.onlyWorksFor(TEST_OPERATOR_3_ID));
//    }
//
//    @Test
//    public void testOnlyWorksFor_operatorParentIsTrue() {
//        testOperators.add(CO_2);
//        testOperators.add(CO_1);
//        testOperator2.setParent(testOperator3);
//        contractorUnderTest.setOperators(testOperators);
//        assertTrue(contractorUnderTest.onlyWorksFor(new int[] { TEST_OPERATOR_3_ID, TEST_OPERATOR_1_ID }));
//    }
//
//    @Test
//    public void testOnlyWorksFor_operatorParentIsTrue2() {
//        testOperators.add(CO_2);
//        testOperator2.setParent(testOperator3);
//        contractorUnderTest.setOperators(testOperators);
//        assertTrue(contractorUnderTest.onlyWorksFor(TEST_OPERATOR_2_ID));
//    }
//
//    @Test
//    public void testIsListOnlyEligible_CEDA_CANADA() {
//        // Set up CEDA Canada
//        OperatorAccount CC = new OperatorAccount();
//        CC.setId(OperatorAccount.CEDA_CANADA);
//        CC.setName("CEDA_CANADA");
//        ContractorOperator ConOpCC = new ContractorOperator();
//        ConOpCC.setContractorAccount(contractorUnderTest);
//        ConOpCC.setOperatorAccount(CC);
//        testOperators.add(ConOpCC);
//
//        // Set up test criteria
//        setInternalState(contractorUnderTest, "materialSupplier", true);
//        setInternalState(contractorUnderTest, "productRisk", LowMedHigh.Med);
//        contractorUnderTest.setOperators(testOperators);
//
//        assertTrue(contractorUnderTest.isListOnlyEligible());
//    }
//
//    @Test
//    public void testIsListOnlyEligible_CEDA_USA() {
//        // Set up CEDA USA
//        OperatorAccount C_US = new OperatorAccount();
//        C_US.setId(OperatorAccount.CEDA_USA);
//        C_US.setName("CEDA_USA");
//        ContractorOperator ConOpC_US = new ContractorOperator();
//        ConOpC_US.setContractorAccount(contractorUnderTest);
//        ConOpC_US.setOperatorAccount(C_US);
//        testOperators.add(ConOpC_US);
//
//        // Set up test criteria
//        setInternalState(contractorUnderTest, "materialSupplier", true);
//        setInternalState(contractorUnderTest, "productRisk", LowMedHigh.High);
//        contractorUnderTest.setOperators(testOperators);
//
//        assertTrue(contractorUnderTest.isListOnlyEligible());
//    }
//
//    @Test
//    public void testIsListOnlyEligible_CEDA_Both() {
//        // Set up CEDA Canada
//        OperatorAccount CC = new OperatorAccount();
//        CC.setId(OperatorAccount.CEDA_CANADA);
//        CC.setName("CEDA_CANADA");
//        ContractorOperator ConOpCC = new ContractorOperator();
//        ConOpCC.setContractorAccount(contractorUnderTest);
//        ConOpCC.setOperatorAccount(CC);
//        testOperators.add(ConOpCC);
//
//        // Set up CEDA USA
//        OperatorAccount C_US = new OperatorAccount();
//        C_US.setId(OperatorAccount.CEDA_USA);
//        C_US.setName("CEDA_USA");
//        ContractorOperator ConOpC_US = new ContractorOperator();
//        ConOpC_US.setContractorAccount(contractorUnderTest);
//        ConOpC_US.setOperatorAccount(C_US);
//        testOperators.add(ConOpC_US);
//
//        // Set up test criteria
//        setInternalState(contractorUnderTest, "materialSupplier", true);
//        setInternalState(contractorUnderTest, "productRisk", LowMedHigh.High);
//        contractorUnderTest.setOperators(testOperators);
//
//        assertTrue(contractorUnderTest.isListOnlyEligible());
//    }
//
//    @Test
//    public void testIsListOnlyEligible_CEDA_None() {
//        testOperators.add(CO_1);
//        setInternalState(contractorUnderTest, "materialSupplier", true);
//        setInternalState(contractorUnderTest, "productRisk", LowMedHigh.High);
//        contractorUnderTest.setOperators(testOperators);
//
//        assertFalse(contractorUnderTest.isListOnlyEligible());
//    }
//
//    @Test
//    public void testValidVat_isUK() throws Exception {
//        when(country.getIsoCode()).thenReturn(Country.UK_ISO_CODE);
//        when(countryDAO.findbyISO(Country.UK_ISO_CODE)).thenReturn(country);
//        contractorUnderTest.setCountry(country);
//
//        boolean validationResult = contractorUnderTest.isValidVAT("145798");
//
//        assertTrue(validationResult);
//    }
//
//    @Test
//    public void testDoesNotHaveOperatorWithCompetencyRequiringDocumentation() throws Exception {
//        assertFalse("contractorUnderTest has no operators",
//                contractorUnderTest.hasOperatorWithCompetencyRequiringDocumentation());
//
//        List<ContractorOperator> contractorOperators = new ArrayList<>();
//        ContractorOperator contractorOperator = mock(ContractorOperator.class);
//        contractorOperators.add(contractorOperator);
//        OperatorAccount operatorAccount = mock(OperatorAccount.class);
//
//        when(contractorOperator.getOperatorAccount()).thenReturn(operatorAccount);
//        contractorUnderTest.setOperators(contractorOperators);
//
//        assertFalse("contractorUnderTest has no operator with a competency requiring documentation",
//                contractorUnderTest.hasOperatorWithCompetencyRequiringDocumentation());
//    }
//
//    @Test
//    public void testHasOperatorWithCompetencyRequiringDocumentation() throws Exception {
//        List<ContractorOperator> contractorOperators = new ArrayList<>();
//        ContractorOperator contractorOperator = mock(ContractorOperator.class);
//        contractorOperators.add(contractorOperator);
//        OperatorAccount operatorAccount = mock(OperatorAccount.class);
//
//        when(contractorOperator.getOperatorAccount()).thenReturn(operatorAccount);
//        when(operatorAccount.hasCompetencyRequiringDocumentation()).thenReturn(true);
//        contractorUnderTest.setOperators(contractorOperators);
//
//        assertTrue("contractorUnderTest has at least one operator with a competency requiring documentation",
//                contractorUnderTest.hasOperatorWithCompetencyRequiringDocumentation());
//    }
}