package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.ProjectCompany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.TypedQuery;
import java.util.List;

public class ProjectCompanyDAO extends AbstractBaseEntityDAO<ProjectCompany> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectCompanyDAO.class);

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

    public ProjectCompany findProject(int projectId, int accountId) {
        ProjectCompany result = null;
        try {
            TypedQuery<ProjectCompany> query = em.createQuery("FROM ProjectCompany pc WHERE pc.project.id = :projectId AND pc.accountId = :accountId", ProjectCompany.class);
            query.setParameter("projectId", projectId);
            query.setParameter("accountId", accountId);
            return query.getSingleResult();
        } catch (Exception e) {
            LOG.error("Error finding ProjectCompany projectId = {} and accountId = {}", projectId, accountId);
        }

        return result;
    }

	public List<ProjectCompany> findByContractorExcludingSite(final int contractorId, final int siteId) {
		TypedQuery<ProjectCompany> query = em.createQuery("FROM ProjectCompany pc " +
				"JOIN Project p " +
				"WHERE p.accountId != :siteId " +
				"AND pc.accountId = :contractorId", ProjectCompany.class);

		query.setParameter("siteId", siteId);
		query.setParameter("contractorId", contractorId);

		return query.getResultList();
	}
}
