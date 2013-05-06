package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.EmployeeCompetency;

public class EmployeeCompetencyTable extends AbstractTable {
    public static final String Employee = "Employee";
    public static final String Competency = "Competency";

    public EmployeeCompetencyTable() {
        super("employee_competency");
        addFields(EmployeeCompetency.class);

    }

    protected void addJoins() {
        addJoinKey(new ReportForeignKey(Employee, new EmployeeTable(), new ReportOnClause("employeeID"))).setMinimumImportance(FieldImportance.Required);
        addJoinKey(new ReportForeignKey(Competency, new OperatorCompetencyTable(), new ReportOnClause("competencyID"))).setMinimumImportance(FieldImportance.Required);
    }
}
