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
		return findByOperator(opID, false);
	}

	public List<JobSite> findByOperator(int opID, boolean notExpired) {
		Query query = em.createQuery("SELECT j FROM JobSite j WHERE opID = :opID "
				+ (notExpired ? "AND (j.projectStart IS NULL or j.projectStart < :date) "
						+ "AND (j.projectStop IS NULL OR j.projectStop > :date) " : "") + "ORDER BY name");
		query.setParameter("opID", opID);

		if (notExpired)
			query.setParameter("date", new Date());

		return query.getResultList();
	}

	public List<JobSite> findByContractor(int conID) {
		return findByContractor(conID, false);
	}

	public List<JobSite> findByContractor(int conID, boolean notExpired) {
		Query query = em.createQuery("SELECT DISTINCT es.jobSite FROM EmployeeSite es "
				+ "WHERE es.employee.account.id = :conID "
				+ (notExpired ? "AND (es.jobSite.projectStart IS NULL OR es.jobSite.projectStart < :date) "
						+ "AND (es.jobSite.projectStop IS NULL OR es.jobSite.projectStop > :date) " : "")
				+ "ORDER BY name");
		query.setParameter("conID", conID);

		if (notExpired)
			query.setParameter("date", new Date());

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

	public List<JobSite> findOutsideProjects(int conID) {
		Query query = em.createQuery("SELECT DISTINCT j FROM JobSite j "
				+ "WHERE j NOT IN (SELECT jc.job FROM JobContractor jc WHERE jc.contractor.id = ?) "
				+ "ORDER BY j.projectStart DESC");
		query.setParameter(1, conID);

		return query.getResultList();
	}
}
