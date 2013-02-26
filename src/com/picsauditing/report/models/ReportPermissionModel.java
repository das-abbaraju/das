package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.ReportPermissionView;
import com.picsauditing.report.tables.ReportTable;

public class ReportPermissionModel extends AbstractModel {

	public ReportPermissionModel(Permissions permissions) {
		super(permissions, new ReportTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec report = new ModelSpec(null, "Report");
		ModelSpec reportUser = report.join(ReportTable.User);
		reportUser.alias = "ReportUser";
		reportUser.minimumImportance = FieldImportance.Average;

		ModelSpec permission = report.join(ReportTable.Permission);
		permission.alias = "Permission";
		permission.minimumImportance = FieldImportance.Average;

		ModelSpec user = permission.join(ReportPermissionView.User);
		user.minimumImportance = FieldImportance.Required;
		user.alias = "User";
		user.category = FieldCategory.AccountInformation;

		ModelSpec account = permission.join(ReportPermissionView.Account);
		account.minimumImportance = FieldImportance.Required;
		account.alias = "Account";
		user.category = FieldCategory.AccountInformation;

		return report;
	}
}