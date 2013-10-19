package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.forms.IdentifierAndNameCompositeForm;

import java.util.List;

public class ProjectRoleAssignment {

    private IdentifierAndNameCompositeForm role;
    private List<ContractorRoleInfo> contractorRoleInfo;

    public IdentifierAndNameCompositeForm getRole() {
        return role;
    }

    public void setRole(IdentifierAndNameCompositeForm role) {
        this.role = role;
    }

    public List<ContractorRoleInfo> getContractorRoleInfo() {
        return contractorRoleInfo;
    }

    public void setContractorRoleInfo(List<ContractorRoleInfo> contractorRoleInfo) {
        this.contractorRoleInfo = contractorRoleInfo;
    }
}
