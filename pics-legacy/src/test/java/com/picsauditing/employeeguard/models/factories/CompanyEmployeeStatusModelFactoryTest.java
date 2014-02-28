package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.CompanyEmployeeStatusModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
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

//	@Test
//	public void testCreate() throws Exception {
//		Employee fakeEmployee = super.buildFakeEmployee();
//		List<CompanyModel> fakeCompanies = new ArrayList<>();
//		List<ProjectModel> fakeProjects = new ArrayList<>();
//		List<RoleModel> fakeRoleModels = new ArrayList<>();
//
//		CompanyEmployeeStatusModel companyEmployeeStatusModel = companyEmployeeStatusModelFactory.create(fakeEmployee,
//				fakeCompanies, fakeProjects, fakeRoleModels, SkillStatus.Expiring);
//
//		super.verifyTestCreate(fakeCompanies, fakeProjects, fakeRoleModels, companyEmployeeStatusModel);
//		assertEquals(SkillStatus.Expiring, companyEmployeeStatusModel.getStatus());
//	}
}
