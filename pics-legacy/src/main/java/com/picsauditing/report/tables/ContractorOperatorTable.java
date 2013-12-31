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
    public static final String RequestedByUser = "RequestedByUser";

	public ContractorOperatorTable() {
		super("contractor_operator");
		addFields(ContractorOperator.class);

        Field creationDate = addCreationDate();
        creationDate.setImportance(FieldImportance.Average);

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
		networkLevel.setImportance(FieldImportance.Required);
		networkLevel.setTranslationPrefixAndSuffix("NetworkLevel", "");
		networkLevel.setVisible(false);
		networkLevel.setFilterable(false);
		addField(networkLevel);

        // TODO: We should find a way to attach this to ContractorTagView instead of here
        Field contractorOperatorTag = new Field("Tag", "(SELECT GROUP_CONCAT(o.tag ORDER BY o.tag SEPARATOR ', ') FROM contractor_tag c " +
                " JOIN operator_tag o ON c.tagID = o.id AND o.active = 1 " +
                " WHERE " + ReportOnClause.ToAlias + ".conID = c.conID AND o.opID IN (SELECT f.corporateID FROM facilities f WHERE f.opID = "
                + ReportOnClause.ToAlias + ".opID UNION SELECT " + ReportOnClause.ToAlias + ".opID))", FieldType.String);
        contractorOperatorTag.setFilterable(false);
        contractorOperatorTag.setWidth(300);
        contractorOperatorTag.setImportance(FieldImportance.Required);
        addField(contractorOperatorTag);

        Field contractorFlagOverride = new Field("FlagIsForced", "(" + ReportOnClause.ToAlias + ".forceEnd IS NOT NULL OR " +
                " EXISTS(SELECT * FROM flag_data_override fdo " +
                " WHERE fdo.conID = " + ReportOnClause.ToAlias + ".conID " +
                " AND fdo.opID = " + ReportOnClause.ToAlias + ".opID " +
                " AND fdo.forceEnd IS NOT NULL))", FieldType.Boolean);
        addField(contractorFlagOverride);

        addField(ORACLE_FIELD);
        addField(SAP_FIELD);
        addField(VENDOR_FIELD);
	}

    private static final String contractorQuery =
            "EXISTS(SELECT * FROM contractor_operator_number con WHERE " + ReportOnClause.ToAlias +
            ".conID = con.conID AND con.opID IN (SELECT f.opID FROM facilities f WHERE f.corporateID = " +
            ReportOnClause.ToAlias + ".opID UNION SELECT " + ReportOnClause.ToAlias + ".opID) AND con.type = '%s')";

    private static Field fromContractorQuery(String fieldName, String conType) {
        String query = String.format(contractorQuery, conType);
        Field field = new Field(fieldName, query, FieldType.Boolean);
        field.setImportance(FieldImportance.Required);
        return field;
    }

    private static final Field ORACLE_FIELD = fromContractorQuery("HasOracleNumber", "Oracle");
    private static final Field SAP_FIELD = fromContractorQuery("HasSAPNumber", "SAP");
    private static final Field VENDOR_FIELD = fromContractorQuery("HasVendorNumber", "Vendor");

	public void addJoins() {
		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
		addRequiredKey(operator);

		addRequiredKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID")));

		ReportForeignKey forcedByUser = new ReportForeignKey(ForcedByUser, new UserTable(), new ReportOnClause("forcedBy"));
		forcedByUser.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(forcedByUser);

        ReportForeignKey requestedByUser = new ReportForeignKey(RequestedByUser, new UserTable(), new ReportOnClause("requestedByUserID"));
        addOptionalKey(requestedByUser);
	}
}