package com.picsauditing.report.models;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

import java.util.List;
import java.util.Map;

public class InvoiceFeeCountriesModel extends AbstractModel {

	public InvoiceFeeCountriesModel(Permissions permissions) {
		super(permissions, new InvoiceFeeTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Fee");

		spec.join(InvoiceFeeTable.Country);

        return spec;
	}
}