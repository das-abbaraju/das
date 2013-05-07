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
        ModelSpec employee = empComp.join(EmployeeCompetencyTable.Employee);
        ModelSpec contractor = employee.join(EmployeeTable.Account);

        if (permissions.isOperatorCorporate()) {
            ModelSpec flag = contractor.join(ContractorTable.Flag);
            flag.alias = "ContractorOperator";
            flag.minimumImportance = FieldImportance.Average;
            flag.category = FieldCategory.AccountInformation;
        }

        return empComp;
    }

}
