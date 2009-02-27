package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.InvoiceItem;

@Transactional
public class InvoiceItemDAO extends PicsDAO {
	public InvoiceItem save(InvoiceItem o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		InvoiceItem row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(InvoiceItem row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public InvoiceItem find(int id) {
		return em.find(InvoiceItem.class, id);
	}

}
