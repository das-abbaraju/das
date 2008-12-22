package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AccountName;

@Transactional
@SuppressWarnings("unchecked")
public class AccountNameDAO extends PicsDAO {
	public AccountName save(AccountName o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AccountName row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AccountName find(int id) {
		AccountName a = em.find(AccountName.class, id);
		return a;
	}
}
