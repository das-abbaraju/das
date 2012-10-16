package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.ReportPermissionAccountTable;
import com.picsauditing.report.tables.ReportPermissionUserTable;
import com.picsauditing.report.tables.ReportTable;
import com.picsauditing.report.tables.ReportUserTable;

public class ReportPermissionModel extends AbstractModel {
	public ReportPermissionModel(Permissions permissions) {
		super(permissions, new ReportTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec report = new ModelSpec(null, "Report");
		{
			ModelSpec reportUser = report.join(ReportTable.User);
			reportUser.alias = "ReportUser";
			reportUser.minimumImportance = FieldImportance.Average;
			{
				ModelSpec userPermission = reportUser.join(ReportUserTable.UserPermission);
				userPermission.alias = "UserPermission";
				userPermission.minimumImportance = FieldImportance.Average;
				ModelSpec user = userPermission.join(ReportPermissionUserTable.User);
				user.minimumImportance = FieldImportance.Required;
				user.alias = "User";
				user.category = FieldCategory.AccountInformation;
			}
		}

		{
			ModelSpec accountPermission = report.join(ReportTable.AccountPermission);
			accountPermission.alias = "AccountPermission";
			accountPermission.minimumImportance = FieldImportance.Average;
			{
				ModelSpec account = accountPermission.join(ReportPermissionAccountTable.Account);
				account.minimumImportance = FieldImportance.Required;
				account.alias = "Account";
			}
		}

		return report;
	}
}