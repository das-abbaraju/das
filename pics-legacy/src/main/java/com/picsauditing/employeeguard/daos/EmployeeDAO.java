package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.daos.querymapper.EmployeeQueryMapper;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EmployeeDAO extends BaseEntityDAO<Employee> {

	public static final int EMPLOYEE_BATCH_SIZE = 100;

	private static final String INSERT_EMPLOYEE_QUERY = "INSERT INTO account_employee(`accountID`,`slug`,`firstName`,`lastName`," +
			"`positionName`,`email`,`phone`,`emailToken`," +
			"`createdBy`,`updatedBy`,`deletedBy`," +
			"`createdDate`,`updatedDate`,`deletedDate`) VALUES (" +
			"?,?,?,?," +
			"?,?,?,?,?," +
			"?,?,?,?,?)"
			+ " ON DUPLICATE KEY UPDATE `slug`=?, `firstName`=?, `lastName`=?, `positionName`=?, `phone`=?, `createdBy`=?, `createdDate`=?;";

	public EmployeeDAO() {
		this.type = Employee.class;
	}

	public List<Employee> findByAccount(int accountId) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId", Employee.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public Employee findEmployeeByAccount(int employeeId, int accountId) {
		TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId AND e.id = :employeeId", Employee.class);
		query.setParameter("accountId", accountId);
		query.setParameter("employeeId", employeeId);
		return query.getSingleResult();
	}

	public Employee findEmployeeByAccountIdAndEmail(int accountId, String email) {
		if (accountId > 0 && Strings.isNotEmpty(email)) {
			try {
				TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId AND e.email = :email", Employee.class);
				query.setParameter("accountId", accountId);
				query.setParameter("email", email);
				return query.getSingleResult();
			} catch (NoResultException noResultException) {
			}
		}

		return null;
	}

	public Employee findEmployeeByAccountIdAndSlug(int accountId, String slug) {
		if (accountId > 0 && Strings.isNotEmpty(slug)) {
			try {
				TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.accountId = :accountId AND e.slug = :slug", Employee.class);
				query.setParameter("accountId", accountId);
				query.setParameter("slug", slug);
				return query.getSingleResult();
			} catch (NoResultException noResultException) {
			}
		}

		return null;
	}

	public List<Employee> findByIds(List<Integer> employeeIds) {
		if (CollectionUtils.isNotEmpty(employeeIds)) {
			TypedQuery<Employee> query = em.createQuery("FROM Employee e WHERE e.id IN (:employeeIds)", Employee.class);
			query.setParameter("employeeIds", employeeIds);
			return query.getResultList();
		}

		return Collections.emptyList();
	}

	public List<Employee> findEmployeesByGroups(Set<Integer> groupIds) {
		if (CollectionUtils.isNotEmpty(groupIds)) {
			String sql2 = "SELECT DISTINCT e FROM AccountGroupEmployee age INNER JOIN age.employee e WHERE age.group.id IN (:groupIds)";
			TypedQuery query = em.createQuery(sql2, Employee.class);
			query.setParameter("groupIds", groupIds);
			return query.getResultList();
		}

		return Collections.emptyList();
	}

	public List<Employee> search(String searchTerm, int accountId) {
		TypedQuery<Employee> query = em.createQuery(
				"SELECT DISTINCT e FROM Employee e " +
						"WHERE e.accountId = :accountId " +
						"AND (e.firstName LIKE :searchTerm " +
						"OR e.lastName LIKE :searchTerm " +
						"OR e.slug LIKE :searchTerm " +
						"OR e.email LIKE :searchTerm)", Employee.class);
		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void save(List<Employee> employees) throws Exception {
		Database.executeBatch(INSERT_EMPLOYEE_QUERY, employees, new EmployeeQueryMapper());
	}

	public void delete(int id, int accountId) {
		Employee employee = findEmployeeByAccount(id, accountId);
		super.delete(employee);
	}

}
