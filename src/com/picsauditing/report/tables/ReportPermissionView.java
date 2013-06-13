package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ReportPermissionView extends AbstractTable {
	public static final String User = "User";
	public static final String Account = "Account";

	public ReportPermissionView() {
		super("(SELECT CASE WHEN u.isGroup = 'No' THEN 'User' ELSE 'Group' END AS type, rpu.reportID, rpu.userID AS entityID, rpu.editable" +
				" FROM report_permission_user rpu" +
				" JOIN users u ON rpu.userID = u.id" +
				" UNION " +
				" SELECT 'Account' AS type, reportID, accountID AS entityID, editable" +
				" FROM report_permission_account)");
        Field type = new Field("Type","type", FieldType.String);
        addField(type).setImportance(FieldImportance.Average);
        Field editable = new Field("Editable","editable", FieldType.Boolean);
        addField(editable).setImportance(FieldImportance.Required);
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