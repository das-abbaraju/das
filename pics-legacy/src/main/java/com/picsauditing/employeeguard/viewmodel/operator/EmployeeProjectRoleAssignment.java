package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class EmployeeProjectRoleAssignment {

    private final int contractorId;
    private final String contractorName;
    private final int employeeId;
    private final String employeeName;
    private final List<SkillStatus> skillStatuses;

    public EmployeeProjectRoleAssignment(final Builder builder) {
        this.contractorId = builder.contractorId;
        this.contractorName = builder.contractorName;
        this.employeeId = builder.employeeId;
        this.employeeName = builder.employeeName;
        this.skillStatuses = CollectionUtils.isEmpty(builder.skillStatuses) ? Collections.<SkillStatus>emptyList()
                : Collections.unmodifiableList(builder.skillStatuses);
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

    public List<SkillStatus> getSkillStatuses() {
        return skillStatuses;
    }

    public static class Builder {

        private int contractorId;
        private String contractorName;
        private int employeeId;
        private String employeeName;
        private List<SkillStatus> skillStatuses;

        public Builder contractorId(int contractorId) {
            this.contractorId = contractorId;
            return this;
        }

        public Builder contractorName(String contractorName) {
            this.contractorName = contractorName;
            return this;
        }

        public Builder employeeId(int employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeName(String employeeName) {
            this.employeeName = employeeName;
            return this;
        }

        public Builder skillStatuses(List<SkillStatus> skillStatuses) {
            this.skillStatuses = skillStatuses;
            return this;
        }

        public EmployeeProjectRoleAssignment build() {
            return new EmployeeProjectRoleAssignment(this);
        }
    }
}
