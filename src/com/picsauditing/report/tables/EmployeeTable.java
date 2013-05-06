package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Employee;

public class EmployeeTable extends AbstractTable {
    public static final String Account = "Account";

    public EmployeeTable() {
        super("employee");
        addFields(Employee.class);
    }

    protected void addJoins() {
        addJoinKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID"))).setMinimumImportance(FieldImportance.Required);

    }
}
