package com.picsauditing.report.models;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.SupplierDiversity;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.search.SelectCase;
import com.picsauditing.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmailsModel extends AbstractModel {

	public EmailsModel(Permissions permissions) {
		super(permissions, new EmailQueueTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec email = new ModelSpec(null, "Email");
        ModelSpec contractor = email.join(EmailQueueTable.Contractor);
        contractor.join(ContractorTable.Account).minimumImportance = FieldImportance.Required;

		email.join(EmailQueueTable.Template);

		return email;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);

        String where = permissionQueryBuilder.buildWhereClause();

        if (permissions.isOperatorCorporate())
            where = " AND email.createdBy = " + permissions.getUserId();

        if (!permissions.isPicsEmployee()) {
            where += " AND 1 = 0";
        }
        return where;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("EmailContractorAccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={EmailContractorAccountID}");

       return fields;
	}
}
