package com.picsauditing.PICS;

public class SearchUsers extends SearchRaw {

	public SearchUsers() {
		super();
		this.sql.setFromTable("users u");
		this.sql.addField("u.id");
		this.sql.addField("u.name");
		this.sql.addField("u.isActive");
	}
	
	public void inGroups(String groupIDs) {
		if (groupIDs.length() == 0) return;
		this.sql.addWhere("u.id IN (SELECT userID FROM usergroup WHERE groupID IN (" + groupIDs + "))");
	}
	
	/**
	 * Support search by first character in the name
	 * @param startsWith
	 */
	public void startsWith (String startsWith) {
		if (startsWith != null && startsWith.length() > 0) {
			this.sql.addWhere("a.name LIKE '"+Utilities.escapeQuotes(startsWith)+"%'");
		}
	}
}
