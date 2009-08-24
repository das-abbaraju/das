package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.search.SelectContractorAudit;

public class CalendarBean extends DataBean {
	private Permissions permissions;
	private Account account;
	
	private ArrayList<CalendarEntry> auditDates = new ArrayList<CalendarEntry>();
	ArrayList<String> blockedDates = new ArrayList<String>();
	public String[] blockedDatesArray = { "" };
	boolean isBlank = true;

	static final int FEBRUARY = 1; // special month during leap years

	int DaysInMonth[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	int auditMonth = 0;
	int auditYear = 2005;

	public void setDate(int m, int y) {
		auditMonth = m;
		auditYear = y;
	}

	public String writeCalendar(int month, int year) throws Exception {
		int numRows; // number of rows in cell table (4, 5, 6)
		int numDays; // number of days in month
		int day = 1;
		String dayStr = "";
		int maxchars = 18;
		int thismaxchars = 18;
		int today = Calendar.getInstance().get(Calendar.DATE); // used to
																// change the
																// background
																// color of
																// today

		auditMonth = month;
		auditYear = year;
		setFromDB();
		
		// Get number of calendar rows needed for this month.
		numRows = numberRowsNeeded(auditYear, auditMonth);
		// Get number of days in month, adding one if February of leap year.
		numDays = DaysInMonth[auditMonth] + ((isLeapYear(auditYear) && (auditMonth == FEBRUARY)) ? 1 : 0);
		dayStr += "<tr>";

		for (int rowcount = 1; rowcount <= numRows; rowcount++) {
			// Loop through each week
			dayStr += "<tr>";
			for (int dayofweekcount = 1; dayofweekcount <= 7; dayofweekcount++) {
				// Loop through each day of the week (Sun-Sat)
				if ((rowcount == 1 && dayofweekcount >= (calcFirstOfMonth(auditYear, auditMonth) + 1))
						|| (rowcount > 1 && day <= numDays)) {
					if (day == today && (month == DateBean.getCurrentMonth()))
						dayStr += "<td class=\"day\" style='background-color: #CCCCCC'><span class=\"daynum\">" + day + "</span>";
					else
						dayStr += "<td class=\"day\"><span class=\"daynum\">" + day + "</span>";
					// check to see if blocked date
					// TODO change this to a permission and check hasPermission
					// instead
					if (permissions.isPicsEmployee()) {
						for (int i = 0; i < blockedDatesArray.length; i += 6) {
							if (day == Integer.parseInt(blockedDatesArray[i].substring(8, 10))) {
								dayStr += "<br>BLOCKED: " + blockedDatesArray[i + 1] + "<br>";
								if (!"0".equals(blockedDatesArray[i + 2]))
									dayStr += blockedDatesArray[i + 2] + blockedDatesArray[i + 3] + "-"
											+ blockedDatesArray[i + 4] + blockedDatesArray[i + 5];
								dayStr += "<br><a class='buttons' href='?whichMonth=" + auditMonth + "&whichYear="
										+ auditYear + "&unblock=" + blockedDatesArray[i] + "'>unblock</a>";
							}
						}
					}
					for (CalendarEntry entry : auditDates) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(entry.getEntryDate());
						
						if (day == cal.get(Calendar.DAY_OF_MONTH)) {
							if (entry.getConName().length() < maxchars)
								thismaxchars = entry.getConName().length();
							else
								thismaxchars = maxchars;
//							dayStr += "<span class='buttons'>";
							if (permissions.isPicsEmployee()) {
								if ("Web".equals(entry.getAuditLocation()))
									dayStr += "<br><font color=\"#003366\">" + DateBean.format(entry.getEntryDate(), "h:mm a") + "</font>";
								else
									dayStr += "<br><font color=\"#993300\">" + DateBean.format(entry.getEntryDate(), "h:mm a") + "</font>";
							} else
								dayStr += "<br><strong>" + DateBean.format(entry.getEntryDate(), "h:mm a") + "</strong>";
							dayStr += " - <a href=\"Audit.action?auditID="+entry.getAuditID()+"\">"+entry.getConName().substring(0, thismaxchars)+"</a>";
							if (null != entry.getAuditorName())
								dayStr += " (" + entry.getAuditorName() + ")";
						}
					}
					dayStr += "</td>";
					day += 1;
				} else
					dayStr += "<td class=\"day\">&nbsp;</td>";
			}// for
			dayStr += "</tr>\n";
		}// for
		return dayStr;
	}

	int numberRowsNeeded(int year, int month) {
		int firstDay; // day of week for first day of month
		int numCells;

		firstDay = calcFirstOfMonth(year, month);
		// Non leap year February with 1st on Sunday: 4 rows.
		if ((month == FEBRUARY) && (firstDay == 0) && !isLeapYear(year))
			return (4);
		// Number of cells needed = blanks on 1st row + days in month.
		numCells = firstDay + DaysInMonth[month];
		// One more cell needed for the Feb 29th in leap year.
		if ((month == FEBRUARY) && (isLeapYear(year)))
			numCells++;
		// 35 cells or less is 5 rows; more is 6.
		return ((numCells <= 35) ? 5 : 6);
	}// numberRowsNeeded

	int calcFirstOfMonth(int year, int month) {
		int firstDay; // day of week for Jan 1, then first day of month
		int i; // to traverse months before given month
		// Catch month out of range.
		if ((month < 0) || (month > 11))
			return (-1);
		// Get day of week for Jan 1 of given year.
		firstDay = calcJanuaryFirst(year);
		// Increase firstDay by days in year before given month to get first day
		// of month.
		for (i = 0; i < month; i++)
			firstDay += DaysInMonth[i];
		// Increase by one if month after February and leap year.
		if ((month > FEBRUARY) && isLeapYear(year))
			firstDay++;
		// Convert to day of the week and return.
		return (firstDay % 7);
	}// calcFirstOfMonth

	boolean isLeapYear(int year) {
		// If multiple of 100, leap year iff multiple of 400.
		if ((year % 100) == 0)
			return ((year % 400) == 0);
		// Otherwise leap year iff multiple of 4.
		return ((year % 4) == 0);
	}// isLeapYear

	int calcJanuaryFirst(int year) {
		// Start Fri 01-01-1582; advance a day for each year, 2 for leap yrs.
		return ((5 + (year - 1582) + calcLeapYears(year)) % 7);
	}// calcJanuaryFirst

	int calcLeapYears(int year) {
		int leapYears; // number of leap years to return
		int hundreds; // number of years multiple of a hundred
		int fourHundreds; // number of years multiple of four hundred
		// Calculate number of years in interval that are a multiple of 4.
		leapYears = (year - 1581) / 4;
		// Calculate number of years in interval that are a multiple of 100;
		// subtract, since they are not leap years.
		hundreds = (year - 1501) / 100;
		leapYears -= hundreds;
		// Calculate number of years in interval that are a multiple of 400; add
		// back in, since they are still leap years.
		fourHundreds = (year - 1201) / 400;
		leapYears += fourHundreds;
		return leapYears;
	}// calcLeapYears

	public void setFromDB(int auditMonth, int auditYear) throws Exception {
		setDate(auditMonth, auditYear);
		setFromDB();
	}

	public void setFromDB() throws Exception {
		if ((auditYear < 0) || (auditMonth < 0))
			throw new Exception("Audit Date not set");

		SelectContractorAudit selectAudit = new SelectContractorAudit();
		selectAudit.addField("ca.scheduledDate");
		selectAudit.addField("ca.auditLocation");
		selectAudit.addJoin("LEFT JOIN users ua ON ca.auditorID = ua.id");
		selectAudit.addJoin("JOIN audit_type at ON ca.auditTypeID = at.id AND at.isScheduled = 1");
		selectAudit.addField("ca.auditorID");
		selectAudit.addField("ua.name as auditor_name");
		selectAudit.setPermissions(permissions);
		
		selectAudit.addWhere("year(ca.scheduledDate) = " + auditYear);
		selectAudit.addWhere("month(ca.scheduledDate) = " + (auditMonth+1));
		selectAudit.addOrderBy("ca.scheduledDate");

		auditDates.clear();
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectAudit.toString());
			while (SQLResult.next()) {
				CalendarEntry entry = new CalendarEntry();
				entry.setEntryDate(SQLResult.getTimestamp("scheduledDate"));
				entry.setConID(SQLResult.getInt("id"));
				entry.setConName(SQLResult.getString("name"));
				entry.setAuditorName(SQLResult.getString("auditor_name"));
				entry.setAuditLocation(SQLResult.getString("auditLocation"));
				entry.setAuditID(SQLResult.getInt("auditID"));
				auditDates.add(entry);
			}
			SQLResult.close();
			blockedDates = new ArrayList<String>();
			String selectQuery = "SELECT DISTINCT(blockedDate), description, startHour, startAmPm, endHour, endAmPm FROM blockedDates "
					+ "WHERE YEAR(blockedDate)='"
					+ auditYear
					+ "' AND MONTH(blockedDate)='"
					+ (auditMonth + 1)
					+ "' ORDER BY blockedDate;";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				blockedDates.add(SQLResult.getString("blockedDate"));
				blockedDates.add(SQLResult.getString("description"));
				blockedDates.add(SQLResult.getString("startHour"));
				blockedDates.add(SQLResult.getString("startAmPm"));
				blockedDates.add(SQLResult.getString("endHour"));
				blockedDates.add(SQLResult.getString("endAmPm"));
			}// while
			SQLResult.close();
		} finally {
			DBClose();
		}
		blockedDatesArray = (String[]) blockedDates.toArray(new String[0]);
	}

	public void writeBlockedDatetoDB(String blockedDate, String description, String startHour, String startAmPm,
			String endHour, String endAmPm) throws Exception {
		if ("--".equals(startAmPm))
			startAmPm = "am";
		if ("--".equals(endAmPm))
			endAmPm = "am";
		String insertQuery = "INSERT INTO blockedDates (blockedDate,description,startHour,startAmPm, endHour, endAmPm) VALUES ('"
				+ DateBean.toDBFormat(blockedDate)
				+ "','"
				+ description
				+ "','"
				+ startHour
				+ "','"
				+ startAmPm
				+ "','" + endHour + "','" + endAmPm + "');";
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		} finally {
			DBClose();
		}
	}

	public void deleteBlockedDate(String deleteDate) throws Exception {
		String deleteQuery = "DELETE FROM blockedDates WHERE blockeddate='" + deleteDate + "';";
		try {
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		} finally {
			DBClose();
		}
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	public void setAccountID(int accountID) {
	}
	
}