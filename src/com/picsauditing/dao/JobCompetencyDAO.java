package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class JobCompetencyDAO extends PicsDAO {
	public JobCompetency find(int id) {
		return em.find(JobCompetency.class, id);
	}

	public List<JobCompetency> findAll() {
		Query q = em.createQuery("FROM JobCompetency jc");
		return q.getResultList();
	}

	public List<JobCompetency> findWhere(String where) {
		if (Strings.isEmpty(where))
			where = "";
		else
			where = " WHERE " + where;
		
		Query query = em.createQuery("FROM JobCompetency jc" + where);
		return query.getResultList();
	}
}
