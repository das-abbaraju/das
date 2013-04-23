package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;

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
}
