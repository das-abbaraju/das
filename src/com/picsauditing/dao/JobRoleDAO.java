package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobRole;

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
	
	public List<JobRole> findContractorJobRoles(int accountID) {
		Query query = em.createQuery("From JobRole WHERE accountID = :accountID ORDER BY name");
		query.setParameter("accountID", accountID);
		return query.getResultList();
	}
	
	public int getUsedCount(String name) {
		Query query = em.createQuery("From JobRole WHERE name LIKE :name");
		query.setParameter("name", name);
		return query.getResultList().size();
	}
}
