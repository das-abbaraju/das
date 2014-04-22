package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.employee.CompanySkillInfo;
import com.picsauditing.employeeguard.forms.employee.CompanySkillsForm;
import com.picsauditing.employeeguard.services.external.AccountService;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import com.picsauditing.employeeguard.services.ProjectService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class CompanySkillsFormBuilder {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;

	public CompanySkillsForm build(Profile profile) {
		List<CompanySkillInfo> companySkillInfoList = new ArrayList<>();

		addEmployeeCompanySkillInfo(profile, companySkillInfoList);
		addProjectSiteSkillInfo(profile, companySkillInfoList);
		addProjectCompanySkillInfo(profile, companySkillInfoList);
		Collections.sort(companySkillInfoList, new Comparator<CompanySkillInfo>() {
			@Override
			public int compare(CompanySkillInfo o1, CompanySkillInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		CompanySkillsForm companySkillsForm = new CompanySkillsForm();
		companySkillsForm.setCompanySkillInfoList(companySkillInfoList);
		return companySkillsForm;
	}

	private void addEmployeeCompanySkillInfo(Profile profile, List<CompanySkillInfo> companySkillInfoList) {
		List<AccountModel> accounts = getEmployeeAccounts(profile);
		for (AccountModel accountModel : accounts) {
			Employee employee = getEmployeeForAccount(profile.getEmployees(), accountModel.getId());

			CompanySkillInfo companySkillInfo = new CompanySkillInfo();
			companySkillInfo.setName(accountModel.getName());
			companySkillInfo.setCompletedSkills(getSkillInfoForEmployee(employee, SkillStatus.Completed));
			companySkillInfo.setAboutToExpireSkills(getSkillInfoForEmployee(employee, SkillStatus.Expiring));
			companySkillInfo.setExpiredSkills(getSkillInfoForEmployee(employee, SkillStatus.Expired));
			companySkillInfo.sortSkills();
			companySkillInfoList.add(companySkillInfo);
		}
	}

	private void addProjectSiteSkillInfo(Profile profile, List<CompanySkillInfo> companySkillInfoList) {
		Set<Integer> allAccountIds = new HashSet<>();
		for (Employee employee : profile.getEmployees()) {
			List<Project> employeeProjects = projectService.getProjectsForEmployee(employee);
			List<Integer> accounts = ExtractorUtil.extractList(employeeProjects, new Extractor<Project, Integer>() {
				@Override
				public Integer extract(Project project) {
					return project.getAccountId();
				}
			});

			for (int accountId : accounts) {
				allAccountIds.add(accountId);
				allAccountIds.addAll(accountService.getTopmostCorporateAccountIds(accountId));
			}
		}

		List<SiteSkill> siteSkills = siteSkillDAO.findByAccountIds(new ArrayList<>(allAccountIds));
		List<AccountModel> accountModels = accountService.getAccountsByIds(allAccountIds);

		Map<AccountModel, Set<AccountSkill>> accountToSkill = new TreeMap<>();
		for (SiteSkill siteSkill : siteSkills) {
			for (AccountModel accountModel : accountModels) {
				if (accountModel.getId() == siteSkill.getSiteId()) {
					if (accountToSkill.get(accountModel) == null) {
						accountToSkill.put(accountModel, new HashSet<AccountSkill>());
					}

					accountToSkill.get(accountModel).add(siteSkill.getSkill());
				}
			}
		}


		List<AccountSkill> skills = ExtractorUtil.extractList(siteSkills, SiteSkill.SKILL_EXTRACTOR);
		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService.findByEmployeesAndSkills(profile.getEmployees(), skills);

		Map<AccountModel, List<AccountSkillEmployee>> accountToEmployeeSkills = new TreeMap<>();
		for (Map.Entry<AccountModel, Set<AccountSkill>> entry : accountToSkill.entrySet()) {
			List<AccountSkillEmployee> accountEmployeeSkills = filterAccountSkillEmployeesFor(accountSkillEmployees, entry.getValue());
			accountToEmployeeSkills.put(entry.getKey(), accountEmployeeSkills);
		}

		for (Map.Entry<AccountModel, List<AccountSkillEmployee>> entry : accountToEmployeeSkills.entrySet()) {
			CompanySkillInfo companySkillInfo = new CompanySkillInfo();
			companySkillInfo.setName(entry.getKey().getName());
			companySkillInfo.setCompletedSkills(buildSkillInfoFilteredOnStatus(entry.getValue(), SkillStatus.Completed));
			companySkillInfo.setAboutToExpireSkills(buildSkillInfoFilteredOnStatus(entry.getValue(), SkillStatus.Expiring));
			companySkillInfo.setExpiredSkills(buildSkillInfoFilteredOnStatus(entry.getValue(), SkillStatus.Expired));

			companySkillInfoList.add(companySkillInfo);
		}
	}

	private void addProjectCompanySkillInfo(Profile profile, List<CompanySkillInfo> companySkillInfoList) {
		Map<Project, List<AccountSkillEmployee>> projects = mapProjectEmployeeSkills(profile);
		Map<Project, AccountModel> projectAccounts = mapProjectAccounts(projects.keySet());
		for (Project project : projects.keySet()) {
			List<AccountSkillEmployee> accountSkillEmployees = projects.get(project);
			CompanySkillInfo companySkillInfo = new CompanySkillInfo();

			companySkillInfo.setName(projectAccounts.get(project).getName() + ": " + project.getName());
			companySkillInfo.setCompletedSkills(buildSkillInfoFilteredOnStatus(accountSkillEmployees, SkillStatus.Completed));
			companySkillInfo.setAboutToExpireSkills(buildSkillInfoFilteredOnStatus(accountSkillEmployees, SkillStatus.Expiring));
			companySkillInfo.setExpiredSkills(buildSkillInfoFilteredOnStatus(accountSkillEmployees, SkillStatus.Expired));
			companySkillInfo.sortSkills();

			companySkillInfoList.add(companySkillInfo);
		}
	}

	private Map<Project, List<AccountSkillEmployee>> mapProjectEmployeeSkills(final Profile profile) {
		// Find project role employee by employees
		List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO.findByEmployees(profile.getEmployees());
		Set<AccountSkill> allRequiredSkills = new TreeSet<>();
		// Find skills (including required) for project
		Map<Project, Set<AccountSkill>> projectSkills = mapProjectRequiredSkills(projectRoleEmployees, allRequiredSkills);
		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService.findByEmployeesAndSkills(profile.getEmployees(), new ArrayList<>(allRequiredSkills));

		Map<Project, List<AccountSkillEmployee>> projectEmployeeSkills = new TreeMap<>();
		for (Map.Entry<Project, Set<AccountSkill>> entrySet : projectSkills.entrySet()) {
			projectEmployeeSkills.put(entrySet.getKey(), filterAccountSkillEmployeesFor(accountSkillEmployees, entrySet.getValue()));
		}
		return projectEmployeeSkills;
	}

	private Map<Project, AccountModel> mapProjectAccounts(Collection<Project> projects) {
		Set<Integer> accountIds = new HashSet<>();

		for (Project project : projects) {
			accountIds.add(project.getAccountId());
		}

		List<AccountModel> accounts = accountService.getAccountsByIds(accountIds);

		Map<Project, AccountModel> projectAccounts = new TreeMap<>();
		for (Project project : projects) {
			for (AccountModel account : accounts) {
				if (project.getAccountId() == account.getId()) {
					projectAccounts.put(project, account);
				}
			}
		}
		return projectAccounts;
	}

	private List<AccountSkillEmployee> filterAccountSkillEmployeesFor(final List<AccountSkillEmployee> accountSkillEmployees, final Set<AccountSkill> accountSkills) {
		List<AccountSkillEmployee> filtered = new ArrayList<>(accountSkillEmployees);
		CollectionUtils.filter(filtered, new GenericPredicate<AccountSkillEmployee>() {
			@Override
			public boolean evaluateEntity(AccountSkillEmployee accountSkillEmployee) {
				return accountSkills.contains(accountSkillEmployee.getSkill());
			}
		});

		return filtered;
	}

	private Map<Project, Set<AccountSkill>> mapProjectRequiredSkills(List<ProjectRoleEmployee> projectRoleEmployees, Set<AccountSkill> allRequiredSkills) {
		Map<Project, Set<AccountSkill>> projectSkills = new TreeMap<>();
		for (ProjectRoleEmployee projectRoleEmployee : projectRoleEmployees) {
			Project project = projectRoleEmployee.getProjectRole().getProject();
			if (projectSkills.get(project) == null) {
				projectSkills.put(project, new TreeSet<AccountSkill>());

				for (ProjectSkill projectSkill : project.getSkills()) {
					projectSkills.get(project).add(projectSkill.getSkill());
					allRequiredSkills.add(projectSkill.getSkill());
				}
			}

			Role role = projectRoleEmployee.getProjectRole().getRole();
			for (AccountSkillRole accountSkillRole : role.getSkills()) {
				projectSkills.get(project).add(accountSkillRole.getSkill());
				allRequiredSkills.add(accountSkillRole.getSkill());
			}
		}

		return projectSkills;
	}

	private List<SkillInfo> buildSkillInfoFilteredOnStatus(final List<AccountSkillEmployee> accountSkillEmployees, final SkillStatus status) {
		List<AccountSkillEmployee> accountSkills = new ArrayList<>(accountSkillEmployees);
		return filterAccountSkillEmployeesByStatus(status, accountSkills);
	}

	private List<SkillInfo> filterAccountSkillEmployeesByStatus(final SkillStatus status, List<AccountSkillEmployee> accountSkills) {
		CollectionUtils.filter(accountSkills, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				return status == SkillStatusCalculator.calculateStatusFromSkill((AccountSkillEmployee) object);
			}
		});

		return mapAccountSkillToSkillInfo(accountSkills);
	}

	private List<AccountModel> getEmployeeAccounts(Profile profile) {
		List<Employee> employees = profile.getEmployees();
		List<Integer> accountIds = getAccountIds(employees);
		return accountService.getAccountsByIds(accountIds);
	}

	private List<Integer> getAccountIds(List<Employee> employees) {
		List<Integer> accountIds = new ArrayList<>();
		for (Employee employee : employees) {
			accountIds.add(employee.getAccountId());
		}

		return accountIds;
	}

	private Employee getEmployeeForAccount(List<Employee> employees, int accountId) {
		for (Employee employee : employees) {
			if (employee.getAccountId() == accountId) {
				return employee;
			}
		}

		return null;
	}

	private List<SkillInfo> getSkillInfoForEmployee(Employee employee, final SkillStatus status) {
		List<AccountSkillEmployee> accountSkills = getSkillsForAccountAndEmployee(employee);

		return filterAccountSkillEmployeesByStatus(status, accountSkills);
	}

	private List<AccountSkillEmployee> getSkillsForAccountAndEmployee(Employee employee) {
		return accountSkillEmployeeService.getSkillsForAccountAndEmployee(employee);
	}

	private List<SkillInfo> mapAccountSkillToSkillInfo(List<AccountSkillEmployee> accountSkills) {
		List<SkillInfo> skillInfoList = new ArrayList<>();
		for (AccountSkillEmployee accountSkill : accountSkills) {
			skillInfoList.add(mapAccountSkillToSkillInfo(accountSkill.getSkill()));
		}

		return skillInfoList;
	}

	private SkillInfo mapAccountSkillToSkillInfo(AccountSkill accountSkill) {
		SkillInfo skillInfo = new SkillInfo();
		skillInfo.setId(accountSkill.getId());
		skillInfo.setName(accountSkill.getName());
		skillInfo.setSkillStatus(SkillStatus.Completed);
		return skillInfo;
	}
}
