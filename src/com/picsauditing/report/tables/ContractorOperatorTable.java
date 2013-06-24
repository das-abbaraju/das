package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.NetworkLevel;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.search.SelectCase;

public class ContractorOperatorTable extends AbstractTable {

	public static final String Operator = "Operator";
	public static final String Contractor = "Contractor";
	public static final String ForcedByUser = "ForcedByUser";
    public static final String ForcedFlagPercent = "ForcedFlagPercent";

	public ContractorOperatorTable() {
		super("generalcontractors");
		addFields(ContractorOperator.class);

        Field creationDate = addCreationDate();
        creationDate.setCategory(FieldCategory.AccountInformation);
        creationDate.setImportance(FieldImportance.Low);

		String networkLevelDatabaseColumn = "";
		{
			String relationshipExist = ReportOnClause.ToAlias + ".id IS NOT NULL";
			String statusActive = ReportOnClause.FromAlias + ".status = 'Active'";
			String statusRequestedPending = ReportOnClause.FromAlias + ".status IN ('Requested','Pending')";
			String statusRequestedPendingDeactivated = ReportOnClause.FromAlias
					+ ".status IN ('Requested','Pending','Deactivated')";
			String workstatusApproved = ReportOnClause.ToAlias + ".workStatus = 'Y'";


			SelectCase caseWorkStatusApproved = new SelectCase();
			caseWorkStatusApproved.addCondition(workstatusApproved, NetworkLevel.Preferred.ordinal());
			caseWorkStatusApproved.setElse(NetworkLevel.Hidden.ordinal());

			SelectCase caseConnectedStatus = new SelectCase();
			caseConnectedStatus.addCondition(statusActive, caseWorkStatusApproved.toString());
			caseConnectedStatus.addCondition(statusRequestedPending, NetworkLevel.Requested.ordinal());
			caseWorkStatusApproved.setElse(NetworkLevel.Other.ordinal());

			SelectCase caseNotConnectedStatus = new SelectCase();
			caseNotConnectedStatus.addCondition(statusActive, NetworkLevel.Member.ordinal());
			caseNotConnectedStatus.addCondition(statusRequestedPendingDeactivated, NetworkLevel.Other.ordinal());

			SelectCase caseRelationshipExists = new SelectCase();
			caseRelationshipExists.addCondition(relationshipExist, caseConnectedStatus.toString());
			caseRelationshipExists.setElse(caseNotConnectedStatus.toString());

			networkLevelDatabaseColumn = caseRelationshipExists.toString();
		}

		Field networkLevel = new Field("NetworkLevel", networkLevelDatabaseColumn, FieldType.NetworkLevel);
		networkLevel.setCategory(FieldCategory.AccountInformation);
		networkLevel.setImportance(FieldImportance.Required);
		networkLevel.setTranslationPrefixAndSuffix("NetworkLevel", "");
		networkLevel.setVisible(false);
		networkLevel.setFilterable(false);
		addField(networkLevel);
	}

	public void addJoins() {
		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("genID"));
		operator.setCategory(FieldCategory.ReportingClientSite);
		addRequiredKey(operator);

		addRequiredKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("subID")));

		ReportForeignKey forcedByUser = new ReportForeignKey(ForcedByUser, new UserTable(), new ReportOnClause("forcedBy"));
		forcedByUser.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(forcedByUser);

        ReportForeignKey forcedFlagPercent = new ReportForeignKey(ForcedFlagPercent, new ForcedFlagPercentageView(), new ReportOnClause("genID","opID"));
        forcedFlagPercent.setMinimumImportance(FieldImportance.Average);
        addOptionalKey(forcedFlagPercent);
	}
}