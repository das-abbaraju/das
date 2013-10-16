package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountGroup;
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

	public AccountGroup findGroupByAccount(int groupId, int accountId) {
		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId AND g.id = :groupId", AccountGroup.class);
		query.setParameter("accountId", accountId);
		query.setParameter("groupId", groupId);
		return query.getSingleResult();
	}

	public List<AccountGroup> findGroupByAccountIdAndNames(int accountId, List<String> names) {
		if (accountId > 0 && CollectionUtils.isNotEmpty(names)) {
			TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId AND g.name IN ( :names )", AccountGroup.class);
			query.setParameter("accountId", accountId);
			query.setParameter("names", names);
			return query.getResultList();
		}

		return Collections.emptyList();
	}

	public AccountGroup findGroupByAccountIdAndName(int accountId, String name) {
		if (accountId > 0 && Strings.isNotEmpty(name)) {
			TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId AND g.name = :name", AccountGroup.class);
			query.setParameter("accountId", accountId);
			query.setParameter("name", name);
			return query.getSingleResult();
		}

		return null;
	}

	public List<AccountGroup> search(String searchTerm, int accountId) {
		TypedQuery<AccountGroup> query = em.createQuery("FROM AccountGroup g WHERE g.accountId = :accountId " +
				"AND (g.name LIKE :searchTerm " +
				"OR g.description LIKE :searchTerm)", AccountGroup.class);
		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	public void delete(int id, int accountId) {
		AccountGroup group = findGroupByAccount(id, accountId);
		super.delete(group);
	}
}
