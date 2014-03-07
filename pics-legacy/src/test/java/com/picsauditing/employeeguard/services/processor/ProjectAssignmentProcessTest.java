package com.picsauditing.employeeguard.services.processor;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ProjectAssignmentProcessTest {

	public static final int SITE_ID = 671;
	public static final int CONTRACTOR_ID = 1234;
	public static final int CORPORATE_ID = 89891;

	private static final List<Project> FAKE_PROJECTS = Collections.unmodifiableList(Arrays.asList(
			new ProjectBuilder().accountId(SITE_ID).name("Project 1").build(),
			new ProjectBuilder().accountId(SITE_ID).name("Project 2").build()));

	private static final List<Employee> FAKE_EMPLOYEES = Collections.unmodifiableList(Arrays.asList(
			new EmployeeBuilder().accountId(CONTRACTOR_ID).email("first.employee@test.com").build(),
			new EmployeeBuilder().accountId(CONTRACTOR_ID).email("second.employee@test.com").build()
	));

	private static final List<Role> FAKE_ROLES = Collections.unmodifiableList(Arrays.asList(
			new RoleBuilder().accountId(SITE_ID).name("Role 1").build(),
			new RoleBuilder().accountId(SITE_ID).name("Role 2").build(),
			new RoleBuilder().accountId(SITE_ID).name("No Skills").build()
	));

	private static final Map<String, AccountSkill> FAKE_SKILLS = Collections.unmodifiableMap(new HashMap<String, AccountSkill>() {{
		put("Project 1 Welder", new AccountSkillBuilder().accountId(SITE_ID).name("Project 1 Welder").build());
		put("Project 1 Driller", new AccountSkillBuilder().accountId(SITE_ID).name("Project 1 Driller").build());
		put("Site Skill", new AccountSkillBuilder().accountId(SITE_ID).name("Site Skill").build());
		put("Corporate Skill", new AccountSkillBuilder().accountId(CORPORATE_ID).name("Corporate Skill").build());
		put("Role 1 Manager", new AccountSkillBuilder().accountId(SITE_ID).name("Role 1 Manager").build());
		put("Role 1 Architect", new AccountSkillBuilder().accountId(SITE_ID).name("Role 1 Architect").build());
		put("Role 2 Driller", new AccountSkillBuilder().accountId(SITE_ID).name("Role 2 Driller").build());
	}});

	@Test
	public void testGetProjectSkillsForEmployee() {
		Map<Project, Map<Employee, Set<AccountSkill>>> result = new ProjectAssignmentProcess()
				.getProjectSkillsForEmployees(buildFakeProjectAssignmentDataSet());

		verifyResult(result);
	}

	private ProjectAssignmentDataSet buildFakeProjectAssignmentDataSet() {
		return new ProjectAssignmentDataSet.Builder()
				.projectEmployees(buildFakeProjectEmployeeMap())
				.projectRequiredSkills(buildFakeProjectRequiredSkillsMap())
				.projectRoles(buildFakeProjectRolesMap())
				.projectRoleSkills(buildFakeProjectRoleSkillsMap())
				.projects(buildFakeProjects())
				.roleEmployees(buildFakeRoleEmployeesMap())
				.siteAndCorporateRequiredSkills(buildFakeSiteAndCorporateRequiredSkills())
				.build();
	}

	private Map<Project, Set<Employee>> buildFakeProjectEmployeeMap() {
		return new HashMap<Project, Set<Employee>>() {{
			put(FAKE_PROJECTS.get(0),
					new HashSet<Employee>() {{
						add(FAKE_EMPLOYEES.get(0));
					}});

			put(FAKE_PROJECTS.get(1),
					new HashSet<Employee>() {{
						add(FAKE_EMPLOYEES.get(0));
						add(FAKE_EMPLOYEES.get(1));
					}});
		}};
	}

	private Map<Project, Set<AccountSkill>> buildFakeProjectRequiredSkillsMap() {
		return new HashMap<Project, Set<AccountSkill>>() {{
			put(FAKE_PROJECTS.get(0),
					new HashSet<AccountSkill>() {{
						add(FAKE_SKILLS.get("Project 1 Welder"));
						add(FAKE_SKILLS.get("Project 1 Driller"));
					}});
		}};
	}

	private Map<Project, Set<Role>> buildFakeProjectRolesMap() {
		return new HashMap<Project, Set<Role>>() {{
			put(FAKE_PROJECTS.get(0),
					new HashSet<Role>() {{
						add(FAKE_ROLES.get(0));
						add(FAKE_ROLES.get(1));
					}});

			put(FAKE_PROJECTS.get(1),
					new HashSet<Role>() {{
						add(FAKE_ROLES.get(1));
						add(FAKE_ROLES.get(2));
					}});
		}};
	}

	private Map<Role, Set<AccountSkill>> buildFakeProjectRoleSkillsMap() {
		return new HashMap<Role, Set<AccountSkill>>() {{
			put(FAKE_ROLES.get(0),
					new HashSet<AccountSkill>() {{
						add(FAKE_SKILLS.get("Role 1 Manager"));
						add(FAKE_SKILLS.get("Role 1 Architect"));
					}});

			put(FAKE_ROLES.get(1),
					new HashSet<AccountSkill>() {{
						add(FAKE_SKILLS.get("Role 2 Driller"));
					}});
		}};
	}

	private List<Project> buildFakeProjects() {
		return new ArrayList<>(FAKE_PROJECTS);
	}

	private Map<Role, Set<Employee>> buildFakeRoleEmployeesMap() {
		return new HashMap<Role, Set<Employee>>() {{
			put(FAKE_ROLES.get(0),
					new HashSet<Employee>() {{
						add(FAKE_EMPLOYEES.get(0));
					}});

			put(FAKE_ROLES.get(1),
					new HashSet<Employee>() {{
						add(FAKE_EMPLOYEES.get(0));
						add(FAKE_EMPLOYEES.get(1));
					}});

			put(FAKE_ROLES.get(2),
					new HashSet<Employee>() {{
						add(FAKE_EMPLOYEES.get(0));
					}});
		}};
	}

	private Set<AccountSkill> buildFakeSiteAndCorporateRequiredSkills() {
		return new HashSet<AccountSkill>() {{
			add(FAKE_SKILLS.get("Site Skill"));
			add(FAKE_SKILLS.get("Corporate Skill"));
		}};
	}

	private void verifyResult(final Map<Project, Map<Employee, Set<AccountSkill>>> result) {
		assertEquals(2, result.size());

		Map<Project, Map<Employee, Set<String>>> expectedResult = getExpectedResults();
		for (Project project : result.keySet()) {
			if (!expectedResult.containsKey(project)) {
				fail("Expected result does not contain project " + project.getName());
			}

			for (Employee employee : result.get(project).keySet()) {
				if (!expectedResult.get(project).containsKey(employee)) {
					fail("Expected result does not contain employee " + employee.getEmail());
				}

				verifySkillsForEmployee(result.get(project).get(employee),
						expectedResult.get(project).get(employee).toArray(new String[0]));
			}
		}
	}

	private Map<Project, Map<Employee, Set<String>>> getExpectedResults() {
		return new HashMap<Project, Map<Employee, Set<String>>>() {{
			put(FAKE_PROJECTS.get(1),
					new HashMap<Employee, Set<String>>() {{
						put(FAKE_EMPLOYEES.get(0), new HashSet<String>() {{
							add("Site Skill");
							add("Corporate Skill");
							add("Role 2 Driller");
						}});

						put(FAKE_EMPLOYEES.get(1), new HashSet<String>() {{
							add("Site Skill");
							add("Corporate Skill");
							add("Role 2 Driller");
						}});
					}});

			put(FAKE_PROJECTS.get(0),
					new HashMap<Employee, Set<String>>() {{
						put(FAKE_EMPLOYEES.get(0), new HashSet<String>() {{
							addAll(Arrays.asList("Project 1 Welder",
									"Project 1 Driller",
									"Site Skill",
									"Corporate Skill",
									"Role 1 Manager",
									"Role 1 Architect",
									"Role 2 Driller"));
						}});
					}});
		}};
	}

	private void verifySkillsForEmployee(final Collection<AccountSkill> skills, final String... skillNames) {
		assertEquals(skills.size(), skillNames.length);

		for (AccountSkill skill : skills) {
			assertTrue(ArrayUtils.contains(skillNames, skill.getName()));
		}
	}
}
