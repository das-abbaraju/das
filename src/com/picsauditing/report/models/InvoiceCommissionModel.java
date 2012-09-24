package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.InvoiceCommissionTable;

public class InvoiceCommissionModel extends AbstractModel {

	public InvoiceCommissionModel(Permissions permissions) {
		super(permissions, new InvoiceCommissionTable());
		// rootTable.getTable("account").includeRequiredAndAverageColumns();
		// rootTable.removeJoin("accountContact");
		// rootTable.getTable("contractor").includeRequiredAndAverageColumns();
		// rootTable.removeJoin("contractorCustomerService");
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "InvoiceCommission");
		return spec;
	}
}