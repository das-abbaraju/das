package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class InvoiceFeeTable extends AbstractTable {

	public InvoiceFeeTable() {
		super("invoice_fee");
		addFields(InvoiceFee.class);
		
		Field feeName;
		feeName = new Field("Name", "id", FieldType.String);
		feeName.setTranslationPrefixAndSuffix("InvoiceFee", "fee");
		feeName.setCategory(FieldCategory.Invoicing);
		feeName.setWidth(200);
		addField(feeName);

	}

	protected void addJoins() {
	}
}
