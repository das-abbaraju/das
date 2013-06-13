package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.OshaStatistics;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorStatisticsView extends AbstractTable {

	public static final String Contractor = "Contractor";

	public ContractorStatisticsView() {
		super("(SELECT ca1.conID, ca1.auditFor, "
				+ " CASE WHEN pd1.questionID = 8812 THEN 'Fatalities' ELSE 'TRIR' END AS rateType, pd1.answer "
				+ " FROM contractor_audit ca1	" + " JOIN pqfdata pd1 ON ca1.id = pd1.auditID "
				+ " WHERE ca1.auditTypeID = 11 " + " AND pd1.questionID IN ("
				+ OshaStatistics.QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR + ","
				+ OshaStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR + "))");
		
		Field statisticFor = new Field("StatisticFor", "auditFor", FieldType.String);
        statisticFor.setImportance(FieldImportance.Average);
		addField(statisticFor);
		Field rateType = new Field("RateType", "rateType", FieldType.String);
        rateType.setImportance(FieldImportance.Average);
		addField(rateType);
		Field rate = new Field("Rate", "answer", FieldType.String);
        rate.setImportance(FieldImportance.Average);
		addField(rate);
	}

	protected void addJoins() {
		ReportForeignKey contractorKey = new ReportForeignKey(Contractor, new AccountTable(), new ReportOnClause(
				"conID", "id"));
		contractorKey.setMinimumImportance(FieldImportance.Required);
		addRequiredKey(contractorKey);
	}
}