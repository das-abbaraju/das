package com.picsauditing.employeeguard;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.*;

public class EGTestDataUtil {
  private static int sequencer=1;
  private static final int CORPORATE_ID = sequencer++;
  private static final int SITE_ID = sequencer++;
  private static final int CONTRACTOR_ID = sequencer++;
  public static final int EMPLOYEE_ID = sequencer++;

  public static final List<Integer> CORPORATE_ACCOUNT_IDS = Arrays.asList(CORPORATE_ID);

  // Project Mock Data
  public static final Project PROJECT_NO_SKILLS_NO_ROLES = new ProjectBuilder().accountId(SITE_ID).name("Test Project No Skills No Roles").build();
  public static final Project PROJECT_WITH_SKILLS = new ProjectBuilder().accountId(SITE_ID).name("Test Project Has Skills").build();
  public static final Project PROJECT_NO_SKILLS = new ProjectBuilder().accountId(SITE_ID).name("Test Project No Skills").build();

  // Skill Mock Data
  public static final AccountSkill SITE_REQUIRE_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("SITE_REQUIRE_SKILL 1").build();
  public static final AccountSkill CORPORATE_REQUIRED_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("CORPORATE_REQUIRED_SKILL 1").build();
  public static final AccountSkill SKILL_FOR_ROLE_WITH_SKILLS = new AccountSkillBuilder(CORPORATE_ID).name("SKILL_FOR_ROLE_WITH_SKILLS").build();
  public static final AccountSkill SITE_ASSIGNMENT_ROLE_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("SITE_ASSIGNMENT_ROLE_SKILL").build();
  public static final AccountSkill PROJECT_REQUIRED_SKILL_2 = new AccountSkillBuilder(CORPORATE_ID).name("PROJECT_REQUIRED_SKILL_2").build();
  public static final AccountSkill PROJECT_REQUIRED_SKILL_1 = new AccountSkillBuilder(CORPORATE_ID).name("PROJECT_REQUIRED_SKILL_1").build();

  // Role Mock Data
  public static final Role SITE_ASSIGNMENT_ROLE = new RoleBuilder().accountId(CORPORATE_ID).name("SITE_ASSIGNMENT_ROLE").build();
  public static final Role ROLE_WITH_SKILLS = new RoleBuilder().accountId(CORPORATE_ID).name("ROLE_WITH_SKILLS").build();
  public static final Role ROLE_NO_SKILLS = new RoleBuilder().accountId(CORPORATE_ID).name("ROLE_NO_SKILLS").build();


  public AccountModel buildFakeContractorAccountModel() {
    return new AccountModel.Builder()
            .id(CONTRACTOR_ID)
            .accountType(AccountType.CONTRACTOR)
            .build();
  }

  public ProfileDocument buildNewFakeProfileDocument() {
    return new ProfileDocumentBuilder()
            .id(sequencer++)
            .build();
  }


  public List<Employee> buildNewFakeEmployees() {
    return Arrays.asList(
            new EmployeeBuilder(sequencer++,CONTRACTOR_ID).email("bob@test.com").build(),
            new EmployeeBuilder(sequencer++,CONTRACTOR_ID).email("joe@test.com").build(),
            new EmployeeBuilder(sequencer++,CONTRACTOR_ID).email("jill@test.com").build()
    );
  }

  public List<AccountSkillEmployee> buildFakeAccountSkillEmployees_Completed(final List<Employee> employees,
                                                                    final List<AccountSkill> skills) {
    return Arrays.asList(
            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(0))
                    .employee(employees.get(0))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), 45))
                    .build(),

            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(1))
                    .employee(employees.get(1))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), 50))
                    .build(),

            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(2))
                    .employee(employees.get(1))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), 40))
                    .build()
    );
  }

  public List<AccountSkillEmployee> buildFakeAccountSkillEmployees_Expired(final List<Employee> employees,
                                                                             final List<AccountSkill> skills) {
    return Arrays.asList(
            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(0))
                    .employee(employees.get(0))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), -45))
                    .build(),

            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(1))
                    .employee(employees.get(1))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), -50))
                    .build(),

            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(2))
                    .employee(employees.get(1))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), -40))
                    .build()
    );
  }

  public List<AccountSkillEmployee> buildFakeAccountSkillEmployees_Expiring(final List<Employee> employees,
                                                                             final List<AccountSkill> skills) {
    return Arrays.asList(
            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(0))
                    .employee(employees.get(0))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), 1))
                    .build(),

            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(1))
                    .employee(employees.get(1))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), 1))
                    .build(),

            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(2))
                    .employee(employees.get(1))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), 1))
                    .build()
    );
  }

  public List<AccountSkillEmployee> buildFakeAccountSkillEmployees_MixedBag(final List<Employee> employees,
                                                                             final List<AccountSkill> skills) {
    return Arrays.asList(
            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(0))
                    .employee(employees.get(0))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), 45))
                    .build(),

            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(1))
                    .employee(employees.get(1))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), -50))
                    .build(),

            new AccountSkillEmployeeBuilder()
                    .accountSkill(skills.get(2))
                    .employee(employees.get(1))
                    .startDate(DateBean.today())
                    .endDate(DateBean.addDays(DateBean.today(), 1))
                    .build()
    );
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

  public Employee buildNewFakeEmployee() {
    int newId=sequencer++;
    return new EmployeeBuilder()
            .id(newId)
            .accountId(CONTRACTOR_ID)
            .firstName("Bob-Id-"+newId)
            .lastName("Smith")
            .positionName("Master Welder-"+newId)
            .build();
  }

  public Project buildNewFakeProject() {
    int newId=sequencer++;
    return new ProjectBuilder()
            .id(newId)
            .accountId(SITE_ID)
            .name("Project-Id-" + newId)
            .build();
  }

  public Role buildNewFakeRole() {
    int newId=sequencer++;
    return new RoleBuilder()
            .accountId(CORPORATE_ID)
            .name("Role-Id-"+newId)
            .build();
  }

  public List<Role> buildNewFakeRoles() {
    return Arrays.asList(
            buildNewFakeRole(),
            buildNewFakeRole(),
            buildNewFakeRole());
  }

  public List<AccountSkill> buildNewFakeCertificationSkills() {
    return Arrays.asList(
            new AccountSkillBuilder(sequencer++,CORPORATE_ID)
                    .accountId(CORPORATE_ID)
                    .skillType(SkillType.Certification)
                    .name("Skill 1")
                    .build(),
            new AccountSkillBuilder(sequencer++,CORPORATE_ID)
                    .accountId(CORPORATE_ID)
                    .skillType(SkillType.Certification)
                    .name("Skill 2")
                    .build(),
            new AccountSkillBuilder(sequencer++,CORPORATE_ID)
                    .accountId(CORPORATE_ID)
                    .skillType(SkillType.Certification)
                    .name("Skill 3")
                    .build()
    );
  }

  public List<AccountSkill> buildNewFakeTrainingSkillsExpiringInOneDay() {
    return Arrays.asList(
            new AccountSkillBuilder(sequencer++,CORPORATE_ID)
                    .accountId(CORPORATE_ID)
                    .skillType(SkillType.Training)
                    .intervalPeriod(1)
                    .intervalType(IntervalType.DAY)
                    .name("Skill 1 Expires in one day")
                    .build(),
            new AccountSkillBuilder(sequencer++,CORPORATE_ID)
                    .accountId(CORPORATE_ID)
                    .skillType(SkillType.Training)
                    .intervalPeriod(1)
                    .intervalType(IntervalType.DAY)
                    .name("Skill 2 Expires in one day")
                    .build(),
            new AccountSkillBuilder(sequencer++,CORPORATE_ID)
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
