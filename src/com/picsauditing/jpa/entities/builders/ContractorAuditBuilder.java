package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;

public class ContractorAuditBuilder {

    private ContractorAudit audit = new ContractorAudit();

    public ContractorAuditBuilder data(AuditData data) {
        audit.getData().add(data);
        data.setAudit(audit);
        return this;
    }

    public ContractorAudit build() {
        return audit;
    }

    public ContractorAuditBuilder auditType(AuditType auditType) {
        audit.setAuditType(auditType);
        return this;
    }

    public ContractorAuditBuilder contractor(ContractorAccount contractor) {
        audit.setContractorAccount(contractor);
        return this;
    }

    public ContractorAuditBuilder cao(ContractorAuditOperator operator) {
        if (audit.getOperators() == null) {
            audit.setOperators(new ArrayList<ContractorAuditOperator>());
        }
        audit.getOperators().add(operator);
        return this;
    }

    public ContractorAuditBuilder expirationDate(Date date) {
        audit.setExpiresDate(date);
        return this;
    }

    public ContractorAuditBuilder auditFor(String auditFor) {
        audit.setAuditFor(auditFor);
        return this;
    }
}
