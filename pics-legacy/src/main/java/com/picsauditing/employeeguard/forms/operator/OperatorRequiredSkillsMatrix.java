package com.picsauditing.employeeguard.forms.operator;

import java.util.List;

public class OperatorRequiredSkillsMatrix {

    private List<String> skillNames;
    private List<RoleInfo> roles;
    private List<OperatorContractorSkill> contractorSkills;

    public List<String> getSkillNames() {
        return skillNames;
    }

    public void setSkillNames(List<String> skillNames) {
        this.skillNames = skillNames;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }

    public List<OperatorContractorSkill> getContractorSkills() {
        return contractorSkills;
    }

    public void setContractorSkills(List<OperatorContractorSkill> contractorSkills) {
        this.contractorSkills = contractorSkills;
    }
}
