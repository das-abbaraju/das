package com.picsauditing.PICS;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.EntityFactory;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.OperatorAccount;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class BillingCalculatorSingleTest extends TestCase {

	@Autowired
	protected InvoiceFeeDAO feeDao;
	
	@Test
	public void testCalculate() throws Exception {
		
		ContractorAccount contractor = EntityFactory.makeContractor();

		InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("there should be no fee if the contractor has no operators", lookup(InvoiceFee.FREE), lookup(fee));
		
		/***************************************************************************/
		
		OperatorAccount operator = EntityFactory.makeOperator();
		operator.setDoContractorsPay("No");
		EntityFactory.addContractorOperator(contractor, operator);
		
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("there should be no fee if there is only one operator with DoContractorsPay == 'No'",  lookup(InvoiceFee.FREE), lookup(fee));
		
		/***************************************************************************/
		
		operator.setDoContractorsPay("Yes");
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor); 
		assertEquals("contractor should have the PQF Only fee", lookup(InvoiceFee.PQFONLY), lookup(fee));
		
		/***************************************************************************/
		
		EntityFactory.makeAuditOperator(AuditType.DESKTOP, operator);
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor should be at tier 1", lookup(InvoiceFee.FACILITIES1), lookup(fee));
		
		/***************************************************************************/
		
		operator.getAudits().clear();
		
		operator.setDoContractorsPay("Multiple");
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor has only one operator with DoContractorsPay == 'Multiple', should not have to pay", lookup(InvoiceFee.FREE), lookup(fee));
		
		/***************************************************************************/
		
		operator = EntityFactory.makeOperator();
		operator.setDoContractorsPay("No");
		EntityFactory.addContractorOperator(contractor, operator);
		
		assertEquals("contractor should still be free", lookup(InvoiceFee.FREE), lookup(fee));
		
		/***************************************************************************/
		
		operator.setDoContractorsPay("Yes");
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor should be at the PQF Only fee since there are no audits", lookup(InvoiceFee.PQFONLY), lookup(fee));
		
		/***************************************************************************/
		
		EntityFactory.makeAuditOperator(AuditType.DESKTOP, operator);
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor should be at 2-4 operators price since there is at least one audit now", lookup(InvoiceFee.FACILITIES2), lookup(fee));
		
		/***************************************************************************/
		
		List<OperatorAccount> ops = makeOperators(2);
		for (OperatorAccount o : ops) {
			EntityFactory.addContractorOperator(contractor, o);
		}
		
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor should still be in the 2-4 operators price", lookup(InvoiceFee.FACILITIES2), lookup(fee));
		
		/***************************************************************************/
		
		operator = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractor, operator);
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor should be in the 5-8 operators range", lookup(InvoiceFee.FACILITIES5), lookup(fee));
		
		/***************************************************************************/
		
		ops = makeOperators(4);
		for (OperatorAccount o : ops) {
			EntityFactory.addContractorOperator(contractor, o);
		}
		
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor should be in the 9-12 range", lookup(InvoiceFee.FACILITIES9), lookup(fee));
		
		/***************************************************************************/
		
		ops = makeOperators(5);
		for (OperatorAccount o : ops) {
			EntityFactory.addContractorOperator(contractor, o);
		}
		
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor should be in the 13-19 range", lookup(InvoiceFee.FACILITIES13), lookup(fee));
		
		/***************************************************************************/
		
		ops = makeOperators(8);
		for (OperatorAccount o : ops) {
			EntityFactory.addContractorOperator(contractor, o);
		}
		
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor should be in the 20+ range", lookup(InvoiceFee.FACILITIES20), lookup(fee));
		
		/***************************************************************************/
		
		for (ContractorOperator o : contractor.getOperators()) {
			o.getOperatorAccount().getAudits().clear();
		}
		
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor has no audits - should be at the PQF Only Range", lookup(InvoiceFee.PQFONLY), lookup(fee));
		
		/***************************************************************************/
		
		contractor.getOperators().clear();
		
		fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		assertEquals("contractor has no operators - should be FREE", lookup(InvoiceFee.FREE), lookup(fee));
		
	}
	
	private int lookup( InvoiceFee fee ) {
		InvoiceFee connected = feeDao.find(fee.getId());
		return connected.getAmount();
	}
	
	private int lookup(int feeID) {
		InvoiceFee connected = feeDao.find(feeID);
		return connected.getAmount();
	}

	private List<OperatorAccount> makeOperators(int x) {
		List<OperatorAccount> ops = new Vector<OperatorAccount>();
		
		for( int i = 0; i < x; i++ ) {
			ops.add(EntityFactory.makeOperator());
		}
		return ops;
	}
	
	
}
