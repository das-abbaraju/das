package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class ContractorTagsModel extends AbstractModel {

	public ContractorTagsModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec contractor = new ModelSpec(null, "Contractor");

        contractor.join(ContractorTable.Account).minimumImportance = FieldImportance.Average;

        ModelSpec conTag = contractor.join(ContractorTable.SingleTag);
        ModelSpec opTag = conTag.join(ContractorTagTable.OperatorTag);
        ModelSpec operator = opTag.join(OperatorTagTable.Operator);
        operator.minimumImportance = FieldImportance.Required;

        if (permissions.isOperatorCorporate()) {
            ModelSpec flag = contractor.join(ContractorTable.Flag);
            flag.join(ContractorOperatorTable.ForcedByUser);
        }

        return contractor;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);
        permissionQueryBuilder.setAccountAlias("ContractorAccount");
		permissionQueryBuilder.setContractorOperatorAlias("ContractorFlag");

        Filter accountStatusFilter = getValidAccountStatusFilter(filters);

		if (accountStatusFilter != null) {
			for (String filterValue : accountStatusFilter.getValues()) {
				AccountStatus filterStatus = AccountStatus.valueOf(filterValue);
				if (filterStatus.isVisibleTo(permissions)) {
					permissionQueryBuilder.addVisibleStatus(filterStatus);
				}
			}
		}

        String where = permissionQueryBuilder.buildWhereClause();

        if (permissions.isOperatorCorporate()) {
            where += " AND (ContractorSingleTagOperatorTag.opID IN (" + permissionQueryBuilder.getOperatorIDs() + "))";
        }

        return where;
	}

	private Filter getValidAccountStatusFilter(List<Filter> filters) {
		for (Filter filter : filters) {
			if (filter.getName().equalsIgnoreCase("ContractorAccountStatus") && filter.isValid()) {
				return filter;
			}
		}
		return null;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("ContractorAccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={ContractorAccountID}");

        if (permissions.isOperatorCorporate()) {
            Field flagColor = fields.get("ContractorFlagFlagColor".toUpperCase());
            flagColor.setUrl("ContractorFlag.action?id={ContractorAccountID}");
        }

        Field reportingClient = new Field("ContractorWorksForReportingClient","ContractorAccount.id",FieldType.Operator);
        reportingClient.setVisible(false);
        reportingClient.setPrefixValue("SELECT co.subID " +
                "FROM generalcontractors co " +
                "JOIN operators o ON o.id = co.genID " +
                "WHERE o.reportingID IN ");
        reportingClient.setSuffixValue("");
        fields.put(reportingClient.getName().toUpperCase(), reportingClient);

        return fields;
	}
}