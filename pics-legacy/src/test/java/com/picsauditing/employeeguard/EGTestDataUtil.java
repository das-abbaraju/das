package com.picsauditing.employeeguard;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.builders.UserBuilder;
import org.joda.time.DateTime;

import java.util.*;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.ACCOUNT_ID;
import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.ENTITY_ID;
import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.buildFakeAccountSkill;

public class EGTestDataUtil {

	private static int sequencer = 1;
	public static final int CORPORATE_ID = sequencer++;
	public static final int SITE_ID = sequencer++;
	public static final int CONTRACTOR_ID = sequencer++;
	public static final int EMPLOYEE_ID = sequencer++;
	public static final int PROFILE_ID = sequencer++;
	public static final int APP_USER_ID = sequencer++;

	public static final String INVALID_EMAIL_HASH_STRING = "invalid hash";
	public static final EmailHash INVALID_EMAIL_HASH = new EmailHashBuilder()
			.createdDate(DateBean.today())
			.expirationDate(DateBean.addDays(DateBean.today(), -4))
			.hashCode(INVALID_EMAIL_HASH_STRING)
			.softDeletedEmployee(new SoftDeletedEmployeeBuilder()
					.accountId(123)
					.firstName("Bob")
					.lastName("Bad")
					.email("bob.bad@test.com")
					.build())
			.build();

	public static final String VALID_EMAIL_HASH_STRING = "valid hash";
	public static final EmailHash VALID_EMAIL_HASH = new EmailHashBuilder()
			.createdDate(DateBean.today())
			.expirationDate(DateBean.addDays(DateBean.today(), 4))
			.hashCode(VALID_EMAIL_HASH_STRING)
			.softDeletedEmployee(new SoftDeletedEmployeeBuilder()
					.accountId(124)
					.firstName("Bob")
					.lastName("Good")
					.email("bob.good@test.com")
					.build())
			.build();

	public static final String EXISTING_PROFILE_EMAIL_HASH_STRING = "existing profile hash";
	public static final EmailHash EXISTING_PROFILE_EMAIL_HASH = new EmailHashBuilder()
			.createdDate(DateBean.today())
			.expirationDate(DateBean.addDays(DateBean.today(), 35))
			.hashCode(EXISTING_PROFILE_EMAIL_HASH_STRING)
			.softDeletedEmployee(new SoftDeletedEmployeeBuilder()
					.accountId(125)
					.firstName("Bob")
					.lastName("Existing")
					.email("bob.existing@test.com")
					.profile(new Profile())
					.build())
			.build();

	public static final String NO_EMPLOYEE_EMAIL_HASH_STRING = "no employee hash";
	public static final EmailHash NO_EMPLOYEE_EMAIL_HASH = new EmailHashBuilder()
			.createdDate(DateBean.today())
			.expirationDate(DateBean.addDays(DateBean.today(), 35))
			.hashCode(NO_EMPLOYEE_EMAIL_HASH_STRING)
			.build();

	public static final List<Integer> CORPORATE_ACCOUNT_IDS = Arrays.asList(CORPORATE_ID);

	// Project Mock Data
	public static final Project PROJECT_NO_SKILLS_NO_ROLES = new ProjectBuilder().accountId(SITE_ID).name("Test Project No Skills No Roles").build();
	public static final Project PROJECT_WITH_SKILLS = new ProjectBuilder().accountId(SITE_ID).name("Test Project Has Skills").build();
	public static final Project PROJECT_NO_SKILLS = new ProjectBuilder().accountId(SITE_ID).name("Test Project No Skills").build();

	// Set of Mock Projects
	public static final Set<Project> FAKE_PROJECT_SET = new HashSet<>(Arrays.asList(PROJECT_NO_SKILLS,
			PROJECT_WITH_SKILLS, PROJECT_NO_SKILLS_NO_ROLES));

	// Skill Mock Data
	public static final AccountSkill SITE_REQUIRE_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("SITE_REQUIRE_SKILL 1").build();
	public static final AccountSkill CORPORATE_REQUIRED_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("CORPORATE_REQUIRED_SKILL 1").build();
	public static final AccountSkill SKILL_FOR_ROLE_WITH_SKILLS = new AccountSkillBuilder(CORPORATE_ID).name("SKILL_FOR_ROLE_WITH_SKILLS").build();
	public static final AccountSkill SITE_ASSIGNMENT_ROLE_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("SITE_ASSIGNMENT_ROLE_SKILL").build();
	public static final AccountSkill PROJECT_REQUIRED_SKILL_2 = new AccountSkillBuilder(CORPORATE_ID).name("PROJECT_REQUIRED_SKILL_2").build();
	public static final AccountSkill PROJECT_REQUIRED_SKILL_1 = new AccountSkillBuilder(CORPORATE_ID).name("PROJECT_REQUIRED_SKILL_1").build();
	public static final AccountSkill GROUP_SKILL = new AccountSkillBuilder(CONTRACTOR_ID).name("CONTRACTOR_GROUP_SKILL").build();

	public static final Map<Project, Set<AccountSkill>> PROJECT_REQUIRED_SKILLS_MAP =
			new HashMap<Project, Set<AccountSkill>>() {{

				put(PROJECT_WITH_SKILLS,
						new HashSet<>(Arrays.asList(PROJECT_REQUIRED_SKILL_1, PROJECT_REQUIRED_SKILL_2)));

			}};

	// Role Mock Data
	public static final Role SITE_ASSIGNMENT_ROLE = new RoleBuilder().accountId(CORPORATE_ID).name("SITE_ASSIGNMENT_ROLE").build();
	public static final Role ROLE_WITH_SKILLS = new RoleBuilder().accountId(CORPORATE_ID).name("ROLE_WITH_SKILLS").build();
	public static final Role ROLE_NO_SKILLS = new RoleBuilder().accountId(CORPORATE_ID).name("ROLE_NO_SKILLS").build();

	// Group Mock Data
	public static final Group CONTRACTOR_GROUP = new GroupBuilder().accountId(CONTRACTOR_ID).name("CONTRACTOR_GROUP").build();

	// ProfileDocument Mock Data

	public static final Date DOCUMENT_1_CREATED_DATE = new Date(1391198400000l);
	public static final Date DOCUMENT_1_END_DATE = new Date(64060703999000l);
	public static final ProfileDocument PROFILE_DOCUMENT_1 = new ProfileDocumentBuilder()
			.id(1)
			.name("Document 1")
			.createdDate(DOCUMENT_1_CREATED_DATE)
			.startDate(DOCUMENT_1_CREATED_DATE)
			.endDate(DOCUMENT_1_END_DATE)
			.build();

	public static final Date DOCUMENT_2_CREATED_DATE = new Date(1359662400000l);
	public static final Date DOCUMENT_2_END_DATE = new Date(1425283200000l);
	public static final ProfileDocument PROFILE_DOCUMENT_2 = new ProfileDocumentBuilder()
			.id(2)
			.name("Document 2")
			.createdDate(DOCUMENT_2_CREATED_DATE)
			.endDate(DOCUMENT_2_CREATED_DATE)
			.endDate(DOCUMENT_2_END_DATE)
			.build();

	public static final Map<Project, Set<Role>> PROJECT_ROLES_MAP = new HashMap<Project, Set<Role>>() {{

		put(PROJECT_WITH_SKILLS, new HashSet<>(Arrays.asList(ROLE_WITH_SKILLS, ROLE_NO_SKILLS)));

		put(PROJECT_NO_SKILLS, new HashSet<>(Arrays.asList(ROLE_NO_SKILLS)));

	}};

	public static final Map<Role, Set<AccountSkill>> ROLE_SKILLS_MAP = new HashMap<Role, Set<AccountSkill>>() {{

		put(ROLE_WITH_SKILLS, new HashSet<>(Arrays.asList(SKILL_FOR_ROLE_WITH_SKILLS)));

	}};

	public static final Map<Group, Set<AccountSkill>> GROUP_SKILLS_MAP = new HashMap<Group, Set<AccountSkill>>() {{

		put(CONTRACTOR_GROUP, new HashSet<>(Arrays.asList(GROUP_SKILL)));

	}};

	public AccountModel buildFakeContractorAccountModel() {
		return new AccountModel.Builder()
				.id(CONTRACTOR_ID)
				.accountType(AccountType.CONTRACTOR)
				.build();
	}

	public AccountModel buildFakeCorporateAccountModel() {
		return new AccountModel.Builder()
						.id(CORPORATE_ID)
						.accountType(AccountType.CORPORATE)
						.build();
	}

	public AccountModel buildFakeSiteAccountModel() {
		return new AccountModel.Builder()
						.id(SITE_ID)
						.accountType(AccountType.OPERATOR)
						.build();
	}

	public ProfileDocument buildNewFakeProfileDocument() {
		return new ProfileDocumentBuilder()
				.id(sequencer++)
				.build();
	}


	public List<Employee> buildNewFakeEmployees() {
		return Arrays.asList(
						new EmployeeBuilder(sequencer++, CONTRACTOR_ID).email("bob@test.com").profile(buildFakeProfile()).build(),
						new EmployeeBuilder(sequencer++, CONTRACTOR_ID).email("joe@test.com").profile(buildFakeProfile()).build(),
						new EmployeeBuilder(sequencer++, CONTRACTOR_ID).email("jill@test.com").profile(buildFakeProfile()).build()
		);
	}

	public List<AccountSkillProfile> buildFakeAccountSkillProfiles_Completed(final List<Employee> employees,
																			 final List<AccountSkill> skills) {
		return Arrays.asList(
				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(0))
						.profile(employees.get(0).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 45))
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(1))
						.profile(employees.get(1).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 50))
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(2))
						.profile(employees.get(1).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 40))
						.build()
		);
	}

	public List<AccountSkillProfile> buildFakeAccountSkillProfiles_Expired(final List<Employee> employees,
																		   final List<AccountSkill> skills) {
		return Arrays.asList(
				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(0))
						.profile(employees.get(0).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), -45))
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(1))
						.profile(employees.get(1).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), -50))
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(2))
						.profile(employees.get(1).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), -40))
						.build()
		);
	}

	public List<AccountSkillProfile> buildFakeAccountSkillProfiles_Expiring(final List<Employee> employees,
																			final List<AccountSkill> skills) {
		return Arrays.asList(
				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(0))
						.profile(employees.get(0).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 1))
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(1))
						.profile(employees.get(1).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 1))
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(2))
						.profile(employees.get(1).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 1))
						.build()
		);
	}

	public List<AccountSkillProfile> buildFakeAccountSkillProfiles_MixedBag(final List<Employee> employees,
																			final List<AccountSkill> skills) {
		return Arrays.asList(
				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(0))
						.profile(employees.get(0).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 45))
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(1))
						.profile(employees.get(1).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), -50))
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(2))
						.profile(employees.get(1).getProfile())
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 1))
						.build()
		);
	}


	public List<AccountSkillProfile> buildFakeAccountSkillProfiles_MixedBag(final Employee employee) {

		List<AccountSkill> skills = Arrays.asList(
						new AccountSkillBuilder(sequencer++, CORPORATE_ID)
										.accountId(CORPORATE_ID)
										.skillType(SkillType.Training)
										.intervalPeriod(1)
										.intervalType(IntervalType.DAY)
										.name("Skill 1 Expires in one day")
										.build(),
						new AccountSkillBuilder(sequencer++, CORPORATE_ID)
										.accountId(CORPORATE_ID)
										.skillType(SkillType.Certification)
										.name("Skill 2 - Certification")
										.build(),
						new AccountSkillBuilder(sequencer++, CORPORATE_ID)
										.accountId(CORPORATE_ID)
										.skillType(SkillType.Training)
										.doesNotExpire(true)
										.name("Skill 3 - Doesnt expire")
										.build()
		);

		return Arrays.asList(
						new AccountSkillProfileBuilder()
										.accountSkill(skills.get(0))
										.profile(employee.getProfile())
										.startDate(DateBean.today())
										.endDate(DateBean.addDays(DateBean.today(), 45))
										.build(),

						new AccountSkillProfileBuilder()
										.accountSkill(skills.get(1))
										.profile(employee.getProfile())
										.startDate(DateBean.today())
										.endDate(DateBean.addDays(DateBean.today(), -50))
										.build(),

						new AccountSkillProfileBuilder()
										.accountSkill(skills.get(2))
										.profile(employee.getProfile())
										.startDate(DateBean.today())
										.endDate(DateBean.addDays(DateBean.today(), 1))
										.build()
		);
	}

	public List<ProjectRoleEmployee> buildFakeProjectRoleEmployees() {
		return Arrays.asList(
						new ProjectRoleEmployeeBuilder()
										.projectRole(new ProjectRoleBuilder()
														.project(new ProjectBuilder().accountId(SITE_ID).name("Test Project").build())
														.role(new RoleBuilder().accountId(CORPORATE_ID).name("Test Role").build())
														.build())
										.employee(buildNewFakeEmployee())
										.build()
		);
	}

	public List<ProjectRoleEmployee> buildFakeProjectRoleEmployees(Employee employee, AccountSkill skill) {
		return Arrays.asList(
						new ProjectRoleEmployeeBuilder()
										.projectRole(new ProjectRoleBuilder()
														.project(new ProjectBuilder().accountId(SITE_ID).name("Test Project").build())
														.role(new RoleBuilder().accountId(CORPORATE_ID).name("Test Role").skills(Arrays.asList(skill)).build())
														.build())
										.employee(employee)
										.build()
		);
	}

	public List<ProjectRoleEmployee> buildFakeProjectRoleEmployees(Employee employee) {
		ProjectRoleEmployee pre = 						new ProjectRoleEmployeeBuilder()
						.projectRole(new ProjectRoleBuilder()
										.project(new ProjectBuilder().accountId(SITE_ID).name("Test Project").build())
										.role(new RoleBuilder().accountId(CORPORATE_ID).name("Test Role").build())
										.build())
						.employee(employee)
						.build();

		pre.getProjectRole().setEmployees(Arrays.asList(pre));

		return Arrays.asList(pre);
	}

	public List<ProjectSkill> buildFakeProjectSkills(final Project fakeProject) {
		return new ArrayList<ProjectSkill>() {{
			add(new ProjectSkillBuilder()
							.project(fakeProject)
							.skill(buildNewFakeSkill())
							.build());

		}};
	}

	public ProjectSkill buildFakeProjectSkill(final Project fakeProject) {
		return new ProjectSkillBuilder()
							.project(fakeProject)
							.skill(buildNewFakeSkill())
							.build();
	}

	public Set<Project> buildFakeProjects() {
		return new HashSet<Project>() {{
			add(PROJECT_NO_SKILLS);

			add(PROJECT_WITH_SKILLS);

			add(PROJECT_NO_SKILLS_NO_ROLES);
		}};
	}

	public Map<Project, Set<Role>> buildFakeProjectRoles() {
		return new HashMap<Project, Set<Role>>() {{

			put(PROJECT_NO_SKILLS,
					new HashSet<>(Arrays.asList(ROLE_NO_SKILLS)));

			put(PROJECT_WITH_SKILLS,
					new HashSet<>(Arrays.asList(ROLE_NO_SKILLS, ROLE_WITH_SKILLS)));

		}};
	}

	public Set<Role> buildFakeSiteAssignmentRoles() {
		return new HashSet<Role>() {{

			add(SITE_ASSIGNMENT_ROLE);

		}};
	}

	public Set<AccountSkill> buildFakeSiteAndCorporateSkills() {
		return new HashSet<AccountSkill>() {{

			add(CORPORATE_REQUIRED_SKILL);

			add(SITE_REQUIRE_SKILL);

		}};
	}

	public Set<AccountSkill> buildFakeCorporateReqdSkills() {
		return new HashSet<AccountSkill>() {{
			add(CORPORATE_REQUIRED_SKILL);
		}};
	}

	public Set<AccountSkill> buildFakeSiteReqdSkills() {
		return new HashSet<AccountSkill>() {{
			add(SITE_REQUIRE_SKILL);
		}};
	}

	public List<AccountSkill> buildFakeCorporateReqdSkillsList() {
		return Arrays.asList(CORPORATE_REQUIRED_SKILL);
	}

	public List<AccountSkill> buildFakeSiteReqdSkillsList() {
		return Arrays.asList(SITE_REQUIRE_SKILL);
	}

	public Map<Project, Set<AccountSkill>> buildFakeProjectRequiredSkills() {
		return new HashMap<Project, Set<AccountSkill>>() {{

			put(PROJECT_WITH_SKILLS, new HashSet<>(Arrays.asList(PROJECT_REQUIRED_SKILL_1, PROJECT_REQUIRED_SKILL_2)));

		}};
	}

	public Map<Role, Set<AccountSkill>> buildFakeRoleSkills() {
		return new HashMap<Role, Set<AccountSkill>>() {{

			put(SITE_ASSIGNMENT_ROLE, new HashSet<>(Arrays.asList(SITE_ASSIGNMENT_ROLE_SKILL)));

			put(ROLE_WITH_SKILLS, new HashSet<>(Arrays.asList(SKILL_FOR_ROLE_WITH_SKILLS)));

		}};
	}

	public Map<AccountSkill, SkillStatus> buildFakeSkillStatusMap() {
		return new HashMap<AccountSkill, SkillStatus>() {{

			// Project Required Skill Statuses
			put(PROJECT_REQUIRED_SKILL_1, SkillStatus.Completed);
			put(PROJECT_REQUIRED_SKILL_2, SkillStatus.Expiring);

			put(SKILL_FOR_ROLE_WITH_SKILLS, SkillStatus.Expired);
			put(SITE_ASSIGNMENT_ROLE_SKILL, SkillStatus.Expiring);

			put(CORPORATE_REQUIRED_SKILL, SkillStatus.Completed);
			put(SITE_REQUIRE_SKILL, SkillStatus.Completed);
		}};
	}

	public Profile buildFakeProfile(){
		return new ProfileBuilder()
						.id(PROFILE_ID)
						.build();

	}

	public Profile buildFakeProfileWithSettings(){
		Settings settings = new Settings();
		settings.setLocale(Locale.UK);
		return new ProfileBuilder()
						.id(PROFILE_ID)
						.appUserId(APP_USER_ID)
						.firstName("First Name")
						.lastName("Last Name")
						.email("Email Id")
						.phone("1234567988")
						.settings(settings)
						.build();

	}

	public User buildFakeUserWithSettings() {
		return new UserBuilder()
				.id(PROFILE_ID)
				.firstName("First Name")
				.lastName("Last Name")
				.email("Email Id")
				.phone("1234567988")
				.locale(Locale.UK)
				.build();

	}

	public Employee buildNewFakeEmployee() {
		int newId = sequencer++;
		return new EmployeeBuilder()
				.id(newId)
				.accountId(CONTRACTOR_ID)
				.firstName("Bob-Id-" + newId)
				.lastName("Smith")
				.positionName("Master Welder-" + newId)
				.profile(buildFakeProfile())
				.build();
	}

	public Project buildNewFakeProject() {
		int newId = sequencer++;
		return new ProjectBuilder()
				.id(newId)
				.accountId(SITE_ID)
				.name("Project-Id-" + newId)
				.build();
	}

	public Role buildNewFakeRole() {
		int newId = sequencer++;
		return new RoleBuilder()
				.accountId(CORPORATE_ID)
				.name("Role-Id-" + newId)
				.build();
	}

	public Group buildNewFakeGroup() {
		int newId = sequencer++;
		return new GroupBuilder()
						.accountId(CONTRACTOR_ID)
						.name("GROUP-Id-" + newId)
						.build();
	}

	public List<Role> buildNewFakeRoles() {
		return Arrays.asList(
				buildNewFakeRole(),
				buildNewFakeRole(),
				buildNewFakeRole());
	}

	public AccountSkill buildNewFakeSkill(){
		return new AccountSkillBuilder(sequencer++, CORPORATE_ID)
						.accountId(CORPORATE_ID)
						.skillType(SkillType.Training)
						.intervalPeriod(1)
						.intervalType(IntervalType.DAY)
						.name("Skill 1 Expires in one day")
						.build();
	}

	public List<AccountSkill> buildNewFakeCertificationSkills() {
		return Arrays.asList(
				new AccountSkillBuilder(sequencer++, CORPORATE_ID)
						.accountId(CORPORATE_ID)
						.skillType(SkillType.Certification)
						.name("Skill 1")
						.build(),
				new AccountSkillBuilder(sequencer++, CORPORATE_ID)
						.accountId(CORPORATE_ID)
						.skillType(SkillType.Certification)
						.name("Skill 2")
						.build(),
				new AccountSkillBuilder(sequencer++, CORPORATE_ID)
						.accountId(CORPORATE_ID)
						.skillType(SkillType.Certification)
						.name("Skill 3")
						.build()
		);
	}

	public List<AccountSkill> buildNewFakeTrainingSkillsExpiringInOneDay() {
		return Arrays.asList(
				new AccountSkillBuilder(sequencer++, CORPORATE_ID)
						.accountId(CORPORATE_ID)
						.skillType(SkillType.Training)
						.intervalPeriod(1)
						.intervalType(IntervalType.DAY)
						.name("Skill 1 Expires in one day")
						.build(),
				new AccountSkillBuilder(sequencer++, CORPORATE_ID)
						.accountId(CORPORATE_ID)
						.skillType(SkillType.Training)
						.intervalPeriod(1)
						.intervalType(IntervalType.DAY)
						.name("Skill 2 Expires in one day")
						.build(),
				new AccountSkillBuilder(sequencer++, CORPORATE_ID)
						.accountId(CORPORATE_ID)
						.skillType(SkillType.Training)
						.intervalPeriod(1)
						.intervalType(IntervalType.DAY)
						.name("Skill 3 Expires in one day")
						.build()
		);
	}

	public List<AccountSkill> buildNewFakeSkillsMixedBag() {
		return Arrays.asList(
						new AccountSkillBuilder(sequencer++, CORPORATE_ID)
										.accountId(CORPORATE_ID)
										.skillType(SkillType.Training)
										.intervalPeriod(1)
										.intervalType(IntervalType.DAY)
										.name("Skill 1 Expires in one day")
										.build(),
						new AccountSkillBuilder(sequencer++, CORPORATE_ID)
										.accountId(CORPORATE_ID)
										.skillType(SkillType.Certification)
										.name("Skill 2 - Certification")
										.build(),
						new AccountSkillBuilder(sequencer++, CORPORATE_ID)
										.accountId(CORPORATE_ID)
										.skillType(SkillType.Training)
										.doesNotExpire(true)
										.name("Skill 3 - Doesnt expire")
										.build()
		);
	}

	public List<AccountSkill> buildNewFakeContractorSkillsMixedBag() {
		return Arrays.asList(
						new AccountSkillBuilder(sequencer++, CONTRACTOR_ID)
										.accountId(CONTRACTOR_ID)
										.skillType(SkillType.Training)
										.intervalPeriod(1)
										.intervalType(IntervalType.DAY)
										.name("Skill 1 Expires in one day")
										.build(),
						new AccountSkillBuilder(sequencer++, CONTRACTOR_ID)
										.accountId(CONTRACTOR_ID)
										.skillType(SkillType.Certification)
										.name("Skill 2 - Certification")
										.build(),
						new AccountSkillBuilder(sequencer++, CONTRACTOR_ID)
										.accountId(CONTRACTOR_ID)
										.skillType(SkillType.Training)
										.doesNotExpire(true)
										.name("Skill 3 - Doesnt expire")
										.build()
		);
	}


	public Map<Integer,AccountSkill>  buildNewFakeSkillsMixedBagMap() {
		List<AccountSkill> skills = buildNewFakeSkillsMixedBag();

		Map<Integer,AccountSkill> map = PicsCollectionUtil.convertToMap(skills,

						new PicsCollectionUtil.MapConvertable<Integer, AccountSkill>() {

							@Override
							public Integer getKey(AccountSkill skill) {
								return skill.getId();
							}
						}
		);

		return map;
	}

	public Map<Integer,AccountSkill>  buildNewFakeContractorSkillsMixedBagMap() {
		List<AccountSkill> skills = buildNewFakeContractorSkillsMixedBag();

		Map<Integer,AccountSkill> map = PicsCollectionUtil.convertToMap(skills,

						new PicsCollectionUtil.MapConvertable<Integer, AccountSkill>() {

							@Override
							public Integer getKey(AccountSkill skill) {
								return skill.getId();
							}
						}
		);

		return map;
	}

	public Map<AccountModel, Set<Employee>> buildNewContractorEmployeeMap() {
		List<AccountModel> contractors = Arrays.asList(buildFakeContractorAccountModel());

		Map<AccountModel, Set<Employee>> contractorEmployeeMap = new HashMap<>();
		for (AccountModel accountModel : contractors) {
			if (!contractorEmployeeMap.containsKey(accountModel)) {
				contractorEmployeeMap.put(accountModel, new HashSet<Employee>());
			}

			contractorEmployeeMap.get(accountModel).addAll(buildNewFakeEmployees());
		}

		return contractorEmployeeMap;
	}

	public List<ProjectCompany> getFakeProjectCompanies() {
		return Arrays.asList(
				new ProjectCompanyBuilder()
						.project(
								new ProjectBuilder()
										.accountId(SITE_ID)
										.build())
						.build(),
				new ProjectCompanyBuilder()
						.project(
								new ProjectBuilder()
										.accountId(SITE_ID)
										.build()
						)
						.build());
	}

	public AccountSkillProfile prepareExpiredAccountSkillProfile() {
		DateTime skillDocSaveDate = (new DateTime().minusDays(35));
		AccountSkill skill = this.buildNewFakeTrainingSkill();
		skill.setIntervalType(IntervalType.MONTH);

		DateTime oneYrFromNowDate = new DateTime().plusDays(365);
		AccountSkillProfile accountSkillProfile = new AccountSkillProfileBuilder()
				.startDate(skillDocSaveDate.toDate())
				.endDate(oneYrFromNowDate.toDate()) // End date is not used.  Its intentionally populated to make sure its not
				.build();

		accountSkillProfile.setSkill(skill);

		return accountSkillProfile;
	}

	public AccountSkillProfile prepareExpiringAccountSkillProfile() {
		DateTime skillDocSaveDate = (new DateTime().minusDays(2));
		AccountSkill skill = this.buildNewFakeTrainingSkill();
		skill.setIntervalType(IntervalType.WEEK);

		DateTime oneYrFromNowDate = new DateTime().plusDays(365);
		AccountSkillProfile accountSkillProfile = new AccountSkillProfileBuilder()
				.startDate(skillDocSaveDate.toDate())
				.endDate(oneYrFromNowDate.toDate()) // End date is not used.  Its intentionally populated to make sure its not
				.build();

		accountSkillProfile.setSkill(skill);

		return accountSkillProfile;
	}

	public AccountSkillProfile prepareCompletedAccountSkillProfile() {
		DateTime skillDocSaveDate = (new DateTime().minusDays(3));
		AccountSkill skill = this.buildNewFakeTrainingSkill();
		skill.setIntervalType(IntervalType.YEAR);

		AccountSkillProfile accountSkillProfile = new AccountSkillProfileBuilder()
				.startDate(skillDocSaveDate.toDate())
				.endDate((new DateTime().plusDays(5).toDate())) // End date is not used.  Its intentionally populated to make sure its not
				.build();

		accountSkillProfile.setSkill(skill);

		return accountSkillProfile;
	}

	public AccountSkill buildNewFakeTrainingSkill() {
		int id = sequencer++;
		return new AccountSkillBuilder(id, CORPORATE_ID)
				.accountId(CORPORATE_ID)
				.skillType(SkillType.Training)
				.intervalPeriod(1)
				.intervalType(IntervalType.DAY)
				.name("Training Skill " + id)
				.build();
	}

	public AccountSkill buildNewFakeCertificationSkill() {
		return new AccountSkillBuilder(sequencer++, CORPORATE_ID)
				.accountId(CORPORATE_ID)
				.skillType(SkillType.Certification)
				.name("Certification Skill 1")
				.build();
	}

	public static int getCorporateId() {
		return CORPORATE_ID;
	}

	public static int getSiteId() {
		return SITE_ID;
	}

	public static int getContractorId() {
		return CONTRACTOR_ID;
	}

	public static int getEmployeeId() {
		return EMPLOYEE_ID;
	}

	public static List<Integer> getCorporateAccountIds() {
		return CORPORATE_ACCOUNT_IDS;
	}

	public static Project getProjectNoSkillsNoRoles() {
		return PROJECT_NO_SKILLS_NO_ROLES;
	}

	public static Project getProjectWithSkills() {
		return PROJECT_WITH_SKILLS;
	}

	public static Project getProjectNoSkills() {
		return PROJECT_NO_SKILLS;
	}

	public static AccountSkill getSiteRequireSkill() {
		return SITE_REQUIRE_SKILL;
	}

	public static AccountSkill getCorporateRequiredSkill() {
		return CORPORATE_REQUIRED_SKILL;
	}

	public static AccountSkill getSkillForRoleWithSkills() {
		return SKILL_FOR_ROLE_WITH_SKILLS;
	}

	public static AccountSkill getSiteAssignmentRoleSkill() {
		return SITE_ASSIGNMENT_ROLE_SKILL;
	}

	public static AccountSkill getProjectRequiredSkill2() {
		return PROJECT_REQUIRED_SKILL_2;
	}

	public static AccountSkill getProjectRequiredSkill1() {
		return PROJECT_REQUIRED_SKILL_1;
	}

	public static Role getSiteAssignmentRole() {
		return SITE_ASSIGNMENT_ROLE;
	}

	public static Role getRoleWithSkills() {
		return ROLE_WITH_SKILLS;
	}

	public static Role getRoleNoSkills() {
		return ROLE_NO_SKILLS;
	}
}
