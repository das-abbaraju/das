package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.SiteAssignment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SiteAssignmentDAO extends AbstractBaseEntityDAO<SiteAssignment> {

	public SiteAssignmentDAO() {
		this.type = SiteAssignment.class;
	}

	public List<SiteAssignment> findBySiteIdAndContractorId(final int siteId, final int contractorId) {
		return findBySiteIdAndContractorIds(siteId, Arrays.asList(contractorId));
	}

	public List<SiteAssignment> findBySiteIdAndContractorIds(final int siteId, final Collection<Integer> contractorIds) {
		TypedQuery<SiteAssignment> query = em.createQuery("FROM SiteAssignment sa " +
				"JOIN sa.employee e " +
				"WHERE sa.siteId = :siteId " +
				"AND e.accountID IN (:contractorIds)", SiteAssignment.class);
		query.setParameter("siteId", siteId);
		query.setParameter("contractorIds", contractorIds);

		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void deleteAssignmentByEmployeeRoleSiteId(final Employee employee, final Role role, final int siteId) {
		Query query = em.createQuery("DELETE FROM SiteAssignment sa " +
				"WHERE sa.siteId = :siteId " +
				"AND sa.employee = :employee " +
				"AND sa.role = :role");
		query.setParameter("siteId", siteId);
		query.setParameter("employee", employee);
		query.setParameter("role", role);

		query.executeUpdate();
	}

	public void deleteByEmployeeIdAndSiteId(final int employeeId, final int siteId) {
		Query query = em.createQuery("DELETE FROM SiteAssignment sa " +
				"WHERE sa.siteId = :siteId " +
				"AND sa.employee.id = :employeeId");
		query.setParameter("siteId", siteId);
		query.setParameter("employeeId", employeeId);

		query.executeUpdate();
	}
}
