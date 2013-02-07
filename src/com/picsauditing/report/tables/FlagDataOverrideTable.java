package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class FlagDataOverrideTable extends AbstractTable {

	public static final String FlagCriteria = "FlagCriteria";
	public static final String FlagCriteriaContractor = "FlagCriteriaContractor";
	public static final String Operator = "Operator";
	public static final String Contractor = "Contractor";
	public static final String ContractorOperator = "ContractorOperator";

	public FlagDataOverrideTable() {
		super("flag_data_override");
		addFields(FlagData.class);

		Field createdBy = new Field("CreatedBy", "createdBy", FieldType.UserID);
		createdBy.setImportance(FieldImportance.Required);
		addField(createdBy).setCategory(FieldCategory.CompanyStatistics);

		Field creationDate = new Field("CreationDate", "creationDate", FieldType.Date);
		creationDate.setImportance(FieldImportance.Required);
		addField(creationDate).setCategory(FieldCategory.CompanyStatistics);

		Field updateDate = new Field("UpdateDate", "updateDate", FieldType.Date);
		updateDate.setImportance(FieldImportance.Required);
		addField(updateDate).setCategory(FieldCategory.CompanyStatistics);
	}

	public void addJoins() {
		addRequiredKey(new ReportForeignKey(FlagCriteria, new FlagCriteriaTable(), new ReportOnClause("criteriaID")));

		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
		operator.setCategory(FieldCategory.ReportingClientSite);
		operator.setMinimumImportance(FieldImportance.Required);
		addRequiredKey(operator);

		addRequiredKey(new ReportForeignKey(Contractor, new AccountTable(), new ReportOnClause("conID")));

		ReportForeignKey contractorOperator = new ReportForeignKey(ContractorOperator, new ContractorOperatorTable(),
				new ReportOnClause("conID", "subID", ReportOnClause.FromAlias + ".opID = " + ReportOnClause.ToAlias
						+ ".genID"));
		contractorOperator.setMinimumImportance(FieldImportance.Average);
		addJoinKey(contractorOperator);

		// TODO: Placeholder for when we will need to implement the flag criteria contractor
		// ReportForeignKey flagCriteriaContractor = new
		// ReportForeignKey(FlagCriteriaContractor, new
		// FlagCriteriaContractorTable(),
		// new ReportOnClause("conID", "conID", ReportOnClause.FromAlias +
		// ".criteriaID = " + ReportOnClause.ToAlias
		// + ".criteriaID"));
		// flagCriteriaContractor.setMinimumImportance(FieldImportance.Average);
		// addJoinKey(flagCriteriaContractor);
	}
}