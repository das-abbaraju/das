package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
			TypedQuery<AccountEmployeeGuard> query = em.createQuery("FROM AccountEmployeeGuard aeg WHERE aeg.accountId = :accountId", AccountEmployeeGuard.class);
			query.setParameter("accountId", accountId);
			result = query.getSingleResult();
		} catch (Exception e) {
			LOG.info("Unable to find account {}", accountId, e);
		}

		return result;
	}

  @Transactional
	public void merge(AccountEmployeeGuard accountEmployeeGuard) {
		//em.persist(accountEmployeeGuard);
    /**
     Persist takes an entity instance, adds it to the context and makes that instance managed (ie future updates to the entity will be tracked)
     Merge creates a new instance of your entity, copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be managed (any changes you make will not be part of the transaction - unless you call merge again)
     */
    em.merge(accountEmployeeGuard);
		em.flush();
	}

  @Transactional
	public void remove(AccountEmployeeGuard accountEmployeeGuard) {
		em.remove(accountEmployeeGuard);
		em.flush();
	}
}