package com.picsauditing.util;

import java.math.BigDecimal;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.jpa.entities.Invoice;

public class BrainTreeTest extends TestCase {
	public BrainTreeTest(String name) {
		super(name);
	}
	
	public void testPayment() {
		BrainTreeService service = new BrainTreeService();
		service.setUserName("testapi");
		service.setPassword("password1");
		Invoice invoice = new Invoice();
		invoice.setAccount(EntityFactory.makeContractor());
		invoice.setId(123);
		invoice.setTotalAmount(new BigDecimal(99));
		try {
			service.processPayment(invoice);
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
		String hash = BrainTree.buildHash("", "", "1", "0", "", "", "891", "20090120054536", "UVgCejU48ANga4mKF77WFXfm2yUve76W");
		assertEquals("b91b1cba19380839575c68ffdf6434d8", hash);
	}

}
