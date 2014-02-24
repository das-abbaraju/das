package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.CompanyEmployeeModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.services.models.AccountModel;

import java.util.List;

public class CompanyModelFactory {

	public CompanyModel create(final AccountModel accountModel, final List<CompanyEmployeeModel> employees) {
		CompanyModel companyModel = new CompanyModel();
		companyModel.setId(accountModel.getId());
		companyModel.setName(accountModel.getName());
		companyModel.setEmployees(employees);
		return companyModel;
	}

}
