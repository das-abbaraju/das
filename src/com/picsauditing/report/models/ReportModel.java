package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.ReportTable;

public class ReportModel extends AbstractModel {

	public ReportModel(Permissions permissions) {
		super(permissions, new ReportTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec report = new ModelSpec(null, "Report");

		ModelSpec reportUser = report.join(ReportTable.User);
		reportUser.alias = "ReportUser";
		reportUser.minimumImportance = FieldImportance.Average;

		return report;
	}
}