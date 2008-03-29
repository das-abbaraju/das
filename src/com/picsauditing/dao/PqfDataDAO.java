package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.PqfData;

public class PqfDataDAO extends PicsDAO {
	public PqfData save(PqfData o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		Account row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public PqfData find(int id) {
		PqfData a = em.find(PqfData.class, id);
		return a;
	}

	public List<PqfData> findAnswers(int conID, List<Integer> questionIds) {
		Query query = em.createQuery("SELECT d FROM PqfData d WHERE  a.name");
		return query.getResultList();
	}

	public List<Account> findOperators() {
		Query query = em.createQuery("select ac from Account ac where ac.type='Operator' order by ac.name");
		return query.getResultList();
	}

	public List<Account> findAuditors() {
		Query query = em.createQuery("select ac from Account ac where ac.type='Auditor' order by ac.name");
		return query.getResultList();
	}

}
