package com.picsauditing.employeeguard.models;


import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.models.operations.MOperations;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MContractorEmployeeManagerTest extends MManagersTest {
	private AccountModel accountModel;
	private int contractorAccountId = egTestDataUtil.CONTRACTOR_ID;
	private Employee employee;
	private List<AccountSkill> accountSkills;
	private List<AccountSkillProfile> accountSkillProfiles;


	@Before
	public void setUp() throws Exception {
		super.setUp();
		accountModel = egTestDataUtil.buildFakeContractorAccountModel();

		when( accountService.getAccountById(contractorAccountId)).thenReturn(accountModel);

		employee = egTestDataUtil.buildNewFakeEmployee();
		accountSkillProfiles = egTestDataUtil.buildFakeAccountSkillProfiles_MixedBag(employee);
	}

	@Test
	public void testAttachWithModel() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MContractorEmployeeManager mManager = MModels.fetchContractorEmployeeManager();
		mManager.attachWithModel(employee);
		assertNotNull(mManager.fetchModel(employee.getId()));

	}

	@Test
	public void testCopyProjectRoleEmployees() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());

		List<ProjectRoleEmployee> pres = egTestDataUtil.buildFakeProjectRoleEmployees(employee);

		MModels.fetchContractorEmployeeManager().operations().copyId().copyName().attachDocumentations();
		MModels.fetchContractorEmployeeManager().copyProjectRoleEmployees(pres);
		MContractorEmployeeManager.MContractorEmployee mContractorEmployee = MModels.fetchContractorEmployeeManager().fetchModel(employee.getId());

		assertTrue(mContractorEmployee.hasProjectSkills());
		assertNotNull(mContractorEmployee.getEmployeeRoles());

	}

	@Test
	public void testCopyEmployee_attachContractor() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());

		MModels.fetchContractorManager().operations().copyId().copyName();


		MModels.fetchContractorEmployeeManager().operations().copyId().copyName().attachContractor();
		MModels.fetchContractorEmployeeManager().copyEmployee(employee);
		MContractorEmployeeManager.MContractorEmployee mContractorEmployee = MModels.fetchContractorEmployeeManager().fetchModel(employee.getId());

		assertTrue(contractorAccountId == mContractorEmployee.getContractor().getAccountId());

	}

	@Test
	public void testCopyEmployee_attachDocumentation() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());


		MModels.fetchContractorEmployeeManager().operations().copyId().copyName().attachDocumentations();
		MModels.fetchContractorEmployeeManager().copyEmployee(employee);
		MContractorEmployeeManager.MContractorEmployee mContractorEmployee = MModels.fetchContractorEmployeeManager().fetchModel(employee.getId());

		assertNotNull(mContractorEmployee.getEmployeeDocumentation());
	}

}
