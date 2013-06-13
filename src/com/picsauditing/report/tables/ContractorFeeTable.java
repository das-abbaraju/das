package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorFeeTable extends AbstractTable {

    public static final String Contractor = "Contractor";
    public static final String NewFee = "NewFee";
    public static final String CurrentFee = "CurrentFee";

    public ContractorFeeTable() {
        super("contractor_fee");
        addFields(ContractorFee.class);

    }

    protected void addJoins() {
        addRequiredKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID"))).setMinimumImportance(FieldImportance.Average);
        addRequiredKey(new ReportForeignKey(NewFee, new InvoiceFeeTable(), new ReportOnClause("newLevel")));
        addRequiredKey(new ReportForeignKey(CurrentFee, new InvoiceFeeTable(), new ReportOnClause("currentLevel")));
    }
}