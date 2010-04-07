package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.util.Strings;

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
}
