package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.SiteAssignmentDAO;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleAssignmentHelperTest {

	public static final int EMPLOYEE_ID = 1234;
	public static final int SITE_ID = 5678;
	private RoleAssignmentHelper roleAssignmentHelper;

	@Mock
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Mock
	private SiteAssignmentDAO siteAssignmentDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		roleAssignmentHelper = new RoleAssignmentHelper();

		Whitebox.setInternalState(roleAssignmentHelper, "projectRoleEmployeeDAO", projectRoleEmployeeDAO);
		Whitebox.setInternalState(roleAssignmentHelper, "siteAssignmentDAO", siteAssignmentDAO);
	}

	@Test
	public void testDeleteProjectRolesFromEmployee() throws Exception {
		List<ProjectRoleEmployee> projectRoleEmployees = Arrays.asList(new ProjectRoleEmployee());
		when(projectRoleEmployeeDAO.findByEmployeeAndSiteId(EMPLOYEE_ID, SITE_ID)).thenReturn(projectRoleEmployees);

		roleAssignmentHelper.deleteProjectRolesFromEmployee(EMPLOYEE_ID, SITE_ID);

		verify(projectRoleEmployeeDAO).delete(projectRoleEmployees);
	}

	@Test
	public void testDeleteSiteRolesFromEmployee() throws Exception {
		roleAssignmentHelper.deleteSiteRolesFromEmployee(EMPLOYEE_ID, SITE_ID);

		verify(siteAssignmentDAO).deleteByEmployeeIdAndSiteId(EMPLOYEE_ID, SITE_ID);
	}
}
