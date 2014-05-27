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
																  final Map<AccountModel, Set<AccountModel>> siteHierarchy) {
		ContractorAssignmentData contractorAssignmentData = new ContractorAssignmentData();

//		contractorAssignmentData.getAccountProjects(),
//				contractorAssignmentData.getContractorSiteAssignments()
//		contractorAssignmentData.getEmployeeAssignmentMap()
//		contractorAssignmentData.getRoleSkills();

		return contractorAssignmentData;
	}

	public Map<Project, Map<SkillStatus, Integer>> buildProjectAssignmentStatistics(ContractorAssignmentData contractorAssignmentData) {
		Map<Project, Map<SkillStatus, Integer>> result = new HashMap<>();

		Map<Project, Map<Employee, Set<AccountSkill>>> employeeProjects = null;
		for (Project project : employeeProjects.keySet()) {
			Map<Employee, List<SkillStatus>> employeeStatuses = statusCalculatorService
					.getEmployeeSkillStatusList(employeeProjects.get(project));
			Map<Employee, SkillStatus>

			result.put(project, SkillStatusCalculator.statusCount(employeeStatuses));
		}

		return result;
	}

	public Map<AccountModel, Map<SkillStatus, Integer>> buildSiteAssignmentStatistics(final Map<AccountModel, Set<AccountModel>> siteHierarchy,
																					  final ContractorAssignmentData contractorAssignmentData) {
		Map<AccountModel, Map<SkillStatus, Integer>> result = new HashMap<>();

		Map<AccountModel, Map<Employee, Set<AccountSkill>>> employeeAssignments = employeeSkillsForSite(siteHierarchy,
				contractorAssignmentData);
		for (AccountModel accountModel : employeeAssignments.keySet()) {
			Map<Employee, List<SkillStatus>> employeeStatuses = statusCalculatorService
					.getEmployeeSkillStatusList(employeeAssignments.get(accountModel));

			result.put(accountModel, SkillStatusCalculator.statusCount(employeeStatuses));
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

		Map<AccountModel, Map<Employee, Set<AccountSkill>>> result = null;
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
		}

		return result;
	}

//	private Map<AccountModel, Integer> accountMap(final Collection<AccountModel> accountModels) {
//		Map<Integer, AccountModel> idAccountModelMap = getIdAccountModelMap(accountModels);
//
//		return PicsCollectionUtil.invertMap(idAccountModelMap);
//	}

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
