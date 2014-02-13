package com.picsauditing.report.tables;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorTradeSafetySensitivityView extends AbstractTable {

	public ContractorTradeSafetySensitivityView() {
		super("vwaccounttradesafetysensitivity");

        Field field = new Field("TradeSafetySensitive", "tradeSafetySensitive", FieldType.Boolean);
        field.setImportance(FieldImportance.Required);
        field.requirePermission(OpPerms.RiskRank);
        addField(field);
	}

	public void addJoins() {
	}
}