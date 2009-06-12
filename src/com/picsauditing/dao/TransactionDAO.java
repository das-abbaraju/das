package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Transaction;

@Transactional
public class TransactionDAO extends PicsDAO {
	
	public Transaction save(Transaction o) {
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

	public void remove(Transaction row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Transaction find(int id) {
		return em.find(Transaction.class, id);
	}

}
