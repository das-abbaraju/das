package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.InvoiceFee;

@Transactional
public class InvoiceFeeDAO extends PicsDAO {
	
	public InvoiceFee save(InvoiceFee o) {
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

	public void remove(InvoiceFee row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public InvoiceFee find(int id) {
		return em.find(InvoiceFee.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<InvoiceFee> findAll() {
		Query query = em.createQuery("FROM InvoiceFee ORDER BY fee");
		return query.getResultList();
	}
	
	public InvoiceFee findByName(String feeName) {
		Query query = em.createQuery("FROM InvoiceFee where fee = ?");
		
		query.setParameter(1, feeName );
		
		try {
			return (InvoiceFee) query.getSingleResult();
		}
		catch( Exception nre ) {
			return null;
		}
	}

}
