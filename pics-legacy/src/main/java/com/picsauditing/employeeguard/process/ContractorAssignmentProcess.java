package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ContractorAssignmentProcess {

	@Autowired
	private ProcessHelper processHelper;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public ContractorAssignmentData buildContractorAssignmentData(final int contractorId,
																  final Set<AccountModel> contractorSites,
																  final Map<AccountModel, Set<AccountModel>> siteHierarchy) {
		ContractorAssignmentData contractorAssignmentData = new ContractorAssignmentData();

		Set<Project> projects = processHelper.allProjectsForContractor(contractorId);
		Map<Project, Set<AccountSkill>> projectRequiredSkills = processHelper.getProjectRequiredSkills(projects);
		Map<Project, Set<Role>> projectRoles = processHelper.getProjectRoles(projects);
		Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills = processHelper.siteAndCorporateRequiredSkills(siteHierarchy);
		Map<Role, Set<AccountSkill>> roleSkills = processHelper.getRoleSkills(contractorId);
		Map<Project, Set<Employee>> projectEmployeeAssignments = processHelper.projectEmployeeAssignments(contractorId);

		contractorAssignmentData.setAllProjects(projects);
		contractorAssignmentData.setContractorSiteAssignments(contractorSites);
		contractorAssignmentData.setEmployeeAssignmentMap(processHelper.employeeSiteAssignment(contractorId, contractorSites));
		contractorAssignmentData.setRoleSkills(roleSkills);
		contractorAssignmentData.setProjectRoles(projectRoles);
		contractorAssignmentData.setProjectEmployeeAssignments(projectEmployeeAssignments);
		contractorAssignmentData.setSiteAndCorporateRequiredSkills(siteAndCorporateRequiredSkills);
		contractorAssignmentData.setProjectRequiredSkills(projectRequiredSkills);
		contractorAssignmentData.setAccountProjects(processHelper.accountProjects(contractorSites, projects));

		return contractorAssignmentData;
	}

	public Map<Project, Map<SkillStatus, Integer>> buildProjectAssignmentStatistics(final int contractorId,
																					final ContractorAssignmentData contractorAssignmentData) {
		Map<Project, Map<Employee, Set<AccountSkill>>> employeeProjects = processHelper
				.projectEmployeeSkills(contractorId, contractorAssignmentData.getAllProjects(),
						contractorAssignmentData.getProjectRequiredSkills(), contractorAssignmentData.getProjectRoles(),
						contractorAssignmentData.getRoleSkills(), contractorAssignmentData.getProjectEmployeeAssignments(),
						contractorAssignmentData.getSiteAndCorporateRequiredSkills());

		Map<Project, Map<SkillStatus, Integer>> result = new HashMap<>();
		for (Project project : employeeProjects.keySet()) {
			Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService
					.getEmployeeStatusRollUpForSkills(employeeProjects.get(project));

			result.put(project, SkillStatusCalculator.statusCountPerEntity(employeeStatuses));
		}

		return result;
	}

	public Map<AccountModel, Map<SkillStatus, Integer>> buildSiteAssignmentStatistics(final Map<AccountModel, Set<AccountModel>> siteHierarchy,
																					  final ContractorAssignmentData contractorAssignmentData) {
		Map<AccountModel, Map<SkillStatus, Integer>> result = new HashMap<>();

		Map<AccountModel, Map<Employee, Set<AccountSkill>>> employeeAssignments = employeeSkillsForSite(siteHierarchy,
				contractorAssignmentData);

		for (AccountModel accountModel : employeeAssignments.keySet()) {
			Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService
					.getEmployeeStatusRollUpForSkills(employeeAssignments.get(accountModel));

			result.put(accountModel, SkillStatusCalculator.statusCountPerEntity(employeeStatuses));
		}

		return result;
	}

	// Site required skills, role required skills
	private Map<AccountModel, Map<Employee, Set<AccountSkill>>> employeeSkillsForSite(final Map<AccountModel, Set<AccountModel>> siteHierarchy,
																					  final ContractorAssignmentData contractorAssignmentData) {
		Map<AccountModel, Set<Employee>> employeeAssignment = contractorAssignmentData.getEmployeeAssignmentMap();
		Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills = processHelper.siteAndCorporateRequiredSkills(siteHierarchy);
		Map<Role, Set<AccountSkill>> roleSkills = contractorAssignmentData.getRoleSkills();
		Map<AccountModel, Map<Employee, Set<Role>>> siteEmployeeRoles = processHelper
				.siteEmployeeRoles(getIdAccountModelMap(siteHierarchy.keySet()));

		Map<AccountModel, Map<Employee, Set<AccountSkill>>> result = new HashMap<>();
		for (AccountModel accountModel : employeeAssignment.keySet()) {
			result.put(accountModel, new HashMap<Employee, Set<AccountSkill>>());

			Map<Employee, Set<AccountSkill>> employeeSkillsForSite = new HashMap<>();
			for (Employee employee : employeeAssignment.get(accountModel)) {
				if (!employeeSkillsForSite.containsKey(employee)) {
					employeeSkillsForSite.put(employee, new HashSet<AccountSkill>());
				}

				employeeSkillsForSite.get(employee).addAll(siteAndCorporateRequiredSkills.get(accountModel));

				Set<Role> employeeSiteRoles = employeeSiteRoles(accountModel, employee, siteEmployeeRoles);
				for (Role role : employeeSiteRoles) {
					Set<AccountSkill> skills = roleSkills.get(role);
					if (CollectionUtils.isNotEmpty(skills)) {
						employeeSkillsForSite.get(employee).addAll(skills);
					}
				}
			}

			if (MapUtils.isNotEmpty(employeeSkillsForSite)) {
				result.put(accountModel, employeeSkillsForSite);
			}
		}

		return result;
	}

	private Map<Integer, AccountModel> getIdAccountModelMap(Collection<AccountModel> accountModels) {
		return PicsCollectionUtil.convertToMap(accountModels,

				new PicsCollectionUtil.MapConvertable<Integer, AccountModel>() {

					@Override
					public Integer getKey(AccountModel accountModel) {
						return accountModel.getId();
					}
				});
	}

	private Set<Role> employeeSiteRoles(final AccountModel accountModel,
										final Employee employee,
										final Map<AccountModel, Map<Employee, Set<Role>>> siteEmployeeRoles) {
		if (MapUtils.isEmpty(siteEmployeeRoles) || MapUtils.isEmpty(siteEmployeeRoles.get(accountModel))
				|| CollectionUtils.isEmpty(siteEmployeeRoles.get(accountModel).get(employee))) {
			return Collections.emptySet();
		}

		return siteEmployeeRoles.get(accountModel).get(employee);
	}
}
