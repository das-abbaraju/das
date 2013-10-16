package com.picsauditing.employeeguard.forms.employee;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.services.models.AccountModel;

import java.util.List;

public class CompanyGroupInfo {

    private AccountModel accountModel;
    private List<AccountGroup> groupInfoList;

    public AccountModel getAccountModel() {
        return accountModel;
    }

    public void setAccountModel(AccountModel accountModel) {
        this.accountModel = accountModel;
    }

    public List<AccountGroup> getGroupInfoList() {
        return groupInfoList;
    }

    public void setGroupInfoList(List<AccountGroup> groupInfoList) {
        this.groupInfoList = groupInfoList;
    }
}
