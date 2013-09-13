package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorTagTable extends AbstractTable {

    public static final String Contractor = "Contractor";
    public static final String OperatorTag = "OperatorTag";

    public ContractorTagTable() {
        super("contractor_tag");
        addFields(ContractorTag.class);
    }

    protected void addJoins() {
        ReportForeignKey contractor = new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID", "id"));
        addRequiredKey(contractor);

        ReportForeignKey flagKey = addOptionalKey(new ReportForeignKey(OperatorTag, new OperatorTagTable(),
                new ReportOnClause("tagID", "id")));
        flagKey.setMinimumImportance(FieldImportance.Low);
   }
}