package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class ContractorEmployeeRoleAssignment {

    private int employeeId;
    private String name;
    private String title;
    private List<SkillStatus> skillStatuses;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SkillStatus> getSkillStatuses() {
        return skillStatuses;
    }

    public void setSkillStatuses(List<SkillStatus> skillStatuses) {
        this.skillStatuses = skillStatuses;
    }
}
