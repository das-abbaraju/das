package com.picsauditing.PICS;

import java.util.Calendar;
import java.sql.*;
import java.util.*;

import com.picsauditing.access.PermissionsBean;

public class CalendarBean extends DataBean {
  	public SearchBean sBean = new SearchBean();
  	ArrayList<String> auditDates = new ArrayList<String>();
	public String[] auditDatesArray = {""};
	ArrayList<String> blockedDates = new ArrayList<String>();
	public String[] blockedDatesArray = {""};
	boolean isBlank = true;
//	public String star = "<img src='/images/yellow_star.gif' style='position: relative; top: 3px;' />";
	
	static final int FEBRUARY = 1;   //special month during leap years

	int DaysInMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	int auditMonth = 0;
	int auditYear = 2005;

	public void setDate(int m, int y){
		auditMonth = m;
		auditYear = y;
	}//setDate
  
	public String writeCalendar(int month,int year,PermissionsBean pBean) throws Exception{
		int numRows;        // number of rows in cell table (4, 5, 6)
		int numDays;        // number of days in month
		int day = 1;
		String dayStr = "";      
		int maxchars = 18;
		int thismaxchars = 18;
		int today = Calendar.getInstance().get(Calendar.DATE);	//used to change the background color of today

		auditMonth = month;
    	auditYear = year;

		setFromDB(auditMonth,auditYear);
		// Get number of calendar rows needed for this month.
		numRows = numberRowsNeeded(auditYear, auditMonth);
		// Get number of days in month, adding one if February of leap year.
		numDays = DaysInMonth[auditMonth] +((isLeapYear(auditYear) && (auditMonth==FEBRUARY))?1:0);	
		dayStr  += "<tr height='60' class='bluemain'>";

		for (int rowcount = 1; rowcount <=numRows; rowcount++){
			dayStr += "<tr height='60' class='bluemain'>";
			for (int dayofweekcount = 1; dayofweekcount <=7; dayofweekcount++){
				if ((rowcount==1 && dayofweekcount >= (calcFirstOfMonth(auditYear,auditMonth)+1)) || (rowcount>1 && day<=numDays)){
					if (day == today && (month == DateBean.getCurrentMonth()))
						dayStr += "<td valign='top' bgcolor='CCCCCC'><strong>"+String.valueOf(day)+"</strong>";
					else
						dayStr += "<td valign='top'><strong>"+String.valueOf(day)+"</strong>";
					//check to see if blocked date
					if (pBean.isAdmin() || pBean.isAuditor()){
						for (int i=0; i < blockedDatesArray.length; i+=6){
							if (day==Integer.parseInt(blockedDatesArray[i].substring(8,10))){
								dayStr += "<br>BLOCKED: "+blockedDatesArray[i+1]+"<br>"; 
								if (!"0".equals(blockedDatesArray[i+2])) 
									dayStr += blockedDatesArray[i+2]+blockedDatesArray[i+3]+"-"+blockedDatesArray[i+4]+blockedDatesArray[i+5];
								dayStr += "<br><a class='buttons' href='?whichMonth="+auditMonth+"&whichYear="+auditYear+"&unblock="+blockedDatesArray[i]+"'>unblock</a>";
							}//if
						}//for
					}//if
					//check to see if audit date
					for (int i=0; i < auditDatesArray.length; i+=8){
						String auditDate = (String)auditDatesArray[i];
						String conID = (String)auditDatesArray[i+1];
						String contractorName = (String)auditDatesArray[i+2];
						String auditHour = (String)auditDatesArray[i+3];
						String auditAmPm = (String)auditDatesArray[i+4];
						String auditorID = (String)auditDatesArray[i+5];
						String auditorName = (String)auditDatesArray[i+6];
						String auditLocation = (String)auditDatesArray[i+7];

						if (day == Integer.parseInt(auditDatesArray[i].substring(8, 10))){
							if (pBean.isAdmin() || 
									((pBean.isOperator() || pBean.isCorporate()) && pBean.canSeeSet.contains(conID)) ||
									(pBean.isAuditor() && pBean.userID.equals(auditorID))){							 	
								if (contractorName.length() < maxchars)
									thismaxchars = contractorName.length();
								else
									thismaxchars = maxchars;
								dayStr += "<span class='buttons'>";
								if (pBean.isAdmin() || pBean.isAuditor()){
									dayStr += "<br><span class='buttons'><strong>"+auditHour+auditAmPm+"</strong>";
									if ("Web".equals(auditLocation))
										dayStr+="*";
							  	}//if
									dayStr += "<br>"+contractorName.substring(0,thismaxchars);
								if (pBean.isAdmin() && null!=auditorName)
									dayStr += "<br>("+auditorName+")";
								dayStr += "</span>";
							}//if
						}//if day==
					}//for
					dayStr+="</td>";
					day+=1;
				} else
					dayStr+="<td>&nbsp;</td>";
			}//for
		dayStr+="</tr>\n";			
		}//for
	return dayStr;
    }//WriteCalendar
 
	int numberRowsNeeded(int year, int month){
		int firstDay;       //day of week for first day of month
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
	}//numberRowsNeeded
  
	int calcFirstOfMonth(int year, int month){
		int firstDay;       // day of week for Jan 1, then first day of month
		int i;              // to traverse months before given month
		// Catch month out of range.
		if ((month<0) || (month>11))
			return (-1);
		// Get day of week for Jan 1 of given year.
		firstDay = calcJanuaryFirst(year);
		// Increase firstDay by days in year before given month to get first day of month.
		for (i = 0; i<month; i++)
			firstDay += DaysInMonth[i];
		// Increase by one if month after February and leap year.
		if ((month>FEBRUARY) && isLeapYear(year))
			firstDay++;
		// Convert to day of the week and return.
		return (firstDay % 7);
	}//calcFirstOfMonth
  
	boolean isLeapYear(int year){
		// If multiple of 100, leap year iff multiple of 400.
		if ((year%100)==0)
			return((year%400)==0);
		// Otherwise leap year iff multiple of 4.
		return ((year % 4) == 0);
    }//isLeapYear

	int calcJanuaryFirst(int year){
		// Start Fri 01-01-1582; advance a day for each year, 2 for leap yrs.
		return ((5+(year-1582)+calcLeapYears(year))%7);
	}//calcJanuaryFirst

	int calcLeapYears(int year){
		int leapYears;      // number of leap years to return
		int hundreds;       // number of years multiple of a hundred
		int fourHundreds;   // number of years multiple of four hundred
		// Calculate number of years in interval that are a multiple of 4.
		leapYears = (year-1581)/4;
		//Calculate number of years in interval that are a multiple of 100; subtract, since they are not leap years.
		hundreds = (year-1501)/100;
		leapYears -= hundreds;
		//Calculate number of years in interval that are a multiple of 400; add back in, since they are still leap years.
		fourHundreds = (year-1201)/400;
		leapYears += fourHundreds;
		return leapYears;
	}//calcLeapYears

	public void setFromDB(int auditMonth, int auditYear) throws Exception {
		setDate(auditMonth, auditYear);
		setFromDB();
	}//setFromDB

	public void setFromDB() throws Exception {
		if ((auditYear < 0) || (auditMonth < 0))
			throw new Exception("Audit Date not set");
		String selectQuery = "SELECT accounts.id AS id,accounts.name AS contractor_name,auditLocation,auditDate,auditHour,"+
				"auditAmPm,auditor_id,a2.name AS auditor_name FROM contractor_info INNER JOIN accounts ON contractor_info.id=accounts.id "+
				"LEFT OUTER JOIN accounts a2 ON contractor_info.auditor_id=a2.id WHERE YEAR(auditDate)='"+auditYear+
				"' AND MONTH(auditDate)='"+(auditMonth+1)+"' ORDER BY auditDate,auditAmPm,auditHour";
		int i = 0;
		auditDates.clear();
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				auditDates.add(SQLResult.getString("auditDate"));
				auditDates.add(SQLResult.getString("id"));
				auditDates.add(SQLResult.getString("contractor_name"));
				auditDates.add(SQLResult.getString("auditHour"));
				auditDates.add(SQLResult.getString("auditAmPm"));
				auditDates.add(SQLResult.getString("auditor_id"));
				auditDates.add(SQLResult.getString("auditor_name"));
				auditDates.add(SQLResult.getString("auditLocation"));
			}//while
			SQLResult.close();
			auditDatesArray = (String[])auditDates.toArray(new String[0]);
			blockedDates = new ArrayList<String>();
			selectQuery = "SELECT DISTINCT(blockedDate), description, startHour, startAmPm, endHour, endAmPm FROM blockedDates "+
			"WHERE YEAR(blockedDate)='"+auditYear+"' AND MONTH(blockedDate)='"+(auditMonth+1)+"' ORDER BY blockedDate;";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				blockedDates.add(SQLResult.getString("blockedDate"));
				blockedDates.add(SQLResult.getString("description"));
				blockedDates.add(SQLResult.getString("startHour"));
				blockedDates.add(SQLResult.getString("startAmPm"));
				blockedDates.add(SQLResult.getString("endHour"));
				blockedDates.add(SQLResult.getString("endAmPm"));
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		blockedDatesArray = (String[])blockedDates.toArray(new String[0]);
	}//setFromDB()

	public void writeBlockedDatetoDB(String blockedDate,String description,String startHour,String startAmPm,String endHour,String endAmPm) throws Exception {
		if ("--".equals(startAmPm))
				startAmPm = "am";
		if ("--".equals(endAmPm))
				endAmPm = "am";
		String insertQuery = "INSERT INTO blockedDates (blockedDate,description,startHour,startAmPm, endHour, endAmPm) VALUES ('"+
				DateBean.toDBFormat(blockedDate)+"','"+description+"','"+startHour+"','"+startAmPm+"','"+endHour+"','"+endAmPm+"');";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//writeBlockedDatetoDB()
	
	public void deleteBlockedDate(String deleteDate) throws Exception {
		String deleteQuery = "DELETE FROM blockedDates WHERE blockeddate='"+deleteDate+"';";
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
	}//deleteBlockedDate
}//Calendar