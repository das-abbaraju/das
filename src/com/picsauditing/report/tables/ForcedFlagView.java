package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.NetworkLevel;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.search.SelectCase;

public class ForcedFlagView extends AbstractTable {

    public static final String Contractor = "Contractor";
    public static final String ForcedByUser = "ForcedByUser";

	public ForcedFlagView() {
		super("(SELECT gc.subID AS conID, gc.genID AS opID, gc.forceBegin, gc.forceEnd, gc.forcedBy, gc.forceFlag, 'Overall' AS label " +
                "FROM generalcontractors gc WHERE gc.forceFlag IS NOT NULL " +
                "UNION " +
                "SELECT fdo.conID, fdo.opID, fdo.updateDate AS forceBegin, fdo.forceEnd, fdo.updatedBy AS forcedBy, fdo.forceFlag, CONCAT(fc1.id, '.label') AS label " +
                "FROM flag_data_override fdo " +
                "JOIN flag_criteria fc1 ON fdo.criteriaID = fc1.id " +
                "WHERE fdo.forceFlag IS NOT NULL )");

		addField(new Field("ForceBegin", "forceBegin", FieldType.Date));
        addField(new Field("ForceEnd", "forceEnd", FieldType.Date));
        addField(new Field("ForceFlag", "forceFlag", FieldType.FlagColor));
        Field label = new Field("Label", "label", FieldType.String);
        label.setTranslationPrefixAndSuffix("FlagCriteria","");
        addField(label);
	}

	public void addJoins() {
        addRequiredKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID")));

        ReportForeignKey forcedByUser = new ReportForeignKey(ForcedByUser, new UserTable(), new ReportOnClause("forcedBy"));
        forcedByUser.setMinimumImportance(FieldImportance.Average);
        addOptionalKey(forcedByUser);
    }
}