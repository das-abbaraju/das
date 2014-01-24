package com.picsauditing.employeeguard.forms.employee;

import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;

import java.util.List;

public class EmployeeProfileForm {

    private EmployeeProfileEditForm personalInformation;
    private List<SkillInfo> skillInfoList;
    private List<AccountModel> employmentInfo;
    private List<CompanyGroupInfo> companyGroupInfoList;

    public EmployeeProfileEditForm getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(EmployeeProfileEditForm personalInformation) {
        this.personalInformation = personalInformation;
    }

    public List<SkillInfo> getSkillInfoList() {
        return skillInfoList;
    }

    public void setSkillInfoList(List<SkillInfo> skillInfoList) {
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
