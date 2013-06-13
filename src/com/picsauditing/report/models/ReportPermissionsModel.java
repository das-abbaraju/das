package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.ReportPermissionView;
import com.picsauditing.report.tables.ReportTable;

public class ReportPermissionsModel extends AbstractModel {

	public ReportPermissionsModel(Permissions permissions) {
		super(permissions, new ReportTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec report = new ModelSpec(null, "Report");

		return report;
	}
}