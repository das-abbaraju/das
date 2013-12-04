package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.EmailTemplate;

public class EmailTemplateTable extends AbstractTable {

    public EmailTemplateTable() {
        super("email_template");
        addFields(EmailTemplate.class);
    }

    protected void addJoins() {
    }
}