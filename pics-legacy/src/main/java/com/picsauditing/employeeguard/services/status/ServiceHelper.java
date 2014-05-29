package com.picsauditing.employeeguard.services.status;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class ServiceHelper {

	public static Map<Profile, Set<Employee>> buildProfileToEmployeesMap(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		Map<Profile, Set<Employee>> profileToEmployeesMap = new HashMap<>();
		for (Employee employee : employees) {
			Profile profile = employee.getProfile();
			if (profile == null) {
				continue;
			}

			if (!profileToEmployeesMap.containsKey(employee)) {
				profileToEmployeesMap.put(profile, new HashSet<Employee>());
			}

			profileToEmployeesMap.get(profile).add(employee);
		}

		return profileToEmployeesMap;
	}

	public static <T> Map<Employee, T> mapFromProfileToEmployee(final Map<Profile, T> profileMap,
																final Map<Profile, Set<Employee>> profileEmployeeMap) {
		if (MapUtils.isEmpty(profileMap)) {
			return Collections.emptyMap();
		}

		Map<Employee, T> result = new HashMap<>();
		for (Profile profile : profileEmployeeMap.keySet()) {
			for (Employee employee : profileEmployeeMap.get(profile)) {
				result.put(employee, profileMap.get(profile));
			}
		}

		return result;
	}

}
