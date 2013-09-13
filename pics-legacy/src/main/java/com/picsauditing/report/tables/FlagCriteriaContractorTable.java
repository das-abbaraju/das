package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class FlagCriteriaContractorTable extends AbstractTable {

	public static final String FlagCriteria = "FlagCriteria";

	public FlagCriteriaContractorTable() {
		super("flag_criteria_contractor");
		addFields(FlagCriteriaContractor.class);

        Field criteriaID = new Field("CriteriaID", "criteriaID", FieldType.String);
        addField(criteriaID);
	}

	@Override
	protected void addJoins() {
        addOptionalKey(new ReportForeignKey(FlagCriteria, new FlagCriteriaTable(), new ReportOnClause("criteriaID",
				"id")));
	}

}
