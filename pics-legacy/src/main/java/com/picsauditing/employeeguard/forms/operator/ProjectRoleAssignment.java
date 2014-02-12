package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.forms.EntityInfo;

import java.util.List;

public class ProjectRoleAssignment {

    private EntityInfo role;
    private List<ContractorRoleInfo> contractorRoleInfo;

    public EntityInfo getRole() {
        return role;
    }

    public void setRole(EntityInfo role) {
        this.role = role;
    }

    public List<ContractorRoleInfo> getContractorRoleInfo() {
        return contractorRoleInfo;
    }

    public void setContractorRoleInfo(List<ContractorRoleInfo> contractorRoleInfo) {
        this.contractorRoleInfo = contractorRoleInfo;
    }
}
