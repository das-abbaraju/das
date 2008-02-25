package com.picsauditing.search;

import com.picsauditing.PICS.Utilities;

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
	
	/**
	 * Support search by first character in the name
	 * @param startsWith
	 */
	public void startsWith (String startsWith) {
		if (startsWith != null && startsWith.length() > 0) {
			this.addWhere("a.name LIKE '"+Utilities.escapeQuotes(startsWith)+"%'");
		}
	}
}
