package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.OperatorAccount;

import java.util.ArrayList;

public class ContractorAuditOperatorBuilder {
    private ContractorAuditOperator cao = new ContractorAuditOperator();
    public ContractorAuditOperatorBuilder operator(OperatorAccount operator) {
       cao.setOperator(operator);
       return this;
    }

    public ContractorAuditOperator build() {
        return cao;
    }

    public ContractorAuditOperatorBuilder visible() {
        cao.setVisible(true);
        return this;
    }

    public ContractorAuditOperatorBuilder caop(ContractorAuditOperatorPermission caop) {
        if (cao.getCaoPermissions() == null) {
            cao.setCaoPermissions(new ArrayList<ContractorAuditOperatorPermission>());
        }
        cao.getCaoPermissions().add(caop);
        return this;
    }

    public ContractorAuditOperatorBuilder status(AuditStatus auditStatus) {
        cao.setStatus(auditStatus);
        return this;
    }
}
