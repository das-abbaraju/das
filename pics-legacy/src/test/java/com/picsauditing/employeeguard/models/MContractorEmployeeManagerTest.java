package com.picsauditing.employeeguard.models;


import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
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

		List<MOperations> mEmployeeOperations = new ArrayList<>();mEmployeeOperations.add(MOperations.COPY_ID);mEmployeeOperations.add(MOperations.COPY_NAME);mEmployeeOperations.add(MOperations.ATTACH_DOCUMENTATION);
		MModels.fetchContractorEmployeeManager().setmOperations(mEmployeeOperations);
		MModels.fetchContractorEmployeeManager().copyProjectRoleEmployees(pres);
		MContractorEmployeeManager.MContractorEmployee mContractorEmployee = MModels.fetchContractorEmployeeManager().fetchModel(employee.getId());

		assertTrue(mContractorEmployee.hasProjectSkills());
		assertNotNull(mContractorEmployee.getEmployeeRoles());

	}

	@Test
	public void testCopyEmployee_attachContractor() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());

		List<MOperations> mContractorOperations = new ArrayList<>();mContractorOperations.add(MOperations.COPY_ID);mContractorOperations.add(MOperations.COPY_NAME);
		MModels.fetchContractorManager().setmOperations(mContractorOperations);


		List<MOperations> mEmployeeOperations = new ArrayList<>();mEmployeeOperations.add(MOperations.COPY_ID);mEmployeeOperations.add(MOperations.COPY_NAME);mEmployeeOperations.add(MOperations.ATTACH_CONTRACTOR);
		MModels.fetchContractorEmployeeManager().setmOperations(mEmployeeOperations);
		MModels.fetchContractorEmployeeManager().copyEmployee(employee);
		MContractorEmployeeManager.MContractorEmployee mContractorEmployee = MModels.fetchContractorEmployeeManager().fetchModel(employee.getId());

		assertTrue(contractorAccountId == mContractorEmployee.getContractor().getAccountId());

	}

	@Test
	public void testCopyEmployee_attachDocumentation() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());


		List<MOperations> mEmployeeOperations = new ArrayList<>();mEmployeeOperations.add(MOperations.COPY_ID);mEmployeeOperations.add(MOperations.COPY_NAME);mEmployeeOperations.add(MOperations.ATTACH_DOCUMENTATION);
		MModels.fetchContractorEmployeeManager().setmOperations(mEmployeeOperations);
		MModels.fetchContractorEmployeeManager().copyEmployee(employee);
		MContractorEmployeeManager.MContractorEmployee mContractorEmployee = MModels.fetchContractorEmployeeManager().fetchModel(employee.getId());

		assertNotNull(mContractorEmployee.getEmployeeDocumentation());
	}

}
