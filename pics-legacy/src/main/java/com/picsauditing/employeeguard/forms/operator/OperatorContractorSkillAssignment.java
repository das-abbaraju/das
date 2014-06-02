package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.services.status.SkillStatus;

public class OperatorContractorSkillAssignment {

    private int contractorId;
    private String contractorName;
    private int employeeId;
    private String employeeName;
    private SkillStatus skillStatus;

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

    public SkillStatus getSkillStatus() {
        return skillStatus;
    }

    public void setSkillStatus(SkillStatus skillStatus) {
        this.skillStatus = skillStatus;
    }
}
