package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ForcedFlagPercentageView extends AbstractTable {

    public static final String Operator = "Operator";
    public static final String Contractor = "Contractor";

    public ForcedFlagPercentageView() {
		super("(SELECT gc.genID AS opID, gc.subID as conID, gc.workStatus, (ROUND(COUNT(DISTINCT CASE WHEN gc.forceFlag IS NOT NULL OR fdo.forceFlag IS NOT NULL THEN gc.`subID` ELSE NULL END)/COUNT(DISTINCT subID)*100,2)) AS percentForced \n" +
                "FROM generalcontractors gc \n" +
                "JOIN accounts a ON gc.subID = a.id AND a.status = 'Active'\n" +
                "LEFT JOIN flag_data_override fdo ON fdo.`conID` = gc.`subID` AND fdo.`opID` = gc.`genID` \n" +
                "GROUP BY gc.genID)");

        Field percent = new Field("Percent", "percentForced", FieldType.Number);
        percent.setImportance(FieldImportance.Average);
        addField(percent);
	}

	public void addJoins() {
        ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
        operator.setCategory(FieldCategory.ReportingClientSite);
        operator.setMinimumImportance(FieldImportance.Required);
        addRequiredKey(operator);
    }
}