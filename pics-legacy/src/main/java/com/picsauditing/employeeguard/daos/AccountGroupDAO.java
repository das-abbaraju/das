package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AccountGroupDAO extends AbstractBaseEntityDAO<Group> {

	public AccountGroupDAO() {
		this.type = Group.class;
	}

	public List<Group> findByAccount(int accountId) {
		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId = :accountId", Group.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<Group> findByAccounts(final List<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId IN (:accountIds)", Group.class);
		query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public Group findGroupByAccount(int groupId, int accountId) {
		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId = :accountId AND g.id = :groupId", Group.class);
		query.setParameter("accountId", accountId);
		query.setParameter("groupId", groupId);
		return query.getSingleResult();
	}

	public List<Group> findGroupByAccountIdAndNames(int accountId, List<String> names) {
		// Not sure if we need to check accountId
		if (accountId <= 0 || CollectionUtils.isEmpty(names)) {
			return Collections.emptyList();
		}

		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId = :accountId AND g.name IN ( :names )", Group.class);
		query.setParameter("accountId", accountId);
		query.setParameter("names", names);
		return query.getResultList();
	}

	public Group findGroupByAccountIdAndName(int accountId, String name) {
		if (accountId <= 0 || Strings.isEmpty(name)) {
			return null;
		}

		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId = :accountId AND g.name = :name", Group.class);
		query.setParameter("accountId", accountId);
		query.setParameter("name", name);
		return query.getSingleResult();
	}

	public List<Group> findGroupByAccountIdsAndNames(final List<Integer> accountIds, final List<String> names) {
		// Not sure if we need to check accountId
		if (CollectionUtils.isEmpty(accountIds) || CollectionUtils.isEmpty(names)) {
			return Collections.emptyList();
		}

		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId IN (:accountIds) AND g.name IN ( :names )", Group.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("names", names);
		return query.getResultList();
	}

	public List<Group> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId = :accountId " +
				"AND (g.name LIKE :searchTerm " +
				"OR g.description LIKE :searchTerm)", Group.class);
		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	public List<Group> search(final String searchTerm, final List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm) || CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId IN (:accountIds) " +
				"AND (g.name LIKE :searchTerm " +
				"OR g.description LIKE :searchTerm)", Group.class);
		query.setParameter("accountIds", accountIds);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}

	public List<Group> findGroupsForEmployee(final Employee employee) {
		TypedQuery<Group> query = em.createQuery("SELECT age.group FROM GroupEmployee age " +
				"WHERE age.employee = :employee AND age.group.accountId = :accountId", Group.class);
		query.setParameter("employee", employee);
		query.setParameter("accountId", employee.getAccountId());
		return query.getResultList();
	}

	public List<Group> findGroupsByEmployees(final Collection<Employee> employees) {
		TypedQuery<Group> query = em.createQuery("SELECT age.group FROM GroupEmployee age " +
				"JOIN age.employee e " +
				"WHERE e IN (:employees)", Group.class);
		query.setParameter("employees", employees);
		return query.getResultList();
	}

	public List<Group> findEmployeeGroupAssignments(Employee employee) {
		TypedQuery<Group> query = em.createQuery("SELECT age.group FROM GroupEmployee age " +
				"WHERE age.employee = :employee AND age.group.accountId <> :accountId", Group.class);
		query.setParameter("employee", employee);
		query.setParameter("accountId", employee.getAccountId());
		return query.getResultList();
	}

	/**
	 * Job Roles are corporate created groups
	 * <p/>
	 * Contractor employees would not be assigned to these job roles unless they were assigned directly under site assignments
	 * <p/>
	 * Find by filtering out all groups created by the employee's account?
	 *
	 * @param employee
	 * @return
	 */
	public List<Role> findJobRolesForEmployee(final Employee employee) {
		Query query = em.createNativeQuery("SELECT g.* FROM account_group_employee age " +
				"JOIN account_group g ON g.id = age.groupID " +
				"WHERE age.employeeID = :employeeID AND g.type = 'Role'", Role.class);
		query.setParameter("employeeID", employee.getId());

		return (List<Role>) query.getResultList();
	}
}
