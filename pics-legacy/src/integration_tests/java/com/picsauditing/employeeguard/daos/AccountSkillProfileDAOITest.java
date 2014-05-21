package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.*;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"AccountSkillProfileDAOITest-context.xml"})
public class AccountSkillProfileDAOITest {
	private static final int LENNY_LEONARD_PROFILE_ID =1;
	private static final int LENNY_LEONARD_EMPLOYEE_ID =31;
	private static final int ANOTHER_EMPLOYEE_ID =41;
	private static final int LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID =54578;
	private static final int LENNY_LEONARD_PROFILE_DOC_ID =9;
	private static final int SKILL_FirePreventionTraining_ID =12;
	private static final int SKILL_GeneralSafetyTraining_ID =13;
	private static final int PROJECT_ID =1;
	private static final int ROLE_ID =1;
	private static final int CORPORATE_ID =55653;
	private static final int SITE_ID =55654;
	private static final Set CONTRACTORS =new HashSet(){{add(LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID);}};
	private static final Set CORPORATES =new HashSet(){{add(CORPORATE_ID);}};

	@Autowired
	private AccountSkillProfileDAO accountSkillProfileDAO;
	@Autowired private ProfileDAO profileDAO;
	@Autowired private ProfileDocumentDAO profileDocumentDAO;
	@Autowired private EmployeeDAO employeeDAO;
	@Autowired private AccountSkillDAO accountSkillDAO;
	@Autowired private ProjectDAO projectDAO;
	@Autowired private RoleDAO roleDAO;


	@Test
	public void testFindByEmployeeAndSkills() throws Exception {
		final int EXPECTED_SIZE =2;

		Employee employee = employeeDAO.find(LENNY_LEONARD_EMPLOYEE_ID);
		List<AccountSkill> accountSkills = new ArrayList<>();
		accountSkills.add(accountSkillDAO.find(SKILL_FirePreventionTraining_ID));
		accountSkills.add(accountSkillDAO.find(SKILL_GeneralSafetyTraining_ID));
		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.findByEmployeeAndSkills(employee, accountSkills);

		assertTrue(profileDocuments.size()==EXPECTED_SIZE);
	}

	@Test
	public void testFindContractorCreatedSkillsForEmployee() throws Exception {
		final int EXPECTED_SIZE =2;

		Employee employee = employeeDAO.find(LENNY_LEONARD_EMPLOYEE_ID);

		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.findByContractorCreatedSkillsForEmployee(employee);

		assertTrue(profileDocuments.size()==EXPECTED_SIZE);
	}


	@Test
	public void testFindByProfileAndSkill() throws Exception {

		Profile profile = profileDAO.find(LENNY_LEONARD_PROFILE_ID);
		AccountSkill accountSkill = accountSkillDAO.find(SKILL_FirePreventionTraining_ID);

		AccountSkillProfile accountSkillProfile = accountSkillProfileDAO.findByProfileAndSkill(profile,accountSkill);

		assertNotNull(accountSkillProfile);
	}

	@Test
	public void testFindByProfile() throws Exception {
		final int EXPECTED_SIZE =12;

		Profile profile = profileDAO.find(LENNY_LEONARD_PROFILE_ID);
		List<AccountSkillProfile> accountSkillProfile = accountSkillProfileDAO.findByProfile(profile);

		assertTrue(accountSkillProfile.size()==EXPECTED_SIZE);
	}


	@Test
	public void testFindByProfileDocument() throws Exception {
		final int EXPECTED_SIZE =1;

		ProfileDocument profileDocument= profileDocumentDAO.find(LENNY_LEONARD_PROFILE_DOC_ID);
		List<AccountSkillProfile> accountSkillProfile = accountSkillProfileDAO.findByProfileDocument(profileDocument);

		assertTrue(accountSkillProfile.size()==EXPECTED_SIZE);
	}

	@Test
	public void testFindByEmployeeAccount() throws Exception {
		final int EXPECTED_SIZE =14;

		List<AccountSkillProfile> accountSkillProfile = accountSkillProfileDAO.findByEmployeeAccount(LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID);

		assertTrue(accountSkillProfile.size()==EXPECTED_SIZE);
	}

	@Test
	public void testFindByEmployeesAndSkills() throws Exception {
		final int EXPECTED_SIZE =2;

		List<Employee> employees = new ArrayList<>();
		employees.add(employeeDAO.find(LENNY_LEONARD_EMPLOYEE_ID));
		employees.add(employeeDAO.find(ANOTHER_EMPLOYEE_ID));

		List<AccountSkill> accountSkills = new ArrayList<>();

		accountSkills.add(accountSkillDAO.find(SKILL_FirePreventionTraining_ID));
		accountSkills.add(accountSkillDAO.find(SKILL_GeneralSafetyTraining_ID));
		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.findByEmployeesAndSkills(employees, accountSkills);

		assertTrue(profileDocuments.size()==EXPECTED_SIZE);
	}

	@Test
	public void testFindByProjectAndContractor() throws Exception {
		final int EXPECTED_SIZE =1;

		Project project = projectDAO.find(PROJECT_ID);

		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.findByProjectAndContractor(project, LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID);

		assertTrue(profileDocuments.size()==EXPECTED_SIZE);

	}

	@Test
	public void testFindDistinctByContractorAndRole() throws Exception {
		final int EXPECTED_SIZE =1;

		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.findDistinctByContractorAndRole(LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID, ROLE_ID);

		assertTrue(profileDocuments.size()==EXPECTED_SIZE);

	}

	@Test
	public void testFindDistinctByEmployeeAndCorporateIds() throws Exception {
		final int EXPECTED_SIZE =10;

		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.findDistinctByEmployeeAndCorporateIds(LENNY_LEONARD_EMPLOYEE_ID, Arrays.asList(CORPORATE_ID));

		assertTrue(profileDocuments.size()==EXPECTED_SIZE);

	}

	@Test
	public void testGetProjectRoleSkillsForContractorsAndSite() throws Exception {
		final int EXPECTED_SIZE =7;

		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.getProjectRoleSkillsForContractorsAndSite(new HashSet() {{
			add(LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID);
		}}, SITE_ID);

		assertTrue(String.format("Expected=[%d], Found=[%d]",EXPECTED_SIZE, profileDocuments.size()),
						profileDocuments.size()==EXPECTED_SIZE);

	}

	@Test
	public void testGetProjectReqdSkillsForContractorsAndSite() throws Exception {
		final int EXPECTED_SIZE =3;

		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.getProjectReqdSkillsForContractorsAndSite(new HashSet() {{
			add(LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID);
		}}, SITE_ID);

		assertTrue(String.format("Expected=[%d], Found=[%d]",EXPECTED_SIZE, profileDocuments.size()),
							profileDocuments.size()==EXPECTED_SIZE);

	}


	@Test
	public void testGetRoleSkillsForContractorsAndRoles() throws Exception {
		final int EXPECTED_SIZE =1;

		Role role = roleDAO.find(ROLE_ID);
		List<Role> roles = Arrays.asList(role);

		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.getRoleSkillsForContractorsAndRoles(CONTRACTORS, roles);

		assertTrue(String.format("Expected=[%d], Found=[%d]",EXPECTED_SIZE, profileDocuments.size()),
						profileDocuments.size()==EXPECTED_SIZE);

	}


	@Test
	public void testGetSiteReqdSkillsForContractorsAndSites() throws Exception {
		final int EXPECTED_SIZE =2;

		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.getSiteReqdSkillsForContractorsAndSites(CONTRACTORS, CORPORATES);

		assertTrue(String.format("Expected=[%d], Found=[%d]",EXPECTED_SIZE, profileDocuments.size()),
						profileDocuments.size()==EXPECTED_SIZE);

	}


	@Test
	public void testFindBySkillAndProfile() throws Exception {
		final int EXPECTED_SIZE =1;

		Profile profile = profileDAO.find(LENNY_LEONARD_PROFILE_ID);
		AccountSkill skillFirePreventionTraining = accountSkillDAO.find(SKILL_FirePreventionTraining_ID);
		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.findBySkillAndProfile(skillFirePreventionTraining, profile);

		assertTrue(String.format("Expected=[%d], Found=[%d]",EXPECTED_SIZE, profileDocuments.size()),
						profileDocuments.size()==EXPECTED_SIZE);

	}

	@Test
	public void testFindBySkillIdAndProfile() throws Exception {
		final int EXPECTED_SIZE =1;

		Profile profile = profileDAO.find(LENNY_LEONARD_PROFILE_ID);
		List<AccountSkillProfile> profileDocuments = accountSkillProfileDAO.findBySkillIdAndProfile(SKILL_FirePreventionTraining_ID, profile);

		assertTrue(String.format("Expected=[%d], Found=[%d]",EXPECTED_SIZE, profileDocuments.size()),
						profileDocuments.size()==EXPECTED_SIZE);

	}

}