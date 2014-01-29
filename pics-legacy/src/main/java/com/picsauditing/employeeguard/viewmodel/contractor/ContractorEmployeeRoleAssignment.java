package com.picsauditing.employeeguard.viewmodel.contractor;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class ContractorEmployeeRoleAssignment {

    private final boolean assigned;
    private final int employeeId;
    private final String name;
    private final String title;
    private final List<SkillStatus> skillStatuses;

    public ContractorEmployeeRoleAssignment(Builder builder) {
        this.assigned = builder.assigned;
        this.employeeId = builder.employeeId;
        this.name = builder.name;
        this.title = builder.title;
        this.skillStatuses = CollectionUtils.isEmpty(builder.skillStatuses)
                ? Collections.<SkillStatus>emptyList() : Collections.unmodifiableList(builder.skillStatuses);
    }

    public boolean isAssigned() {
        return assigned;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public List<SkillStatus> getSkillStatuses() {
        return skillStatuses;
    }

    public static class Builder {

        private boolean assigned;
        private int employeeId;
        private String name;
        private String title;
        private List<SkillStatus> skillStatuses;

        public Builder assigned(boolean assigned) {
            this.assigned = assigned;
            return this;
        }

        public Builder employeeId(int employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder skillStatuses(List<SkillStatus> skillStatuses) {
            this.skillStatuses = skillStatuses;
            return this;
        }

        public ContractorEmployeeRoleAssignment build() {
            return new ContractorEmployeeRoleAssignment(this);
        }
    }
}
