package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class EmailQueueTable extends AbstractTable {

    public static final String Contractor = "Contractor";
    public static final String Template = "Template";

    public EmailQueueTable() {
        super("email_queue");
        addFields(EmailQueue.class);
        addCreationDate();
    }

    protected void addJoins() {
        addRequiredKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID", "id")));

        ReportForeignKey contractorOperator = addRequiredKey(new ReportForeignKey(Template, new EmailTemplateTable(),
                new ReportOnClause("templateID", "id")));
    }
}