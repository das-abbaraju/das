package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@Transactional
@SuppressWarnings("unchecked")
public class JobRoleDAO extends PicsDAO {
	public JobRole find(int id) {
		return em.find(JobRole.class, id);
	}

	public List<JobRole> findAll() {
		Query q = em.createQuery("FROM JobRole jr ORDER BY jr.name");
		return q.getResultList();
	}

	public List<JobRole> findWhere(String where) {
		Query query = em.createQuery("From JobRole WHERE " + where);
		return query.getResultList();
	}
	
	public List<JobRole> findJobRolesByAccount(int accountID) {
		Query query = em.createQuery("From JobRole WHERE account.id = :accountID AND active = 1 ORDER BY name");
		query.setParameter("accountID", accountID);
		return query.getResultList();
	}
	
	public DoubleMap<JobRole, OperatorCompetency, JobCompetency> findJobCompetencies(int accountID) {
		Query query = em.createQuery("From JobCompetency WHERE jobRole.account.id = :accountID AND active = 1");
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
	
	public List<JobCompetency> getCompetenciesByRole(JobRole jobRole) {
		Query query = em.createQuery("From JobCompetency WHERE jobRole.id = :jobRole ORDER BY competency.category, competency.label");
		query.setParameter("jobRole", jobRole.getId());
		return query.getResultList();
	}
}
