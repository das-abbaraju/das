package com.picsauditing.employeeguard.controllers.corporate;

import com.google.gson.Gson;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.IdNameModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SitesListAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;

	public String list() throws NoRightsException {
		if (permissions.isOperator()) {
			jsonString = "[]";
			return JSON_STRING;
		}

		if (!permissions.isCorporate()) {
			throw new NoRightsException("You must be a corporate user");
		}

		jsonString = new Gson().toJson(getIdNameModels());

		return JSON_STRING;
	}

	private List<IdNameModel> getIdNameModels() {
		List<AccountModel> childOperators = accountService.getChildOperators(permissions.getAccountId());

		List<IdNameModel> idNameModels = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(childOperators)) {
			for (AccountModel childOperator : childOperators) {
				idNameModels.add(new IdNameModel.Builder()
						.id(Integer.toString(childOperator.getId()))
						.name(childOperator.getName())
						.build());
			}
		}

		return idNameModels;
	}

}
