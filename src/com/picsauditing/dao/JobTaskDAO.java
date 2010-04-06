package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobTask;

@Transactional
@SuppressWarnings("unchecked")
public class JobTaskDAO extends PicsDAO {
	public JobTask find(int id) {
		return em.find(JobTask.class, id);
	}

	public List<JobTask> findAll() {
		Query q = em.createQuery("FROM JobTask jt ORDER BY jt.label");
		return q.getResultList();
	}

	public List<JobTask> findWhere(String where) {
		Query query = em.createQuery("From JobTask WHERE " + where);
		return query.getResultList();
	}

	public List<JobTask> findDistinctTasks(int id) {
		Query query = em.createQuery("From JobTask jt ORDER BY jt.label, jt.name WHERE jt.id = " + id);
		return query.getResultList();
	}
}
