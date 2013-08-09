package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class FlagArchiveTable extends AbstractTable {

    public static final String Operator = "Operator";
    public static final String Contractor = "Contractor";

    public FlagArchiveTable() {
		super("flag_archive");

        addCreationDate();

        Field flag = new Field("FlagColor","flag", FieldType.FlagColor);
        flag.setTranslationPrefixAndSuffix("FlagColor","");
        addField(flag);
	}

	@Override
	protected void addJoins() {
        ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
        operator.setCategory(FieldCategory.ReportingClientSite);
        operator.setMinimumImportance(FieldImportance.Required);
        addRequiredKey(operator);

        addRequiredKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID")));
    }

}
