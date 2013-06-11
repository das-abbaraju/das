package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class FlagDataTable extends AbstractTable {

	public static final String FlagCriteria = "FlagCriteria";
	public static final String FlagCriteriaContractor = "FlagCriteriaContractor";
	public static final String OperatorCriteria = "OperatorCriteria";
	public static final String Operator = "Operator";
	public static final String Contractor = "Contractor";
	public static final String ContractorOperator = "ContractorOperator";
	public static final String Override = "Override";

	public FlagDataTable() {
		super("flag_data");
		addFields(FlagData.class);


        Field lastModified = addUpdateDate();
        lastModified.setCategory(FieldCategory.CompanyStatistics);
        addField(lastModified);
	}

	public void addJoins() {
		addRequiredKey(new ReportForeignKey(FlagCriteria, new FlagCriteriaTable(), new ReportOnClause("criteriaID")));

		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
		operator.setCategory(FieldCategory.ReportingClientSite);
		operator.setMinimumImportance(FieldImportance.Required);
		addRequiredKey(operator);

		ReportForeignKey operatorCriteria = new ReportForeignKey(OperatorCriteria, new FlagCriteriaOperatorTable(),
				new ReportOnClause("criteriaID", "criteriaID", ReportOnClause.FromAlias + ".opID = "
						+ ReportOnClause.ToAlias + ".opID"));
		operatorCriteria.setCategory(FieldCategory.ReportingClientSite);
		operatorCriteria.setMinimumImportance(FieldImportance.Required);
		addRequiredKey(operatorCriteria);

		addRequiredKey(new ReportForeignKey(Contractor, new AccountTable(), new ReportOnClause("conID")));

		ReportForeignKey contractorOperator = new ReportForeignKey(ContractorOperator, new ContractorOperatorTable(),
				new ReportOnClause("conID", "subID", ReportOnClause.FromAlias + ".opID = " + ReportOnClause.ToAlias
						+ ".genID"));
		contractorOperator.setMinimumImportance(FieldImportance.Average);
		addJoinKey(contractorOperator);

		ReportForeignKey override = new ReportForeignKey(Override, new FlagDataOverrideTable(), new ReportOnClause(
				"conID", "conID", ReportOnClause.FromAlias + ".opID = " + ReportOnClause.ToAlias + ".opID AND "
						+ ReportOnClause.FromAlias + ".criteriaID = " + ReportOnClause.ToAlias + ".criteriaID"));
		override.setMinimumImportance(FieldImportance.Required);
		addOptionalKey(override);

		// TODO: Placeholder for when we will need to implement the flag
		// criteria contractor
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