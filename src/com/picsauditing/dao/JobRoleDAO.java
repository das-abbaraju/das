package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class JobRoleDAO extends PicsDAO {
	public JobRole find(int id) {
		return em.find(JobRole.class, id);
	}

	public List<JobRole> findJobRolesByAccount(int accountID, boolean onlyActive) {
		String queryString = "FROM JobRole WHERE account.id = :accountID";

		if (onlyActive)
			queryString += " AND active = 1";

		queryString += " ORDER BY active DESC, name";

		Query query = em.createQuery(queryString);
		query.setParameter("accountID", accountID);
		return query.getResultList();
	}

	public DoubleMap<JobRole, OperatorCompetency, JobCompetency> findJobCompetencies(int accountID, boolean onlyActive) {
		String queryString = "FROM JobCompetency WHERE jobRole.account.id = :accountID";

		if (onlyActive)
			queryString += " AND jobRole.active = 1";

		Query query = em.createQuery(queryString);
		query.setParameter("accountID", accountID);
		List<JobCompetency> resultList = query.getResultList();

		DoubleMap<JobRole, OperatorCompetency, JobCompetency> map = new DoubleMap<JobRole, OperatorCompetency, JobCompetency>();
		for (JobCompetency jc : resultList) {
			map.put(jc.getJobRole(), jc.getCompetency(), jc);
		}
		return map;
	}

	public int getUsedCount(String name) {
		Query query = em.createQuery("From JobRole WHERE name = :name");
		query.setParameter("name", name);
		return query.getResultList().size();
	}

	// select r.name from job_role r
	// where name like '%wl%'
	// group by r.name
	// order by count(distinct name) desc, name
	// limit 10;
	public List<JobRole> findDistinctRolesOrderByCount(String q) {
		Query query = em.createQuery("FROM JobRole " + "WHERE name LIKE :q " + "GROUP BY name "
				+ "ORDER BY COUNT(*) DESC, name");
		query.setParameter("q", "%" + q + "%");
		return query.getResultList();
	}

	public List<JobRole> findMostUsed(int accountID, boolean active) {
		String queryString = "SELECT jc.jobRole FROM JobCompetency jc WHERE jc.jobRole.account.id = ?";

		if (active)
			queryString += " AND jc.jobRole.active = 1";

		queryString += " GROUP BY jc.jobRole ORDER BY COUNT(*) DESC, jc.jobRole.name";
		Query query = em.createQuery(queryString);
		query.setParameter(1, accountID);

		return query.getResultList();
	}
}