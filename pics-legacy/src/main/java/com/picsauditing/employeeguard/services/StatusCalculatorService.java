package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
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
			// exit early if any status is "Expired", because we have already hit the lowest status
			if (skillStatus.isExpired()) {
				return SkillStatus.Expired;
			}

			if (skillStatus.compareTo(worstStatus) < 0) {
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

	public <E> Map<E, List<SkillStatus>> getSkillStatusListPerEntity(final Employee employee, final Map<E, Set<AccountSkill>> skillMap) {
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

	private <E> Map<E, List<SkillStatus>> buildMapOfSkillStatus(final Map<E, Set<AccountSkill>> skillMap,
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

	public Map<AccountSkill, SkillStatus> getSkillStatuses(final Employee employee, final Collection<AccountSkill> skills) {
		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO
				.findByEmployeeAndSkills(employee, skills);

		return Utilities.convertToMap(accountSkillEmployees,
				new Utilities.EntityKeyValueConvertable<AccountSkillEmployee, AccountSkill, SkillStatus>() {
					@Override
					public AccountSkill getKey(AccountSkillEmployee entity) {
						return entity.getSkill();
					}

					@Override
					public SkillStatus getValue(AccountSkillEmployee entity) {
						return SkillStatusCalculator.calculateStatusFromSkill(entity);
					}
				});
	}

	public <E> Map<E, List<SkillStatus>> getAllSkillStatusesForEntity(final Map<E, Map<Employee, Set<AccountSkill>>> entityEmployeeSkillMap) {
		if (MapUtils.isEmpty(entityEmployeeSkillMap)) {
			return Collections.emptyMap();
		}

		Set<Employee> employees = new HashSet<>();
		Set<AccountSkill> skills = new HashSet<>();
		for (E entity : entityEmployeeSkillMap.keySet()) {
			for (Map.Entry<Employee, Set<AccountSkill>> entry : entityEmployeeSkillMap.get(entity).entrySet()) {
				employees.add(entry.getKey());
				skills.addAll(entry.getValue());
			}
		}

		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO.findByEmployeesAndSkills(employees, skills);

		Map<Employee, Map<AccountSkill, AccountSkillEmployee>> employeeSkillMap = PicsCollectionUtil.convertToMapOfMaps(
				accountSkillEmployees,
				new PicsCollectionUtil.CollectionToMapConverter<Employee, AccountSkill, AccountSkillEmployee>() {
					@Override
					public Employee getRow(AccountSkillEmployee value) {
						return value.getEmployee();
					}

					@Override
					public AccountSkill getColumn(AccountSkillEmployee value) {
						return value.getSkill();
					}
				});

		Map<E, List<SkillStatus>> skillStatusPerEntityEmployee = new HashMap<>();
		for (final E entity : entityEmployeeSkillMap.keySet()) {
			for (final Employee employee : entityEmployeeSkillMap.get(entity).keySet()) {
				if (!entityEmployeeSkillMap.containsKey(entity)) {
					continue;
				}

				if (!entityEmployeeSkillMap.get(entity).containsKey(employee)) {
					continue;
				}

				Collection<AccountSkillEmployee> aseForStatusCalculation = new HashSet<>(employeeSkillMap.get(employee).values());
				CollectionUtils.filter(aseForStatusCalculation, new GenericPredicate<AccountSkillEmployee>() {
					@Override
					public boolean evaluateEntity(AccountSkillEmployee accountSkillEmployee) {
						return entityEmployeeSkillMap.get(entity).get(employee).contains(accountSkillEmployee.getSkill());
					}
				});

				if (!aseForStatusCalculation.isEmpty()) {
					SkillStatus skillStatus = SkillStatusCalculator.calculateStatusRollUp(aseForStatusCalculation);

					if (!skillStatusPerEntityEmployee.containsKey(entity)) {
						skillStatusPerEntityEmployee.put(entity, new ArrayList<SkillStatus>());
					}

					skillStatusPerEntityEmployee.get(entity).add(skillStatus);
				}
			}
		}

		return skillStatusPerEntityEmployee;
	}

	private Map<Employee, SkillStatus> calculateAndReduce(final Map<Employee, Collection<AccountSkillEmployee>> employeeAccountSkillEmployees) {
		if (MapUtils.isEmpty(employeeAccountSkillEmployees)) {
			return Collections.emptyMap();
		}

		Map<Employee, SkillStatus> employeeSkillStatus = new HashMap<>();
		for (Employee employee : employeeAccountSkillEmployees.keySet()) {
			employeeSkillStatus.put(employee, SkillStatusCalculator.calculateStatusRollUp(employeeAccountSkillEmployees.get(employee)));
		}

		return employeeSkillStatus;
	}
}