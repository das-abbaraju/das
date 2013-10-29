package com.picsauditing.employeeguard.forms.employee;

import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class CompanySkillInfo {

    private AccountModel accountModel;
    private List<SkillInfo> completedSkills;
	private List<SkillInfo> expiredSkills;
	private List<SkillInfo> aboutToExpireSkills;

    public AccountModel getAccountModel() {
        return accountModel;
    }

    public void setAccountModel(AccountModel accountModel) {
        this.accountModel = accountModel;
    }

    public List<SkillInfo> getCompletedSkills() {
        return completedSkills;
    }

    public void setCompletedSkills(List<SkillInfo> completedSkills) {
        this.completedSkills = completedSkills;
    }

    public List<SkillInfo> getExpiredSkills() {
        return expiredSkills;
    }

    public void setExpiredSkills(List<SkillInfo> expiredSkills) {
        this.expiredSkills = expiredSkills;
    }

    public List<SkillInfo> getAboutToExpireSkills() {
        return aboutToExpireSkills;
    }

    public void setAboutToExpireSkills(List<SkillInfo> aboutToExpireSkills) {
        this.aboutToExpireSkills = aboutToExpireSkills;
    }

	public boolean isHasSkills() {
		return !CollectionUtils.isEmpty(completedSkills) || !CollectionUtils.isEmpty(expiredSkills) || !CollectionUtils.isEmpty(aboutToExpireSkills);
	}
}