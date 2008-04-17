package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.OperatorAccount;

@Transactional
@SuppressWarnings("unchecked")
public class OperatorAccountDAO extends PicsDAO {
	public OperatorAccount save(OperatorAccount o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		OperatorAccount row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public OperatorAccount find(int id) {
		return em.find(OperatorAccount.class, id);
	}

	public List<OperatorAccount> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("select a from OperatorAccount a " + where
				+ " order by a.name");
		return query.getResultList();
	}
}
