package com.picsauditing.PICS;

public class SearchAccounts extends SearchRaw {

	private Type type;
	public enum Type {Operator, Contractor}

	public SearchAccounts() {
		super();
		this.sql.setFromTable("accounts a");
		this.sql.addField("a.id");
		this.sql.addField("a.name");
		this.sql.addField("a.active");
	}
	
	public Type getType() {
		return type;
	}

	/**
	 * Can only set the type of account search once
	 * Contractor joins to contractor_info c
	 * Operator joins to operators o
	 * @param type
	 */
	public void setType(Type type) {
		if (type == null) return;
		if (this.type != null) return;
		
		this.type = type;
		this.sql.addWhere("a.type = '"+this.type.toString()+"'");
		if (type == Type.Contractor) {
			this.sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		}
		if (type == Type.Operator) {
			this.sql.addJoin("JOIN operators o ON a.id = o.id");
		}
	}
	
	/**
	 * JOIN to pqfdata for this contractor and add q123.answer to the field list
	 * @param questionID PQF question ID must be > 0
	 * @param require if true, do an INNER JOIN, else LEFT JOIN
	 */
	public void addPQFQuestion(int questionID, boolean require, String columnName) {
		String join = "";
		if (!require) join = "LEFT ";
		join = join + "JOIN pqfdata q"+questionID+" on q"+questionID+".conID = a.id AND q"+questionID+".questionID = " + questionID;
		sql.addJoin(join);
		sql.addField("q"+questionID+".answer AS "+columnName);
	}
	/**
	 * LEFT JOIN to pqfdata for this contractor and add q123.answer to the field list
	 * @param questionID PQF question ID must be > 0
	 * @param require if true, do an INNER JOIN, else LEFT JOIN
	 */
	public void addPQFQuestion(int questionID) {
		this.addPQFQuestion(questionID, false, "answer"+questionID);
	}
	public void addPQFQuestion(String questionID) {
		int temp = Integer.parseInt(questionID);
		this.addPQFQuestion(temp);
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
