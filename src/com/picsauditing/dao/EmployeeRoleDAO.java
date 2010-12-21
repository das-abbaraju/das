package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class EmployeeRoleDAO extends PicsDAO {
	public EmployeeRole find(int id) {
		return em.find(EmployeeRole.class, id);
	}

	public List<EmployeeRole> findWhere(String where) {
		if (Strings.isEmpty(where))
			where = "";
		else
			where = " WHERE " + where;

		Query query = em.createQuery("SELECT e FROM EmployeeRole e " + where + " ORDER BY e.employee.lastName");
		return query.getResultList();
	}

	public List<EmployeeRole> findAll() {
		return (List<EmployeeRole>) super.findAll(EmployeeRole.class);
	}
}