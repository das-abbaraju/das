package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobTaskCriteria;

@Transactional
@SuppressWarnings("unchecked")
public class JobTaskCriteriaDAO extends PicsDAO {
	public JobTaskCriteria find(int id) {
		return em.find(JobTaskCriteria.class, id);
	}

	public List<JobTaskCriteria> findAll() {
		Query q = em.createQuery("FROM JobTaskCriteria jt ORDER BY jt.taskID, jt.groupNumber");
		return q.getResultList();
	}

	public List<JobTaskCriteria> findWhere(String where) {
		Query query = em.createQuery("FROM JobTaskCriteria WHERE " + where);
		return query.getResultList();
	}

	public List<JobTaskCriteria> findByTask(int taskID) {
		Query query = em.createQuery("FROM JobTaskCriteria WHERE taskID = :taskID ORDER BY id, groupNumber");
		query.setParameter("taskID", taskID);
		return query.getResultList();
	}

	public List<Date> findHistoryByTask(int taskID) {
		Query query = em.createQuery("SELECT DISTINCT effectiveDate FROM JobTaskCriteria WHERE task.id = :taskID ORDER BY effectiveDate");
		query.setParameter("taskID", taskID);
		return query.getResultList();
	}
}
