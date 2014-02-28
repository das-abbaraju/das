package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.models.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

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
	public void testCreate_ListOf_CompanyEmployeeModel() throws Exception {
		List<Employee> fakeEmployees = buildFakeEmployees();
		Map<Integer, List<EmploymentInfoModel>> companyModelMap = buildFakeCompanyModelMap();

		List<CompanyEmployeeModel> companyEmployeeModels = companyEmployeeModelFactory.create(fakeEmployees,
				companyModelMap);

		verifyTestCreate_ListOf_CompanyEmployeeModel(fakeEmployees, companyEmployeeModels);
	}

	private void verifyTestCreate_ListOf_CompanyEmployeeModel(final List<Employee> fakeEmployees,
															  final List<CompanyEmployeeModel> companyEmployeeModels) {
		assertEquals(2, companyEmployeeModels.size());

		int index = 0;
		for (CompanyEmployeeModel companyEmployeeModel : companyEmployeeModels) {
			assertEquals(fakeEmployees.get(index).getId(), companyEmployeeModel.getId());
			assertEquals(fakeEmployees.get(index).getFirstName(), companyEmployeeModel.getFirstName());
			assertEquals(fakeEmployees.get(index).getLastName(), companyEmployeeModel.getLastName());
			index++;
		}
	}

	private List<Employee> buildFakeEmployees() {
		return new ArrayList<Employee>() {{
			add(new EmployeeBuilder()
					.id(1)
					.firstName("Employee 1 First Name")
					.lastName("Employee 1 Last Name")
					.build());

			add(new EmployeeBuilder()
					.id(2)
					.firstName("Employee 2 First Name")
					.lastName("Employee 2 Last Name")
					.build());
		}};
	}

	private Map<Integer, List<EmploymentInfoModel>> buildFakeCompanyModelMap() {
		return new HashMap<Integer, List<EmploymentInfoModel>>() {{
			put(1, Arrays.asList(buildFakeEmploymentInfoModel(45, "Test Company 1")));
			put(2, Arrays.asList(buildFakeEmploymentInfoModel(46, "Test Company 2")));
		}};
	}

	private EmploymentInfoModel buildFakeEmploymentInfoModel(final int id, final String companyName) {
		EmploymentInfoModel companyModel = new EmploymentInfoModel();
		companyModel.setId(id);
		companyModel.setName(companyName);
		return companyModel;
	}

	@Test
	public void testCreate_Single_CompanyEmployeeModel() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();
		List<EmploymentInfoModel> fakeCompanies = new ArrayList<>();
		List<ProjectModel> fakeProjects = new ArrayList<>();
		List<RoleModel> fakeRoleModels = new ArrayList<>();

		CompanyEmployeeModel companyEmployeeModel = companyEmployeeModelFactory.create(fakeEmployee, fakeCompanies,
				fakeProjects, fakeRoleModels);

		verifyTestCreate(fakeCompanies, fakeProjects, fakeRoleModels, companyEmployeeModel);
	}

	private void verifyTestCreate(final List<EmploymentInfoModel> fakeCompanies,
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
