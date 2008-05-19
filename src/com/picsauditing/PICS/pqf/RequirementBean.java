package com.picsauditing.PICS.pqf;

import java.sql.*;
import java.util.*;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.PICS.DateBean;

// Created on 2/3/0\6 by Jeff Jensen to generate requirements for audits
public class RequirementBean extends DataBean {
	public String rID = "";
	public String conID = "";
	public String questionID = "";
	public String num = "";
	public String category = "";
	public String answer = "";
	public String requirement1 = "";
	public String isReq1Complete = "";
	public String req1CompletedDate = "";
	public String requirement2 = "";
	public String req2CompletedDate = "";

	public CategoryBean pcBean = new CategoryBean();
	public SubCategoryBean psBean = new SubCategoryBean();
	public QuestionBean pqBean = new QuestionBean ();
	public DataBean pdBean = new DataBean();
	
	ResultSet listRS = null;
	int numResults = 0;
	public int count = 0;
	public String numReqCompleted = "0";
	
	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		rID = SQLResult.getString("rID");
		conID = SQLResult.getString("conID");
		questionID = SQLResult.getString("questionID");
		num = SQLResult.getString("num");
		category = SQLResult.getString("category");
		answer = SQLResult.getString("answer");
		requirement1 = SQLResult.getString("requirement1");
		isReq1Complete = SQLResult.getString("isReq1Complete");
		req1CompletedDate = DateBean.toShowFormat(SQLResult.getString("req1CompletedDate"));
		requirement2 = SQLResult.getString("requirement2");
		req2CompletedDate = DateBean.toShowFormat(SQLResult.getString("req2CompletedDate"));
	}

	public void setList(int auditID) throws Exception {
		String selectQuery = "SELECT * FROM pqfCategories AS c " +
				"JOIN pqfSubCategories AS s ON catID=categoryID "+
				"JOIN pqfQuestions AS q ON subCategoryID=subCatID " +
				"JOIN pqfData AS d ON q.questionID=d.questionID "+
				"JOIN requirements AS r ON r.questionID=q.questionID AND r.conID=d.conID "+
				"WHERE auditID = "+auditID+" ORDER BY r.num";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}
	}


	public void updateNumbering(String conID, String auditType) throws Exception {
		ArrayList<String> updateQueries = new ArrayList<String>();
		setList(conID, auditType);
		while (isNextRecord())
			updateQueries.add("UPDATE requirements SET num='"+count+"' WHERE rID="+rID+";");
		try{
			DBReady();
			for(String udpateQuery:updateQueries)
				SQLStatement.executeUpdate(udpateQuery);
		}finally{
			DBClose();
		}//finally
	}//updateNumbering

	public boolean isNextRecord() throws Exception {
		try{
			if (!(count <= numResults && listRS.next()))
				return false;
			count++;
			setFromResultSet(listRS);
			pcBean.setFromResultSet(listRS);
			psBean.setFromResultSet(listRS);
			pqBean.setFromResultSet(listRS);
			pdBean.setFromResultSet(listRS);
			return true;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}
	}

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
		if ((count % 2) == 1)	return " bgcolor=FFFFFF";
		else	return "";
	}//getBGColor

	public void generateRequirements(String conID, String auditType) throws Exception {
		QuestionBean pqBean = new QuestionBean();
		DataBean pdBean = new DataBean();
		CategoryBean pcBean = new CategoryBean();
		String selectQuery = "SELECT * FROM pqfCategories AS c INNER JOIN pqfSubCategories AS s ON catID=categoryID "+
			"INNER JOIN pqfQuestions AS q ON subCategoryID=subCatID INNER JOIN pqfData AS d ON q.questionID=d.questionID "+
			"INNER JOIN requirements AS r ON r.questionID=q.questionID AND r.conID=d.conID "+
			"WHERE auditType='"+auditType+"' AND r.conID="+conID+" ORDER BY r.num;";
//SELECT * FROM pqfCategories AS c INNER JOIN pqfSubCategories AS s ON catID=categoryID INNER JOIN pqfQuestions AS q ON subCategoryID=subCatID INNER JOIN pqfData AS d ON q.questionID=d.questionID INNER JOIN requirements AS r ON r.questionID=q.questionID AND r.conID=d.conID WHERE auditType='Desktop' AND r.conID=249 ORDER BY r.num;
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			HashSet<String> alreadyAnswered = new HashSet<String>();
			while (listRS.next()) {
				pqBean.setFromResultSet(listRS);
				alreadyAnswered.add(pqBean.questionID);
			}//while
	//SELECT * FROM pqfCategories AS c INNER JOIN pqfSubCategories AS s ON catID=categoryID INNER JOIN pqfQuestions AS q ON subCategoryID=subCatID INNER JOIN pqfData AS d ON q.questionID=d.questionID  WHERE auditType='desktop' AND conID=249
			selectQuery = "SELECT * FROM pqfCategories AS c INNER JOIN pqfSubCategories AS s ON catID=categoryID "+
				"INNER JOIN pqfQuestions AS q ON subCategoryID=subCatID INNER JOIN pqfData AS d ON q.questionID=d.questionID "+
				"WHERE auditType='"+auditType+"' AND conID="+conID+";";
			listRS = SQLStatement.executeQuery(selectQuery);
			int reqCount = 0;
			boolean doUpdate = false;
			String replaceQuery = "REPLACE INTO requirements (conID,questionID,num,category,answer,requirement1) VALUES ";
			while (listRS.next()) {
				pqBean.setFromResultSet(listRS);
				pdBean.setFromResultSet(listRS);
				pcBean.setFromResultSet(listRS);
				if (-1 == pqBean.okAnswer.indexOf(pdBean.verifiedAnswer) && !alreadyAnswered.contains(pqBean.questionID)) {
					doUpdate = true;
					reqCount++;
					replaceQuery += "('"+conID+"',"+pqBean.questionID+","+reqCount+
						",'"+Utilities.escapeQuotes(pcBean.category)+"','"+Utilities.escapeQuotes(answer)+
						"','"+Utilities.escapeQuotes(pqBean.requirement)+"'),";
				}//if
			}//while
			replaceQuery = replaceQuery.substring(0,replaceQuery.length()-1);
			replaceQuery +=";";
			if (doUpdate)
				SQLStatement.executeUpdate(replaceQuery);
		}finally{
			DBClose();
		}//finally
	}//generateRequirements

/*	public void saveVerification(javax.servlet.http.HttpServletRequest request, String conID) throws Exception {
		DBReady();
		Enumeration e = request.getParameterNames();
		String query = "";
		boolean doUpdate = false;
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("pqfQuestionID_")) {
				doUpdate = true;
				String qID = temp.substring(14);
				String questionType = request.getParameter("pqfQuestionType_" + qID);
				String dateVerified = "";
				dateVerified = DateBean.toDBFormat(request.getParameter("dateVerified_" + qID));				
				if (!"".equals(dateVerified)) {
					query = "UPDATE pqfData SET dateVerified='"+Utilities.escapeQuotes(dateVerified)+"' WHERE conID='"+conID+"' AND questionID='"+qID+"'";
					SQLStatement.executeUpdate(query);
				} //if
			}//if
		}//while	
		DBClose();
	}//saveVerification
*/
	public String updateRequirements(javax.servlet.http.HttpServletRequest request, String conID) throws Exception {
		String updateQuery = "";
		Enumeration e = request.getParameterNames();
		int reqCompletedCount = 0;
		try{
			DBReady();
			while (e.hasMoreElements()) {
				String temp = (String)e.nextElement();
				if (temp.startsWith("requirement_")) {
					String rID = temp.substring(12);
					String requirement = request.getParameter("requirement_" + rID);
					String isReqComplete = request.getParameter("isReqComplete_" + rID);
					String reqCompletedDate = request.getParameter("reqCompletedDate_" + rID);
//				if (!"".equals(num))
//					throw new Exception ("id"+num
					updateQuery = "UPDATE requirements SET requirement1='"+Utilities.escapeQuotes(requirement)+
						"',isReq1Complete='"+isReqComplete+"',req1CompletedDate='"+DateBean.toDBFormat(reqCompletedDate)+
						"' WHERE rID="+rID+";";
						SQLStatement.executeUpdate(updateQuery);
					if ("Yes".equals(isReqComplete))
						reqCompletedCount++;
				}//if
			}//while
		}finally{
			DBClose();
		}//finally
		numReqCompleted = Integer.toString(reqCompletedCount);
		return updateQuery;
	}//updateRequirements
	
	public String getReqStyle() {
		if ("No".equals(isReq1Complete)) 
			return "redMain";
		return "blueMain";
	}//getReqStyle

	public boolean isOK() {
		return (errorMessages.size() == 0);
	}//isOK

	public String getStatus() {
		if ("Yes".equals(isReq1Complete))
			return "Closed on "+req1CompletedDate;
		return "Open";
	}//getStatus

	public void deleteRequirement(String delID) throws Exception {
		String deleteQuery = "DELETE FROM requirements WHERE rID="+delID+" LIMIT 1;";
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
	}//deleteRequirement
}//RequirementBean