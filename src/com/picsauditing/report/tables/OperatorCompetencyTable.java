package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.OperatorCompetency;

public class OperatorCompetencyTable extends AbstractTable {
    public static final String Operator = "Operator";

    public OperatorCompetencyTable() {
        super("operator_competency");
        addFields(OperatorCompetency.class);
    }

    protected void addJoins() {
        addJoinKey(new ReportForeignKey(Operator, new OperatorTable(), new ReportOnClause("opID"))).setMinimumImportance(FieldImportance.Required);

    }
}
