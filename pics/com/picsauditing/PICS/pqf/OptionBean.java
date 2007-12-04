package com.picsauditing.PICS.pqf
;

import java.sql.*;
import java.util.*;
import com.picsauditing.PICS.Utilities;

public class OptionBean extends com.picsauditing.PICS.DataBean {
	public String questionID = "";
	public String optionID = "";
	public String optionName = "";
	public String number = "";
	public String visible = "Yes";

	ResultSet listRS = null;
	int numResults = 0;
	int count = 0;

	public void setFromDB(String oID) throws Exception {
		optionID = oID;
		setFromDB();
	}//setFromDB
	
	public void setFromDB() throws Exception {
		if ((null == optionID) || ("".equals(optionID)))
			throw new Exception("Can't set PQF Option from DB because optionID is not set");
		String Query = "SELECT * FROM pqfOptions WHERE optionID = " + optionID + ";";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		questionID = SQLResult.getString("questionID");
		optionID = SQLResult.getString("optionID");
		optionName = SQLResult.getString("optionName");
		number = SQLResult.getString("number");
		visible = SQLResult.getString("visible");
	}//setFromResultSet

/*	public void writeToDB() throws Exception {
		DBReady();
		String Query = "UPDATE pqfOptions SET " +
			"optionName = '" + Utilities.escapeQuotes(optionName) +
			"',questionID = '" + questionID +
			"',number = '" + number +
			"',visible = '" + visible +
			"' WHERE optionID = " + optionID + ";";

		SQLStatement.executeUpdate(Query);
		DBClose();
	}//writeToDB
*/
	public void writeNewToDB() throws Exception {
		String Query = "INSERT INTO pqfOptions (questionID,optionName,number)" +
			" VALUES ("+questionID+",'"+Utilities.escapeQuotes(optionName)+"',"+number+");";
		try{
			DBReady();
			SQLStatement.executeUpdate(Query);
		}finally{
			DBClose();
		}//finally
	}//writeNewToDB

	public void deleteOption(String oID) throws Exception {
		String Query = "UPDATE pqfOptions SET visible='No' WHERE optionID = " + oID + ";"; 
		try{
			DBReady();
			SQLStatement.executeUpdate(Query);
		}finally{
			DBClose();
		}//finally
	}//deleteQuestion

	public void setFromRequest(javax.servlet.http.HttpServletRequest r) throws Exception {
		questionID = r.getParameter("editID");
		optionName = r.getParameter("newOption");
		number = r.getParameter("optionNumber");
	}//setFromRequest

	public void setList(String orderBy, String qID) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String Query = "SELECT * FROM pqfOptions WHERE visible='Yes' AND questionID = "+ qID +
				" ORDER BY " + orderBy + ";";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(Query);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setList

	public boolean isNextRecord() throws Exception {
		try{
			if (!(count <= numResults && listRS.next()))
				return false;
			count++;
			setFromResultSet(listRS);
			return true;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//isNextRecord

	public void closeList() throws Exception {
		count = 0;
		numResults = 0;
		if (null != listRS) {
			listRS.close();
			listRS = null;
		}//if
		DBClose();
	}//closeList

	public String getBGColor() {
		if ((count % 2) == 1)	return " bgcolor=\"#FFFFFF\"";
		else	return "";
	}//getBGColor

	public void updateNumbering(javax.servlet.http.HttpServletRequest request) throws Exception {
		try{
			Enumeration e = request.getParameterNames();
			DBReady();
			while (e.hasMoreElements()) {
				String temp = (String)e.nextElement();
				if (temp.startsWith("num_")) {
					String id = temp.substring(4);
					String num = request.getParameter("num_" + id);
					String Query = "UPDATE pqfOptions SET number = " + num + " WHERE optionID = " + id + ";";
					SQLStatement.executeUpdate(Query);
				}//if
			}//while
		}finally{
			DBClose();
		}//finally
	}//updateNumbering
	
	public void renumberPQFOptions(String qID)  throws Exception {
		String SelectQuery = "SELECT optionID FROM pqfOptions WHERE visible='Yes' AND questionID="+
			qID+" ORDER BY number";
		try{
			DBReady();
			ResultSet SQLResult  = SQLStatement.executeQuery(SelectQuery);
			String updateQuery = "";
			int nextNumber = 1;
			statement2Ready();
			while (SQLResult.next()) {
				updateQuery  = " UPDATE pqfOptions SET number = " + nextNumber + " WHERE optionID = " + SQLResult.getInt("optionID") + ";";
				nextNumber = nextNumber +1;	
				SQLStatement2.executeUpdate(updateQuery);
			}//while
		}finally{
			DBClose();
		}//finally
	}//renumberPQF
	
/*	public String getCheckOptions(String qID, String classType, String qNum) throws Exception {
		StringBuffer temp = new StringBuffer();
		temp.append("<td></td></tr>");
		setList("",qID);
		while (isNextRecord()) {
			temp.append("<tr class=").append(classType).append("><td></td><td>").append(qNum).append(".");
			temp.append(number).append(" ").append(optionName).append("</td><td>");
			temp.append(Utilities.getCheckBoxInput("num_"+qID+"_"+optionID,classType));
			temp.append("</td></tr>");
		}//while
//		temp.append("<td></td></tr><tr>");
		return temp.toString();
	}//getCheckOptions
*/
	public String[] getOptionsArray(String qID) throws Exception {
		ArrayList<String> optionsAL = new ArrayList<String>();
		setList("",qID);
		while (isNextRecord())
			optionsAL.add(optionName);
		closeList();
		return (String[])optionsAL.toArray(new String[0]);
	}//getCheckOptions

	public boolean isOK() {
		errorMessages = new Vector<String>();
			if ((null == number) || (number.length() == 0))
				errorMessages.addElement("Please enter the option number");
			if ((null == optionName) || (optionName.length() == 0))
				errorMessages.addElement("Please enter the option name");
			return (errorMessages.size() == 0);
	}//isOK
}//OptionBean