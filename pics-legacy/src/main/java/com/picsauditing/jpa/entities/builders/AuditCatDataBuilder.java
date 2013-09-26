package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditCatData;

public class AuditCatDataBuilder {
    private AuditCatData auditCatData = new AuditCatData();

    public AuditCatData build(){
        return auditCatData;
    }

    public AuditCatDataBuilder numberRequired(int numberRequired) {
        auditCatData.setNumRequired(numberRequired);
        return this;
    }

    public AuditCatDataBuilder numberAnswered(int numberAnswered) {
        auditCatData.setNumAnswered(numberAnswered);
        return this;
    }

    public AuditCatDataBuilder numberVerified(int numberVerified) {
        auditCatData.setNumVerified(numberVerified);
        return this;
    }
}