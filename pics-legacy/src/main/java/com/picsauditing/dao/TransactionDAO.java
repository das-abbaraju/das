package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Transaction;

@SuppressWarnings("unchecked")
public class TransactionDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public Transaction save(Transaction o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		remove(find(id));
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(Transaction row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Transaction find(int id) {
		return em.find(Transaction.class, id);
	}

	public List<Transaction> findWhere(String where) {
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT t from Transaction t " + where + " ORDER BY t.creationDate DESC");
		return query.getResultList();
	}

}
