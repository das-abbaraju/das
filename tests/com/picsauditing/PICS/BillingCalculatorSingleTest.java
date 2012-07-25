package com.picsauditing.PICS;

import junit.framework.TestCase;

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

public class BillingCalculatorSingleTest extends TestCase {
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

	public void testCalculate() throws Exception {

		// ContractorAccount contractor = EntityFactory.makeContractor();
		//
		// InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("there should be no fee if the contractor has no operators", InvoiceFee.FREE, fee.getId());
		//
		// /***************************************************************************/
		//
		// OperatorAccount operator = EntityFactory.makeOperator();
		// operator.setDoContractorsPay("No");
		// EntityFactory.addContractorOperator(contractor, operator);
		//
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("there should be no fee if there is only one operator with DoContractorsPay == 'No'",
		// InvoiceFee.FREE, fee.getId());
		//
		// /***************************************************************************/
		//
		// operator.setDoContractorsPay("Yes");
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should have the PQF Only fee", InvoiceFee.PQFONLY, fee.getId());
		//
		// /***************************************************************************/
		//
		// //EntityFactory.makeAuditOperator(AuditType.DESKTOP, operator);
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should be at tier 1", InvoiceFee.FACILITIES1, fee.getId());
		//
		// /***************************************************************************/
		//
		// //operator.getAudits().clear();
		//
		// operator.setDoContractorsPay("Multiple");
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor has only one operator with DoContractorsPay == 'Multiple', should not have to pay",
		// InvoiceFee.FREE, fee.getId());
		//
		// /***************************************************************************/
		//
		// operator = EntityFactory.makeOperator();
		// operator.setDoContractorsPay("No");
		// EntityFactory.addContractorOperator(contractor, operator);
		//
		// assertEquals("contractor should still be free", InvoiceFee.FREE, fee.getId());
		//
		// /***************************************************************************/
		//
		// operator.setDoContractorsPay("Yes");
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should be at the PQF Only fee since there are no audits", InvoiceFee.PQFONLY, fee
		// .getId());
		//
		// /***************************************************************************/
		//
		// //EntityFactory.makeAuditOperator(AuditType.DESKTOP, operator);
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should be at 2-4 operators price since there is at least one audit now",
		// InvoiceFee.FACILITIES2, fee.getId());
		//
		// /***************************************************************************/
		//
		// List<OperatorAccount> ops = makeOperators(2);
		// for (OperatorAccount o : ops) {
		// EntityFactory.addContractorOperator(contractor, o);
		// }
		//
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should still be in the 2-4 operators price", InvoiceFee.FACILITIES2, fee.getId());
		//
		// /***************************************************************************/
		//
		// operator = EntityFactory.makeOperator();
		// EntityFactory.addContractorOperator(contractor, operator);
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should be in the 5-8 operators range", InvoiceFee.FACILITIES5, fee.getId());
		//
		// /***************************************************************************/
		//
		// ops = makeOperators(4);
		// for (OperatorAccount o : ops) {
		// EntityFactory.addContractorOperator(contractor, o);
		// }
		//
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should be in the 9-12 range", InvoiceFee.FACILITIES9, fee.getId());
		//
		// /***************************************************************************/
		//
		// ops = makeOperators(5);
		// for (OperatorAccount o : ops) {
		// EntityFactory.addContractorOperator(contractor, o);
		// }
		//
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should be in the 13-19 range", InvoiceFee.FACILITIES13, fee.getId());
		//
		// /***************************************************************************/
		//
		// ops = makeOperators(8);
		// for (OperatorAccount o : ops) {
		// EntityFactory.addContractorOperator(contractor, o);
		// }
		//
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor should be in the 20+ range", InvoiceFee.FACILITIES20, fee.getId());
		//
		// /***************************************************************************/
		//
		// for (ContractorOperator o : contractor.getNonCorporateOperators()) {
		// //o.getOperatorAccount().getAudits().clear();
		// }
		//
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor has no audits - should be at the PQF Only Range", InvoiceFee.PQFONLY, fee.getId());
		//
		// /***************************************************************************/
		//
		// contractor.getOperators().clear();
		//
		// fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		// assertEquals("contractor has no operators - should be FREE", InvoiceFee.FREE, fee.getId());

	}

	public void testCreateInvoiceItems() {
		// ContractorAccount contractor = EntityFactory.makeContractor();
		// List<InvoiceItem> items = null;
		//
		// // New contractor with no facilities
		// contractor.setPaymentExpires(new Date());
		// contractor.setMembershipLevel(EntityFactory.makeInvoiceFee(InvoiceFee.FREE));
		// items = BillingCalculatorSingle.createInvoiceItems(contractor, null);
		// assertEquals("Invoice should be empty", 0, items.size());
		//
		// // New PQF-Only contractor
		// contractor.setNewMembershipLevel(EntityFactory.makeInvoiceFee(InvoiceFee.PQFONLY));
		// items = BillingCalculatorSingle.createInvoiceItems(contractor, null);
		// assertNotNull("PQF must be included", findItemWithFee(items, InvoiceFee.PQFONLY));
		// assertNotNull("Activation Fee must be included", findItemWithFee(items, InvoiceFee.PQFONLY));
		//
		// // Upgrade from PQF to 1 facility
		// contractor.setStatus(AccountStatus.Active);
		// contractor.setMembershipDate(DateBean.addMonths(new Date(), -6));
		// contractor.setMembershipLevel(EntityFactory.makeInvoiceFee(InvoiceFee.PQFONLY));
		// contractor.setNewMembershipLevel(EntityFactory.makeInvoiceFee(InvoiceFee.FACILITIES1));
		// contractor.setPaymentExpires(DateBean.addMonths(new Date(), 6));
		// items = BillingCalculatorSingle.createInvoiceItems(contractor, null);
		// assertEquals(1, items.size());
		// assertEquals("Facility 1 Upgrade must be for $150.00", new BigDecimal(150), findItemWithFee(items,
		// InvoiceFee.FACILITIES1).getAmount());
		//
		// // Current with 1 facility
		// contractor.setMembershipLevel(EntityFactory.makeInvoiceFee(InvoiceFee.FACILITIES1));
		// contractor.setNewMembershipLevel(EntityFactory.makeInvoiceFee(InvoiceFee.FACILITIES1));
		// contractor.setPaymentExpires(DateBean.addMonths(new Date(), 6));
		// items = BillingCalculatorSingle.createInvoiceItems(contractor, null);
		// assertEquals(0, items.size());
		//
		// // Renewal with 1 facility
		// contractor.setMembershipLevel(EntityFactory.makeInvoiceFee(InvoiceFee.FACILITIES1));
		// contractor.setNewMembershipLevel(EntityFactory.makeInvoiceFee(InvoiceFee.FACILITIES1));
		// contractor.setPaymentExpires(DateBean.addDays(new Date(), 15));
		// items = BillingCalculatorSingle.createInvoiceItems(contractor, null);
		// assertEquals(1, items.size());
		// assertEquals("Facility 1 Renewal", new BigDecimal(399), findItemWithFee(items, InvoiceFee.FACILITIES1)
		// .getAmount());

	}
	//
	// /***** Private helper methods ****/
	//
	// private InvoiceItem findItemWithFee(List<InvoiceItem> items, int feeID) {
	// for (InvoiceItem item : items) {
	// if (item.getInvoiceFee() != null && item.getInvoiceFee().getId() == feeID)
	// return item;
	// }
	// return null;
	// }
	//
	// private List<OperatorAccount> makeOperators(int x) {
	// List<OperatorAccount> ops = new Vector<OperatorAccount>();
	//
	// for (int i = 0; i < x; i++) {
	// ops.add(EntityFactory.makeOperator());
	// }
	// return ops;
	// }

}
