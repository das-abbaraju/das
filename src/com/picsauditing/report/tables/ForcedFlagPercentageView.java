package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ForcedFlagPercentageView extends AbstractTable {

	public ForcedFlagPercentageView() {
		super("(SELECT gc.genID AS opID, (ROUND(COUNT(DISTINCT CASE WHEN gc.forceFlag IS NOT NULL OR fdo.forceFlag IS NOT NULL THEN gc.`subID` ELSE NULL END)/COUNT(DISTINCT subID)*100,2)) AS percentForced \n" +
                "FROM generalcontractors gc \n" +
                "LEFT JOIN flag_data_override fdo ON fdo.`conID` = gc.`subID` AND fdo.`opID` = gc.`genID` \n" +
                "GROUP BY gc.genID)");

		addField(new Field("Percent", "percentForced", FieldType.Number));
	}

	public void addJoins() {
    }
}