package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class JobSiteDAO extends PicsDAO {
	public JobSite save(JobSite o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		JobSite row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public JobSite find(int id) {
		JobSite a = em.find(JobSite.class, id);
		return a;
	}

	public List<JobSite> findByOperator(int opID) {
		Query query = em.createQuery("SELECT j FROM JobSite j WHERE opID = ? ORDER BY name");
		query.setParameter(1, opID);
		
		return query.getResultList();
	}
	
	public List<JobSite> findByOperatorWhere(int opID, String where) {
		Query query = em.createQuery("SELECT j FROM JobSite j WHERE opID = ? AND " + where + " ORDER BY name");
		query.setParameter(1, opID);
		
		return query.getResultList();
	}
	
	public List<Date> findHistory(String where) {
		if (!Strings.isEmpty(where))
			where = " WHERE " + where;
		else
			where = "";
	
		Query query = em.createQuery("SELECT DISTINCT j.projectStart FROM JobSite j" + where 
				+ " ORDER BY j.projectStart DESC");
		return query.getResultList();
	}
}
