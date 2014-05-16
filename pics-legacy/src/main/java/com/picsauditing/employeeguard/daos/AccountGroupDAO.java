package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AccountGroupDAO extends AbstractBaseEntityDAO<Group> {

	public AccountGroupDAO() {
		this.type = Group.class;
	}

	public List<Group> findByAccount(final int accountId) {
		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId = :accountId", Group.class);
		query.setParameter("accountId", accountId);
		return query.getResultList();
	}

	public List<Group> findByAccounts(final Collection<Integer> accountIds) {
		TypedQuery<Group> query = em.createQuery("FROM Group g " +
				"WHERE g.accountId IN (:accountIds)", Group.class);

		query.setParameter("accountIds", accountIds);

		return query.getResultList();
	}

	public Group findGroupByAccount(final int groupId, final int accountId) {
		TypedQuery<Group> query = em.createQuery("FROM Group g " +
				"WHERE g.accountId = :accountId " +
				"AND g.id = :groupId", Group.class);

		query.setParameter("accountId", accountId);
		query.setParameter("groupId", groupId);

		return query.getSingleResult();
	}

	public List<Group> findGroupByAccountIdAndNames(final int accountId, final List<String> names) {
		TypedQuery<Group> query = em.createQuery("FROM Group g " +
				"WHERE g.accountId = :accountId " +
				"AND g.name IN ( :names )", Group.class);

		query.setParameter("accountId", accountId);
		query.setParameter("names", names);

		return query.getResultList();
	}

  public List<Group> findGroupByAccountIdAndIds(final int accountId, final List<Integer> ids) {

    if (CollectionUtils.isEmpty(ids)) {
      return Collections.emptyList();
    }

    TypedQuery<Group> query = em.createQuery("FROM Group g " +
            "WHERE g.accountId = :accountId " +
            "AND g.id IN ( :ids )", Group.class);

    query.setParameter("accountId", accountId);
    query.setParameter("ids", ids);

    return query.getResultList();
  }

  public List<Group> search(final String searchTerm, final int accountId) {
		TypedQuery<Group> query = em.createQuery("FROM Group g WHERE g.accountId = :accountId " +
				"AND (g.name LIKE :searchTerm " +
				"OR g.description LIKE :searchTerm)", Group.class);

		query.setParameter("accountId", accountId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");

		return query.getResultList();
	}

	public List<Group> search(final String searchTerm, final List<Integer> accountIds) {
		TypedQuery<Group> query = em.createQuery("FROM Group g " +
				"WHERE g.accountId IN (:accountIds) " +
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

	public List<Group> findEmployeeGroupAssignments(final Employee employee) {
		TypedQuery<Group> query = em.createQuery("SELECT age.group " +
				"FROM GroupEmployee age " +
				"WHERE age.employee = :employee " +
				"AND age.group.accountId <> :accountId", Group.class);

		query.setParameter("employee", employee);
		query.setParameter("accountId", employee.getAccountId());

		return query.getResultList();
	}
}
