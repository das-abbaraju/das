package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.entities.AccountSkill;

import java.util.List;

public class JobRoleInfo {

    private int id;
    private String name;
    private List<AccountSkill> skills;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AccountSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<AccountSkill> skills) {
        this.skills = skills;
    }
}
