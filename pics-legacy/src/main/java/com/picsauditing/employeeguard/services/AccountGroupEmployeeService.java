package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountGroupEmployeeDAO;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import com.picsauditing.employeeguard.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AccountGroupEmployeeService {
	@Autowired
	private AccountGroupEmployeeDAO accountGroupEmployeeDAO;

	public List<GroupEmployee> findByProfile(final Profile profile) {
		return accountGroupEmployeeDAO.findByProfile(profile);
	}

	public Map<Integer, List<GroupEmployee>> getMapOfAccountGroupEmployeeByAccountId(final Profile profile) {
		if (profile == null) {
			return Collections.emptyMap();
		}

		Map<Integer, List<GroupEmployee>> map = new HashMap<>();

		List<GroupEmployee> groupEmployees = findByProfile(profile);
		for (GroupEmployee groupEmployee : groupEmployees) {
			int accountId = groupEmployee.getEmployee().getAccountId();
			if (map.get(accountId) == null) {
				map.put(accountId, new ArrayList<GroupEmployee>());
			}

			map.get(accountId).add(groupEmployee);
		}

		return map;
	}
}
