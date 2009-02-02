package com.picsauditing.dao;

import com.picsauditing.jpa.entities.Invoice;

public class InvoiceDAO extends PicsDAO {
	public Invoice save(Invoice o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		Invoice row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(Invoice row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Invoice find(int id) {
		return em.find(Invoice.class, id);
	}

}
