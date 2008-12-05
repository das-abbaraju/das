package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.picsauditing.jpa.entities.MultiYearScope;

/**
 * Basic data object containing a single row from the flagcriteria table
 * Contains no CRUD ability
 * 
 * @see FlagCriteria
 * @see "Table flagcriteria"
 * @author Jeff Jensen
 */
public class FlagCriteriaDO {
	String opID = "";
	String questionID = "";
	String flagStatus = "";
	String isChecked = "";
	String comparison = "";
	String value = "";
	MultiYearScope multiYearScope = null;

	/**
	 * empty constructor
	 */
	public FlagCriteriaDO() {
	}

	public FlagCriteriaDO(String opID, String questionID, String flagStatus, String isFlagged, String questionType,
			String comparison, String value, MultiYearScope multiYearScope) {
		this.opID = opID;
		this.questionID = questionID;
		this.flagStatus = flagStatus;
		this.isChecked = isFlagged;
		this.comparison = comparison;
		this.value = value;
		this.multiYearScope = multiYearScope;
	}

	public void setFromResultSet(ResultSet rs) throws SQLException {
		opID = rs.getString("opID");
		questionID = rs.getString("questionID");
		flagStatus = rs.getString("flagStatus");
		isChecked = rs.getString("isChecked");
		comparison = rs.getString("comparison");
		value = rs.getString("value");
		multiYearScope = null;
		String scope = rs.getString("multiYearScope");
		try {
			if (scope != null)
				multiYearScope = MultiYearScope.valueOf(scope);
		} catch (Exception e) {
			System.out.println("FlagCriteriaDO.setFromResultSet() failed to convert \"" + scope
					+ "\" into a MultiYearScope");
		}
	}

	public boolean isChecked() {
		return "Yes".equals(isChecked);
	}
}
