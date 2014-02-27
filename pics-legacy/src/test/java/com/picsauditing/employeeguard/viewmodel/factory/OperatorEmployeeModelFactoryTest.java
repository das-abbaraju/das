package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.util.UrlUtils;
import com.picsauditing.employeeguard.viewmodel.IdNameTitleModel;
import com.picsauditing.employeeguard.viewmodel.RoleModel;
import com.picsauditing.employeeguard.viewmodel.employee.OperatorEmployeeModel;
import com.picsauditing.employeeguard.viewmodel.employee.ProjectDetailModel;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorEmployeeSkillModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OperatorEmployeeModelFactoryTest {

	private OperatorEmployeeModelFactory factory;

	@Before
	public void setUp() {
		factory = new OperatorEmployeeModelFactory();
	}

	@Test
	public void testCreate() {
		Employee employee = buildFakeEmployee();
		List<IdNameTitleModel> fakeIdNameTitleModels = new ArrayList<>();
		List<ProjectDetailModel> fakeProjectDetailModels = new ArrayList<>();
		List<RoleModel> fakeRoleModels = new ArrayList<>();
		List<OperatorEmployeeSkillModel> fakeOperatorEmployeeSkillModels = new ArrayList<>();
		SkillStatus skillStatus = SkillStatus.Expiring;

		OperatorEmployeeModel operatorEmployeeModel = factory.create(employee, fakeIdNameTitleModels,
				fakeProjectDetailModels, fakeRoleModels, fakeOperatorEmployeeSkillModels, skillStatus);

		verifyCreate(employee, fakeIdNameTitleModels, fakeProjectDetailModels, fakeRoleModels,
				fakeOperatorEmployeeSkillModels, skillStatus, operatorEmployeeModel);
	}

	private void verifyCreate(Employee employee,
							  List<IdNameTitleModel> fakeIdNameTitleModels,
							  List<ProjectDetailModel> fakeProjectDetailModels,
							  List<RoleModel> fakeRoleModels,
							  List<OperatorEmployeeSkillModel> fakeOperatorEmployeeSkillModels,
							  SkillStatus skillStatus,
							  OperatorEmployeeModel operatorEmployeeModel) {
		assertEquals(employee.getName(), operatorEmployeeModel.getName());
		assertEquals(employee.getId(), operatorEmployeeModel.getId());
		assertEquals(UrlUtils.buildUrl(UrlUtils.IMAGE_LINK, employee.getAccountId(), employee.getId()), operatorEmployeeModel.getImage());
		assertEquals(fakeIdNameTitleModels, operatorEmployeeModel.getCompanies());
		assertEquals(fakeProjectDetailModels, operatorEmployeeModel.getProjects());
		assertEquals(fakeRoleModels, operatorEmployeeModel.getRoles());
		assertEquals(fakeOperatorEmployeeSkillModels, operatorEmployeeModel.getSkills());
		assertEquals(skillStatus, operatorEmployeeModel.getOverallStatus());
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.id(345)
				.accountId(12)
				.firstName("Bob")
				.lastName("The Tester")
				.build();
	}
}
