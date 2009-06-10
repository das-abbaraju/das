package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Payment;

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

}
