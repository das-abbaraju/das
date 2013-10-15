package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.Refund;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("unchecked")
public class PaymentDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public Payment save(Payment o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public Refund save(Refund o) {
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

	@Transactional(propagation = Propagation.NESTED)
	public void remove(Payment row) {
		if (row != null) {
			em.remove(row);
		}
		row = null;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(PaymentApplied row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public void refresh(Payment row) {
		if (em.contains(row))
			em.refresh(row);
	}

	public Payment find(int id) {
		return em.find(Payment.class, id);
	}

	public Payment findByListID(String listID) {
		List<Payment> list = findWhere("qbListID = '" + listID + "'", 1);
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public List<Payment> findWhere(String where) {
		return findWhere(where, 0);
	}

	
	public List<Payment> findWhere(String where, int limit) {
		if (where.length() > 0)
			where = "WHERE " + where;

		Query query = em.createQuery("SELECT p from Payment p " + where + " ORDER BY p.creationDate DESC");

		if (limit != 0) {
			query.setMaxResults(limit);
		}

		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void removePaymentInvoice(PaymentAppliedToInvoice pa, User user) {
		if (pa == null)
			return;
		Payment payment = pa.getPayment();
		Invoice invoice = pa.getInvoice();

		payment.getApplied().remove(pa);
		invoice.getPayments().remove(pa);
		em.remove(pa);

		payment.updateAmountApplied();
		invoice.updateAmountApplied();

		em.merge(payment);
		em.merge(invoice);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void removePaymentRefund(PaymentAppliedToRefund pa, User user) {
		if (pa == null)
			return;
		Payment payment = pa.getPayment();
		Refund refund = pa.getRefund();

		payment.getApplied().remove(pa);
		refund.getPayments().remove(pa);
		em.remove(pa);

		payment.updateAmountApplied();
		refund.updateAmountApplied();

		em.merge(payment);
		em.merge(refund);
	}

}
