package com.picsauditing.employeeguard.forms.employee;

import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.models.AccountModel;

import java.util.List;

public class CompanyGroupInfo {

	private AccountModel accountModel;
	private List<Group> groupInfoList;

	public AccountModel getAccountModel() {
		return accountModel;
	}

	public void setAccountModel(AccountModel accountModel) {
		this.accountModel = accountModel;
	}

	public List<Group> getGroupInfoList() {
		return groupInfoList;
	}

	public void setGroupInfoList(List<Group> groupInfoList) {
		this.groupInfoList = groupInfoList;
	}
}
