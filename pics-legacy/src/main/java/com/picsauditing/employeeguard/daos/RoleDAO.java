package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RoleDAO extends AbstractBaseEntityDAO<Role> {
	private static final Logger LOG = LoggerFactory.getLogger(RoleDAO.class);

	public RoleDAO() {
		this.type = Role.class;
	}

	public Role findRoleByAccount(int id, int accountId) {
		TypedQuery<Role> query = em.createQuery("FROM Role r " +
				"WHERE r.accountId = :accountId " +
				"AND r.id = :id", Role.class);
		query.setParameter("id", id);
		query.setParameter("accountId", accountId);
		return query.getSingleResult();
	}

	public List<Role> findRoleByAccountIdsAndNames(final List<Integer> accountIds, final List<String> names) {
		// Not sure if we need to check accountId
		if (CollectionUtils.isEmpty(accountIds) || CollectionUtils.isEmpty(names)) {
			return Collections.emptyList();
		}

		TypedQuery<Role> query = em.createQuery("FROM Role r WHERE r.accountId IN (:accountIds) AND r.name IN ( :names )", Role.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("names", names);
		return query.getResultList();
	}

	public List<Role> findByAccounts(Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<Role> query = em.createQuery("FROM Role r WHERE r.accountId IN (:accountIds)", Role.class);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public List<Role> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<Role> query = em.createQuery("FROM Role r WHERE r.accountId = :accountId " +
				"AND (r.name LIKE :searchTerm " +
				"OR r.description LIKE :searchTerm)", Role.class);
		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	public List<Role> search(final String searchTerm, final List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm) || CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<Role> query = em.createQuery("FROM Role r WHERE r.accountId IN (:accountIds) " +
				"AND (r.name LIKE :searchTerm " +
				"OR r.description LIKE :searchTerm)", Role.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	public List<Role> findSiteRolesForEmployee(final int siteId, final Employee employee) {
		TypedQuery<Role> query = em.createQuery("SELECT r FROM SiteAssignment sa " +
				"JOIN sa.role r " +
				"WHERE sa.siteId = :siteId AND sa.employee = :employee", Role.class);

		query.setParameter("siteId", siteId);
		query.setParameter("employee", employee);

		return query.getResultList();
	}
}
