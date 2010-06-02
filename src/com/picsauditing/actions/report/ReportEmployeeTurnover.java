package com.picsauditing.actions.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportEmployeeTurnover extends ReportAccount {
	SimpleDateFormat dbSafe = new SimpleDateFormat("yyyy-MM-dd");
	Date date = new Date();
	
	private int[] allMonths = new int[] { 3, 6, 12 };
	
	@Override
	protected void checkPermissions() throws Exception {
		// Can contractors view this?
		if (!permissions.isOperatorCorporate() && !permissions.isAdmin())
			throw new NoRightsException("Operator/Corporate or Admin");
	}
	
	@Override
	protected void buildQuery() {
		if (getOrderBy() != null && getOrderBy().equals("a.nameIndex"))
			setOrderBy(null);
		
		String query = "SELECT id, name, SUM(total) AS current, SUM(hours) AS experience, " +
				"SUM(hired3) AS new3, SUM(fired3) AS old3, SUM(hired6) AS new6, SUM(fired6) AS old6, " +
				"SUM(hired12) AS new12, SUM(fired12) AS old12\nFROM (" +
			"(SELECT a.id, a.name, COUNT(*) AS total, FLOOR(AVG(DATEDIFF('" + dbSafe.format(date) + 
				"', e.hireDate)) / 30.4) AS hours, 0 AS hired3, 0 AS fired3, 0 AS hired6, 0 AS fired6, " +
				"0 AS hired12, 0 AS fired12\nFROM accounts a \nJOIN employee e ON a.id = e.accountID";
		
		if (permissions.isOperator())
			query += "\nJOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = " + 
			permissions.getAccountId();
		else if (permissions.isCorporate()) {
			query += "\nJOIN generalcontractors gc ON gc.subID = a.id AND gc.genID IN " +
			"(SELECT id FROM operators WHERE parentID = " + permissions.getAccountId() + ")";
		}
		
		query += "\nWHERE (e.hireDate <= '" + dbSafe.format(date) + "' OR e.hireDate IS NULL)\nAND (e.fireDate > '" + 
				dbSafe.format(date) + "' OR e.fireDate IS NULL)\nAND e.active = 1\nAND a.status = 'Active'" +
				"\nAND a.type = 'Contractor'\nGROUP BY a.name) ";
		
		for (int month : allMonths) {
			query += "\nUNION\n(" + buildSubQuery(month, true) + ")";
			query += "\nUNION\n(" + buildSubQuery(month, false) + ")";
		}
		
		query += ") t\nGROUP BY t.id\nORDER BY " +
				((!Strings.isEmpty(getOrderBy())) ? getOrderBy() : "t.name");
		
		sql.setFullClause(query);
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	private String buildSubQuery(int selected, boolean getHired) {
		List<String> fields = new ArrayList<String>();
		String where = "";
		String subQuery = "SELECT a.id, a.name, 0 AS total, 0 AS hours, ";
		
		for (int month : allMonths) {
			String hired = "0 AS hired" + month;
			String fired = "0 AS fired" + month;
			
			if (month == selected) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.MONTH, 0 - selected);
				
				if (getHired) {
					hired = "COUNT(*) as hired" + selected;
					where = "\nWHERE e.hireDate > '" + dbSafe.format(cal.getTime()) + "'";
				} else {
					fired = "COUNT(*) as fired" + selected;
					where = "\nWHERE e.fireDate > '" + dbSafe.format(cal.getTime()) + "'";
				}
			}
			
			fields.add(hired);
			fields.add(fired);
		}
		
		
		subQuery += Strings.implode(fields) + "\nFROM accounts a\nJOIN employee e ON a.id = e.accountID";
		
		if (permissions.isOperator())
			subQuery += "\nJOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = " + 
					permissions.getAccountId();
		else if (permissions.isCorporate()) {
			subQuery += "\nJOIN generalcontractors gc ON gc.subID = a.id AND gc.genID IN " +
					"(SELECT id FROM operators WHERE parentID = " + permissions.getAccountId() + ")";
		}
		
		subQuery += (where.length() > 0 ? where + "\nAND" : "\nWHERE") + 
				" a.status = 'Active'\nAND a.type = 'Contractor'\nGROUP BY a.id\nORDER BY a.name";
		
		return subQuery;
	}
}
