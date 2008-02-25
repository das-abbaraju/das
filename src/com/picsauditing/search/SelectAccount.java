package com.picsauditing.search;

import com.picsauditing.PICS.Utilities;

public class SelectAccount extends SelectSQL {
	private Type type;
	public static enum Type {Operator, Contractor}

	public SelectAccount() {
		super();
		this.setFromTable("accounts a");
		this.addField("a.id");
		this.addField("a.name");
		this.addField("a.active");
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
		if (type == Type.Contractor) {
			this.addJoin("JOIN contractor_info c ON a.id = c.id");
			this.addWhere("a.type='Contractor'");
		}
		if (type == Type.Operator) {
			this.addJoin("JOIN operators o ON a.id = o.id");
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
		this.addJoin(join);
		this.addField("q"+questionID+".answer AS "+columnName);
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
			this.addWhere("a.name LIKE '"+Utilities.escapeQuotes(startsWith)+"%'");
		}
	}
}
