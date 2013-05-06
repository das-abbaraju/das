package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.*;

public class EmployeeCompetencyModel extends AbstractModel {
    public EmployeeCompetencyModel(Permissions permissions) {
        super(permissions, new EmployeeCompetencyTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec empComp = new ModelSpec(null, "EmployeeCompetency");

        empComp.join(EmployeeCompetencyTable.Competency);
        ModelSpec emp = empComp.join(EmployeeCompetencyTable.Employee);
        emp.join(EmployeeTable.Account);

        return empComp;
    }

}
