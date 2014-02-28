package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.CompanyEmployeeModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CompanyModelFactoryTest {

	public static final int COMPANY_ID = 678;
	public static final String COMPANY_NAME = "Test Account";

	CompanyModelFactory companyModelFactory = new CompanyModelFactory();

	@Before
	public void setUp() {
		companyModelFactory = new CompanyModelFactory();
	}

	@Test
	public void testCreate_CompanyModelList_NoAccounts_ExpectEmptyList() {
		List<CompanyModel> companyModels = companyModelFactory.create(Collections.<Integer, AccountModel>emptyMap(),
				Collections.<Integer, List<CompanyEmployeeModel>>emptyMap());

		assertTrue(companyModels.isEmpty());
	}

	@Test
	public void testCreate_CompanyModelList() {
		Map<Integer, AccountModel> fakeAccountModelMap = buildFakeAccountModelMap();
		Map<Integer, List<CompanyEmployeeModel>> fakeCompanyEmployeeModelMap = buildFakeCompanyEmployeeModelMap();

		List<CompanyModel> companyModels = companyModelFactory.create(fakeAccountModelMap, fakeCompanyEmployeeModelMap);

		verifyTestCreate_CompanyModelList(fakeAccountModelMap, fakeCompanyEmployeeModelMap, companyModels);
	}

	private void verifyTestCreate_CompanyModelList(final Map<Integer, AccountModel> fakeAccountModelMap,
												   final Map<Integer, List<CompanyEmployeeModel>> fakeCompanyEmployeeModelMap,
												   final List<CompanyModel> companyModels) {
		assertNotNull(companyModels);
		assertEquals(2, companyModels.size());

		int companyId = 1;
		for (CompanyModel companyModel : companyModels) {
			assertEquals(fakeAccountModelMap.get(companyId).getName(), companyModel.getName());
			assertEquals(fakeCompanyEmployeeModelMap.get(companyId).get(0).getFirstName(), companyModel.getEmployees()
					.get(0).getFirstName());
			companyId++;
		}
	}

	@Test
	public void testCreate_FromAccountModelMap_NoAccountModels() {
		List<CompanyModel> companyModels = companyModelFactory.create(Collections.<Integer, AccountModel>emptyMap());

		assertTrue(companyModels.isEmpty());
	}

	@Test
	public void testCreate_FromAccountModelMap() {
		Map<Integer, AccountModel> fakeAccountModelMap = buildFakeAccountModelMap();

		List<CompanyModel> companyModels = companyModelFactory.create(fakeAccountModelMap);

		verifyTestCreate_FromAccountModelMap(fakeAccountModelMap, companyModels);
	}

	private void verifyTestCreate_FromAccountModelMap(final Map<Integer, AccountModel> fakeAccountModelMap,
													  final List<CompanyModel> companyModels) {
		assertNotNull(companyModels);
		assertEquals(2, companyModels.size());

		int companyId = 1;
		for (CompanyModel companyModel : companyModels) {
			assertEquals(fakeAccountModelMap.get(companyId).getName(), companyModel.getName());
			companyId++;
		}
	}

	@Test
	public void testCreate_FromAccountModelCollection_EmptyCollection() {
		List<CompanyModel> companyModels = companyModelFactory.create(Collections.<AccountModel>emptyList());

		assertTrue(companyModels.isEmpty());
	}

	@Test
	public void testCreate_FromAccountModelCollection() {
		Collection<AccountModel> accountModels = buildFakeAccountModels();

		List<CompanyModel> companyModels = companyModelFactory.create(accountModels);

		verifyTestCreate_FromAccountModelCollection(companyModels);
	}

	private void verifyTestCreate_FromAccountModelCollection(final List<CompanyModel> companyModels) {
		assertEquals(2, companyModels.size());

		for (CompanyModel companyModel : companyModels) {
			if (companyModel.getId() == 1) {
				verifyCompanyModel(1, "Test Company 1", companyModel);
			}

			if (companyModel.getId() == 2) {
				verifyCompanyModel(2, "Test Company 2", companyModel);
			}
		}
	}

	private void verifyCompanyModel(final int expectedId, final String expectedName, final CompanyModel companyModel) {
		assertEquals(expectedId, companyModel.getId());
		assertEquals(expectedName, companyModel.getName());
	}

	private Collection<AccountModel> buildFakeAccountModels() {
		return buildFakeAccountModelMap().values();
	}

	private Map<Integer, AccountModel> buildFakeAccountModelMap() {
		return new HashMap<Integer, AccountModel>() {{
			put(1, new AccountModel.Builder().id(1).name("Test Company 1").build());
			put(2, new AccountModel.Builder().id(2).name("Test Company 2").build());
		}};
	}

	private Map<Integer, List<CompanyEmployeeModel>> buildFakeCompanyEmployeeModelMap() {
		return new HashMap<Integer, List<CompanyEmployeeModel>>() {{
			put(1, Arrays.asList(buildFakeCompanyEmployeeModel(4, "Bob", "Boulder")));
			put(2, Arrays.asList(buildFakeCompanyEmployeeModel(5, "Jack", "Johnson")));
		}};
	}

	private CompanyEmployeeModel buildFakeCompanyEmployeeModel(final int id,
															   final String firstName,
															   final String lastName) {
		CompanyEmployeeModel companyEmployeeModel = new CompanyEmployeeModel();
		companyEmployeeModel.setId(id);
		companyEmployeeModel.setFirstName(firstName);
		companyEmployeeModel.setLastName(lastName);
		return companyEmployeeModel;
	}

	@Test
	public void testCreate() {
		AccountModel fakeAccountModel = buildFakeAccountModel();
		List<CompanyEmployeeModel> fakeEmployees = new ArrayList<>();

		CompanyModel companyModel = companyModelFactory.create(fakeAccountModel, fakeEmployees);

		verifyTestCreate(fakeEmployees, companyModel);
	}

	private void verifyTestCreate(final List<CompanyEmployeeModel> fakeEmployees,
								  final CompanyModel companyModel) {
		assertEquals(COMPANY_ID, companyModel.getId());
		assertEquals(COMPANY_NAME, companyModel.getName());
		assertEquals(fakeEmployees, companyModel.getEmployees());
	}

	private AccountModel buildFakeAccountModel() {
		return new AccountModel.Builder()
				.id(COMPANY_ID)
				.name(COMPANY_NAME)
				.build();
	}
}