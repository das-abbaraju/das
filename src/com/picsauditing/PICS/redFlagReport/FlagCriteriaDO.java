package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Basic data object containing a single row from the flagcriteria table
 * Contains no CRUD ability
 * 
 * @see FlagCriteria
 * @see "Table flagcriteria"
 * @author Jeff Jensen
 */
public class FlagCriteriaDO{
	String opID = "";
	String questionID = "";
	String flagStatus = "";
	String isChecked = "";
	String comparison = "";
	String value = "";
	
	/**
	 * empty constructor
	 */
	public FlagCriteriaDO() {
	}
	
	public FlagCriteriaDO(String opID, String questionID, String flagStatus, String isFlagged, 
				String questionType, String comparison, String value) {
		this.opID = opID;
		this.questionID = questionID;
		this.flagStatus = flagStatus;
		this.isChecked = isFlagged;
		this.comparison = comparison;
		this.value = value;
	}
	
	public void setFromResultSet(ResultSet rs) throws SQLException {
		opID = rs.getString("opID");
		questionID = rs.getString("questionID");
		flagStatus = rs.getString("flagStatus");
		isChecked = rs.getString("isChecked");
		comparison = rs.getString("comparison");
		value = rs.getString("value");
	}
	
	public boolean isChecked() {
		return "Yes".equals(isChecked);
	}
}
