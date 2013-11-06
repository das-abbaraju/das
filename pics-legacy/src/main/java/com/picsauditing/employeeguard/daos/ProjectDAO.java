package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Project;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class ProjectDAO extends BaseEntityDAO<Project> {

	public ProjectDAO() {
		this.type = Project.class;
	}

	public Project findProjectByAccount(final int id, final int accountId) {
		if (id == 0 || accountId == 0) {
			return null;
		}

		TypedQuery<Project> query = em.createQuery("FROM Project p WHERE p.id = :id AND p.accountId = :accountId", Project.class);
		query.setParameter("id", id);
		query.setParameter("accountId", accountId);
		return query.getSingleResult();
	}

	public Project findProjectByAccounts(final int id, final List<Integer> accountIds) {
		if (id == 0 || CollectionUtils.isEmpty(accountIds)) {
			return null;
		}

		TypedQuery<Project> query = em.createQuery("FROM Project p WHERE p.id = :id AND p.accountId IN (:accountIds)", Project.class);
		query.setParameter("id", id);
		query.setParameter("accountIds", accountIds);
		return query.getSingleResult();
	}


	public List<Project> findByAccount(final int accountId) {
		TypedQuery<Project> query = em.createQuery("FROM Project p WHERE p.accountId = :accountId", Project.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<Project> findByAccounts(final List<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<Project> query = em.createQuery("FROM Project p WHERE p.accountId IN (:accountIds)", Project.class);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public List<Project> findByContractorAccount(final int accountId) {
		TypedQuery<Project> query = em.createQuery("SELECT p FROM ProjectCompany pc INNER JOIN pc.project p WHERE pc.accountId = :accountId", Project.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<Project> search(final String searchTerm, final int accountId) {
		TypedQuery<Project> query = em.createQuery("FROM Project p WHERE p.accountId = :accountId " +
				"AND (p.name LIKE :searchTerm OR p.location LIKE :searchTerm)", Project.class);
		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}
}
