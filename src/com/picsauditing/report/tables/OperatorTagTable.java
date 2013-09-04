package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class OperatorTagTable extends AbstractTable {

    public static final String Operator = "Operator";

    public OperatorTagTable() {
        super("operator_tag");
        addFields(OperatorTag.class);
    }

    protected void addJoins() {
        ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID", "id"));
        addOptionalKey(operator);
   }
}