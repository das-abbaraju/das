package com.picsauditing.dao;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.InvoiceItem;

public class InvoiceItemDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public InvoiceItem save(InvoiceItem o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		InvoiceItem row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(InvoiceItem row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public InvoiceItem find(int id) {
		return em.find(InvoiceItem.class, id);
	}

}
