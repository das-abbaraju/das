package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AccountEmployeeGuardDAO {
    private static final Logger LOG = LoggerFactory.getLogger(AccountEmployeeGuardDAO.class);

	@PersistenceContext
	protected EntityManager em;

	public AccountEmployeeGuard find(int accountId) {
		if (accountId == 0) {
			return null;
		}

        AccountEmployeeGuard result = null;

        try {
            TypedQuery<AccountEmployeeGuard> query = em.createQuery("FROM AccountEmployeeGuard aeg WHERE aeg.accountId = :accountId", AccountEmployeeGuard.class);
            query.setParameter("accountId", accountId);
            result = query.getSingleResult();
        } catch (Exception e) {
            LOG.error("Unable to find account {}", accountId, e);
        }

        return result;
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
