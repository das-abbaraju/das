package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ReportFilterTable extends AbstractTable {
    public static final String Report = "Report";

    public ReportFilterTable() {
        super("report_filter");
        addFields(Filter.class);

        Field name = new Field("FieldName", "name", FieldType.String);
        name.setImportance(FieldImportance.Required);
        addField(name);
        Field sqlFunction = new Field("SqlFunction", "sqlFunction", FieldType.String);
        addField(sqlFunction);
    }

    protected void addJoins() {
        addRequiredKey(new ReportForeignKey(Report, new ReportTable(), new ReportOnClause("reportID"))).setMinimumImportance(FieldImportance.Average);
    }
}