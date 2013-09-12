package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;

import java.util.Date;

public class AuditDataBuilder {
    private AuditData data = new AuditData();

    public AuditDataBuilder answer(String answer) {
       data.setAnswer(answer);
       return this;
    }

    public AuditDataBuilder question(AuditQuestion question) {
        data.setQuestion(question);
        return this;
    }

    public AuditData build() {
        return data;
    }

    public AuditDataBuilder audit(ContractorAudit audit) {
        data.setAudit(audit);
        return this;
    }

    public AuditDataBuilder creationDate(Date date) {
        data.setCreationDate(date);
        return this;
    }
}
