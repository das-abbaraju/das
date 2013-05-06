package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

public class ForcedFlagPercentModel extends AbstractModel {

    public static final String CONTRACTOR_OPERATOR = "ContractorOperator";

    public ForcedFlagPercentModel(Permissions permissions) {
        super(permissions, new ForcedFlagPercentageView());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec spec = new ModelSpec(null, "ForcedFlag");
        ModelSpec opAccount = spec.join(ForcedFlagPercentageView.Operator);
        opAccount.alias = "ContractorOperatorOperator";

        return spec;
    }

    @Override
    public Map<String, Field> getAvailableFields() {
        Map<String, Field> fields = super.getAvailableFields();

        if (permissions.isOperatorCorporate() || permissions.isPicsEmployee()) {
            Field operatorName = fields.get("ContractorOperatorOperatorName".toUpperCase());
            operatorName.setUrl("FacilitiesEdit.action?operator={ContractorOperatorOperatorID}");
        }

        return fields;
    }

    @Override
    public String getWhereClause(List<Filter> filters) {
        // TODO This should be eventually moved into PQB
        if (permissions.isAdmin()) {
            return "";
        }

        if (permissions.isContractor()) {
            return CONTRACTOR_OPERATOR + ".subID = " + permissions.getAccountId();
        }

        if (permissions.isOperator()) {
            return CONTRACTOR_OPERATOR + ".workStatus = 'Y' AND " + CONTRACTOR_OPERATOR + ".genID = " + permissions.getAccountId();
        }

        if (permissions.isCorporate()) {
            return CONTRACTOR_OPERATOR + ".workStatus = 'Y' AND " + CONTRACTOR_OPERATOR + ".genID IN (" + Strings.implodeForDB(permissions.getOperatorChildren()) + ")";
        }

        return "1 = 0";
    }
}