package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class EmployeeAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeService employeeService;

	private int siteId;

	private EmployeeModel employee;

	public String show() {
		Employee employeeEntity = employeeService.findEmployee(id);

		Map<Integer, AccountModel> contractors = accountService.getContractorsForEmployee(employeeEntity);
		employee = ViewModelFactory.getEmployeeModelFactory().create(employeeEntity, contractors);

		return SHOW;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public EmployeeModel getEmployee() {
		return employee;
	}
}
