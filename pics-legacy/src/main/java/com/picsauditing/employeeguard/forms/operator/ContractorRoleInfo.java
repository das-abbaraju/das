package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.forms.IdentifierAndNameCompositeForm;
import com.picsauditing.employeeguard.viewmodel.SkillInfo;
import com.picsauditing.employeeguard.services.models.AccountModel;

import java.util.List;

public class ContractorRoleInfo {

    private AccountModel accountModel;
    private IdentifierAndNameCompositeForm employeeInfo;
    private List<SkillInfo> skillInfoList;

    public AccountModel getAccountModel() {
        return accountModel;
    }

    public void setAccountModel(AccountModel accountModel) {
        this.accountModel = accountModel;
    }

    public IdentifierAndNameCompositeForm getEmployeeInfo() {
        return employeeInfo;
    }

    public void setEmployeeInfo(IdentifierAndNameCompositeForm employeeInfo) {
        this.employeeInfo = employeeInfo;
    }

    public List<SkillInfo> getSkillInfoList() {
        return skillInfoList;
    }

    public void setSkillInfoList(List<SkillInfo> skillInfoList) {
        this.skillInfoList = skillInfoList;
    }
}
