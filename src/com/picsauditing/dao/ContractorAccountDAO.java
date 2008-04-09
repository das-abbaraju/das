package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAccount;

@Transactional
@SuppressWarnings("unchecked")
public class ContractorAccountDAO extends PicsDAO {
	public ContractorAccount save(ContractorAccount o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorAccount row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorAccount find(int id) {
		return em.find(ContractorAccount.class, id);
	}

	public List<ContractorAccount> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("select a from Account a " + where
				+ " order by a.name");
		return query.getResultList();
	}
}
