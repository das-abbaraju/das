package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;

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
}
