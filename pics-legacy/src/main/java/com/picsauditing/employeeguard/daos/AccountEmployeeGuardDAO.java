package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public class AccountEmployeeGuardDAO {

	private static final Logger LOG = LoggerFactory.getLogger(AccountEmployeeGuardDAO.class);

	@PersistenceContext
	protected EntityManager em;

	@Transactional(readOnly = true)
	public AccountEmployeeGuard find(int accountId) {
		if (accountId == 0) {
			return null;
		}

		AccountEmployeeGuard result = null;

		try {
			TypedQuery<AccountEmployeeGuard> query = em.createQuery("FROM AccountEmployeeGuard aeg " +
					"WHERE aeg.accountId = :accountId", AccountEmployeeGuard.class);

			query.setParameter("accountId", accountId);
			result = query.getSingleResult();
		} catch (Exception e) {
			LOG.info("Unable to find account {}", accountId, e);
		}

		return result;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void save(AccountEmployeeGuard accountEmployeeGuard) {
		if (accountEmployeeGuard.getId() == 0) {
			em.persist(accountEmployeeGuard);
		} else {
			em.merge(accountEmployeeGuard);
		}

		em.flush();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(AccountEmployeeGuard accountEmployeeGuard) {
		em.remove(accountEmployeeGuard);
		em.flush();
	}
}