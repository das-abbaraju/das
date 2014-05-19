package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.IdNameTitleModel;

import java.util.List;

public class ContractorRoleInfo implements Comparable<ContractorRoleInfo> {

	private final AccountModel accountModel;
	private final IdNameTitleModel employeeInfo;
	private final List<SkillStatus> statuses;

	public ContractorRoleInfo(Builder builder) {
		this.accountModel = builder.accountModel;
		this.employeeInfo = builder.employeeInfo;
		this.statuses = builder.statuses;
	}

	public AccountModel getAccountModel() {
		return accountModel;
	}

	public IdNameTitleModel getEmployeeInfo() {
		return employeeInfo;
	}

	public List<SkillStatus> getStatuses() {
		return statuses;
	}

	@Override
	public int compareTo(ContractorRoleInfo that) {
		if (this.accountModel.equals(that.accountModel)) {
			return this.employeeInfo.compareTo(that.employeeInfo);
		}

		return this.accountModel.compareTo(that.accountModel);
	}

	public static class Builder {
		private AccountModel accountModel;
		private IdNameTitleModel employeeInfo;
		private List<SkillStatus> statuses;

		public Builder accountModel(AccountModel accountModel) {
			this.accountModel = accountModel;
			return this;
		}

		public Builder employeeInfo(IdNameTitleModel employeeInfo) {
			this.employeeInfo = employeeInfo;
			return this;
		}

		public Builder statuses(List<SkillStatus> statuses) {
			this.statuses = statuses;
			return this;
		}

		public ContractorRoleInfo build() {
			return new ContractorRoleInfo(this);
		}
	}
}
