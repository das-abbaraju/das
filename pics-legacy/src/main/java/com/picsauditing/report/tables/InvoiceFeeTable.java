package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class InvoiceFeeTable extends AbstractTable {
    public static final String Country = "Country";

	public InvoiceFeeTable() {
		super("invoice_fee");
		addFields(InvoiceFee.class);

        Field feeID = new Field("Id","id", FieldType.Integer);
        feeID.setVisible(false);
        feeID.setFilterable(false);
        feeID.setImportance(FieldImportance.Required);
        addField(feeID);

        Field feeName = new Field("Name", "id", FieldType.String);
		feeName.setTranslationPrefixAndSuffix("InvoiceFee", "fee");
        feeName.setDrillDownField(feeID.getName());
        feeName.setImportance(FieldImportance.Required);
		feeName.setWidth(200);
		addField(feeName);

	}

	protected void addJoins() {
        ReportForeignKey country = new ReportForeignKey(Country, new InvoiceFeeCountryTable(), new ReportOnClause("id", "feeID"));
        country.setMinimumImportance(FieldImportance.Low);
        addOptionalKey(country);
	}
}
