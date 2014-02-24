package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.CompanyEmployeeModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CompanyModelFactory {

	public List<CompanyModel> create(final Map<Integer, AccountModel> accountMap, final Map<Integer, List<CompanyEmployeeModel>> employees) {
		if (MapUtils.isEmpty(accountMap) || MapUtils.isEmpty(employees)) {
			return Collections.emptyList();
		}

		List<CompanyModel> companies = new ArrayList<>();
		for (int accountId : accountMap.keySet()) {
			companies.add(create(accountMap.get(accountId), employees.get(accountId)));
		}

		return companies;
	}

	public CompanyModel create(final AccountModel accountModel, final List<CompanyEmployeeModel> employees) {
		CompanyModel companyModel = new CompanyModel();
		companyModel.setId(accountModel.getId());
		companyModel.setName(accountModel.getName());
		companyModel.setEmployees(employees);
		return companyModel;
	}

}
