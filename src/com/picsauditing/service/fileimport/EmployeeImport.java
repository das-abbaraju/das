package com.picsauditing.service.fileimport;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Set;

public class EmployeeImport {
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;

	public void importEmployees(final File file, Account account, Permissions permissions) {
		EmployeeFileParser parser = new EmployeeFileParser(operatorAccountDAO, account, permissions);
		Set<Employee> employees = parser.parseFile(file);

		EmployeeDuplicateResolver resolver = new EmployeeDuplicateResolver(employees, employeeDAO);
		resolver.resolveDuplicates();
		List<Employee> uniqueEmployees = resolver.getUniqueEmployees();
		List<Employee> duplicateEmployees = resolver.getDuplicateEmployees();

		// Only save the unique employees
		if (!uniqueEmployees.isEmpty()) {
			employeeDAO.save(uniqueEmployees);
		}

		if (!duplicateEmployees.isEmpty()) {
			// Updating duplicated employees (in case their employee sites have changed)
			employeeDAO.save(duplicateEmployees);
		}
	}
}
