package com.picsauditing.dao;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.Luhn;
import com.picsauditing.util.Strings;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unchecked")
public class LegacyEmployeeDAO extends PicsDAO {

	public Employee find(int id) {
		return em.find(Employee.class, id);
	}

	public List<Employee> findAll() {
		return (List<Employee>) findAll(Employee.class);
	}

	public List<Employee> findWhere(String where) {
		return findWhere(where, -1);
	}

	public List<Employee> findWhere(String where, int limit) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT e FROM LegacyEmployee e " + where);
		if (limit > 0)
			query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<Employee> findRandom(int limit) {
		Query query = em.createQuery("SELECT e FROM LegacyEmployee e ORDER BY RAND()");
		if (limit > 0)
			query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<Employee> findByEmail(String email) {
		Query query = em.createQuery("SELECT e FROM LegacyEmployee e WHERE email = ?");
		query.setParameter(1, email);
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<Employee> findByAccount(Account account) {
		Query query = em.createQuery("SELECT e FROM LegacyEmployee e WHERE e.account = :account ORDER BY e.firstName");
		query.setParameter("account", account);
		return query.getResultList();
	}

	public List<Employee> findByJobRole(int jobRoleID, int accountID) {
		Query query = em.createQuery("SELECT e FROM LegacyEmployee e"
				+ " WHERE e.id IN (SELECT er.employee.id FROM EmployeeRole er WHERE er.jobRole.id = ?)"
				+ " AND e.account.id = ? ORDER BY e.lastName");
		query.setParameter(1, jobRoleID);
		query.setParameter(2, accountID);
		return query.getResultList();
	}

	public List<Employee> findByCompetencies(int[] competencyIDs, int accountID) {
		Query query = em.createQuery("SELECT e FROM LegacyEmployee e WHERE e.id IN "
				+ "(SELECT ec.employee.id FROM EmployeeCompetency ec WHERE ec.competency.id IN ("
				+ Strings.implode(competencyIDs) + ")) AND e.account.id = ? ORDER BY e.lastName");

		query.setParameter(1, accountID);

		return query.getResultList();
	}

	public List<String> findCommonLocations(int accountID) {
		Query query = em.createQuery("SELECT DISTINCT e.location FROM LegacyEmployee e "
				+ "WHERE e.account.id = :accountID AND e.location NOT LIKE '' "
				+ "GROUP BY location HAVING COUNT(*) > 1 ORDER BY COUNT(*) DESC");

		query.setParameter("accountID", accountID);

		return query.getResultList();
	}

	public List<String> findCommonTitles() {
		/*
		 * select title, count(*) from employee where accountID in (select id
		 * from accounts where status in ('Active','Pending')) group by title
		 * having count(*) > 1 order by title
		 */
		Query query = em.createQuery("SELECT e.title FROM LegacyEmployee e "
				+ "WHERE e.account.id IN (SELECT a.id FROM Account a WHERE a.status IN ('Active', 'Pending')) "
				+ "AND e.title IS NOT NULL GROUP BY e.title HAVING COUNT(*) > 1 ORDER BY e.title");
		return query.getResultList();
	}

	static public String generatePicsNumber(Employee employee) {
		Random random = new Random(employee.getId() + Calendar.getInstance().getTimeInMillis());
		long value = random.nextInt(899999999) + 100000000;
		String value2 = Luhn.addCheckDigit(Long.toString(value));
		return value2.substring(0, 5) + "-" + value2.substring(5, 10);
	}

	public void setPicsNumber(Employee employee) {
		employee.setPicsNumber(generatePicsNumber(employee));
	}

	@Transactional(propagation = Propagation.NESTED)
	public void save(List<Employee> employees) {
		for (Employee employee : employees) {
			if (employee.getId() == 0) {
				em.persist(employee);
			} else {
				em.merge(employee);
			}
		}

		em.flush();
	}

	public List<Employee> findByFirstNameLastNameAndAccount(Set<Employee> employees) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("FROM LegacyEmployee e WHERE ");

		boolean first = true;
		for (Employee employee : employees) {
			if (!first) {
				queryBuilder.append(" OR ");
			}

			queryBuilder.append("(e.account.id = ? AND e.firstName = ? AND e.lastName = ?)");
			first = false;
		}

		TypedQuery<Employee> query = em.createQuery(queryBuilder.toString(), Employee.class);

		int position = 1;
		for (Employee employee : employees) {
			query.setParameter(position++, employee.getAccount().getId());
			query.setParameter(position++, employee.getFirstName());
			query.setParameter(position++, employee.getLastName());
		}

		return query.getResultList();
	}
}