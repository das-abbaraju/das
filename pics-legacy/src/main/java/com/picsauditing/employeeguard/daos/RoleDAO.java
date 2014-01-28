package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

public class RoleDAO extends AbstractBaseEntityDAO<Role> {

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

    public List<Role> findByAccounts(List<Integer> accountIds) {
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

	public Map<Role, Role> findDuplicatedRoles(List<Integer> corporateIds, int siteId) {
		List<Role> siteRoles = findByAccounts(Arrays.asList(siteId));

		Query query = em.createNativeQuery("SELECT corp.* FROM account_group site " +
				"JOIN account_group corp ON corp.name = site.name " +
				"WHERE site.accountId = :siteId " +
				"AND corp.accountId IN (:corporateIds) " +
				"AND site.type = 'Role' " +
				"AND corp.type = 'Role'", Role.class);

		query.setParameter("siteId", siteId);
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
	}
}
