package com.picsauditing.rules;

import java.math.BigDecimal;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PICS.DroolsSessionFactory;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.User;

import edu.emory.mathcs.backport.java.util.Arrays;

public class BillingRulesTest extends PicsTest {
	@Autowired
	@Qualifier("BillingDroolsSessionFactory")
	private DroolsSessionFactory droolsSessionFactory;

	@Autowired
	private ContractorAccountDAO conDao;

	private StatelessKnowledgeSession statelessSession;

	@Before
	public void createStatelessSession() {
		statelessSession = droolsSessionFactory.getStatelessSession();
	}

	@Test
	public void test() throws Exception {
		ContractorAccount con = conDao.find(3);
		ContractorAccount con2 = conDao.find(14);

		statelessSession.execute(Arrays.asList(new Object[] { con, con2 }));

	}

	@Test
	public void testInvoiceBalance() throws Exception {

		StatefulKnowledgeSession kSession = droolsSessionFactory.getStatefulSession();
		Invoice invoice = new Invoice();
		FactHandle invoiceHandle = kSession.insert(invoice);

		invoice.setAccount(new Account());
		invoice.getAccount().setId(3);

		InvoiceItem item = new InvoiceItem(EntityFactory.makeInvoiceFee(InvoiceFee.ACTIVATION));
		item.setInvoice(invoice);
		invoice.getItems().add(item);
		InvoiceItem item2 = new InvoiceItem(EntityFactory.makeInvoiceFee(InvoiceFee.FACILITIES13));
		item2.setInvoice(invoice);
		invoice.getItems().add(item2);

		kSession.update(invoiceHandle, invoice);
		kSession.fireAllRules();
		// statelessSession.execute(invoice);

		assertTrue("should be unpaid", invoice.getStatus().isUnpaid());
		assertTrue("should have a total amount", invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);

		Payment payment = new Payment();
		payment.setTotalAmount(new BigDecimal(125));

		PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, new User(2357), payment.getTotalAmount());

		kSession.update(invoiceHandle, invoice);
		kSession.fireAllRules();
		// statelessSession.execute(invoice);

		assertTrue("should be unpaid", invoice.getStatus().isUnpaid());
		assertTrue("the first payment should be applied", invoice.getAmountApplied().compareTo(BigDecimal.ZERO) > 0);

		Payment payment2 = PaymentProcessor.PayOffInvoice(invoice, new User(2357), PaymentMethod.CreditCard);
		PaymentProcessor.ApplyPaymentToInvoice(payment2, invoice, new User(2357), payment2.getTotalAmount());

		kSession.update(invoiceHandle, invoice);
		kSession.fireAllRules();
		// statelessSession.execute(invoice);

		assertTrue("balance should be 0", invoice.getBalance().compareTo(BigDecimal.ZERO) == 0);
		assertTrue("should be paid", invoice.getStatus().isPaid());

		kSession.dispose();
	}
}
