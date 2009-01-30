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
@SuppressWarnings("serial")
public class HurdleQuestions extends DataBean {
	public int auditTypeID;
	public String questionID = "";
	public String catNum = "";
	public String subCatNum = "";
	public String questionNum = "";
	public String question = "";
	public String questionType = "";
	public String classType = "";
	public ArrayList<String> qIDsAL = null;
	static final String[] COMPARISON_NUMBER_ARRAY = {">","<","="};
	static final String[] COMPARISON_CHECKED_ARRAY = {"=","!="};
	static final String[] VALUE_CHECKED_ARRAY = {"X","Checked","-","Not Checked"};

	public int count = 0;
	ResultSet listRS = null;

	public String getComparisonInput(String comparison){
		if ("Decimal Number".equals(questionType) || "Money".equals(questionType))
			return Inputs.inputSelect("hurdleComparisonQ_"+questionID,"forms",comparison,COMPARISON_NUMBER_ARRAY);
		if ("Check Box".equals(questionType) || "Yes/No/NA".equals(questionType) || 
				"Yes/No".equals(questionType) || "Manual".equals(questionType))
			return Inputs.inputSelect("hurdleComparisonQ_"+questionID,"forms",comparison,COMPARISON_CHECKED_ARRAY);
		return "<input type=hidden name=hurdleComparisonQ_"+questionID+" value=0>";
	}//getComparisonInput

	public String getValueInput(String value){
		if ("Decimal Number".equals(questionType) || "Money".equals(questionType))
			return "<input class=forms type=text size=10 name=hurdleValueQ_"+questionID+" value='"+value+"'>";
		if ("Check Box".equals(questionType))
			return Inputs.inputSelect2("hurdleValueQ_"+questionID,"forms",value,VALUE_CHECKED_ARRAY);
		if ("Yes/No/NA".equals(questionType) || "Yes/No".equals(questionType) || "Manual".equals(questionType))
			return Inputs.inputSelect("hurdleValueQ_"+questionID,"forms",value,Inputs.YES_NO_NA_ARRAY);
		return "<input type=hidden name=hurdleValueQ_"+questionID+" value=0>";
	}
	
	public void setList(String opID) throws Exception {
		String selectQuery = "SELECT at.classType, pc.auditTypeID, pc.number, ps.number, pq.number, pq.ID questionid, concat(case when at.classType = 'Policy' then concat(pc.category , ' - ') else '' end,  pq.question) question, questionType " +
								"FROM audit_type at " + 
								"JOIN pqfCategories pc ON (pc.auditTypeId = at.id)" +
								"JOIN pqfSubCategories ps ON (catID=categoryID) " +
								"JOIN pqfQuestions pq ON (subCatID=subCategoryID) " +
								"WHERE isRedFlagQuestion='Yes' " +
								"AND ( " + 
								"(pc.auditTypeID = 1 AND pc.catID IN (SELECT catID FROM pqfopmatrix WHERE opID = " + opID + ")) " +
								"OR (pc.auditTypeID > 1 AND pc.auditTypeID IN (SELECT auditTypeID FROM audit_operator WHERE opID = " + opID + " AND canSee = 1))" +
								") " + 
								"ORDER BY at.classType, at.auditName, pc.number, ps.number, pq.number";
		try {
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
	}

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		//auditType = SQLResult.getString("auditType");
		auditTypeID = SQLResult.getInt("auditTypeID");
		questionID = SQLResult.getString("questionID");
		catNum = SQLResult.getString("pc.number");
		subCatNum = SQLResult.getString("ps.number");
		questionNum = SQLResult.getString("pq.number");
		question = SQLResult.getString("question");
		questionType = SQLResult.getString("questionType");
		classType = SQLResult.getString("classType");
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
