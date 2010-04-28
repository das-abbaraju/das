package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmployeeQualification;

@Transactional
@SuppressWarnings("unchecked")
public class EmployeeQualificationDAO extends PicsDAO {

	public EmployeeQualification find(int id) {
		return em.find(EmployeeQualification.class, id);
	}

	public List<EmployeeQualification> findByEmployee(int employeeID) {
		Query query = em.createQuery("SELECT e FROM EmployeeQualification e WHERE employeeID = ?");
		query.setParameter(1, employeeID);
		
		return query.getResultList();
	}
	
	public List<EmployeeQualification> findByTask(int taskID) {
		Query query = em.createQuery("SELECT e FROM EmployeeQualification e WHERE taskID = ?");
		query.setParameter(1, taskID);
		
		return query.getResultList();
	}
	
	public Map<Integer, List<EmployeeQualification>> findBySite(int siteID) {
		Query query = em.createQuery("SELECT e FROM EmployeeQualification e WHERE e.task IN " +
				"(SELECT j.task FROM JobSiteTask j where j.job.id = ?)");
		query.setParameter(1, siteID);
		
		Map<Integer, List<EmployeeQualification>> map = new TreeMap<Integer, List<EmployeeQualification>>();
		
		List<EmployeeQualification> list = query.getResultList();
		for (EmployeeQualification eq : list) {
			if (!map.containsKey(eq.getTask().getId()))
				map.put(eq.getTask().getId(), new ArrayList<EmployeeQualification>());
			
			map.get(eq.getTask().getId()).add(eq);
		}
		
		return map;
	}
}
