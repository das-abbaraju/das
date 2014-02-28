package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.EmploymentInfoModel;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class EmploymentInfoModelFactory {

	public List<EmploymentInfoModel> create(final Set<AccountModel> accountModels, final Map<Integer, Employee> mapAccountIdToEmployee) {
		if (CollectionUtils.isEmpty(accountModels) || MapUtils.isEmpty(mapAccountIdToEmployee)) {
			return Collections.emptyList();
		}

		List<EmploymentInfoModel> employmentInfoModels = new ArrayList<>();
		for (AccountModel accountModel : accountModels) {
			employmentInfoModels.add(create(accountModel, mapAccountIdToEmployee.get(accountModel.getId())));
		}

		return employmentInfoModels;
	}

	public EmploymentInfoModel create(final AccountModel accountModel, final Employee employee) {
		EmploymentInfoModel employmentInfoModel = new EmploymentInfoModel();
		employmentInfoModel.setId(accountModel.getId());
		employmentInfoModel.setName(accountModel.getName());
		employmentInfoModel.setTitle(employee.getPositionName());
		return employmentInfoModel;
	}

}
