package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;

public class BillingCalculatorSingleTest extends TestCase {

	public void testCalculate() {
		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
		BillingCalculatorSingle calculator = new BillingCalculatorSingle(operators);
		
		ContractorAccount contractor = EntityFactory.makeContractor();
		
		int fee = 0; //calculator.calculateAnnualFee(contractor);
		
		assertEquals(99, fee);
	}

}
