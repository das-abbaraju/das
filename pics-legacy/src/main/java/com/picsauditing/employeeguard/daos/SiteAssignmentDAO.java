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

	public SiteAssignment find(final int siteId, final int roleId, final int employeeId) {
		TypedQuery<SiteAssignment> query = em.createQuery("FROM SiteAssignment sa " +
				"WHERE sa.siteId = :siteId " +
				"AND sa.role.id = :roleId " +
				"AND sa.employee.id = :employeeId", SiteAssignment.class);

		query.setParameter("siteId", siteId);
		query.setParameter("roleId", roleId);
		query.setParameter("employeeId", employeeId);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<SiteAssignment> findBySiteIdAndContractorId(final int siteId, final int contractorId) {
		return findBySiteIdAndContractorIds(siteId, Arrays.asList(contractorId));
	}

	public List<SiteAssignment> findBySiteIdAndContractorIds(final int siteId, final Collection<Integer> contractorIds) {
		TypedQuery<SiteAssignment> query = em.createQuery("SELECT sa FROM SiteAssignment sa " +
				"JOIN sa.employee e " +
				"WHERE sa.siteId = :siteId " +
				"AND e.accountId IN (:contractorIds)", SiteAssignment.class);
		query.setParameter("siteId", siteId);
		query.setParameter("contractorIds", contractorIds);

		return query.getResultList();
	}

	public void delete(final Employee employee, final Role role, final int siteId) {
		delete(employee.getId(), role.getId(), siteId);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void delete(final int employeeId, final int roleId, final int siteId) {
		Query query = em.createQuery("DELETE FROM SiteAssignment sa " +
				"WHERE sa.siteId = :siteId " +
				"AND sa.employee.id = :employeeId " +
				"AND sa.role.id = :roleId");

		query.setParameter("siteId", siteId);
		query.setParameter("employeeId", employeeId);
		query.setParameter("roleId", roleId);

		query.executeUpdate();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void deleteByEmployeeIdAndSiteId(final int employeeId, final int siteId) {
		Query query = em.createQuery("DELETE FROM SiteAssignment sa " +
				"WHERE sa.siteId = :siteId " +
				"AND sa.employee.id = :employeeId");

		query.setParameter("siteId", siteId);
		query.setParameter("employeeId", employeeId);

		query.executeUpdate();
	}

	public List<SiteAssignment> findByEmployeesAndSiteId(final Collection<Employee> employees, final int siteId) {
		return findByEmployeesAndSiteIds(employees, Arrays.asList(siteId));
	}

	public List<SiteAssignment> findByEmployeesAndSiteIds(final Collection<Employee> employees,
														  final Collection<Integer> siteIds) {

		TypedQuery<SiteAssignment> query = em.createQuery("FROM SiteAssignment sa " +
				"WHERE sa.siteId IN (:siteIds) " +
				"AND sa.employee IN (:employees)", SiteAssignment.class);
		query.setParameter("siteIds", siteIds);
		query.setParameter("employees", employees);

		return query.getResultList();
	}

	public List<SiteAssignment> findByEmployee(final Employee employee) {
		return findByEmployees(Arrays.asList(employee));
	}

	public List<SiteAssignment> findByEmployees(final Collection<Employee> employees) {
		TypedQuery<SiteAssignment> query = em.createQuery("FROM SiteAssignment sa " +
				"WHERE sa.employee IN (:employees)", SiteAssignment.class);
		query.setParameter("employees", employees);

		return query.getResultList();
	}

	public List<SiteAssignment> findBySiteIds(final Collection<Integer> siteIds) {
		TypedQuery<SiteAssignment> query = em.createQuery("FROM SiteAssignment sa " +
				"WHERE sa.siteId IN (:siteIds)", SiteAssignment.class);
		query.setParameter("siteIds", siteIds);

		return query.getResultList();
	}
}
