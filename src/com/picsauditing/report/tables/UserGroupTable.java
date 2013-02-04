package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.UserGroup;

public class UserGroupTable extends AbstractTable {
	public static final String Group = "Group";

	public UserGroupTable() {
		super("usergroup");
		addFields(UserGroup.class);
	}

	protected void addJoins() {
		ReportForeignKey group = new ReportForeignKey(Group, new UserTable(), new ReportOnClause("groupID","id", ReportOnClause.ToAlias + ".isGroup = 'Yes'"));
		group.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(group);
	}
}