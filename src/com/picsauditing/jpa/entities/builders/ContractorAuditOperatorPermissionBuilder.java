package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ContractorAuditOperatorPermissionBuilder {
    private ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();

    public ContractorAuditOperatorPermission build() {
        return caop;
    }

    public ContractorAuditOperatorPermissionBuilder operator(OperatorAccount operator) {
        caop.setOperator(operator);
        return this;
    }
}
