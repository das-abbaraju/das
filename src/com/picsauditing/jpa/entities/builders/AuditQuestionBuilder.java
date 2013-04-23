package com.picsauditing.jpa.entities.builders;

import com.picsauditing.actions.rules.AuditRuleActionSupport;
import com.picsauditing.jpa.entities.AuditQuestion;

public class AuditQuestionBuilder {
    private AuditQuestion question = new AuditQuestion();

    public AuditQuestionBuilder id(int id) {
       question.setId(id);
        return this;

    }

    public AuditQuestion build() {
        return question;
    }
}
