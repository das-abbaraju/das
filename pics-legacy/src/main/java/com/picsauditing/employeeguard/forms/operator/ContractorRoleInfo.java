package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.forms.IdentifierAndNameCompositeForm;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import com.picsauditing.employeeguard.services.models.AccountModel;

import java.util.List;

public class ContractorRoleInfo {

    private AccountModel accountModel;
    private IdentifierAndNameCompositeForm employeeInfo;
    private List<Skill> skillList;

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

    public List<Skill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Skill> skillInfoList) {
        this.skillList = skillInfoList;
    }
}
