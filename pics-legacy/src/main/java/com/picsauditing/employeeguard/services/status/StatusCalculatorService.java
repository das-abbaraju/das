package com.picsauditing.employeeguard.services.status;

import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class StatusCalculatorService {

	@Autowired
	private AccountSkillProfileDAO accountSkillProfileDAO;

	public Map<Employee, SkillStatus> getEmployeeStatusRollUpForSkills(final Map<Employee, Set<AccountSkill>> employeeSkills) {
		if (MapUtils.isEmpty(employeeSkills)) {
			return Collections.emptyMap();
		}

		Set<Employee> employees = employeeSkills.keySet();
		Map<Employee, Set<AccountSkillProfile>> accountSkillProfileMap =
				convertToMap(employees, accountSkillProfileDAO.findByEmployeesAndSkills(employees,
						PicsCollectionUtil.mergeCollectionOfCollections(employeeSkills.values())));

		Map<Employee, SkillStatus> employeeStatuses = new HashMap<>();
		for (Employee employee : employees) {
			SkillStatus rollUp = SkillStatus.Expired;

			Set<AccountSkillProfile> accountSkillProfiles = accountSkillProfileMap.get(employee);
			if (CollectionUtils.isNotEmpty(accountSkillProfiles)) {
				rollUp = SkillStatusCalculator.calculateStatusRollUp(accountSkillProfiles);
			}

			employeeStatuses.put(employee, rollUp);
		}

		return employeeStatuses;
	}

	@Deprecated
	public Map<Employee, SkillStatus> getEmployeeStatusRollUpForSkills(final Collection<Employee> employees,
																	   final Map<Employee, Set<AccountSkill>> employeeRequiredSkills) {
		if (CollectionUtils.isEmpty(employees) || MapUtils.isEmpty(employeeRequiredSkills)) {
			return Collections.emptyMap();
		}

		Map<Employee, SkillStatus> employeeStatuses = new HashMap<>();
		for (final Employee employee : employees) {
			SkillStatus rollUp = SkillStatus.Expired;
			if (CollectionUtils.isNotEmpty(employeeRequiredSkills.get(employee)) && employee.getProfile() != null) {
				List<AccountSkillProfile> employeeSkills = new ArrayList<>(employee.getProfile().getSkills());
				CollectionUtils.filter(employeeSkills, new GenericPredicate<AccountSkillProfile>() {

					@Override
					public boolean evaluateEntity(AccountSkillProfile accountSkillProfile) {
						return employeeRequiredSkills.get(employee).contains(accountSkillProfile.getSkill());
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

		if (CollectionUtils.isEmpty(employees) || CollectionUtils.isEmpty(orderedSkills)) {
			return Collections.emptyMap();
		}

		List<AccountSkillProfile> accountSkillProfiles = accountSkillProfileDAO
				.findByEmployeesAndSkills(employees, orderedSkills);

		Map<Employee, Set<AccountSkillProfile>> employeeSetMap = convertToMap(employees, accountSkillProfiles);

		return buildSkillStatusMap(new HashSet<>(employees), employeeSetMap, orderedSkills);
	}

	private Map<Employee, Set<AccountSkillProfile>> convertToMap(final Collection<Employee> employees,
																 final List<AccountSkillProfile> accountSkillProfiles) {
		Map<Profile, Set<Employee>> employeeProfileMap = ServiceHelper.buildProfileToEmployeesMap(employees);

		Map<Profile, Set<AccountSkillProfile>> profileSkills = PicsCollectionUtil.convertToMapOfSets(accountSkillProfiles,

				new PicsCollectionUtil.MapConvertable<Profile, AccountSkillProfile>() {

					@Override
					public Profile getKey(AccountSkillProfile entity) {
						return entity.getProfile();
					}
				});

		return ServiceHelper.mapFromProfileToEmployee(profileSkills, employeeProfileMap);
	}

	private Map<Employee, List<SkillStatus>> buildSkillStatusMap(final Set<Employee> employees,
																 final Map<Employee, Set<AccountSkillProfile>> employeeMap,
																 final List<AccountSkill> orderedSkills) {

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

	private List<SkillStatus> buildOrderedSkillStatusList(final Set<AccountSkillProfile> accountSkillProfiles,
														  final List<AccountSkill> orderedSkills) {
		List<SkillStatus> skillStatusList = fillWithExpiredStatus(orderedSkills.size());
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			int index = orderedSkills.indexOf(accountSkillProfile.getSkill());
			skillStatusList.set(index, SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile));
		}

		return skillStatusList;
	}

	private List<SkillStatus> fillWithExpiredStatus(final int size) {
		SkillStatus[] skillStatusArray = new SkillStatus[size];
		Arrays.fill(skillStatusArray, SkillStatus.Expired);
		return Arrays.asList(skillStatusArray);
	}

	public SkillStatus calculateOverallStatus(final Collection<SkillStatus> skillStatuses) {
		return SkillStatusCalculator.calculateOverallStatus(skillStatuses);
	}

	public <E> Map<E, SkillStatus> getOverallStatusPerEntity(final Map<E, List<SkillStatus>> entitySkillStatusMap) {
		return SkillStatusCalculator.getOverallStatusPerEntity(entitySkillStatusMap);
	}

	public <E> Map<E, List<SkillStatus>> getSkillStatusListPerEntity(final Employee employee, final Map<E, Set<AccountSkill>> skillMap) {
		if (employee == null || MapUtils.isEmpty(skillMap)) {
			return Collections.emptyMap();
		}

		Set<AccountSkill> skills = getSkillsFromMap(skillMap);
		if (CollectionUtils.isEmpty(skills)) {
			return Collections.emptyMap();
		}

		List<AccountSkillProfile> accountSkillEmployees = accountSkillProfileDAO
				.findByEmployeeAndSkills(employee, skills);
		Map<AccountSkill, AccountSkillProfile> accountSkillEmployeeMap =
				buildAccountSkillToAccountSkillEmployeeMap(accountSkillEmployees);

		return buildMapOfSkillStatus(skillMap, accountSkillEmployeeMap);
	}

	public <E> Map<E, List<SkillStatus>> getSkillStatusListPerEntity(final Employee employee,
																	 final Map<E, Set<AccountSkill>> skillMap,
																	 final SkillStatus defaultStatus) {
		if (employee == null || MapUtils.isEmpty(skillMap)) {
			return Collections.emptyMap();
		}

		Set<AccountSkill> skills = getSkillsFromMap(skillMap);
		if (CollectionUtils.isEmpty(skills)) {
			return Collections.emptyMap();
		}

		List<AccountSkillProfile> accountSkillEmployees = accountSkillProfileDAO
				.findByEmployeeAndSkills(employee, skills);

		Map<AccountSkill, AccountSkillProfile> accountSkillEmployeeMap =
				buildAccountSkillToAccountSkillEmployeeMap(accountSkillEmployees);

		return buildMapOfSkillStatus(skillMap, accountSkillEmployeeMap, defaultStatus);
	}

	public <E> Map<E, SkillStatus> getSkillStatusPerEntity(final Employee employee,
														   final Map<E, Set<AccountSkill>> skillMap,
														   final SkillStatus defaultStatus) {
		return getOverallStatusPerEntity(getSkillStatusListPerEntity(employee, skillMap, defaultStatus));
	}

	public <E> Map<E, SkillStatus> getSkillStatusPerEntity(final Employee employee, final Map<E, Set<AccountSkill>> skillMap) {
		return getOverallStatusPerEntity(getSkillStatusListPerEntity(employee, skillMap));
	}

	private <E> Set<AccountSkill> getSkillsFromMap(final Map<E, Set<AccountSkill>> skillMap) {
		if (MapUtils.isEmpty(skillMap)) {
			return Collections.emptySet();
		}

		Set<AccountSkill> skills = new HashSet<>();
		for (Set<AccountSkill> accountSkills : skillMap.values()) {
			skills.addAll(accountSkills);
		}

		return skills;
	}

	private Map<AccountSkill, AccountSkillProfile> buildAccountSkillToAccountSkillEmployeeMap(
			final List<AccountSkillProfile> accountSkillProfile) {

		return PicsCollectionUtil.convertToMap(accountSkillProfile,

				new PicsCollectionUtil.MapConvertable<AccountSkill, AccountSkillProfile>() {

					@Override
					public AccountSkill getKey(AccountSkillProfile accountSkillProfile) {
						return accountSkillProfile.getSkill();
					}
				});
	}

	private <E> Map<E, List<SkillStatus>> buildMapOfSkillStatus(final Map<E, Set<AccountSkill>> skillMap,
																final Map<AccountSkill, AccountSkillProfile> accountSkillEmployeeMap) {
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

	private <E> Map<E, List<SkillStatus>> buildMapOfSkillStatus(final Map<E, Set<AccountSkill>> skillMap,
																final Map<AccountSkill, AccountSkillProfile> accountSkillEmployeeMap,
																final SkillStatus defaultStatus) {
		if (MapUtils.isEmpty(skillMap)) {
			return Collections.emptyMap();
		}

		Map<E, List<SkillStatus>> entityStatusMap = new HashMap<>();
		for (E entity : skillMap.keySet()) {
			List<SkillStatus> skillStatusList = new ArrayList<>();
			if (CollectionUtils.isEmpty(skillMap.get(entity))) {
				skillStatusList.add(defaultStatus);
			} else {
				for (AccountSkill accountSkill : skillMap.get(entity)) {
					skillStatusList.add(SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployeeMap.get(accountSkill)));
				}
			}

			entityStatusMap.put(entity, skillStatusList);
		}

		return entityStatusMap;
	}

	public Map<AccountSkill, SkillStatus> getSkillStatuses(final Employee employee,
														   final Collection<AccountSkill> skills) {
		if (CollectionUtils.isEmpty(skills)) {
			return Collections.emptyMap();
		}

		List<AccountSkillProfile> accountSkillProfiles = accountSkillProfileDAO
				.findByEmployeeAndSkills(employee, skills);

		Map<AccountSkill, SkillStatus> skillStatuses = getAccountSkillStatusMap(accountSkillProfiles);
		return addExpiredStatusToSkillsEmployeeIsMissing(skills, skillStatuses);
	}

	private Map<AccountSkill, SkillStatus> addExpiredStatusToSkillsEmployeeIsMissing(final Collection<AccountSkill> skills,
																					 final Map<AccountSkill, SkillStatus> skillStatuses) {
		Map<AccountSkill, SkillStatus> allSkillStatuses = new HashMap<>();
		if (MapUtils.isNotEmpty(skillStatuses)) {
			allSkillStatuses.putAll(skillStatuses);
		}

		for (AccountSkill accountSkill : skills) {
			if (!allSkillStatuses.containsKey(accountSkill)) {
				allSkillStatuses.put(accountSkill, SkillStatus.Expired);
			}
		}

		return allSkillStatuses;
	}

	private Map<AccountSkill, SkillStatus> getAccountSkillStatusMap(List<AccountSkillProfile> accountSkillProfiles) {
		return PicsCollectionUtil.convertToMap(accountSkillProfiles,

				new PicsCollectionUtil.EntityKeyValueConvertable<AccountSkillProfile, AccountSkill, SkillStatus>() {
					@Override
					public AccountSkill getKey(AccountSkillProfile accountSkillProfile) {
						return accountSkillProfile.getSkill();
					}

					@Override
					public SkillStatus getValue(AccountSkillProfile accountSkillProfile) {
						return SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);
					}
				});
	}

	public <E> Map<E, List<SkillStatus>> getAllSkillStatusesForEntity(final Map<E, Map<Employee, Set<AccountSkill>>> entityEmployeeSkillMap) {
		if (MapUtils.isEmpty(entityEmployeeSkillMap)) {
			return Collections.emptyMap();
		}

		///-- Prepare Unique list of employees and Skills
		Set<Employee> employees = new HashSet<>();
		Set<AccountSkill> skills = new HashSet<>();
		for (E entity : entityEmployeeSkillMap.keySet()) {
			for (Map.Entry<Employee, Set<AccountSkill>> entry : entityEmployeeSkillMap.get(entity).entrySet()) {
				employees.add(entry.getKey());
				skills.addAll(entry.getValue());
			}
		}

		//-- Get documentations for the list of employees with list of skills
		List<AccountSkillProfile> accountSkillProfile = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(employees) && CollectionUtils.isNotEmpty(skills)) {
			accountSkillProfile = accountSkillProfileDAO.findByEmployeesAndSkills(employees, skills);
		}

    /* Prepare collection of Employees with skills that have documentation attached. This map of maps contains
	only the skills that the employee has provided documentation for !
    * */

		Map<Profile, Map<AccountSkill, AccountSkillProfile>> employeeSkillMap = PicsCollectionUtil.convertToMapOfMaps(

				accountSkillProfile,

				new PicsCollectionUtil.CollectionToMapConverter<Profile, AccountSkill, AccountSkillProfile>() {

					@Override
					public Profile getRow(AccountSkillProfile accountSkillProfile) {
						return accountSkillProfile.getProfile();
					}

					@Override
					public AccountSkill getColumn(AccountSkillProfile accountSkillProfile) {
						return accountSkillProfile.getSkill();
					}
				});

		//-- Roll up skill status of each Employee
		Map<E, List<SkillStatus>> skillStatusPerEntityEmployee = new HashMap<>();
		for (final E entity : entityEmployeeSkillMap.keySet()) {
			for (final Employee employee : entityEmployeeSkillMap.get(entity).keySet()) {

				SkillStatus skillStatus = SkillStatus.Expired;
				//-- If employee has any documentations at all.
				if (employeeSkillMap.containsKey(employee.getProfile())) {
					//-- Documentations provided for this employee
					Collection<AccountSkillProfile> aspForStatusCalculation = new HashSet<>(employeeSkillMap.get(employee.getProfile()).values());
					//-- Distill the collection specific to this employee's skills.
					CollectionUtils.filter(aspForStatusCalculation, new GenericPredicate<AccountSkillProfile>() {
						@Override
						public boolean evaluateEntity(AccountSkillProfile accountSkillProfile) {
							return entityEmployeeSkillMap.get(entity).get(employee).contains(accountSkillProfile.getSkill());
						}
					});

					if (!aspForStatusCalculation.isEmpty()) {
						skillStatus = SkillStatusCalculator.calculateStatusRollUp(aspForStatusCalculation);
					}
				}

				//-- Add Skill status information to the entity in question.
				if (!skillStatusPerEntityEmployee.containsKey(entity)) {
					skillStatusPerEntityEmployee.put(entity, new ArrayList<SkillStatus>());
				}
				skillStatusPerEntityEmployee.get(entity).add(skillStatus);

			}
		}

		return skillStatusPerEntityEmployee;
	}
}