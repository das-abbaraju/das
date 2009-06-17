package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoicePayment;
import com.picsauditing.jpa.entities.Payment;

@Transactional
public class InvoicePaymentDAO extends PicsDAO {
	public InvoicePayment save(InvoicePayment o) {
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

	public void remove(InvoicePayment row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public InvoicePayment find(int id) {
		return em.find(InvoicePayment.class, id);
	}

	public List<InvoicePayment> findByInvoiceAndPayment(Invoice i, Payment p) {
		return findByInvoiceAndPayment(i.getId(), p.getId());
	}

	@SuppressWarnings("unchecked")
	public List<InvoicePayment> findByInvoiceAndPayment(int i, int p) {
		Query query = em
				.createQuery("SELECT ip FROM InvoicePayment ip WHERE ip.invoice.id = :i AND ip.payment.id = :p");
		query.setParameter("i", i);
		query.setParameter("p", p);

		return query.getResultList();
	}

	public List<InvoicePayment> findByInvoice(Invoice i) {
		return findByInvoice(i.getId());
	}

	@SuppressWarnings("unchecked")
	public List<InvoicePayment> findByInvoice(int i) {
		Query query = em.createQuery("SELECT ip FROM InvoicePayment ip WHERE ip.invoice.id = :i");
		query.setParameter("i", i);

		return query.getResultList();

	}

	public List<InvoicePayment> findByPayment(Payment p) {
		return findByPayment(p.getId());
	}

	@SuppressWarnings("unchecked")
	public List<InvoicePayment> findByPayment(int p) {
		Query query = em.createQuery("SELECT ip FROM InvoicePayment ip WHERE ip.payment.id = :p");
		query.setParameter("p", p);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<InvoicePayment> findWhere(String where, int limit) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT ip FROM InvoicePayment ip " + where + " ORDER BY ip.id");
		query.setMaxResults(limit);
		return query.getResultList();
	}

}
