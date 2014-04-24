package com.picsauditing.employeeguard.forms.employee;

import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.model.Skill;

import java.util.List;

public class EmployeeProfileForm {

    private EmployeeProfileEditForm personalInformation;
    private List<Skill> skillInfoList;
    private List<AccountModel> employmentInfo;
    private List<CompanyGroupInfo> companyGroupInfoList;

    public EmployeeProfileEditForm getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(EmployeeProfileEditForm personalInformation) {
        this.personalInformation = personalInformation;
    }

    public List<Skill> getSkillInfoList() {
        return skillInfoList;
    }

    public void setSkillInfoList(List<Skill> skillInfoList) {
        this.skillInfoList = skillInfoList;
    }

    public List<AccountModel> getEmploymentInfo() {
        return employmentInfo;
    }

    public void setEmploymentInfo(List<AccountModel> employmentInfo) {
        this.employmentInfo = employmentInfo;
    }

    public List<CompanyGroupInfo> getCompanyGroupInfoList() {
        return companyGroupInfoList;
    }

    public void setCompanyGroupInfoList(List<CompanyGroupInfo> companyGroupInfoList) {
        this.companyGroupInfoList = companyGroupInfoList;
    }

    public String getDisplayName() {
        return personalInformation.getFirstName() + " " + personalInformation.getLastName();
    }
}
