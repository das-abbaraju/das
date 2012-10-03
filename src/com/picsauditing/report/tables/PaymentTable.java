package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class PaymentTable extends AbstractTable {
	public PaymentTable() {
		super("invoice");
		addFields(Payment.class);
		
		Field creationDate = new Field("CreationDate", "creationDate", FieldType.Date);
		creationDate.setImportance(FieldImportance.Required);
		addField(creationDate);
	}

	protected void addJoins() {
	}
}