package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;

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

	public List<JobSiteTask> findByJob(int jobID) {
		Query query = em.createQuery("SELECT j FROM JobSiteTask j WHERE j.job.id = ? ORDER BY j.task.label, j.task.name");
		query.setParameter(1, jobID);
		
		return query.getResultList();
	}
	
	public List<JobSiteTask> findByOperator(int opID) {
		Query query = em.createQuery("SELECT j FROM JobSiteTask j WHERE j.job.operator.id = ? ORDER BY j.task.label");
		query.setParameter(1, opID);
		
		return query.getResultList();
	}
	
	public List<JobSiteTask> findByEmployeeAccount(int accountID) {
		Query query = em.createQuery("SELECT j FROM JobSiteTask j WHERE j.job IN " +
				"(SELECT e.jobSite FROM EmployeeSite e WHERE e.employee.account.id = ?)");
		query.setParameter(1, accountID);
		
		return query.getResultList();
	}
	
	public Map<JobSite, List<JobTask>> findByEmployee(int employeeID) {
		Query query = em.createQuery("SELECT j FROM JobSiteTask j WHERE j.job IN " +
			"(SELECT e.jobSite FROM EmployeeSite e WHERE e.employee.id = ?) " +
			"ORDER BY j.task.label, j.task.name");
		query.setParameter(1, employeeID);
		
		Map<JobSite, List<JobTask>> map = new TreeMap<JobSite, List<JobTask>>();
		List<JobSiteTask> jsts = query.getResultList();
		
		for (JobSiteTask jst : jsts) {
			if (map.get(jst.getJob()) == null)
				map.put(jst.getJob(), new ArrayList<JobTask>());
			
			map.get(jst.getJob()).add(jst.getTask());
		}
		
		return map;
	}
}
