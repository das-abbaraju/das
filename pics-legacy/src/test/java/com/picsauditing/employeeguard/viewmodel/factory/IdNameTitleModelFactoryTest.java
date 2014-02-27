package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.IdNameTitleModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IdNameTitleModelFactoryTest {

	IdNameTitleModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new IdNameTitleModelFactory();
	}

	@Test
	public void testCreate_ListNoEmployeeProfile() throws Exception {
		List<IdNameTitleModel> result = factory.create(buildFakeEmployee(), buildFakeAccountModel());

		verifyTestCreate(result.get(0), "12", "Test Account 1", "The Welder");
	}

	@Test
	public void testCreate_ListWithEmployeeProfile() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();
		fakeEmployee.setProfile(buildFakeProfile(fakeEmployee));
		List<IdNameTitleModel> result = factory.create(fakeEmployee, buildFakeAccountModel());

		verifyTestCreate_ListWithEmployeeProfile(result);
	}

	private void verifyTestCreate_ListWithEmployeeProfile(List<IdNameTitleModel> result) {
		verifyTestCreate(result.get(0), "12", "Test Account 1", "The Welder");
		verifyTestCreate(result.get(1), "67", "Test Account 2", "Welder Type II");
	}

	@Test
	public void testCreate() throws Exception {
		IdNameTitleModel result = factory.create(buildFakeEmployee(), buildFakeAccountModel().get(12));

		verifyTestCreate(result, "12", "Test Account 1", "The Welder");
	}

	private void verifyTestCreate(IdNameTitleModel result,
								  final String id,
								  final String accountName,
								  final String employeeTitle) {
		assertEquals(id, result.getId());
		assertEquals(accountName, result.getName());
		assertEquals(employeeTitle, result.getTitle());
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.id(345)
				.accountId(12)
				.firstName("Bob")
				.lastName("The Tester")
				.positionName("The Welder")
				.build();
	}

	private Profile buildFakeProfile(final Employee employee) {
		return new ProfileBuilder()
				.employees(new ArrayList<Employee>() {{
					add(employee);
					add(new EmployeeBuilder().accountId(67).positionName("Welder Type II").build());
				}})
				.build();
	}

	private Map<Integer, AccountModel> buildFakeAccountModel() {
		return new HashMap<Integer, AccountModel>() {{
			put(12, new AccountModel.Builder().id(12).name("Test Account 1").build());
			put(67, new AccountModel.Builder().id(67).name("Test Account 2").build());
		}};
	}
}
