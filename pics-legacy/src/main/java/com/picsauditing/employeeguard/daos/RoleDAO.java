package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

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

	public Map<Role, Role> findSiteToCorporateRoles(List<Integer> corporateIds, int siteId) {
		return findSiteToCorporateRoles(corporateIds, Arrays.asList(siteId));
	}

	public Map<Role, Role> findSiteToCorporateRoles(final Collection<Integer> corporateIds, final Collection<Integer> siteIds) {
		try {
			List<Role> siteRoles = findByAccounts(siteIds);

			Query query = em.createNativeQuery("SELECT corp.* FROM account_group site " +
					"JOIN account_group corp ON corp.name = site.name " +
					"WHERE site.accountId IN (:siteIds) " +
					"AND corp.accountId IN (:corporateIds) " +
					"AND site.type = 'Role' " +
					"AND corp.type = 'Role'", Role.class);

			query.setParameter("siteIds", siteIds);
			query.setParameter("corporateIds", corporateIds);

			List<Role> corporateRoles = query.getResultList();

			Map<Role, Role> siteToCorporateRoles = new HashMap<>();
			for (Role siteRole : siteRoles) {
				for (Role corpRole : corporateRoles) {
					if (siteRole.getName().equals(corpRole.getName())) {
						siteToCorporateRoles.put(siteRole, corpRole);
					}
				}
			}

			return siteToCorporateRoles;
		} catch (Exception e) {
			return Collections.emptyMap();
		}
	}

	public Role findSiteRoleByCorporateRole(final List<Integer> corporateIds, final int siteId, final int corporateRoleId) {
		try {
			Query query = em.createNativeQuery("SELECT site.* FROM account_group site " +
					"JOIN account_group corp ON corp.name = site.name " +
					"WHERE site.accountId = :siteId " +
					"AND corp.accountId IN (:corporateIds) " +
					"AND site.type = 'Role' " +
					"AND corp.type = 'Role' " +
					"AND corp.id = :corporateRoleId", Role.class);

			query.setParameter("siteId", siteId);
			query.setParameter("corporateIds", corporateIds);
			query.setParameter("corporateRoleId", corporateRoleId);

			return (Role) query.getSingleResult();
		} catch (Exception e) {
			LOG.error("Error finding site role for corporate role {}", corporateRoleId, e);
		}

		return null;
	}

	public List<Role> findSiteRolesByCorporateRole(final Role corporateRole) {
		Query query = em.createNativeQuery("SELECT site.* FROM account_group site " +
				"JOIN account_group corp ON corp.name = site.name " +
				"WHERE corp.accountId IN (:corporateId) " +
				"AND site.type = 'Role' " +
				"AND site.accountId != :corporateId " +
				"AND corp.type = 'Role' " +
				"AND corp.id = :roleId", Role.class);

		query.setParameter("corporateId", corporateRole.getAccountId());
		query.setParameter("roleId", corporateRole.getId());

		return query.getResultList();
	}

	public List<Role> findSiteRolesForEmployee(final int siteId, final Employee employee) {
		TypedQuery<Role> query = em.createQuery("FROM Role r " +
				"JOIN r.employees re " +
				"JOIN re.employee e " +
				"WHERE r.accountId = :siteId AND e = :employee", Role.class);

		query.setParameter("siteId", siteId);
		query.setParameter("employee", employee);

		return query.getResultList();
	}
}
