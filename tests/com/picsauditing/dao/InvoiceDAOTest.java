package com.picsauditing.dao;

import java.math.BigDecimal;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.TransactionStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class InvoiceDAOTest extends TestCase {

	@Autowired
	private InvoiceDAO invoiceDAO;

	@Test
	public void testSaveAndRemove() {
		Invoice invoice = new Invoice();
		invoice.setAccount(new Account());
		invoice.getAccount().setId(3);
		invoice.setCurrency(Currency.USD);
		invoice.setQbSync(false);
		invoice.setAmountApplied(BigDecimal.TEN);
		invoice.setStatus(TransactionStatus.Void);
		invoice = invoiceDAO.save(invoice);
		assertTrue(invoice.getId() > 0);
		invoiceDAO.remove(invoice.getId());
		assertNull(invoiceDAO.find(invoice.getId()));
	}
	
	@Test
	public void testCurrencyTest() {
		Currency currency = Currency.getFromISO("US");
		List<Invoice> invoices = invoiceDAO.findWhere("i.account.qbListID is not null AND i.status != 'Void' AND i.qbSync = true AND i.qbListID is null "
						+ "AND i.account.qbListID not like 'NOLOAD%' AND i.currency = '"+ currency + "'", 10);
		assertNotNull(invoices);
		assertEquals(invoices.size(), 10);
	}
}
