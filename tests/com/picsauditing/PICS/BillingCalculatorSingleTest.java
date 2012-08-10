package com.picsauditing.PICS;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;

public class BillingCalculatorSingleTest {
	private BillingCalculatorSingle billingService;

	@Mock private Database databaseForTesting;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		billingService = new BillingCalculatorSingle();

		assert(OAMocksSet.isEmpty());
	}


	@Test
	public void testSetPayingFacilities() {
		ContractorAccount timecGcCon = new ContractorAccount();
		timecGcCon.setId(1);
		ContractorAccount welder = new ContractorAccount();
		welder.setId(2);

		OperatorAccount timecGcOp = new OperatorAccount();
		timecGcOp.setId(3);
		timecGcOp.setStatus(AccountStatus.Active);
		OperatorAccount txi = new OperatorAccount();
		txi.setId(4);
		txi.setStatus(AccountStatus.Active);
		OperatorAccount basf = new OperatorAccount();
		basf.setId(5);
		basf.setStatus(AccountStatus.Active);
		OperatorAccount bp = new OperatorAccount();
		bp.setId(6);
		bp.setStatus(AccountStatus.Active);
		OperatorAccount tesoro = new OperatorAccount();
		tesoro.setId(7);
		tesoro.setStatus(AccountStatus.Active);

		ContractorOperator co = new ContractorOperator();

		// Welder COs
		co.setOperatorAccount(timecGcOp);
		co.setContractorAccount(welder);
		timecGcOp.getContractorOperators().add(co);
		welder.getOperators().add(co);

		co = new ContractorOperator();
		co.setOperatorAccount(tesoro);
		co.setContractorAccount(welder);
		tesoro.getContractorOperators().add(co);
		welder.getOperators().add(co);

		co = new ContractorOperator();
		co.setOperatorAccount(txi);
		co.setContractorAccount(welder);
		txi.getContractorOperators().add(co);
		welder.getOperators().add(co);

		co = new ContractorOperator();
		co.setOperatorAccount(bp);
		co.setContractorAccount(welder);
		bp.getContractorOperators().add(co);
		welder.getOperators().add(co);

		// Timec GC COs
		co = new ContractorOperator();
		co.setOperatorAccount(basf);
		co.setContractorAccount(timecGcCon);
		basf.getContractorOperators().add(co);
		timecGcCon.getOperators().add(co);

		co = new ContractorOperator();
		co.setOperatorAccount(bp);
		co.setContractorAccount(timecGcCon);
		bp.getContractorOperators().add(co);
		timecGcCon.getOperators().add(co);

		co = new ContractorOperator();
		co.setOperatorAccount(tesoro);
		co.setContractorAccount(timecGcCon);
		tesoro.getContractorOperators().add(co);
		timecGcCon.getOperators().add(co);

		co = new ContractorOperator();
		co.setOperatorAccount(timecGcOp);
		co.setContractorAccount(timecGcCon);
		timecGcOp.getContractorOperators().add(co);
		timecGcCon.getOperators().add(co);

		billingService.setPayingFacilities(welder);
		assertTrue(welder.getPayingFacilities() == 4);
	}



	@Mock OperatorAccount mockOA1;
	@Mock OperatorAccount mockOA2;
	Set<OperatorAccount> OAMocksSet = new HashSet<OperatorAccount>();

	@After
	public void clean() {
		OAMocksSet.clear();
	}

	@Test
	public void InsureGuardQualificationTest_yes () {
		OAMocksSet.add(mockOA1);
		OAMocksSet.add(mockOA2);
		when(mockOA1.getId()).thenReturn(333);
		when(mockOA2.getId()).thenReturn(555);
		assertTrue(billingService.qualifiesForInsureGuard(OAMocksSet));
	}

	@Test
	public void InsureGuardQualificationTest_no () {
		OAMocksSet.add(mockOA1);
		OAMocksSet.add(mockOA2);
		when(mockOA1.getId()).thenReturn(OperatorAccount.AI);
		when(mockOA2.getId()).thenReturn(OperatorAccount.CINTAS_CANADA);
		assertFalse(billingService.qualifiesForInsureGuard(OAMocksSet));
	}

	@Test
	public void InsureGuardQualificationTest_no2 () {
		OAMocksSet.add(mockOA1);
		OAMocksSet.add(mockOA2);
		when(mockOA1.getId()).thenReturn(333);
		when(mockOA2.getId()).thenReturn(OperatorAccount.OLDCASTLE);
		assertTrue(billingService.qualifiesForInsureGuard(OAMocksSet));
	}

	//Test for PICS-6344
	@Test
	public void InsureGuardQualificationTest_checkParentage () {
		OperatorAccount oa = new OperatorAccount();
		oa.setId(19427);
		oa.setParent(mockOA2);
		OAMocksSet.add(oa);
		when(mockOA2.getId()).thenReturn(OperatorAccount.AI);
		assertFalse(billingService.qualifiesForInsureGuard(OAMocksSet));
	}

}
