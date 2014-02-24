package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.models.CompanyEmployeeModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompanyEmployeeModelFactoryTest {

	public static final int EMPLOYEE_ID = 789;
	public static final String EMPLOYEE_FIRST_NAME = "Bob";
	public static final String EMPLOYEE_LAST_NAME = "Jackson";
	public static final String EMPLOYEE_TITLE = "Master Welder";

	CompanyEmployeeModelFactory companyEmployeeModelFactory;

	@Before
	public void setUp() throws Exception {
		companyEmployeeModelFactory = new CompanyEmployeeModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();
		List<CompanyModel> fakeCompanies = new ArrayList<>();
		List<ProjectModel> fakeProjects = new ArrayList<>();
		List<RoleModel> fakeRoleModels = new ArrayList<>();

		CompanyEmployeeModel companyEmployeeModel = companyEmployeeModelFactory.create(fakeEmployee, fakeCompanies,
				fakeProjects, fakeRoleModels);

		verifyTestCreate(fakeCompanies, fakeProjects, fakeRoleModels, companyEmployeeModel);
	}

	protected void verifyTestCreate(final List<CompanyModel> fakeCompanies,
								  final List<ProjectModel> fakeProjects,
								  final List<RoleModel> fakeRoleModels,
								  final CompanyEmployeeModel companyEmployeeModel) {
		assertEquals(EMPLOYEE_ID, companyEmployeeModel.getId());
		assertEquals(EMPLOYEE_FIRST_NAME, companyEmployeeModel.getFirstName());
		assertEquals(EMPLOYEE_LAST_NAME, companyEmployeeModel.getLastName());
		assertEquals(EMPLOYEE_TITLE, companyEmployeeModel.getTitle());
		assertEquals(fakeCompanies, companyEmployeeModel.getCompanies());
		assertEquals(fakeProjects, companyEmployeeModel.getProjects());
		assertEquals(fakeRoleModels, companyEmployeeModel.getRoles());
	}

	protected Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.id(EMPLOYEE_ID)
				.firstName(EMPLOYEE_FIRST_NAME)
				.lastName(EMPLOYEE_LAST_NAME)
				.positionName(EMPLOYEE_TITLE)
				.build();
	}
}
