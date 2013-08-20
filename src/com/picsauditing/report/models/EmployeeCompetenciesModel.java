package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.Map;

public class EmployeeCompetenciesModel extends AbstractModel {
    public EmployeeCompetenciesModel(Permissions permissions) {
        super(permissions, new EmployeeCompetencyTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec empComp = new ModelSpec(null, "EmployeeCompetency");

        ModelSpec competency = empComp.join(EmployeeCompetencyTable.Competency);
        competency.alias = "Competency";
        competency.minimumImportance = FieldImportance.Average;

        ModelSpec employee = empComp.join(EmployeeCompetencyTable.Employee);
        employee.alias = "Employee";
        employee.minimumImportance = FieldImportance.Average;
        ModelSpec contractor = employee.join(EmployeeTable.Account);
        contractor.alias = "Account";
        contractor.minimumImportance = FieldImportance.Average;

        if (permissions.isOperatorCorporate()) {
            ModelSpec flag = contractor.join(ContractorTable.Flag);
            flag.alias = "ContractorOperator";
            flag.minimumImportance = FieldImportance.Average;
        }

        return empComp;
    }

    @Override
    public Map<String, Field> getAvailableFields() {
        Map<String, Field> fields = super.getAvailableFields();

        Field name = new Field("EmployeeName","CONCAT(Employee.firstName,' ',Employee.lastName)", FieldType.String);
        name.setImportance(FieldImportance.Required);
        fields.put(name.getName().toUpperCase(), name);

        return fields;
    }
}
