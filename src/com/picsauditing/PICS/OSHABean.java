package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;


public class OSHABean extends DataBean {
/*	History
	12/15/04 jj - getFileNYearAgoIconLink()
	12/14/04 jj - added fileNYearAgo, uploadFiles(), getFileNLink() to store last 3 years of osha pdf files
*/
	public String OID = "";
	public String conID = "";
	public String SHAType = "OSHA";
	public String location = "";
	public String description = "";
	public String verifiedDate = "";
	public String auditorID = "";
	public boolean na1 = false;
	public boolean na2 = false;
    public boolean na3 = false;

	public static final int MAN_HOURS = 0;
	public static final int FATALITIES = 1;
	public static final int LOST_WORK_CASES = 2;
	public static final int LOST_WORK_DAYS = 3;
	public static final int INJURY_ILLNESS_CASES = 4;
	public static final int RESTRICTED_WORK_CASES = 5;
	public static final int RECORDABLE_TOTAL = 6;
		
	public static final String[] OSHA_TYPE_ARRAY = {"Corporate","Division","Region","Site"};
	public static final String SHA_TYPE_DEFAULT = "-Type-";
	public static final String[] SHA_TYPE_ARRAY = {SHA_TYPE_DEFAULT,"OSHA","MSHA"};
	public ArrayList<String> oshaIDsAL = null;
	public ListIterator li = null;
	
	public static final int YEAR1 = 0;
	public static final int YEAR2 = 1;
	public static final int YEAR3 = 2;
	public static final int YEAR4 = 3;
	private boolean duringGracePeriod = false;
	
	private int[][] stats = new int[7][4];
	public String file1YearAgo = "No";
	public String file2YearAgo = "No";
	public String file3YearAgo = "No";
	public boolean isInDB = false;

	boolean showLinks = false;

	DecimalFormat decFormatter = new DecimalFormat("###,##0.00");
	DecimalFormat intFormatter = new DecimalFormat("###,##0");
	
	public String getSHATypeSelect(String name, String classType, String selectedSHAType) {
		return Inputs.inputSelect(name, classType, selectedSHAType, SHA_TYPE_ARRAY);
	}//getSHATypeSelect

	public String getLocationDescription() {
	  if ("Corporate".equals(location))
	  	return "Corporate";
	return location+"-"+description;
	}//getLocationDescription

	public String calcRate(int field, int year) {
		float rate=0;
		if (isDuringGracePeriod()) {
			// During Jan/Feb use 2-4 instead
			switch(year) {
				case YEAR1: year=YEAR2; break;
				case YEAR2: year=YEAR3; break;
				case YEAR3: year=YEAR4; break;
			}
		}
		rate = ((float)stats[field][year]*200000) / stats[MAN_HOURS][year];
		try {
			return decFormatter.format(rate);
		}
		catch (Exception e) {return "";}//catch	
	}//calcRate

	public String getStat(int field, int year) {
		if (isDuringGracePeriod()) {
			// During Jan/Feb use 2-4 instead
			switch(year) {
				case YEAR1: year=YEAR2; break;
				case YEAR2: year=YEAR3; break;
				case YEAR3: year=YEAR4; break;
			}
		}
		try {return Float.toString(stats[field][year]);}
		catch (Exception e) {return "";}//catch
	}//calcRate

	private int getNumberNA() {
		int temp = 0;
		boolean[] bna = {na1, na2, na3};
		if (duringGracePeriod) {
			// we're during the grace period, so use na2, na3,and na4 (which doesn't, so assume true)
			bna[0] = na2;
			bna[1]= na3;
			bna[2] = false;
		}
		for(int i = 0; i < 3; i++)
			if(bna[i])
				temp++;

		return temp;
	}
	public String calcAverageStat(int i) {
		if (getNumberNA() == 3) return "N/A";
		int s1 = stats[i][YEAR1];
		int s2 = stats[i][YEAR2];
		int s3 = stats[i][YEAR3];
		// if today is during the grace period (Jan-Feb), then use years 2,3,& 4 for avg
		if (duringGracePeriod) s1 = stats[i][YEAR4];
		
		long total = s1 + s2 + s3;

		float avg = total/(3-getNumberNA());
		return decFormatter.format(avg);
	}//calcAverage

	public String calcTotalStat(int i) {
		float temp = (float)(stats[i][YEAR1] + stats[i][YEAR2] + stats[i][YEAR3]);
		if (duringGracePeriod) temp = (float)(stats[i][YEAR2] + stats[i][YEAR3] + stats[i][YEAR4]);
		try {return decFormatter.format(temp);}
		catch (Exception e) {return "";}//catch	
	}//calcAverage

	private float calcRateTemp(int year, int manhours) {
		float value = 0;
		value = year*200000;
		value = value/manhours;
		return value;
	}
	public String calcAverageRate(int i) {
		if (getNumberNA() == 3) return "N/A";
		
		float value1 = calcRateTemp(stats[i][YEAR1], stats[MAN_HOURS][YEAR1]);
		float value2 = calcRateTemp(stats[i][YEAR2], stats[MAN_HOURS][YEAR2]);
		float value3 = calcRateTemp(stats[i][YEAR3], stats[MAN_HOURS][YEAR3]);
		if (duringGracePeriod) value1 = calcRateTemp(stats[i][YEAR4], stats[MAN_HOURS][YEAR4]);
		
		float temp = (value1 + value2 + value3) / (3 - getNumberNA());
		try {return decFormatter.format(temp);}
		catch (Exception e) {return "";}//catch	
	}//calcAverage

	public boolean hasNext() throws Exception {
		if (!li.hasNext())
			return false;
		setFromDB((String)li.next());
		return true;
	}//hasNext

	public void setListFromDB(String cID) throws Exception {
		conID = cID;
		if ((null == conID) || ("".equals(conID)))
			throw new Exception("Can't set OSHA IDs list from DB because conID is not set");
		String selectQuery = "SELECT * FROM OSHA WHERE conID='"+conID+"' ORDER BY location;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			oshaIDsAL = new ArrayList<String>();
			while (SQLResult.next()) {
				oshaIDsAL.add(SQLResult.getString("OID"));
				isInDB = true;
			}//if
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
		li = oshaIDsAL.listIterator();
	}//setListFromDB
	
	public void setFromDB(String oshaID) throws Exception {
		if ((null == oshaID) || ("".equals(oshaID)))
			throw new Exception("Can't set OSHA info from DB because OID is not set");
		String Query = "SELECT * FROM OSHA WHERE OID='"+oshaID+"';";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next()) {
				setFromResultSet(SQLResult);
				isInDB = true;
			}//if
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setFromDB

	public void setCorporateFromDB(String conID) throws Exception {
		if ((null == conID) || ("".equals(conID)))
			throw new Exception("Can't set OSHA info from DB because conID is not set");
		String selectQuery = "SELECT * FROM OSHA WHERE conID='"+conID+"' AND location='Corporate';";
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			if (rs.next()) {
				setFromResultSet(rs);
				isInDB = true;
			}//if
			rs.close();
		}finally{
			DBClose();
		}//finally		
	}//setCorporateFromDB

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		OID = SQLResult.getString("OID");
		if(OID == null){
			OID = "";
			return;
		}
		conID = SQLResult.getString("conID");
		SHAType = SQLResult.getString("SHAType");
		location = SQLResult.getString("location");
		description = SQLResult.getString("OSHA.description");
		verifiedDate = SQLResult.getString("verifiedDate");
		auditorID = SQLResult.getString("auditorID");
		na1 = SQLResult.getString("NA1").equals("Yes") ? true : false;
		na2 = SQLResult.getString("NA2").equals("Yes") ? true : false;
		na3 = SQLResult.getString("NA3").equals("Yes") ? true : false;
		
		stats[MAN_HOURS][YEAR1] = SQLResult.getInt("manHours1");
		stats[FATALITIES][YEAR1] = SQLResult.getInt("fatalities1");
		stats[LOST_WORK_CASES][YEAR1] = SQLResult.getInt("lostWorkCases1");
		stats[LOST_WORK_DAYS][YEAR1] = SQLResult.getInt("lostWorkDays1");
		stats[INJURY_ILLNESS_CASES][YEAR1] =  SQLResult.getInt("injuryIllnessCases1");
		stats[RESTRICTED_WORK_CASES][YEAR1] = SQLResult.getInt("restrictedWorkCases1");
		stats[RECORDABLE_TOTAL][YEAR1] = SQLResult.getInt("recordableTotal1");
		
		stats[MAN_HOURS][YEAR2] = SQLResult.getInt("manHours2");
		stats[FATALITIES][YEAR2] =  SQLResult.getInt("fatalities2");
		stats[LOST_WORK_CASES][YEAR2] = SQLResult.getInt("lostWorkCases2");
		stats[LOST_WORK_DAYS][YEAR2] = SQLResult.getInt("lostWorkDays2");
		stats[INJURY_ILLNESS_CASES][YEAR2] = SQLResult.getInt("injuryIllnessCases2");
		stats[RESTRICTED_WORK_CASES][YEAR2] =  SQLResult.getInt("restrictedWorkCases2");
		stats[RECORDABLE_TOTAL][YEAR2] = SQLResult.getInt("recordableTotal2");
		
		stats[MAN_HOURS][YEAR3] =  SQLResult.getInt("manHours3");
		stats[FATALITIES][YEAR3] =  SQLResult.getInt("fatalities3");
		stats[LOST_WORK_CASES][YEAR3] =  SQLResult.getInt("lostWorkCases3");
		stats[LOST_WORK_DAYS][YEAR3] =  SQLResult.getInt("lostWorkDays3");
		stats[INJURY_ILLNESS_CASES][YEAR3] =  SQLResult.getInt("injuryIllnessCases3");
		stats[RESTRICTED_WORK_CASES][YEAR3] =  SQLResult.getInt("restrictedWorkCases3");
		stats[RECORDABLE_TOTAL][YEAR3] =  SQLResult.getInt("recordableTotal3");
		
		stats[MAN_HOURS][YEAR4] =  SQLResult.getInt("manHours4");
		stats[FATALITIES][YEAR4] =  SQLResult.getInt("fatalities4");
		stats[LOST_WORK_CASES][YEAR4] =  SQLResult.getInt("lostWorkCases4");
		stats[LOST_WORK_DAYS][YEAR4] =  SQLResult.getInt("lostWorkDays4");
		stats[INJURY_ILLNESS_CASES][YEAR4] =  SQLResult.getInt("injuryIllnessCases4");
		stats[RESTRICTED_WORK_CASES][YEAR4] =  SQLResult.getInt("restrictedWorkCases4");
		stats[RECORDABLE_TOTAL][YEAR4] =  SQLResult.getInt("recordableTotal4");  
		//Files
		file1YearAgo = SQLResult.getString("file1YearAgo");
		file2YearAgo = SQLResult.getString("file2YearAgo");
		file3YearAgo = SQLResult.getString("file3YearAgo");
	}//setFromResultSet

	public void writeToDB(String cID) throws Exception {
		conID = cID;
		try{
			DBReady();
			if (!isInDB) {
				String insertQuery = "INSERT INTO OSHA (conID) VALUES ("+conID+");";
				SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ResultSet SQLResult = SQLStatement.getGeneratedKeys();
				if (SQLResult.next())
					OID = SQLResult.getString("GENERATED_KEY");
				else {
					SQLResult.close();
					DBClose();
					throw new Exception("No OID returned after inserting new OSHA record");
				}//else
			}//if
			String updateQuery = "UPDATE OSHA SET "+
				"SHAType='"+Utilities.escapeQuotes(SHAType)+
				"',location='"+Utilities.escapeQuotes(location)+
				"',description='"+Utilities.escapeQuotes(description)+
//Year 1
				"',manHours1='"+stats[MAN_HOURS][YEAR1]+
				"',fatalities1='"+stats[FATALITIES][YEAR1]+
				"',lostWorkCases1='"+stats[LOST_WORK_CASES][YEAR1]+
				"',lostWorkDays1='"+stats[LOST_WORK_DAYS][YEAR1]+
				"',injuryIllnessCases1='"+stats[INJURY_ILLNESS_CASES][YEAR1]+
				"',restrictedWorkCases1='"+stats[RESTRICTED_WORK_CASES][YEAR1]+
				"',recordableTotal1='"+stats[RECORDABLE_TOTAL][YEAR1]+
				"',NA1='"+convertNA(na1)+
//Year 2
				"',manHours2='"+stats[MAN_HOURS][YEAR2]+
				"',fatalities2='"+stats[FATALITIES][YEAR2]+
				"',lostWorkCases2='"+stats[LOST_WORK_CASES][YEAR2]+
				"',lostWorkDays2='"+stats[LOST_WORK_DAYS][YEAR2]+
				"',injuryIllnessCases2='"+stats[INJURY_ILLNESS_CASES][YEAR2]+
				"',restrictedWorkCases2='"+stats[RESTRICTED_WORK_CASES][YEAR2]+
				"',recordableTotal2='"+stats[RECORDABLE_TOTAL][YEAR2]+
				"',NA2='"+convertNA(na2)+
//Year 3
				"',manHours3='"+stats[MAN_HOURS][YEAR3]+
				"',fatalities3='"+stats[FATALITIES][YEAR3]+
				"',lostWorkCases3='"+stats[LOST_WORK_CASES][YEAR3]+
				"',lostWorkDays3='"+stats[LOST_WORK_DAYS][YEAR3]+
				"',injuryIllnessCases3='"+stats[INJURY_ILLNESS_CASES][YEAR3]+
				"',restrictedWorkCases3='"+stats[RESTRICTED_WORK_CASES][YEAR3]+
				"',recordableTotal3='"+stats[RECORDABLE_TOTAL][YEAR3]+
				"',NA3='"+convertNA(na3)+
				"',verifiedDate='"+verifiedDate+
				"',auditorID='"+auditorID+
				
				"' WHERE OID="+OID+";";
				System.out.println("OshaBean:WriteToDB = " + updateQuery);
				SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public void updateNumRequired(String cID) throws Exception {
		if (!"Corporate".equals(location))
			return;
		com.picsauditing.PICS.pqf.CategoryBean pcBean = new com.picsauditing.PICS.pqf.CategoryBean();
		int numRequired = 9;
		int requiredCompleted = 0;
		//Adjust for NA
		if(na1)
			requiredCompleted+=3;
		if(na2)
			requiredCompleted+=3;		
		if(na3)
			requiredCompleted+=3;
		
		//Adjust for man hours
		if(!na1 && 0 != stats[MAN_HOURS][YEAR1]){
			requiredCompleted+=1;
			numRequired = 6;
		}		
		if(!na2 && 0 != stats[MAN_HOURS][YEAR2]){
			requiredCompleted+=1;
			numRequired = 6;
		}
		if(!na3 && 0 != stats[MAN_HOURS][YEAR3]){
			requiredCompleted+=1;
			numRequired = 6;
		}
		
		//Adjust for file uploads
		if(!na1 && "Yes".equals(file1YearAgo)){
			requiredCompleted+=1;
			numRequired = 6;
		}		
		if(!na2 && "Yes".equals(file2YearAgo)){
			requiredCompleted+=1;
			numRequired = 6;
		}
		if(!na3 && "Yes".equals(file3YearAgo)){
			requiredCompleted+=1;
			numRequired = 6;
		}
					
		String percentCompleted = "100";
		if(numRequired !=0)			
			percentCompleted = intFormatter.format(((float)requiredCompleted*100)/numRequired);		

		pcBean.replaceCatData("29",cID,"Yes",""+requiredCompleted,""+numRequired,percentCompleted);	
	}//udpateNumRequired
	
	public void setFromRequest(javax.servlet.http.HttpServletRequest r) throws Exception {
		OID = r.getParameter("OID");
		SHAType = r.getParameter("SHAType");
		location = r.getParameter("location");
		description = r.getParameter("description");

// Year 1
		stats[MAN_HOURS][YEAR1] = parseInt(r.getParameter("manHours1"));
		stats[FATALITIES][YEAR1] = parseInt(r.getParameter("fatalities1"));
		stats[LOST_WORK_CASES][YEAR1] = parseInt(r.getParameter("lostWorkCases1"));
		stats[LOST_WORK_DAYS][YEAR1] = parseInt(r.getParameter("lostWorkDays1"));
		stats[INJURY_ILLNESS_CASES][YEAR1] = parseInt(r.getParameter("injuryIllnessCases1"));
		stats[RESTRICTED_WORK_CASES][YEAR1] = parseInt(r.getParameter("restrictedWorkCases1"));
		stats[RECORDABLE_TOTAL][YEAR1] = parseInt(r.getParameter("recordableTotal1"));
		na1 = convertNA(r.getParameter("na1"));
// Year 2
		stats[MAN_HOURS][YEAR2] = parseInt(r.getParameter("manHours2"));
		stats[FATALITIES][YEAR2] = parseInt(r.getParameter("fatalities2"));
		stats[LOST_WORK_CASES][YEAR2] = parseInt(r.getParameter("lostWorkCases2"));
		stats[LOST_WORK_DAYS][YEAR2] = parseInt(r.getParameter("lostWorkDays2"));
		stats[INJURY_ILLNESS_CASES][YEAR2] = parseInt(r.getParameter("injuryIllnessCases2"));
		stats[RESTRICTED_WORK_CASES][YEAR2] = parseInt(r.getParameter("restrictedWorkCases2"));
		stats[RECORDABLE_TOTAL][YEAR2] = parseInt(r.getParameter("recordableTotal2"));
		na2 = convertNA(r.getParameter("na2"));
// Year 3
		stats[MAN_HOURS][YEAR3] = parseInt(r.getParameter("manHours3"));
		stats[FATALITIES][YEAR3] = parseInt(r.getParameter("fatalities3"));
		stats[LOST_WORK_CASES][YEAR3] = parseInt(r.getParameter("lostWorkCases3"));
		stats[LOST_WORK_DAYS][YEAR3] = parseInt(r.getParameter("lostWorkDays3"));
		stats[INJURY_ILLNESS_CASES][YEAR3] = parseInt(r.getParameter("injuryIllnessCases3"));
		stats[RESTRICTED_WORK_CASES][YEAR3] = parseInt(r.getParameter("restrictedWorkCases3"));
		stats[RECORDABLE_TOTAL][YEAR3] = parseInt(r.getParameter("recordableTotal3"));
		na3 = convertNA(r.getParameter("na3"));
	}//setFromRequest

	public boolean isOK() {
		errorMessages = new Vector<String>();
		if (SHA_TYPE_DEFAULT.equals(SHAType))
			errorMessages.addElement("Please select if you are reporting OSHA or MSHA statistics");
		return (errorMessages.size() == 0);
	}//isOK

	public int parseInt(String s) {
		try {
			return decFormatter.parse(s, new java.text.ParsePosition(0)).intValue();
		} catch (Exception e) {
			return 0;
		}//catch
	}//parseInt

	public String formatNumber(int i) {
		return intFormatter.format(i);
	}//formatNumber
	
	public void updateFilesDB(HttpServletRequest request) throws Exception {
		Map<String,String> fields = (Map<String,String>)request.getAttribute("uploadfields");
		String fn1 = fields.get("osha1_file");
		String fn2 = fields.get("osha2_file");
		String fn3 = fields.get("osha3_file");
		String OID = fields.get("OID");
		
		if(fn1 != "") 
			file1YearAgo = "Yes";
		
		if(fn2 != "") 
			file2YearAgo = "Yes";
		
		if(fn3 != "") 
			file3YearAgo = "Yes";
		
		String updateQuery = "UPDATE OSHA SET file1YearAgo='"+file1YearAgo+"',file2YearAgo='"+file2YearAgo+
		"',file3YearAgo='"+file3YearAgo+"' WHERE OID='"+OID+"';";
		
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally		
	}
	
	public void setShowLinks(PermissionsBean pBean) {
		if (pBean.isAdmin() || pBean.isAuditor())
			showLinks = true;
		if ((pBean.isOperator() || pBean.isCorporate()) && pBean.canSeeSet.contains(conID))
			showLinks = true;
		if (pBean.isContractor() && pBean.userID.equals(conID))
			showLinks = true;
	}//setShowLinks

	public String getFile1YearAgoLink() {
		if ("No".equals(file1YearAgo))	return "No";
		else if (!showLinks)	return "Yes";
		else	return "<a href=\"#\" onClick=\"window.open('/servlet/showpdf?id="+conID+"&OID="+OID+ 
			"&file=osha1','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450')\" onMouseOver=\"status='Osha 300 Logs';return true\">Yes</a>";
	}//getFile1YearAgoLink
	public String getFile2YearAgoLink() {
		if ("No".equals(file2YearAgo))	return "No";
		else if (!showLinks)	return "Yes";
		else	return "<a href=\"#\" onClick=\"window.open('/servlet/showpdf?id="+conID+"&OID="+OID+
			"&file=osha2','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450')\" onMouseOver=\"status='Osha 300 Logs';return true\">Yes</a>";
	}//getFile2YearAgoLink
	public String getFile3YearAgoLink() {
		if ("No".equals(file3YearAgo))	return "No";
		else if (!showLinks)	return "Yes";
		else	return "<a href=\"#\" onClick=\"window.open('/servlet/showpdf?id="+conID+"&OID="+OID+
			"&file=osha3','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450')\" onMouseOver=\"status='Osha 300 Logs';return true\">Yes</a>";
	}//getFile3YearAgoLink

	public String getFile1YearAgoIconLink() {
		if ("No".equals(file1YearAgo))	return "<img src=\"images/iconGhost_safety.gif\" width=\"20\" height=\"20\" border=\"0\">";
		else	return "<a href=\"#\" onClick=\"window.open('/servlet/showpdf?id="+conID+"&OID="+OID+
			"&file=osha1','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450')\" onMouseOver=\"status='Osha 300 Logs';return true\">" + 
			"<img src=\"images/icon_safety.gif\" width=\"20\" height=\"20\" border=\"0\"></a>";
	}//getFile1YearAgoIconLink
	public String getFile2YearAgoIconLink() {
		if ("No".equals(file2YearAgo))	return "<img src=\"images/iconGhost_safety.gif\" width=\"20\" height=\"20\" border=\"0\">";
		else	return "<a href=\"#\" onClick=\"window.open('/servlet/showpdf?id="+conID+"&OID="+OID+
			"&file=osha2','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450')\" onMouseOver=\"status='Osha 300 Logs';return true\">" + 
			"<img src=\"images/icon_safety.gif\" width=\"20\" height=\"20\" border=\"0\"></a>";
	}//getFile2YearAgoIconLink
	public String getFile3YearAgoIconLink() {
		if ("No".equals(file3YearAgo))	return "<img src=\"images/iconGhost_safety.gif\" width=\"20\" height=\"20\" border=\"0\">";
		else	return "<a href=\"#\" onClick=\"window.open('/servlet/showpdf?id="+conID+"&OID="+OID+
			"&file=osha3','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450')\" onMouseOver=\"status='Osha 300 Logs';return true\">" + 
			"<img src=\"images/icon_safety.gif\" width=\"20\" height=\"20\" border=\"0\"></a>";
	}//getFile3YearAgoIconLink

	public void test(String path) throws Exception {
	// called oBean.test(config.getServletContext().getRealPath("/"));
/*		DBReady();
		String Query = "SELECT * FROM OSHA;";
		ResultSet SQLResult = SQLStatement.executeQuery(Query);
		int count = 1;
		while (SQLResult.next()) {
			setFromResultSet(SQLResult);
			java.io.File f = null;
			java.io.File newF = null;
			f = new java.io.File(path+"files/oshas/osha2_"+conID+".pdf");
			newF = new java.io.File(path+"files/oshas/osha2_"+OID+".pdf");
			if (f.exists())
				f.renameTo(newF);
			f = new java.io.File(path+"files/oshas/osha3_"+conID+".pdf");
			newF = new java.io.File(path+"files/oshas/osha3_"+OID+".pdf");
			if (f.exists())
				f.renameTo(newF);
			f = new java.io.File(path+"files/oshas/osha4_"+conID+".pdf");
			newF = new java.io.File(path+"files/oshas/osha4_"+OID+".pdf");
			if (f.exists())
				f.renameTo(newF);
//			String Query2 = "UPDATE OSHA SET OID="+count+" WHERE conID="+conID+" LIMIT 1;";
//			SQLStatement.executeUpdate(Query2);
			count++;
		}//if
		SQLResult.close();
		DBClose();
*/	}//test

	public void deleteLocation(String deleteID, String path) throws Exception {
		String deleteQuery = "DELETE FROM OSHA WHERE OID="+deleteID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
		// Delete OSHA files 
		java.io.File f = null;
		f = new java.io.File(path+"/files/oshas/osha1_"+deleteID+".pdf");
		if (f.exists())		f.delete();
		f = new java.io.File(path+"/files/oshas/osha2_"+deleteID+".pdf");
		if (f.exists())		f.delete();
		f = new java.io.File(path+"/files/oshas/osha3_"+deleteID+".pdf");
		if (f.exists())		f.delete();
	}//deleteLocation

	public void setOSHAoesNotApply(String conID) throws Exception {
		String Query = "REPLACE INTO pqfCatData (catID,conID,applies) VALUES ("+
				com.picsauditing.PICS.pqf.CategoryBean.OSHA_CATEGORY_ID+","+conID+",'No');";
		try{
			DBReady();
			SQLStatement.executeUpdate(Query);
		}finally{
			DBClose();
		}//finally
	}//setOSHAoesNotApply

	public String getDateVerifiedView() {
		if ("".equals(verifiedDate))
			return "";
		return "<img src=images/okCheck.gif width=19 height=15><span class=greenMain>Verified on "+verifiedDate+"</span>";
	}//getDateVerifiedView

	public boolean isNa1() {
		return na1;
	}

	public boolean isNa2() {
		return na2;
	}

	public boolean isNa3() {
		return na3;
	}

	public boolean isDuringGracePeriod() {
		return duringGracePeriod;
	}

	public void setDuringGracePeriod(boolean duringGracePeriod) {
		this.duringGracePeriod = duringGracePeriod;
	}
	
	public String convertNA(boolean b){
		if(b)
			return "Yes";
		else
			return "No";
	}
	
	private boolean convertNA(String str){
		if(str.equals("Yes"))
			return true;
		else
			return false;
	}
		
	
	
}//OSHABean