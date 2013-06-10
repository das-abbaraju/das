package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

import java.util.List;
import java.util.Map;

public class ForcedFlagsModel extends AbstractModel {

	public static final String CONTRACTOR_OPERATOR = "ContractorOperator";

	public ForcedFlagsModel(Permissions permissions) {
		super(permissions, new ForcedFlagView());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "ContractorFlag");

		ModelSpec contractor = spec.join(ForcedFlagView.Contractor);
        contractor.minimumImportance = FieldImportance.Required;
		ModelSpec account = contractor.join(ContractorTable.Account);
        account.alias = "Account";
        account.minimumImportance = FieldImportance.Required;

        ModelSpec forcedBy = spec.join(ForcedFlagView.ForcedByUser);
        forcedBy.alias = "ContractorOperatorForcedByUser";
        forcedBy.minimumImportance = FieldImportance.Required;

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

		return fields;
	}
}