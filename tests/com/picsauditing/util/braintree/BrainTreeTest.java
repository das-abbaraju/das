package com.picsauditing.util.braintree;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.util.braintree.BrainTree;

public class BrainTreeTest extends TestCase {
	@Autowired
	private BrainTreeService service;

	public BrainTreeTest(String name) {
		super(name);
	}

	public void testPayment() {
		Payment payment = new Payment();
		payment.setAccount(EntityFactory.makeContractor());
		payment.setCurrency(payment.getAccount().getCountry().getCurrency());
		payment.setId(123);
		payment.setTotalAmount(new BigDecimal(99));
		try {
			service.processPayment(payment, new Invoice());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void testHashGenerator() {
		// Used values from http://tools.getbraintree.com/hasher
		String hash = BrainTree.buildHash("123", "100", "954812", "20091231235959", "jf294lka9rhjtr981jfkig491");
		assertEquals("46fc4025b07f3ee09febc3f562e474a2", hash);
	}

	public void testHashGenerator2() {
		// Used values from http://tools.getbraintree.com/hasher
		String hash = BrainTree.buildHash("", "", "", "20091231235959", "jf294lka9rhjtr981jfkig491");
		assertEquals("70b71f5c7ec93657c006e5b9f72e021d", hash);
	}

	public void testHashGenerator3() {
		// Used values from http://tools.getbraintree.com/hasher
		String hash = BrainTree.buildHash("", "", "1", "0", "", "", "891", "20090120054536",
				"UVgCejU48ANga4mKF77WFXfm2yUve76W");
		assertEquals("b91b1cba19380839575c68ffdf6434d8", hash);
	}

}