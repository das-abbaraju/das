package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import org.springframework.web.util.UriComponentsBuilder;

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
}
