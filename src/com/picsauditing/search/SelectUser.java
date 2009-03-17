package com.picsauditing.search;


/**
 * Creates a basic SQL statement that looks like this:<br>
 * SELECT u.id, u.name, u.isActive FROM users u;
 */
public class SelectUser extends SelectSQL {
	public SelectUser() {
		super();
		this.setFromTable("users u");
		this.addField("u.id");
		this.addField("u.name");
		this.addField("u.isActive");
	}
	
	public void inGroups(String groupIDs) {
		if (groupIDs.length() == 0) return;
		this.addWhere("u.id IN (SELECT userID FROM usergroup WHERE groupID IN (" + groupIDs + "))");
	}
	
}
