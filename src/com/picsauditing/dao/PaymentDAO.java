package com.picsauditing.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoicePayment;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.User;

@Transactional
public class PaymentDAO extends PicsDAO {

	public Payment save(Payment o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		remove(find(id));
	}

	public void remove(Payment row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Payment find(int id) {
		return em.find(Payment.class, id);
	}

	public List<Payment> findWhere(String where) {
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT p from Payment p " + where + " ORDER BY p.creationDate DESC");
		return query.getResultList();
	}

	public boolean applyPayment(Payment payment, Invoice invoice, User user, BigDecimal amount) {
		// Don't ever try to apply more than the invoice or payment balance
		if (amount.compareTo(invoice.getBalance()) < 0)
			return false;
		if (amount.compareTo(payment.getBalance()) < 0)
			return false;

		// Create the new InvoicePayment
		InvoicePayment ip = new InvoicePayment();
		ip.setInvoice(invoice);
		ip.setPayment(payment);
		ip.setAmount(amount);
		ip.setAuditColumns(user);

		payment.getInvoices().add(ip);
		payment.updateAmountApplied();
		save(payment);

		invoice.getPayments();
		invoice.updateAmountApplied();

		return true;
	}

}
