package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import com.picsauditing.employeeguard.entities.builders.ProfileDocumentBuilder;
import com.picsauditing.employeeguard.forms.contractor.EmployeeEmploymentForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePersonalForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePhotoForm;
import com.picsauditing.employeeguard.services.EmployeeService;
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
		List<Employee> employees = Arrays.asList(employee,
				new EmployeeBuilder()
						.firstName("Second")
						.lastName("Last")
						.slug("Slug")
						.profile(
								new ProfileBuilder()
										.id(1)
										.documents(Arrays.asList(
												new ProfileDocumentBuilder()
														.build()))
										.build())
						.build());

		when(employeeService.getEmployeesForAccount(anyInt())).thenReturn(employees);
		when(employeeService.findEmployee(anyInt(), anyInt())).thenReturn(employee);
		when(employeeService.search(anyString(), anyInt())).thenReturn(employees);
		when(employeeService.updateEmployment(any(EmployeeEmploymentForm.class), anyInt(), anyInt(), anyInt())).thenReturn(employee);
		when(employeeService.updatePersonal(any(EmployeePersonalForm.class), anyInt(), anyInt(), anyInt())).thenReturn(employee);
		when(employeeService.updatePhoto(any(EmployeePhotoForm.class), anyString(), anyString(), anyInt())).thenReturn(employee);
		when(employeeService.exportEmployees(Account.PICS_ID)).thenReturn(new byte[0]);
		when(employeeService.exportEmployees(BAD_ACCOUNT_ID)).thenThrow(new Exception("Testing"));
		when(employeeService.exportTemplate()).thenReturn(new byte[0]);

		return employeeService;
	}
}
