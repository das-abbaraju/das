package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.forms.contractor.EmployeeEmploymentForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePersonalForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePhotoForm;
import com.picsauditing.jpa.entities.Account;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class EmployeeServiceFactory {
	public static final int BAD_ACCOUNT_ID = 12345;

	private static EmployeeService employeeService = Mockito.mock(EmployeeService.class);

	public static EmployeeService getEmployeeService() throws Exception {
		Mockito.reset(employeeService);

		Employee employee = new EmployeeBuilder().firstName("First").lastName("Last").slug("Slug").build();
		List<Employee> employees = Arrays.asList(employee, new EmployeeBuilder().firstName("Second").lastName("Last").slug("Slug").build());

		when(employeeService.getEmployeesForAccount(anyInt())).thenReturn(employees);
		when(employeeService.findEmployee(anyString(), anyInt())).thenReturn(employee);
		when(employeeService.search(anyString(), anyInt())).thenReturn(employees);
		when(employeeService.updateEmployment(any(EmployeeEmploymentForm.class), anyString(), anyInt(), anyInt())).thenReturn(employee);
		when(employeeService.updatePersonal(any(EmployeePersonalForm.class), anyString(), anyInt(), anyInt())).thenReturn(employee);
		when(employeeService.updatePhoto(any(EmployeePhotoForm.class), anyString(), anyString(), anyInt())).thenReturn(employee);
		when(employeeService.exportEmployees(Account.PicsID)).thenReturn(new byte[0]);
		when(employeeService.exportEmployees(BAD_ACCOUNT_ID)).thenThrow(new Exception("Testing"));
		when(employeeService.exportTemplate()).thenReturn(new byte[0]);

		return employeeService;
	}
}
