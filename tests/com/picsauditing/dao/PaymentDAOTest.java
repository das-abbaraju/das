package com.picsauditing.dao;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.Payment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class PaymentDAOTest extends TestCase {

	@Autowired
	private PaymentDAO paymentDAO;

	@Test
	public void testFind() {
		Payment payment = paymentDAO.find(33825);
		assertEquals("Ancon Marine", payment.getAccount().getName());
	}
}
