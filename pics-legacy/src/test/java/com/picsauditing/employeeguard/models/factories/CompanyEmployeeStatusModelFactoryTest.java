package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompanyEmployeeStatusModelFactoryTest extends CompanyEmployeeModelFactoryTest {

	CompanyEmployeeStatusModelFactory companyEmployeeStatusModelFactory;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		companyEmployeeStatusModelFactory = new CompanyEmployeeStatusModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		Employee fakeEmployee = super.buildFakeEmployee();
		List<EmploymentInfoModel> fakeCompanies = new ArrayList<>();
		List<ProjectStatusModel> fakeProjects = new ArrayList<>();
		List<RoleStatusModel> fakeRoleModels = new ArrayList<>();

		CompanyEmployeeStatusModel companyEmployeeStatusModel = companyEmployeeStatusModelFactory.create(fakeEmployee,
				fakeCompanies, fakeProjects, fakeRoleModels, SkillStatus.Expiring);

		verifyTestCreateStatus(fakeCompanies, fakeProjects, fakeRoleModels, companyEmployeeStatusModel);
		assertEquals(SkillStatus.Expiring, companyEmployeeStatusModel.getStatus());
	}

	private void verifyTestCreateStatus(final List<EmploymentInfoModel> fakeCompanies,
									final List<ProjectStatusModel> fakeProjects,
									final List<RoleStatusModel> fakeRoleModels,
									final CompanyEmployeeStatusModel companyEmployeeModel) {
		assertEquals(EMPLOYEE_ID, companyEmployeeModel.getId());
		assertEquals(EMPLOYEE_FIRST_NAME, companyEmployeeModel.getFirstName());
		assertEquals(EMPLOYEE_LAST_NAME, companyEmployeeModel.getLastName());
		assertEquals(EMPLOYEE_TITLE, companyEmployeeModel.getTitle());

		assertEquals(fakeCompanies, companyEmployeeModel.getCompanies());
		assertEquals(fakeProjects, companyEmployeeModel.getProjects());
		assertEquals(fakeRoleModels, companyEmployeeModel.getRoles());
	}
}
