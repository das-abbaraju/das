package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class StatusCalculatorService {

	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;

	public Map<Employee, SkillStatus> getEmployeeStatusRollUpForSkills(final Collection<Employee> employees,
																	   final Map<Employee, Set<AccountSkill>> employeeRequiredSkills) {
		if (CollectionUtils.isEmpty(employees) || MapUtils.isEmpty(employeeRequiredSkills)) {
			return Collections.emptyMap();
		}

		Map<Employee, SkillStatus> employeeStatuses = new HashMap<>();
		for (final Employee employee : employees) {
			SkillStatus rollUp = SkillStatus.Expired;
			if (CollectionUtils.isNotEmpty(employeeRequiredSkills.get(employee))) {
				List<AccountSkillEmployee> employeeSkills = new ArrayList<>(employee.getSkills());
				CollectionUtils.filter(employeeSkills, new GenericPredicate<AccountSkillEmployee>() {
					@Override
					public boolean evaluateEntity(AccountSkillEmployee accountSkillEmployee) {
						return employeeRequiredSkills.get(employee).contains(accountSkillEmployee.getSkill());
					}
				});

				if (CollectionUtils.isNotEmpty(employeeSkills)) {
					rollUp = SkillStatusCalculator.calculateStatusRollUp(employeeSkills);
				}
			}

			employeeStatuses.put(employee, rollUp);
		}

		return employeeStatuses;
	}

	/**
	 * Returns a Map where the Key is a unique set of every employee provided in Employees and the Value
	 * is an List contains a SkillStatus for every Skill in orderedSkills, in the same order.
	 *
	 * @param employees
	 * @param orderedSkills
	 * @return
	 */
	public Map<Employee, List<SkillStatus>> getEmployeeStatusRollUpForSkills(final Collection<Employee> employees,
																			 final List<AccountSkill> orderedSkills) {
		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO
				.findByEmployeesAndSkills(employees, orderedSkills);
		Map<Employee, Set<AccountSkillEmployee>> employeeSetMap = convertToMap(accountSkillEmployees);
		return buildSkillStatusMap(new HashSet<>(employees), employeeSetMap, orderedSkills);
	}

	private Map<Employee, Set<AccountSkillEmployee>> convertToMap(final List<AccountSkillEmployee> accountSkillEmployees) {
		return Utilities.convertToMapOfSets(accountSkillEmployees, new Utilities.MapConvertable<Employee, AccountSkillEmployee>() {
			@Override
			public Employee getKey(AccountSkillEmployee accountSkillEmployee) {
				return accountSkillEmployee.getEmployee();
			}
		});
	}

	private Map<Employee, List<SkillStatus>> buildSkillStatusMap(final Set<Employee> employees,
																 final Map<Employee, Set<AccountSkillEmployee>> employeeMap,
																 final List<AccountSkill> orderedSkills) {
		if (MapUtils.isEmpty(employeeMap) || CollectionUtils.isEmpty(orderedSkills)) {
			return Collections.emptyMap();
		}

		final int numberOfSkills = orderedSkills.size();
		Map<Employee, List<SkillStatus>> employeeSkillStatusMap = new HashMap<>();
		for (Employee employee : employees) {
			if (employeeMap.containsKey(employee)) {
				employeeSkillStatusMap.put(employee, buildOrderedSkillStatusList(employeeMap.get(employee),
						orderedSkills));
			} else {
				employeeSkillStatusMap.put(employee, fillWithExpiredStatus(numberOfSkills));
			}
		}

		return employeeSkillStatusMap;
	}

	private List<SkillStatus> buildOrderedSkillStatusList(final Set<AccountSkillEmployee> accountSkillEmployees,
														  final List<AccountSkill> orderedSkills) {
		List<SkillStatus> skillStatusList = fillWithExpiredStatus(orderedSkills.size());
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			int index = orderedSkills.indexOf(accountSkillEmployee.getSkill());
			skillStatusList.set(index, SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
		}

		return skillStatusList;
	}

	private List<SkillStatus> fillWithExpiredStatus(final int size) {
		SkillStatus[] skillStatusArray = new SkillStatus[size];
		Arrays.fill(skillStatusArray, SkillStatus.Expired);
		return Arrays.asList(skillStatusArray);
	}

	public SkillStatus calculateOverallStatus(Collection<SkillStatus> skillStatuses) {
		if (CollectionUtils.isEmpty(skillStatuses)) {
			throw new IllegalArgumentException("skillStatuses should not be empty or null.");
		}

		SkillStatus worstStatus = SkillStatus.Complete;
		for (SkillStatus skillStatus : skillStatuses) {
			if (skillStatus.compareTo(worstStatus) > 0) {
				worstStatus = skillStatus;
			}
		}

		return worstStatus;
	}

	public <E> Map<E, SkillStatus> getOverallStatusPerEntity(final Map<E, List<SkillStatus>> entitySkillStatusMap) {
		if (MapUtils.isEmpty(entitySkillStatusMap)) {
			return Collections.emptyMap();
		}

		Map<E, SkillStatus> overallSkillStatusMap = new HashMap<>();
		for (E entity : entitySkillStatusMap.keySet()) {
			overallSkillStatusMap.put(entity, calculateOverallStatus(entitySkillStatusMap.get(entity)));
		}

		return overallSkillStatusMap;
	}

	public <E> Map<E, List<SkillStatus>> getSkillStatusListPerEntity(final Employee employee, final Map<E, List<AccountSkill>> skillMap) {
		if (employee == null || MapUtils.isEmpty(skillMap)) {
			return Collections.emptyMap();
		}

		Set<AccountSkill> skills = getSkillsFromMap(skillMap);
		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO
				.findByEmployeeAndSkills(employee, skills);
		Map<AccountSkill, AccountSkillEmployee> accountSkillEmployeeMap =
				buildAccountSkillToAccountSkillEmployeeMap(accountSkillEmployees);

		return buildMapOfSkillStatus(skillMap, accountSkillEmployeeMap);
	}

	private <E> Set<AccountSkill> getSkillsFromMap(final Map<E, List<AccountSkill>> skillMap) {
		if (MapUtils.isEmpty(skillMap)) {
			return Collections.emptySet();
		}

		Set<AccountSkill> skills = new HashSet<>();
		for (List<AccountSkill> accountSkills : skillMap.values()) {
			skills.addAll(accountSkills);
		}

		return skills;
	}

	private Map<AccountSkill, AccountSkillEmployee> buildAccountSkillToAccountSkillEmployeeMap(
			final List<AccountSkillEmployee> accountSkillEmployees) {

		return Utilities.convertToMap(accountSkillEmployees,
				new Utilities.MapConvertable<AccountSkill, AccountSkillEmployee>() {
					@Override
					public AccountSkill getKey(AccountSkillEmployee accountSkillEmployee) {
						return accountSkillEmployee.getSkill();
					}
				});
	}

	private <E> Map<E, List<SkillStatus>> buildMapOfSkillStatus(final Map<E, List<AccountSkill>> skillMap,
																final Map<AccountSkill, AccountSkillEmployee> accountSkillEmployeeMap) {
		if (MapUtils.isEmpty(skillMap)) {
			return Collections.emptyMap();
		}

		Map<E, List<SkillStatus>> entityStatusMap = new HashMap<>();
		for (E entity : skillMap.keySet()) {
			List<SkillStatus> skillStatusList = new ArrayList<>();
			for (AccountSkill accountSkill : skillMap.get(entity)) {
				skillStatusList.add(SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployeeMap.get(accountSkill)));
			}

			entityStatusMap.put(entity, skillStatusList);
		}

		return entityStatusMap;
	}

//	public EmployeeProjectAndRoleStatus getEmployeeStatusesForProjectsAndJobRoles(final Employee employee,
//	                                                                              final Map<Role, Role> siteToCorporateRoles,
//	                                                                              final List<AccountSkill> siteRequiredSkills) {
//		Map<Project, SkillStatus> projectSkillStatus = getProjectSkillStatuses(employee, siteRequiredSkills);
//		Map<Role, SkillStatus> roleSkillStatus = getRoleSkillStatuses(employee, siteToCorporateRoles, siteRequiredSkills);
//
//		return new EmployeeProjectAndRoleStatus.Builder().projectStatuses(projectSkillStatus).roleStatuses(roleSkillStatus).build();
//	}
//
//	private Map<Project, SkillStatus> getProjectSkillStatuses(Employee employee, List<AccountSkill> siteRequiredSkills) {
//		Map<Project, Set<AccountSkillEmployee>> projectEmployeeSkills = getEmployeeSkillsForProject(employee, siteRequiredSkills);
//
//		Map<Project, SkillStatus> projectSkillStatus = new HashMap<>();
//		for (Project project : projectEmployeeSkills.keySet()) {
//			projectSkillStatus.put(project, SkillStatusCalculator.calculateStatusRollUp(projectEmployeeSkills.get(project)));
//		}
//
//		return projectSkillStatus;
//	}
//
//	private Map<Project, Set<AccountSkillEmployee>> getEmployeeSkillsForProject(Employee employee, List<AccountSkill> siteRequiredSkills) {
//		Map<Project, Set<AccountSkillEmployee>> projectEmployeeSkills = new HashMap<>();
//
//		for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
//			ProjectRole projectRole = projectRoleEmployee.getProjectRole();
//			Set<AccountSkill> requiredSkills = ExtractorUtil.extractSet(projectRole.getRole().getSkills(), AccountSkillRole.SKILL_EXTRACTOR);
//			requiredSkills.addAll(ExtractorUtil.extractList(projectRole.getProject().getSkills(), ProjectSkill.SKILL_EXTRACTOR));
//			requiredSkills.addAll(siteRequiredSkills);
//
//			List<AccountSkillEmployee> employeeSkills = getEmployeeSkillsForRequiredSkills(employee, requiredSkills);
//			Utilities.addAllToMapOfKeyToSet(projectEmployeeSkills, projectRole.getProject(), employeeSkills);
//		}
//
//		return projectEmployeeSkills;
//	}
//
//	private Map<Role, SkillStatus> getRoleSkillStatuses(Employee employee, Map<Role, Role> siteToCorporateRoles, List<AccountSkill> siteRequiredSkills) {
//		Map<Role, Set<AccountSkillEmployee>> roleEmployeeSkills = getRoleEmployeeSkills(employee, siteToCorporateRoles, siteRequiredSkills);
//
//		Map<Role, SkillStatus> roleSkillStatus = new HashMap<>();
//		for (Role role : roleEmployeeSkills.keySet()) {
//			roleSkillStatus.put(role, SkillStatusCalculator.calculateStatusRollUp(roleEmployeeSkills.get(role)));
//		}
//
//		return roleSkillStatus;
//	}
//
//	private Map<Role, Set<AccountSkillEmployee>> getRoleEmployeeSkills(Employee employee, Map<Role, Role> siteToCorporateRoles, List<AccountSkill> siteRequiredSkills) {
//		Map<Role, Set<AccountSkillEmployee>> roleEmployeeSkills = new HashMap<>();
//
//		for (RoleEmployee roleEmployee : employee.getRoles()) {
//			Role corporateRole = siteToCorporateRoles.get(roleEmployee.getRole());
//			Set<AccountSkill> requiredSkills = ExtractorUtil.extractSet(corporateRole.getSkills(), AccountSkillRole.SKILL_EXTRACTOR);
//			requiredSkills.addAll(siteRequiredSkills);
//
//			List<AccountSkillEmployee> employeeSkills = getEmployeeSkillsForRequiredSkills(employee, requiredSkills);
//			Utilities.addAllToMapOfKeyToSet(roleEmployeeSkills, corporateRole, employeeSkills);
//		}
//
//		return roleEmployeeSkills;
//	}
//
//	private List<AccountSkillEmployee> getEmployeeSkillsForRequiredSkills(final Employee employee, final Set<AccountSkill> skills) {
//		List<AccountSkillEmployee> employeeSkills = new ArrayList<>();
//
//		for (AccountSkill skill : skills) {
//			employeeSkills.add(findEmployeeSkillForSkill(employee, skill));
//		}
//
//		return employeeSkills;
//	}
//
//	private AccountSkillEmployee findEmployeeSkillForSkill(Employee employee, AccountSkill skill) {
//		for (AccountSkillEmployee accountSkillEmployee : employee.getSkills()) {
//			if (accountSkillEmployee.getSkill().equals(skill)) {
//				return accountSkillEmployee;
//			}
//		}
//
//		return new AccountSkillEmployeeBuilder().employee(employee).accountSkill(skill).build();
//	}
}