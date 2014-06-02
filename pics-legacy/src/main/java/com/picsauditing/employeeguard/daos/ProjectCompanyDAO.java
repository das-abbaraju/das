package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.ProjectCompany;

import javax.persistence.TypedQuery;
import java.util.List;

public class ProjectCompanyDAO extends AbstractBaseEntityDAO<ProjectCompany> {

	public ProjectCompanyDAO() {
		this.type = ProjectCompany.class;
	}

	public List<ProjectCompany> findByContractorAccount(final int accountId) {
		TypedQuery<ProjectCompany> query = em.createQuery("FROM ProjectCompany pc WHERE pc.accountId = :accountId", ProjectCompany.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<ProjectCompany> search(final String searchTerm, final int accountId) {
		TypedQuery<ProjectCompany> query = em.createQuery("FROM ProjectCompany pc WHERE pc.accountId = :accountId " +
				"AND (pc.project.name LIKE :searchTerm OR pc.project.location LIKE :searchTerm)", ProjectCompany.class);
		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	public ProjectCompany findProject(final int projectId, final int accountId) {
		TypedQuery<ProjectCompany> query = em.createQuery("FROM ProjectCompany pc " +
				"WHERE pc.project.id = :projectId " +
				"AND pc.accountId = :accountId", ProjectCompany.class);

		query.setParameter("projectId", projectId);
		query.setParameter("accountId", accountId);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<ProjectCompany> findByContractorExcludingSite(final int contractorId, final int siteId) {
		TypedQuery<ProjectCompany> query = em.createQuery("SElECT pc FROM ProjectCompany pc " +
				"JOIN pc.project p " +
				"WHERE p.accountId != :siteId " +
				"AND pc.accountId = :contractorId", ProjectCompany.class);

		query.setParameter("siteId", siteId);
		query.setParameter("contractorId", contractorId);

		return query.getResultList();
	}

	public List<Integer> findClientSitesByContractorAccount(final int accountId) {
		TypedQuery<Integer> query = em.createQuery("select pc.project.accountId FROM ProjectCompany pc WHERE pc.accountId = :accountId", Integer.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

}
