package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class AccountEmployeeGuardDAO {
	private static final Logger LOG = LoggerFactory.getLogger(AccountEmployeeGuardDAO.class);

	@PersistenceContext
	protected EntityManager em;

	public boolean isEmployeeGUARDEnabled(final int accountId) {
		TypedQuery<AccountEmployeeGuard> query = em.createQuery("FROM AccountEmployeeGuard aeg WHERE aeg.accountId = :accountId", AccountEmployeeGuard.class);
		query.setParameter("accountId", accountId);

		try {
			AccountEmployeeGuard accountEmployeeGuard = query.getSingleResult();
			return accountEmployeeGuard != null;
		} catch (Exception exception) {
			LOG.debug("Account {} is not enabled for EmployeeGUARD", accountId);
		}

		return false;
	}

	public List<Integer> getEmployeeGUARDAccounts() {
		TypedQuery<AccountEmployeeGuard> query = em.createQuery("FROM AccountEmployeeGuard aeg", AccountEmployeeGuard.class);
		List<AccountEmployeeGuard> accountEmployeeGuards = query.getResultList();

		List<Integer> accounts = new ArrayList<>();
		for (AccountEmployeeGuard accountEmployeeGuard : accountEmployeeGuards) {
			accounts.add(accountEmployeeGuard.getAccountId());
		}

		return accounts;
	}

    public boolean hasEmployeeGUARDOperator(int conID) {
        Query query = em.createNativeQuery("select * from accountemployeeguard aeg " +
                "WHERE aeg.accountId IN (select co.opID FROM contractor_operator co WHERE co.conID = :conID)", AccountEmployeeGuard.class);
        query.setParameter("conID", conID);

        List<AccountEmployeeGuard> accountEmployeeGuards = query.getResultList();

        return accountEmployeeGuards.size() > 0;
    }
}
