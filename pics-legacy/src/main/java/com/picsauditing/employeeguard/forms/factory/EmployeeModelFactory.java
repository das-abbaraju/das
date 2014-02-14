package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeModel;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeModelFactory {

	public EmployeeModel create(final Employee employee, final Map<Integer, AccountModel> contractors) {
		if (employee == null || MapUtils.isEmpty(contractors)) {
			return null;
		}

		String name = employee.getName();
		List<String> companyNames = new ArrayList<>();

		if (employee.getProfile() != null) {
			Profile profile = employee.getProfile();
			name = profile.getName();

			for (Employee otherContractor : profile.getEmployees()) {
				companyNames.add(getContractorName(contractors, otherContractor.getAccountId()));
			}
		} else {
			companyNames.add(getContractorName(contractors, employee.getAccountId()));
		}

		return new EmployeeModel.Builder()
				.id(employee.getId())
				.name(name)
				.title(employee.getPositionName())
				.companyNames(companyNames)
				.build();
	}

	private String getContractorName(Map<Integer, AccountModel> contractors, int contractorId) {
		AccountModel contractor = contractors.get(contractorId);

		if (contractor == null) {
			return null;
		}

		return contractor.getName();
	}
}
