package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.*;

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

    public ContractorAuditOperatorBuilder invisible() {
        cao.setVisible(false);
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

    public ContractorAuditOperatorBuilder audit(ContractorAudit audit) {
        cao.setAudit(audit);
        return this;
    }

    public ContractorAuditOperatorBuilder caop() {
        return caop(cao.getOperator());
    }

    public ContractorAuditOperatorBuilder caop(OperatorAccount... operators) {
       for (OperatorAccount operator: operators) {
            caop(operator);
        }
       return this;
    }

    public ContractorAuditOperatorBuilder caop(OperatorAccount operator) {
        if (cao.getCaoPermissions() == null) {
            cao.setCaoPermissions(new ArrayList<ContractorAuditOperatorPermission>());
        }
        cao.getCaoPermissions().add(
                ContractorAuditOperatorPermission.builder()
                        .cao(cao)
                        .previousCao(cao)
                        .operator(operator)
                        .build()
        );
        return this;
    }

    public ContractorAuditOperatorBuilder id(int id) {
        cao.setId(id);
        return this;
    }

    public ContractorAuditOperatorBuilder percentComplete(int percentComplete) {
        cao.setPercentComplete(percentComplete);
        return this;
    }

    public ContractorAuditOperatorBuilder percentVerified(int percentVerified) {
        cao.setPercentVerified(percentVerified);
        return this;
    }
}
