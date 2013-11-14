package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AccountGroupDAO extends BaseEntityDAO<AccountGroup> {

	public AccountGroupDAO() {
		this.type = AccountGroup.class;
	}

	public List<AccountGroup> findByAccount(int accountId) {
		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId", AccountGroup.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<AccountGroup> findByAccounts(final List<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId IN (:accountIds)", AccountGroup.class);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public AccountGroup findGroupByAccount(int groupId, int accountId) {
		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId AND g.id = :groupId", AccountGroup.class);
		query.setParameter("accountId", accountId);
		query.setParameter("groupId", groupId);
		return query.getSingleResult();
	}

	public List<AccountGroup> findGroupByAccountIdAndNames(int accountId, List<String> names) {
		// Not sure if we need to check accountId
		if (accountId <= 0 || CollectionUtils.isEmpty(names)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId AND g.name IN ( :names )", AccountGroup.class);
		query.setParameter("accountId", accountId);
		query.setParameter("names", names);
		return query.getResultList();
	}

	public AccountGroup findGroupByAccountIdAndName(int accountId, String name) {
		if (accountId <= 0 || Strings.isEmpty(name)) {
			return null;
		}

		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId AND g.name = :name", AccountGroup.class);
		query.setParameter("accountId", accountId);
		query.setParameter("name", name);
		return query.getSingleResult();
	}

	public List<AccountGroup> findGroupByAccountIdsAndNames(final List<Integer> accountIds, final List<String> names) {
		// Not sure if we need to check accountId
		if (CollectionUtils.isEmpty(accountIds) || CollectionUtils.isEmpty(names)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId IN (:accountIds) AND g.name IN ( :names )", AccountGroup.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("names", names);
		return query.getResultList();
	}

	public List<AccountGroup> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId " +
				"AND (g.name LIKE :searchTerm " +
				"OR g.description LIKE :searchTerm)", AccountGroup.class);
		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	public List<AccountGroup> search(final String searchTerm, final List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm) || CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId IN (:accountIds) " +
				"AND (g.name LIKE :searchTerm " +
				"OR g.description LIKE :searchTerm)", AccountGroup.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

    public List<AccountGroup> findGroupsForEmployee(final Employee employee) {
        TypedQuery<AccountGroup> query = em.createQuery("SELECT age.group FROM AccountGroupEmployee age " +
                "WHERE age.employee = :employee AND age.group.accountId = :accountId", AccountGroup.class);
        query.setParameter("employee", employee);
        query.setParameter("accountId", employee.getAccountId());
        return query.getResultList();
    }

    public List<AccountGroup> findEmployeeGroupAssignments(Employee employee) {
        TypedQuery<AccountGroup> query = em.createQuery("SELECT age.group FROM AccountGroupEmployee age " +
                "WHERE age.employee = :employee AND age.group.accountId <> :accountId", AccountGroup.class);
        query.setParameter("employee", employee);
        query.setParameter("accountId", employee.getAccountId());
        return query.getResultList();
    }
}
