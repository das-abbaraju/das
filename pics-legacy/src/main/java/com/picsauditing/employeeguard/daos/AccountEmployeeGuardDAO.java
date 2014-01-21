package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    public boolean hasEmployeeGUARDOperator(String accounts) {
        Query query = em.createNativeQuery("select * from accountemployeeguard aeg WHERE aeg.accountId IN (:accounts)", AccountEmployeeGuard.class);
        query.setParameter("accounts", accounts);

        List<AccountEmployeeGuard> accountEmployeeGuards = query.getResultList();

        return accountEmployeeGuards.size() > 0;
    }
}
