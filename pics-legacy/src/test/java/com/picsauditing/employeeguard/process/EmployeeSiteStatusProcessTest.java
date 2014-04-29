package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

public class EmployeeSiteStatusProcessTest {

	public static final int SITE_ID = 90;
	public static final int EMPLOYEE_ID = 671;
	public static final int CORPORATE_ID = 78090;

	private EmployeeSiteStatusProcess employeeSiteStatusProcess;

	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private ProjectEntityService projectEntityService;
	@Mock
	private RoleEntityService roleEntityService;
	@Mock
	private SkillEntityService skillEntityService;
	@Mock
	private StatusCalculatorService statusCalculatorService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeSiteStatusProcess = new EmployeeSiteStatusProcess();

		Whitebox.setInternalState(employeeSiteStatusProcess, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(employeeSiteStatusProcess, "projectEntityService", projectEntityService);
		Whitebox.setInternalState(employeeSiteStatusProcess, "roleEntityService", roleEntityService);
		Whitebox.setInternalState(employeeSiteStatusProcess, "skillEntityService", skillEntityService);
		Whitebox.setInternalState(employeeSiteStatusProcess, "statusCalculatorService", statusCalculatorService);
	}

	@Test
	public void testGetEmployeeSiteStatusResult() throws Exception {
		employeeSiteStatusProcess.getEmployeeSiteStatusResult(EMPLOYEE_ID, SITE_ID, Arrays.asList(CORPORATE_ID));
	}

	public void setupTestGetEmployeeSiteStatusResult() {
		Employee fakeEmployee = new EmployeeBuilder().build();
		when(employeeEntityService.find(EMPLOYEE_ID)).thenReturn(fakeEmployee);

		when(projectEntityService.getProjectsForEmployeeBySiteId(fakeEmployee, SITE_ID)).thenReturn(buildFakeProjects());
		roleEntityService.getRolesForProjectsAndEmployees(projects, Arrays.asList(employee));
		skillEntityService.getSiteAndCorporateRequiredSkills(siteId, parentSites);
		skillEntityService.getRequiredSkillsForProjects(projects);
		roleEntityService.getSiteRolesForEmployee(employee, siteId);
		skillEntityService.getSkillsForRoles(roles);
		statusCalculatorService.getSkillStatuses(employee, allSkills);
		statusCalculatorService.getOverallStatusPerEntity(roleStatuses);
		statusCalculatorService.getOverallStatusPerEntity(projectStatuses);
		roleEntityService.getSiteRolesForEmployee(employee, siteId);
	}

	private Set<Project> buildFakeProjects() {
		return new HashSet<>();
	}
}
