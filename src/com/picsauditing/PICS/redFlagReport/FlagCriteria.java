package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.util.*;
import com.picsauditing.PICS.*;
import com.picsauditing.jpa.entities.MultiYearScope;

/**
 * A FlagCriteria is a set of criteria for a given Operator used to set contractors to a specific flag color
 * SELECT, INSERT, and DELETE functionality is included for the flagCriteria database table
 *  
 * Stores the criteria in a FlagCriteriaDO map. This stores ALL criteria for an operator
 * regardless if the operator uses the criteria or not (is checked).
 * 
 * @author Jeff Jensen
 */
public class FlagCriteria extends DataBean {
	public String opID = "";
	public String flagStatus = "";
	public FlagOshaCriteriaDO flagOshaCriteriaDO = null;
	public Map<String,FlagCriteriaDO> flagCriteriaMap = null;
	ArrayList<String> checkedQuestionIDsAL = null;

	public static final String EMR_AVE_QUESTION_ID = "0";
	private static final String[] TIME_ARRAY = {"1","3"};
	private static final String[] TIME_OPTIONS_ARRAY = {"Individual Yrs","3 Yr Avg"};
	private static final String[] FLAG_ARRAY = {"Red","Amber"};

	public FlagCriteriaDO getFlagCriteriaDO(String questionID){
		return flagCriteriaMap.get(questionID);
	}

	public void setFromDB(String op_ID,String fStatus) throws Exception {
		opID = op_ID;
		flagStatus = fStatus;
		checkedQuestionIDsAL = null;
		try{
			flagOshaCriteriaDO = new FlagOshaCriteriaDO();
			flagOshaCriteriaDO.setFromDB(opID, flagStatus);
			
			DBReady();
			String selectQuery = "SELECT * FROM flagCriteria WHERE opID="+Utilities.intToDB(opID)
					+" AND flagStatus='"+Utilities.escapeQuotes(flagStatus)+"'";
			flagCriteriaMap = new TreeMap<String, FlagCriteriaDO>();
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			while (rs.next()){
				// Create a data object and fill it with the result set
				FlagCriteriaDO flagCriteriaDO = new FlagCriteriaDO();
				flagCriteriaDO.setFromResultSet(rs);
				// stuff the DO into the map using the questionID as the key
				flagCriteriaMap.put(rs.getString("questionID"),flagCriteriaDO);
			}
			rs.close();
		}finally{
			DBClose();
		}
	}

	@SuppressWarnings("unchecked")
	public void setFromRequest(javax.servlet.http.HttpServletRequest request) throws Exception {
		flagOshaCriteriaDO.setFromRequest(request);
		Enumeration e = request.getParameterNames();
		flagCriteriaMap = new TreeMap<String,FlagCriteriaDO>();
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("hurdleQuestion_")) {
				String qID = temp.substring(15);
				String questionType = request.getParameter("hurdleTypeQ_"+qID);
				String value = request.getParameter("hurdleValueQ_"+qID);
				String comparison = request.getParameter("hurdleComparisonQ_"+qID);
				String tempIsFlagged= Utilities.getIsChecked(request.getParameter("flagQ_"+qID));
				String scope = request.getParameter("hurdleScope_"+qID);
				MultiYearScope multiYearScope = null;
				if (scope != null)
					multiYearScope = MultiYearScope.valueOf(scope);
				flagCriteriaMap.put(qID,new FlagCriteriaDO(opID,qID,flagStatus,tempIsFlagged,questionType,comparison,value,multiYearScope));
			}
		}
	}

	public void writeToDB() throws Exception {
		if ((null == opID) || ("".equals(opID)))
			throw new Exception("can't write operator info to DB because id is not set");
		flagOshaCriteriaDO.writeToDB();

		StringBuffer insertQuery = new StringBuffer("INSERT INTO flagCriteria (opID, questionID, flagStatus, isChecked, comparison, value, multiYearScope) VALUES ");
		boolean doInsert = false;
		if (null != flagCriteriaMap && flagCriteriaMap.size()>0)
			doInsert = true;
			for(FlagCriteriaDO flagCriteriaDO: flagCriteriaMap.values()) {
				insertQuery.append("("+opID+",").append(flagCriteriaDO.questionID).append(",'").append(flagCriteriaDO.flagStatus).
						append("','").append(flagCriteriaDO.isChecked).append("','").append(flagCriteriaDO.comparison).
						append("','").append(flagCriteriaDO.value).append("','").append(flagCriteriaDO.multiYearScope).append("'),");
			}
		try{
			DBReady();
			String deleteQuery = "DELETE FROM flagCriteria WHERE opID="+opID+" AND flagStatus='"+flagStatus+"' AND (questionID<>401 AND questionID<>755);";
			SQLStatement.executeUpdate(deleteQuery);
			if (doInsert)
				SQLStatement.executeUpdate(insertQuery.substring(0,insertQuery.length()-1));
		}finally{
			DBClose();
		}
	}

	public void writeNewToDB(String op_ID) throws Exception {
		opID = op_ID;
		flagOshaCriteriaDO.writeNewToDB(opID);
	}//writeNewToDB

	public boolean isOK(){
		return flagOshaCriteriaDO.isOK();
	} // isOK
	
	/**
	 * containing any questions from the flagCriteriaMap 
	 * that are currently "checked" for this operator (ie active parameters)
	 * 
	 * @return list of PQF question IDs
	 */
	public ArrayList<String> getCheckedQuestionIDsAL(){
		if (null == checkedQuestionIDsAL){
			checkedQuestionIDsAL = new ArrayList<String>();
			for(FlagCriteriaDO flagCriteriaDO: flagCriteriaMap.values()) {
				if (flagCriteriaDO.isChecked())
					checkedQuestionIDsAL.add(flagCriteriaDO.questionID);
			}
		}
		return checkedQuestionIDsAL;
	}//getFlaggedQuestionIDsAL

	public String getIsCheckedFromMap(String questionID){
		if (!flagCriteriaMap.containsKey(questionID))
			return "No";
		return (flagCriteriaMap.get(questionID).isChecked);
	}

	public String getValueFromMap(String questionID){
		if (!flagCriteriaMap.containsKey(questionID))
			return "";
		return (flagCriteriaMap.get(questionID).value);
	}

	public String getComparisonFromMap(String questionID){
		if (!flagCriteriaMap.containsKey(questionID))
			return "";
		return (flagCriteriaMap.get(questionID).comparison);
	}

	public MultiYearScope getScopeFromMap(String questionID){
		if (!flagCriteriaMap.containsKey(questionID))
			return null;
		return (flagCriteriaMap.get(questionID).multiYearScope);
	}

	public String getTimeRadio(String name,String classType,String selected) {
		return Inputs.getRadioInputWithOptions(name,classType,selected, TIME_ARRAY, TIME_OPTIONS_ARRAY);
	}

	public String getFlagStatusSelect(String name,String classType,String selected) {
		return Inputs.inputSelectSubmit(name, classType, selected,FLAG_ARRAY);
	}//getFlagStatusSelect
}//redFlagBean
