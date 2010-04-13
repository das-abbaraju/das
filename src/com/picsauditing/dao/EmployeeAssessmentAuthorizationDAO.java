package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmployeeAssessmentAuthorization;

@Transactional
@SuppressWarnings("unchecked")
public class EmployeeAssessmentAuthorizationDAO extends PicsDAO {
	public EmployeeAssessmentAuthorization find(int id) {
		EmployeeAssessmentAuthorization a = em.find(EmployeeAssessmentAuthorization.class, id);
		return a;
	}

	public List<EmployeeAssessmentAuthorization> findByEmployee(int employeeID) {
		Query query = em.createQuery("SELECT e FROM EmployeeAssessmentAuthorization e WHERE employeeID = ?");
		query.setParameter(1, employeeID);
		
		return query.getResultList();
	}
	
	public List<EmployeeAssessmentAuthorization> findAll() {
		Query query = em.createQuery("SELECT e FROM EmployeeAssessmentAuthorization e");
		
		return query.getResultList();
	}
}
