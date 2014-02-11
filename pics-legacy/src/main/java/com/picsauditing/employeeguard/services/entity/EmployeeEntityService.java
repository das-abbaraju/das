package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;

public class EmployeeEntityService implements EntityService<Employee, Integer> {

    @Autowired
    private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
    @Autowired
    private EmployeeDAO employeeDAO;


    @Override
    public Employee find(final Integer id) {
        return employeeDAO.find(id);
    }

    @Override
    public Employee save(final Employee employee) {
        return employeeDAO.save(employee);
    }

    @Override
    public Employee update(final Employee employee) {
//        Employee employeeToUpdate = find(employee.getId());
//        EntityHelper.
        return employeeDAO.save(employee);
    }

    @Override
    public void delete(Employee employee) {
        employeeDAO.delete(employee);
    }

    public Set<Skill> getSkillsForEmployee(final Employee employee) {
		return Collections.emptySet();
	}
}
