package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.services.status.SkillStatus;

import java.util.List;

public class OperatorContractorSkill {

    private int contractorId;
    private String contractorName;
    private int employeeId;
    private String employeeName;
    private List<SkillStatus> skillStatuses;

    public int getContractorId() {
        return contractorId;
    }

    public void setContractorId(int contractorId) {
        this.contractorId = contractorId;
    }

    public String getContractorName() {
        return contractorName;
    }

    public void setContractorName(String contractorName) {
        this.contractorName = contractorName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public List<SkillStatus> getSkillStatuses() {
        return skillStatuses;
    }

    public void setSkillStatuses(List<SkillStatus> skillStatuses) {
        this.skillStatuses = skillStatuses;
    }
}
