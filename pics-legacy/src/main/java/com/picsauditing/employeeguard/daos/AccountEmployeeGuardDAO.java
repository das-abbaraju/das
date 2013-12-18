package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public class AccountEmployeeGuardDAO {

	@PersistenceContext
	protected EntityManager em;

	public AccountEmployeeGuard find(int accountId) {
		if (accountId == 0) {
			return null;
		}

		TypedQuery<AccountEmployeeGuard> query = em.createQuery("FROM AccountEmployeeGuard aeg WHERE aeg.accountId = :accountId", AccountEmployeeGuard.class);
		query.setParameter("accountId", accountId);

		return query.getSingleResult();
	}

	public boolean isEmployeeGUARDEnabled(final int accountId) {
		return find(accountId) != null;
	}

	public void save(AccountEmployeeGuard accountEmployeeGuard) {
		em.persist(accountEmployeeGuard);
		em.flush();
	}

	public void remove(AccountEmployeeGuard accountEmployeeGuard) {
		em.remove(accountEmployeeGuard);
		em.flush();
	}
}
