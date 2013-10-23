package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ForcedFlagPercentageView extends AbstractTable {

    public static final String Operator = "Operator";
    public static final String Contractor = "Contractor";

    public ForcedFlagPercentageView() {
		super("(SELECT co.opID AS opID, co.conID as conID, co.workStatus, (ROUND(COUNT(DISTINCT CASE WHEN co.forceFlag IS NOT NULL OR fdo.forceFlag IS NOT NULL THEN co.`conID` ELSE NULL END)/COUNT(DISTINCT conID)*100,2)) AS percentForced \n" +
                "FROM contractor_operator co \n" +
                "JOIN accounts a ON co.conID = a.id AND a.status = 'Active'\n" +
                "LEFT JOIN flag_data_override fdo ON fdo.`conID` = co.`conID` AND fdo.`opID` = co.`opID` \n" +
                "GROUP BY co.opID)");

        Field percent = new Field("Percent", "percentForced", FieldType.Number);
        percent.setImportance(FieldImportance.Average);
        addField(percent);
	}

	public void addJoins() {
        ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
        addRequiredKey(operator);
    }
}