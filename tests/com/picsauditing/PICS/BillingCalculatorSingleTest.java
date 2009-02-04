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
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.OperatorAccount;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class BillingCalculatorSingleTest extends TestCase {

	@Autowired
	protected BillingCalculatorSingle calculator;
	
	@Test
	public void testCalculate() throws Exception {
		
		
		ContractorAccount contractor = EntityFactory.makeContractor();

		InvoiceFee fee = calculator.calculateAnnualFee(contractor);
		assertNull("fee should be null if there are no operators for a given contractor", fee);
		
		List<OperatorAccount> ops = makeOperators(40);

		for( OperatorAccount o : ops ) {
			EntityFactory.addContractorOperator(contractor, o);
		}
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNull("none of the operators had a doContractorsPay value which would result in a bill...should be null result", fee);
		
		ops.get(0).setDoContractorsPay("Multiple");

		fee = calculator.calculateAnnualFee(contractor);
		assertNull("one operator had a doContractorsPay with multiple...should be null result", fee);
		
		ops.get(1).setDoContractorsPay("Multiple");
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("two operators had a doContractorsPay with multiple...should not be null result", fee);
		
		
		int i = 0;
		for( OperatorAccount op : ops ) {
			op.setDoContractorsPay( i++ % 2 == 0 ? "Yes" : "No" );
		}
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should be the base fee because nobody is configured to require an Active Status", 99, fee.getAmount());
		
		List<AuditOperator> aops = new Vector<AuditOperator>();
		
		for( OperatorAccount op : ops ) {
			op.setDoContractorsPay( "Yes" );
			
			AuditOperator aop = EntityFactory.makeAuditOperator(1, op);
			aop.setRequiredAuditStatus( AuditStatus.Submitted );
			aops.add( aop );	
		}

		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should be the base fee because nobody is configured to require an Active Status", 99, fee.getAmount());
		

		
		aops.get(1).setRequiredAuditStatus(AuditStatus.Active);
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should be the first tier because there is 1 auditoperator with a requiredauditstatus = active", 399, fee.getAmount());
		

		aops.get(2).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(3).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(4).setRequiredAuditStatus(AuditStatus.Active);
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should be the second tier because there are 2-4 auditoperators with a requiredauditstatus = active", 699, fee.getAmount());
		
		aops.get(5).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(6).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(7).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(8).setRequiredAuditStatus(AuditStatus.Active);
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should be the third tier because there are 5-8 auditoperators with a requiredauditstatus = active", 999, fee.getAmount());
		
		
		aops.get(9).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(10).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(11).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(12).setRequiredAuditStatus(AuditStatus.Active);
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should be the fourth tier because there are 9-12 auditoperators with a requiredauditstatus = active", 1299, fee.getAmount());
		
		
		aops.get(13).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(14).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(15).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(16).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(17).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(18).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(19).setRequiredAuditStatus(AuditStatus.Active);
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should be the fifth tier because there are 13-19 auditoperators with a requiredauditstatus = active", 1699, fee.getAmount());
		
		aops.get(20).setRequiredAuditStatus(AuditStatus.Active);
		
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should be the sixth tier because there are 20+ auditoperators with a requiredauditstatus = active", 1999, fee.getAmount());
		
		aops.get(21).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(22).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(23).setRequiredAuditStatus(AuditStatus.Active);
		aops.get(24).setRequiredAuditStatus(AuditStatus.Active);
		
		
		fee = calculator.calculateAnnualFee(contractor);
		assertNotNull("multiple operators with doContractorsPay = yes, result should not be null", fee);
		assertEquals("Should still be the sixth tier because there are at least 6 auditoperators with a requiredauditstatus = active", 1999, fee.getAmount());
		
		
	}
	
	

	private List<OperatorAccount> makeOperators(int x) {
		List<OperatorAccount> ops = new Vector<OperatorAccount>();
		
		for( int i = 0; i < x; i++ ) {
			ops.add(EntityFactory.makeOperator());
		}
		return ops;
	}
	
	
}
