package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountGroupEmployeeDAO;
import com.picsauditing.employeeguard.entities.AccountGroupEmployee;
import com.picsauditing.employeeguard.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AccountGroupEmployeeService {
	@Autowired
	private AccountGroupEmployeeDAO accountGroupEmployeeDAO;

	public List<AccountGroupEmployee> findByProfile(final Profile profile) {
		return accountGroupEmployeeDAO.findByProfile(profile);
	}

	public Map<Integer, List<AccountGroupEmployee>> getMapOfAccountGroupEmployeeByAccountId(final Profile profile) {
		if (profile == null) {
			return Collections.emptyMap();
		}

		Map<Integer, List<AccountGroupEmployee>> map = new HashMap<>();

		List<AccountGroupEmployee> accountGroupEmployees = findByProfile(profile);
		for (AccountGroupEmployee accountGroupEmployee : accountGroupEmployees) {
			int accountId = accountGroupEmployee.getEmployee().getAccountId();
			if (map.get(accountId) == null) {
				map.put(accountId, new ArrayList<AccountGroupEmployee>());
			}

			map.get(accountId).add(accountGroupEmployee);
		}

		return map;
	}
}
