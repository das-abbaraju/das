package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.util.*;
import com.picsauditing.PICS.*;

/**
 * An operator's OSHA criteria in calculating contractor flags (Red/Amber)
 * CRUD functionality to support the table `flagoshacriteria` 
 * @author Jeff Jensen
 * @see "Table flagoshacriteria"
 */
public class FlagOshaCriteriaDO extends DataBean {
	/**
	 * Operator ID
	 */
	public String opID = "";
	/**
	 * Is this criteria for Red or Amber
	 */
	public String flagStatus = "";
	
	public String lwcrHurdle = "";
	public String lwcrTime = "1";
	public String trirHurdle = "";
	public String trirTime = "1";
	public String fatalitiesHurdle = "";
	public String fatalitiesTime = "1";

	// Assume we DON'T have any criteria, until we query some from DB
	public String flagLwcr = "No";
	public String flagTrir = "No";
	public String flagFatalities = "No";

	public String getIsChecked(String checkBox){
		if ("Yes".equals(checkBox))
			return "Yes";
		return "No";
	}//getIsChecked

	public void setFromDB(String op_ID,String fStatus) throws Exception, IllegalArgumentException {
		opID = op_ID;
		flagStatus = fStatus;
		if ((null == opID) || ("".equals(opID)))
			throw new IllegalArgumentException("Can't set FlagOshaCriteria from DB because opID is not set");
		if (!flagStatus.equals("Red") && !flagStatus.equals("Amber"))
			throw new IllegalArgumentException("Flag status must be either Red or Amber");
		String selectQuery = "SELECT * FROM flagOshaCriteria WHERE opID="+Utilities.intToDB(opID)+" AND flagStatus='"+flagStatus+"'";
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			if (rs.next())
				setFromResultSet(rs);
			rs.close();
		}finally{
			DBClose();
		}
	}//setFromDB

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		lwcrHurdle = SQLResult.getString("lwcrHurdle");
		lwcrTime = SQLResult.getString("lwcrTime");
		trirHurdle = SQLResult.getString("trirHurdle");
		trirTime = SQLResult.getString("trirTime");
		fatalitiesHurdle = SQLResult.getString("fatalitiesHurdle");
		fatalitiesTime = SQLResult.getString("fatalitiesTime");

		flagLwcr = SQLResult.getString("flagLwcr");
		flagTrir = SQLResult.getString("flagTrir");
		flagFatalities = SQLResult.getString("flagFatalities");
	}//setFromResultSet

	public void writeToDB() throws Exception, IllegalArgumentException {
		if ((null == opID) || ("".equals(opID)))
			throw new IllegalArgumentException("can't write operator info to DB because id is not set");
		String updateQuery = "REPLACE flagOshaCriteria SET "+
			"flagStatus='"+flagStatus+
			"',lwcrHurdle='"+lwcrHurdle+
			"',lwcrTime='"+lwcrTime+
			"',trirHurdle='"+ trirHurdle+
			"',trirTime='"+trirTime+
			"',fatalitiesHurdle='"+fatalitiesHurdle+
			"',fatalitiesTime='"+fatalitiesTime+
			"',flagLwcr='"+flagLwcr+
			"',flagTrir='"+flagTrir+
			"',flagFatalities='"+flagFatalities+
			"',opID="+opID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public void writeNewToDB(String op_ID) throws Exception {
		opID = op_ID;
		String insertQuery = "INSERT INTO flagOshaCriteria (opID) VALUES ('"+opID+"');";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
		writeToDB();
	}//writeNewToDB

	public void setFromRequest(javax.servlet.http.HttpServletRequest request) throws Exception {
		lwcrHurdle = request.getParameter("lwcrHurdle");
		lwcrTime = request.getParameter("lwcrTime");
		trirHurdle = request.getParameter("trirHurdle");
		trirTime = request.getParameter("trirTime");
		fatalitiesHurdle = request.getParameter("fatalitiesHurdle");
		fatalitiesTime = request.getParameter("fatalitiesTime");
		flagLwcr = getIsChecked(request.getParameter("flagLwcr"));
		flagTrir = getIsChecked(request.getParameter("flagTrir"));
		flagFatalities = getIsChecked(request.getParameter("flagFatalities"));
	}//setFromRequest

	public boolean isOK(){
		if (null == errorMessages)
			errorMessages = new Vector<String>();
		try{Float.parseFloat(lwcrHurdle);}
		catch (Exception e){
			lwcrHurdle = "0";
			if (!"No".equals(flagLwcr))
				errorMessages.addElement("Please enter a valid number for the OSHA Lost Workday Case Rate Cutoff");
		}//catch
		try{Float.parseFloat(trirHurdle);}
		catch (Exception e){
			trirHurdle = "0";
			if (!"No".equals(flagTrir))
				errorMessages.addElement("Please enter a valid number for the OSHA TRIR Rate Cutoff");
		}//catch
		try{Float.parseFloat(fatalitiesHurdle);}
		catch (Exception e){
			fatalitiesHurdle = "0";
			if (!"No".equals(flagFatalities))
				errorMessages.addElement("Please enter a valid number for the Fatalities Cutoff");
		}//catch
		return (errorMessages.size() == 0);
	} // isOK
	public boolean isTrirTimeAverage(){
		return "3".equals(trirTime);
	}//ifTrirTimeAverage
	public boolean isLwcrTimeAverage(){
		return "3".equals(lwcrTime);
	}//isLwcrTimeAverage
	public boolean isFatalitiesTimeAverage(){
		return "3".equals(fatalitiesTime);
	}//isFatalitiesTimeAverage

	public boolean flagLwcr(){
		return "Yes".equals(flagLwcr);
	}//flagLwcr
	public boolean flagTrir(){
		return "Yes".equals(flagTrir);
	}//flagTrir
	public boolean flagFatalities(){
		return "Yes".equals(flagFatalities);
	}//flagFatalities
}//redFlagBean
