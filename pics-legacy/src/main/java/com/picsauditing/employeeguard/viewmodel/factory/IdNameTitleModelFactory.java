package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.IdNameTitleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IdNameTitleModelFactory {

	public List<IdNameTitleModel> create(Employee employee, Map<Integer, AccountModel> accounts) {
		List<IdNameTitleModel> companies = new ArrayList<>();
		if (employee.getProfile() != null) {
			for (Employee otherCompany : employee.getProfile().getEmployees()) {
				companies.add(create(otherCompany, accounts.get(otherCompany.getAccountId())));
			}
		} else {
			companies.add(create(employee, accounts.get(employee.getAccountId())));
		}

		return companies;
	}

	public IdNameTitleModel create(final Employee employee, final AccountModel accountModel) {
		return new IdNameTitleModel.Builder()
				.id(Integer.toString(accountModel.getId()))
				.name(accountModel.getName())
				.title(employee.getPositionName())
				.build();
	}

}
