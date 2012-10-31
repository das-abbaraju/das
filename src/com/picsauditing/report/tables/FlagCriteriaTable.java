package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.FlagCriteria;

public class FlagCriteriaTable extends AbstractTable {

	public static final String Question = "Question";
	public static final String AuditType = "AuditType";
	public static final String OshaType = "OshaType";
	public static final String OshaRateType = "OshaRateType";

	public static final String Operator = "Operator";
	public static final String Contractor = "Contractor";
	public static final String ForcedByUser = "ForcedByUser";

	public FlagCriteriaTable() {
		super("flag_criteria");
		addFields(FlagCriteria.class);
	}

	public void addJoins() {
//		public static final String Question = "Question";
//		public static final String AuditType = "AuditType";
//		public static final String OshaType = "OshaType";
//		public static final String OshaRateType = "OshaRateType";
//
		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("genID"));
		operator.setCategory(FieldCategory.ReportingClientSite);
		operator.setMinimumImportance(FieldImportance.Required);
		addRequiredKey(operator);

		addRequiredKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("subID")));

		ReportForeignKey forcedByUser = new ReportForeignKey(ForcedByUser, new UserTable(), new ReportOnClause("forcedBy"));
		forcedByUser.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(forcedByUser);
	}
}