package com.picsauditing.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class OperatorCompetencyDAO extends PicsDAO {

	public List<OperatorCompetency> findAll() {
		Query q = em.createQuery("FROM OperatorCompetency o ORDER BY o.category, o.label");
		return q.getResultList();
	}

	public List<OperatorCompetency> findByContractor(int conID) {
		Query query = em.createQuery("SELECT o FROM OperatorCompetency o " +
				"WHERE o IN (SELECT j.competency FROM JobCompetency j WHERE j.jobRole.account.id = ?) " +
				"ORDER BY o.category, o.label");

		query.setParameter(1, conID);
		return query.getResultList();
	}
	
	public DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> findEmployeeCompetencies(Collection<Employee> employees, Collection<OperatorCompetency> opCompetencies) {
		Query query = em.createQuery("SELECT e FROM EmployeeCompetency e " +
				"WHERE e.employee.id IN (?) " +
				"AND e.competency.id IN (?)");

		query.setParameter(1, Strings.implodeForDB(employees, ","));
		query.setParameter(2, Strings.implodeForDB(opCompetencies, ","));
		
		List<EmployeeCompetency> results = query.getResultList();
		
		DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map = new DoubleMap<Employee, OperatorCompetency, EmployeeCompetency>();
		for (EmployeeCompetency employeeCompetency : results) {
			map.put(employeeCompetency.getEmployee(), employeeCompetency.getCompetency(), employeeCompetency);
		}
		
		return map;
	}
}
