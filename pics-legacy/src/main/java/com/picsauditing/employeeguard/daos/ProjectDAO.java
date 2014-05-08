package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProjectDAO extends AbstractBaseEntityDAO<Project> {

	public ProjectDAO() {
		this.type = Project.class;
	}

	public Project findProjectByAccount(final int id, final int accountId) {
		TypedQuery<Project> query = em.createQuery("FROM Project p " +
				"WHERE p.id = :id " +
				"AND p.accountId = :accountId", Project.class);

		query.setParameter("id", id);
		query.setParameter("accountId", accountId);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Project findProjectByAccounts(final int id, final Collection<Integer> accountIds) {
		TypedQuery<Project> query = em.createQuery("FROM Project p " +
				"WHERE p.id = :id " +
				"AND p.accountId IN (:accountIds)", Project.class);

		query.setParameter("id", id);
		query.setParameter("accountIds", accountIds);

		return query.getSingleResult();
	}

	public List<Project> findProjectsByAccounts(final Collection<Integer> projectIds,
												final Collection<Integer> accountIds) {
		TypedQuery<Project> query = em.createQuery("FROM Project p WHERE p.id IN (:projectIds) " +
				"AND p.accountId IN (:accountIds)", Project.class);

		query.setParameter("projectIds", projectIds);
		query.setParameter("accountIds", accountIds);

		return query.getResultList();
	}

	public List<Project> findByAccount(final int accountId) {
		TypedQuery<Project> query = em.createQuery("FROM Project p " +
				"WHERE p.accountId = :accountId", Project.class);

		query.setParameter("accountId", accountId);

		return query.getResultList();
	}

	public List<Project> findByAccounts(final Collection<Integer> accountIds) {
		TypedQuery<Project> query = em.createQuery("FROM Project p " +
				"WHERE p.accountId IN (:accountIds)", Project.class);

		query.setParameter("accountIds", accountIds);

		return query.getResultList();
	}

	public List<Project> findByEmployee(final Employee employee) {
		TypedQuery<Project> query = em.createQuery("SELECT DISTINCT pre.projectRole.project " +
				"FROM ProjectRoleEmployee pre WHERE pre.employee = :employee", Project.class);

		query.setParameter("employee", employee);

		return query.getResultList();
	}

	public List<Project> search(final String searchTerm, final int accountId) {
		TypedQuery<Project> query = em.createQuery("FROM Project p WHERE p.accountId = :accountId " +
				"AND (p.name LIKE :searchTerm OR p.location LIKE :searchTerm)", Project.class);

		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");

		return query.getResultList();
	}

	public Project findProjectByRoleAndAccount(final int roleId, final int accountId) {
		TypedQuery<Project> query = em.createQuery("SELECT p FROM Project p " +
				"JOIN p.roles pr " +
				"JOIN pr.role r " +
				"WHERE r.id = :roleId AND p.accountId = :accountId", Project.class);

		query.setParameter("roleId", roleId);
		query.setParameter("accountId", accountId);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
