package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoicePayment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@Transactional
public class InvoicePaymentDAOTest extends TestCase {

	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private PaymentDAO paymentDAO;
	@Autowired
	private InvoicePaymentDAO invoicePaymentDAO;

	@Test
	public void testSaveAndRemove() {
	}

	@Test
	public void testFind() {
		List<Invoice> iList = invoiceDAO.findWhere("account.id = 5822", 1);
		Invoice invoice = iList.get(0);

		List<InvoicePayment> ipList = invoicePaymentDAO.findByInvoice(invoice);

		assertEquals(ipList.size(), invoice.getPayments().size());

		for (InvoicePayment ip : ipList) {
			assertEquals(ip.getInvoice(), invoice);
			assertTrue(invoice.getPayments().contains(ip));
		}
	}
}
