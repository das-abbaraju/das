package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagCriteria;

public class FlagCriteriaBuilder {

    private FlagCriteria flag = new FlagCriteria();

    public FlagCriteriaBuilder id(int id) {
        flag.setId(id);
        return this;
    }

    public FlagCriteria build() {
        return flag;
    }

    public FlagCriteriaBuilder question(AuditQuestion question) {
        flag.setQuestion(question);
        return this;
    }
}
