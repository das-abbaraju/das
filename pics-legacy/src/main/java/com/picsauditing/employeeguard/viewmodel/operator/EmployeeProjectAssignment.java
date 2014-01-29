package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class EmployeeProjectAssignment {

    private final int contractorId;
    private final String contractorName;
    private final int employeeId;
    private final String employeeName;
    private final SkillStatus skillStatusRollUp;

    public EmployeeProjectAssignment(final Builder builder) {
        this.contractorId = builder.contractorId;
        this.contractorName = builder.contractorName;
        this.employeeId = builder.employeeId;
        this.employeeName = builder.employeeName;
        this.skillStatusRollUp = builder.skillStatusRollUp;
    }

    public int getContractorId() {
        return contractorId;
    }

    public String getContractorName() {
        return contractorName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public SkillStatus getSkillStatusRollUp() {
        return skillStatusRollUp;
    }

    public static class Builder {
        private int contractorId;
        private String contractorName;
        private int employeeId;
        private String employeeName;
        private SkillStatus skillStatusRollUp;

        public Builder contractorId(final int contractorId) {
            this.contractorId = contractorId;
            return this;
        }

        public Builder contractorName(final String contractorName) {
            this.contractorName = contractorName;
            return this;
        }

        public Builder employeeId(final int employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeName(final String employeeName) {
            this.employeeName = employeeName;
            return this;
        }

        public Builder skillStatusRollUp(final SkillStatus skillStatusRollUp) {
            this.skillStatusRollUp = skillStatusRollUp;
            return this;
        }

        public EmployeeProjectAssignment build() {
            return new EmployeeProjectAssignment(this);
        }
    }
}
