package com.picsauditing.PICS.redFlagReport;

import java.sql.*;
import java.util.*;
import com.picsauditing.PICS.*;
import com.picsauditing.jpa.entities.AuditType;

/**
 * The list of PQF questions that are marked as RedFlag report criteria questions
 * 
 * @author Jeff Jensen
 */
public class HurdleQuestions extends DataBean {
	public int auditTypeID;
	//public String auditType = "";
	public String questionID = "";
	public String catNum = "";
	public String subCatNum = "";
	public String questionNum = "";
	public String question = "";
	public String questionType = "";
	public ArrayList<String> qIDsAL = null;
	static final String[] COMPARISON_NUMBER_ARRAY = {">","<","="};
	static final String[] COMPARISON_CHECKED_ARRAY = {"=","!="};
	static final String[] VALUE_CHECKED_ARRAY = {"X","Checked","-","Not Checked"};

	public int count = 0;
	ResultSet listRS = null;

	public String getComparisonInput(String comparison){
		if ("Decimal Number".equals(questionType))
			return Inputs.inputSelect("hurdleComparisonQ_"+questionID,"forms",comparison,COMPARISON_NUMBER_ARRAY);
		if ("Check Box".equals(questionType) || "Yes/No/NA".equals(questionType) || 
				"Yes/No".equals(questionType) || "Manual".equals(questionType))
			return Inputs.inputSelect("hurdleComparisonQ_"+questionID,"forms",comparison,COMPARISON_CHECKED_ARRAY);
		return "<input type=hidden name=hurdleComparisonQ_"+questionID+" value=0>";
	}//getComparisonInput

	public String getValueInput(String value){
		if ("Decimal Number".equals(questionType))
			return "<input class=forms type=text size=5 name=hurdleValueQ_"+questionID+" value='"+value+"'>";
		if ("Check Box".equals(questionType))
			return Inputs.inputSelect2("hurdleValueQ_"+questionID,"forms",value,VALUE_CHECKED_ARRAY);
		if ("Yes/No/NA".equals(questionType) || "Yes/No".equals(questionType) || "Manual".equals(questionType))
			return Inputs.inputSelect("hurdleValueQ_"+questionID,"forms",value,Inputs.YES_NO_NA_ARRAY);
		return "<input type=hidden name=hurdleValueQ_"+questionID+" value=0>";
	}//getValueInput
	
	public ArrayList<String> getQIDsAL() throws Exception {
		if (null != qIDsAL)
			return qIDsAL;
		qIDsAL = new ArrayList<String>();
		setList();
		while (isNext())
			qIDsAL.add(questionID);
		closeList();
		return qIDsAL;
	}//getQIDsAL

	public void setList() throws Exception {
		String selectQuery = "SELECT pc.auditTypeID, pc.number, ps.number, pq.number, questionID, question, questionType "+
				"FROM pqfCategories pc " +
				"INNER JOIN pqfSubCategories ps ON (catID=categoryID) "+
				"INNER JOIN pqfQuestions pq ON (subCatID=subCategoryID) WHERE isRedFlagQuestion='Yes' "+
				"ORDER BY pc.number, ps.number, pq.number";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			count = 0;
		}catch(Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setList

	public boolean isNext() throws Exception {
		if (!listRS.next())
			return false;
		count++;
		setFromResultSet(listRS);
		return true;
	}//isNextRecord

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		//auditType = SQLResult.getString("auditType");
		auditTypeID = SQLResult.getInt("auditTypeID");
		questionID = SQLResult.getString("questionID");
		catNum = SQLResult.getString("pc.number");
		subCatNum = SQLResult.getString("ps.number");
		questionNum = SQLResult.getString("pq.number");
		question = SQLResult.getString("question");
		questionType = SQLResult.getString("questionType");
	}//setFromResultSet
	
	public void setEmrAveQuestion() throws Exception {
		//auditType = "PQF";
		auditTypeID = AuditType.PQF;
		questionID = FlagCriteria.EMR_AVE_QUESTION_ID;
		question = "What is your EMR average for the last 3 years?";
		questionType = "Decimal Number";
	}

	public void closeList() throws Exception {
		count = 0;
		if (null != listRS) {
			listRS.close();
			listRS = null;
		}//if
		DBClose();
	}
}
