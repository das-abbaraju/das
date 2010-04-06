package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobSiteTask;

@Transactional
@SuppressWarnings("unchecked")
public class JobSiteTaskDAO extends PicsDAO {
	public JobSiteTask save(JobSiteTask o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		JobSiteTask row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public JobSiteTask find(int id) {
		JobSiteTask a = em.find(JobSiteTask.class, id);
		return a;
	}

	public List<JobSiteTask> findByJob(int job) {
		Query query = em.createQuery("SELECT j FROM JobSiteTask j WHERE jobID = ? ORDER BY taskID");
		query.setParameter(1, job);
		
		return query.getResultList();
	}
}
