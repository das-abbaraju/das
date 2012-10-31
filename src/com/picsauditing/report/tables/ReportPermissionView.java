package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ReportPermissionUser;

public class ReportPermissionView extends AbstractTable {
	public static final String User = "User";
	public static final String Account = "Account";

	public ReportPermissionView() {
		super("(SELECT CASE WHEN u.isGroup = 'No' THEN 'User' ELSE 'Group' END AS TYPE, rpu.reportID, rpu.userID AS entityID, rpu.editable" +
				" FROM report_permission_user rpu" +
				" JOIN users u ON rpu.userID = u.id" +
				" UNION " +
				" SELECT 'Account' AS TYPE, reportID, accountID, 0 AS editable" +
				" FROM report_permission_account)");
		addFields(ReportPermissionUser.class);
	}

	protected void addJoins() {
		ReportOnClause userOnClause = new ReportOnClause("entityID");
		userOnClause.setExtraClauses(ReportOnClause.FromAlias + ".type IN ('User','Group')");
		addOptionalKey(new ReportForeignKey(User, new UserTable(), userOnClause));
		ReportOnClause accountOnClause = new ReportOnClause("entityID");
		accountOnClause.setExtraClauses(ReportOnClause.FromAlias + ".type = 'Account'");
		addOptionalKey(new ReportForeignKey(Account, new AccountTable(), accountOnClause));
	}
}