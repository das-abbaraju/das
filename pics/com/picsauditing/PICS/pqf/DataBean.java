package com.picsauditing.PICS.pqf;

import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.picsauditing.PICS.*;

import com.picsauditing.servlet.upload.UploadConHelper;
import com.picsauditing.servlet.upload.UploadProcessorFactory;

// Created on 5/28/05 by Jeff Jensen to submit prequal data
// Modifed 12/27/05 by Brittney Jensen Added:
// Added TreeMap verifiedMap, function getDateVerified to retreive PQF verification date
public class DataBean extends com.picsauditing.PICS.DataBean {
	public String conID = "";
	public String questionID = "";
	public String dateVerified = "";
//	public String num = "";
	public String answer = "";
	public String comment = "";

	public String verifiedAnswer = "";
	public String auditorID = "";
	public String isCorrect = "";
	public String wasChanged = "";

	public String[] D_AND_B_OK = {"5A1","4A1","3A1","2A1","5A2","4A2","3A2","2A2",};

	public String catDoesNotApply = "No";
	public boolean alreadySavedCat = false;
	public TreeMap<String,String> QAMap = null;
	public TreeMap<String,String> QCMap = null;
	public TreeMap<String,String> catAnsweredMap = null;
	public TreeMap<String,String> catReqAnsweredMap = null;
	public TreeMap<String,String> catNumRequiredMap = null;
	public TreeMap<String,String> verifiedMap = null;
	boolean isComplete = false;
	boolean isClosed = false;

	ResultSet listRS = null;
	int numResults = 0;
	int count = 0;
	
	String extArr = "pdf,doc,jpg,txt,xls";

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		conID = SQLResult.getString("conID");
		questionID = SQLResult.getString("questionID");
//		num = SQLResult.getString("num");
		answer = SQLResult.getString("answer");
		comment = SQLResult.getString("comment");
		dateVerified = com.picsauditing.PICS.DateBean.toShowFormat(SQLResult.getString("dateVerified"));
		verifiedAnswer = SQLResult.getString("verifiedAnswer");
		auditorID = SQLResult.getString("auditorID");
		isCorrect = SQLResult.getString("isCorrect");
		wasChanged = SQLResult.getString("wasChanged");
	}//setFromResultSet

	public void setFromDB(String conID, String catID) throws Exception {
		if ((null == conID) || ("".equals(conID)))
			throw new Exception("Can't set pqfData from DB because conID is not set");
		try{
			String Query = "SELECT * FROM pqfCatData WHERE conID="+conID+" AND catID="+catID+";";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next()) {
				alreadySavedCat = true;
				String temp = SQLResult.getString("applies");
				if ("Yes".equals(temp))
					catDoesNotApply  = "No";
				else
					catDoesNotApply  = "Yes";
			}//if
			SQLResult.close();
			Query = "SELECT * FROM pqfData WHERE conID="+conID+" ORDER BY num;";
			SQLResult = SQLStatement.executeQuery(Query);
			QAMap = new TreeMap<String,String>();
			QCMap = new TreeMap<String,String>();
			verifiedMap = new TreeMap<String,String>();
			//		questionTextAnswerMap = new TreeMap();
			while (SQLResult.next()) {
				setFromResultSet(SQLResult);
				QAMap.put(questionID,answer);
				QCMap.put(questionID,comment);
				verifiedMap.put(questionID,dateVerified);
//			questionTextAnswerMap.put(id,textAnswer);
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

	public String getAnswer(String qID) throws Exception {
		if (null==QAMap)
			throw new Exception("QAMap is null");
		if (QAMap.containsKey(qID))
			return (String)QAMap.get(qID);
		return "";
	}//getAnswer

	public String getComment(String qID) throws Exception {
		if (null==QCMap)
			throw new Exception("QCMap is null");
		if (QCMap.containsKey(qID))
			return (String)QCMap.get(qID);
		return "";
	}//getComment

	public String getDateVerified(String qID) throws Exception {
		if (null==verifiedMap)
			throw new Exception("verifiedMap is null");
		if (verifiedMap.containsKey(qID))
			return (String)verifiedMap.get(qID);
		return "";
	}//getDateVerified

	public void setList(String conID, String catID) throws Exception {
		if ((null == conID) || ("".equals(conID)))
			throw new Exception("Can't set pqf from DB because conID is not set");
		if ((null == catID) || ("".equals(catID)))
			throw new Exception("Can't set pqf from DB because catID is not set");
		try{
			DBReady();
			String Query = "SELECT * FROM pqfData JOIN pqfQuestion USING(questionID) WHERE conID ="+
					conID+" AND catID="+catID+";";
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
		if ((count % 2) == 1)	return " bgcolor=FFFFFF";
		else	return "";
	}//getBGColor

	public void setFilledOut(String conID) throws Exception {
		String Query = "SELECT * FROM pqfCatData WHERE conID="+conID+";";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			catAnsweredMap = new TreeMap<String,String>();
			catReqAnsweredMap = new TreeMap<String,String>();
			catNumRequiredMap = new TreeMap<String,String>();
			while (SQLResult.next()) {
				String catID = SQLResult.getString("catID");
				String numAnswered = SQLResult.getString("numAnswered");
				String numRequired = SQLResult.getString("numRequired");
				String requiredCompleted = SQLResult.getString("requiredCompleted");
				String applies = SQLResult.getString("applies");
				if ("No".equals(applies)) {
					catAnsweredMap.put(catID,"NA");
					catReqAnsweredMap.put(catID,"NA");
					catReqAnsweredMap.put(catID,"NA");
				} else {
					catAnsweredMap.put(catID,numAnswered);
					catReqAnsweredMap.put(catID,requiredCompleted);
					catNumRequiredMap.put(catID,numRequired);
				}//else
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFilledOut

	public String getCatAnswer(String catID) throws Exception {
		if (null==catAnsweredMap)
			throw new Exception("catAnsweredMap is null");
		return (String)catAnsweredMap.get(catID);
	}//getcatAnswered

	public boolean doesCatApply(String catID) throws Exception {
		return !"NA".equals(getCatAnswer(catID));
	}//doesCatApply

	public String getFilledOut(String catID) throws Exception {
		if (null==catAnsweredMap)
			throw new Exception("catAnsweredMap is null");
		if (null==catReqAnsweredMap)
			throw new Exception("catReqAnsweredMap is null");
		String requiredCompleted = "Not Completed";
		String numAnswered = "Not Completed";
		String numRequired = "Dunno";
		String temp = "";
		if (catAnsweredMap.containsKey(catID))
			numAnswered = (String)catAnsweredMap.get(catID);
		if (catReqAnsweredMap.containsKey(catID))
			requiredCompleted = (String)catReqAnsweredMap.get(catID);
		if (catNumRequiredMap.containsKey(catID))
			numRequired= (String)catNumRequiredMap.get(catID);
		if ("NA".equals(numAnswered))
			return "NA <img src=images/okCheck.gif width=19 height=15>";
		else if ("Not Completed".equals(numAnswered))
			temp = "0%";
//		else if ("0".equals(numRequired))
//			temp = "100%";
		else {
//			int numerator = Integer.parseInt(numAnswered);
//			int denominator= Integer.parseInt(numQuestions);
			int numerator = Integer.parseInt(requiredCompleted);
			int denominator= Integer.parseInt(numRequired);
			if (0 == denominator)
				temp = "100%";
			else {
				if (CategoryBean.OSHA_CATEGORY_ID.equals(catID)) {
					numerator = Integer.parseInt(requiredCompleted);
					denominator= Integer.parseInt(numRequired);			
				}//if
				temp = getShowPercent(numerator,denominator)+"%";
			}//else
		}//else
		if (numRequired.equals(requiredCompleted))
			temp+= " <img src=images/okCheck.gif width=19 height=15>";
		else
			temp+= " <img src=images/notOkCheck.gif width=19 height=15>";
		return temp;
	}//getFilledOut
	
	public void savePQF(javax.servlet.http.HttpServletRequest request, String conID, String catID, String auditType, String userID) throws Exception {
		try{
			DBReady();
			Enumeration e = request.getParameterNames();
			String insertQuery = "REPLACE INTO pqfData (conID,questionID,answer,comment,wasChanged,dateVerified,auditorID,verifiedAnswer) VALUES ";
			boolean doUpdate = false;
			boolean catDoesNotApply = "Yes".equals(request.getParameter("catDoesNotApply"));
			if (catDoesNotApply) {
				String Query = "REPLACE INTO pqfCatData (catID,conID,applies,percentCompleted,percentVerified) VALUES ("+catID+","+conID+",'No',100,100);";
				SQLStatement.executeUpdate(Query);
				DBClose();
				return;
			}//if
			int requiredAnsweredCount = 0;
			int answeredCount = 0;
			int requiredCount = 0;
			int yesNACount = 0;
			TreeMap<String,String> tempQAMap = new TreeMap<String,String>();
			//first set tempQAMap with all the answers
			while (e.hasMoreElements()) {
				String temp = (String)e.nextElement();
				if (temp.startsWith("pqfQuestionID_")) {
					String qID = temp.substring(14);
					String questionType = request.getParameter("pqfQuestionType_" + qID);
					String answer = "";
					if ("Service".equals(questionType)) {
						String tempAnswer1 = request.getParameter("answer_"+qID+"_C");
						String tempAnswer2 = request.getParameter("answer_"+qID+"_S");
						if (null == tempAnswer1 )
							tempAnswer1 = "";
						if (null == tempAnswer2 )
							tempAnswer2 = "";
						answer = tempAnswer1+" "+tempAnswer2;
						if (" ".equals(answer))
							answer = "";
					} else if ("Date".equals(questionType)) {
						answer = com.picsauditing.PICS.DateBean.toDBFormat(request.getParameter("answer_" + qID));
					} else
						answer = request.getParameter("answer_" + qID);
					if (null == answer)
						answer = "";
					tempQAMap.put(qID,answer);
				}//if
			}//while

			e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String temp = (String)e.nextElement();
				if (temp.startsWith("pqfQuestionID_")) {
					String qID = temp.substring(14);
					String oldAnswer = request.getParameter("oldAnswer_"+qID);
					String oldComment = request.getParameter("oldComment_"+qID);
					String wasChanged = request.getParameter("wasChanged_"+qID);
					String questionType = request.getParameter("pqfQuestionType_"+qID);
					String dateVerified = request.getParameter("oldDateVerified_"+qID);
					String auditorID = request.getParameter("oldAuditorID_"+qID);
					String answer = "";
					String comment = "";
					if ("Service".equals(questionType)) {
						String tempAnswer1 = request.getParameter("answer_"+qID+"_C");
						String tempAnswer2 = request.getParameter("answer_"+qID+"_S");
						if (null == tempAnswer1 )
							tempAnswer1 = "";
						if (null == tempAnswer2 )
							tempAnswer2 = "";
						answer = tempAnswer1+" "+tempAnswer2;
						if (" ".equals(answer))
							answer = "";
					} else if ("Date".equals(questionType)) {
						answer = com.picsauditing.PICS.DateBean.toDBFormat(request.getParameter("answer_"+qID));
					} else
						answer = request.getParameter("answer_"+qID);
					if (null == answer)
						answer = "";
					comment = request.getParameter("comment_"+qID);
					wasChanged = request.getParameter("wasChanged_"+qID);
					String oldWasChanged = wasChanged;
					if ((Constants.DESKTOP_TYPE.equals(auditType) || Constants.DA_TYPE.equals(auditType) || 
							Constants.OFFICE_TYPE.equals(auditType)) && "No".equals(answer))
						wasChanged = "Yes";
					if (null == comment)
						comment = "";
					tempQAMap.put(qID,answer);
					boolean isRequired = "Yes".equals(request.getParameter("isRequired_"+qID));
					if ("Depends".equals(request.getParameter("isRequired_"+qID))) {
						String dependsOnQID = request.getParameter("dependsOnQID_"+qID);
						String dependsOnAnswer = request.getParameter("dependsOnAnswer_"+qID);
						if (dependsOnAnswer.equals((String)tempQAMap.get(dependsOnQID)))
							isRequired = true;
					}//if
					if (!answer.equals(oldAnswer) || !comment.equals(oldComment) || !wasChanged.equals(oldWasChanged)) {
						doUpdate = true;
						if ((Constants.DESKTOP_TYPE.equals(auditType) || Constants.DA_TYPE.equals(auditType) || 
								Constants.OFFICE_TYPE.equals(auditType)) && "Yes".equals(answer) && "No".equals(oldAnswer)){
							dateVerified = com.picsauditing.PICS.DateBean.getTodaysDate();
							auditorID = userID;
						}//if
						if ("".equals(wasChanged))
							wasChanged = "No";
						insertQuery += "('"+conID+"',"+qID+",'"+eqDB(answer)+"','"+eqDB(comment)+"','"+eqDB(wasChanged)+
								"','"+com.picsauditing.PICS.DateBean.toDBFormat(dateVerified)+"',"+Utilities.intToDB(auditorID)+",'"+eqDB(verifiedAnswer)+"'),";
					}//if
					if ("Yes".equals(answer) || "NA".equals(answer))
						yesNACount++;
					if (isRequired) {
						requiredCount++;
						if (!"".equals(answer) && !com.picsauditing.PICS.DateBean.NULL_DATE_DB.equals(answer))
							requiredAnsweredCount++;
					}//if
					if (!"".equals(answer))
						answeredCount++;
	//				String qNum = request.getParameter("pqfQuestionNum_" + qID);
	//				String question = request.getParameter("pqfQuestion_" + qID);
	//				if (null != answer && answer.length() != 0)
	//				else if ("Check Box".equals(questionType) && null == answer)
	//					insertQuery += "('"+conID+"','"+question+"',"+qID+","+qNum+",'N'),";
				}//if
			}//while
			insertQuery = insertQuery.substring(0,insertQuery.length()-1);
			insertQuery +=";";
			if (doUpdate)
				SQLStatement.executeUpdate(insertQuery);
			String tempPercentCompleted = getShowPercent(requiredAnsweredCount,requiredCount);
			String tempPercentVerified = getShowPercent(yesNACount,requiredCount);
			String updateQuery = "";
			if (Constants.DESKTOP_TYPE.equals(auditType) || Constants.DA_TYPE.equals(auditType) || Constants.OFFICE_TYPE.equals(auditType))
				updateQuery = "REPLACE INTO pqfCatData (catID,conID,applies,requiredCompleted,numAnswered,numRequired,percentCompleted,"+
					"percentVerified) VALUES ("+catID+","+conID+",'Yes',"+requiredAnsweredCount+","+answeredCount+","+requiredCount+","+
					tempPercentCompleted+","+tempPercentVerified+");";
			else
				updateQuery = "REPLACE INTO pqfCatData (catID,conID,applies,requiredCompleted,numAnswered,numRequired,percentCompleted) VALUES ("+
					catID+","+conID+",'Yes',"+requiredAnsweredCount+","+answeredCount+","+requiredCount+","+tempPercentCompleted+");";
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//savePQF



	public void savePQFUpload(javax.servlet.http.HttpServletRequest request, String conID, String catID, String auditType, String userID) throws Exception {
		try{
			DBReady();
			Enumeration e = request.getParameterNames();
			Map<String,String> params = (Map<String,String>)request.getAttribute("uploadfields");
			String insertQuery = "REPLACE INTO pqfData (conID,questionID,answer,comment,wasChanged,dateVerified,auditorID,verifiedAnswer) VALUES ";
			boolean doUpdate = false;
			boolean catDoesNotApply = "Yes".equals(params.get("catDoesNotApply"));
			if (catDoesNotApply) {
				String Query = "REPLACE INTO pqfCatData (catID,conID,applies,percentCompleted,percentVerified) VALUES ("+catID+","+conID+",'No',100,100);";
				SQLStatement.executeUpdate(Query);
				DBClose();
				return;
			}//if
			int requiredAnsweredCount = 0;
			int answeredCount = 0;
			int requiredCount = 0;
			int yesNACount = 0;
			TreeMap<String,String> tempQAMap = new TreeMap<String,String>();
			boolean bUploadsOnly = true;
			//first set tempQAMap with all the answers
			Iterator iter = params.keySet().iterator();
			while (iter.hasNext()) {
				String temp = (String)iter.next();
				if (temp.startsWith("pqfQuestionID_")) {
					String qID = temp.substring(14);
					String questionType = params.get("pqfQuestionType_" + qID);
					if(!"File".equals(questionType) && bUploadsOnly)
						bUploadsOnly = false;
					String answer = "";
					if ("Service".equals(questionType)) {
						String tempAnswer1 = params.get("answer_"+qID+"_C");
						String tempAnswer2 = params.get("answer_"+qID+"_S");
						if (null == tempAnswer1 )
							tempAnswer1 = "";
						if (null == tempAnswer2 )
							tempAnswer2 = "";
						answer = tempAnswer1+" "+tempAnswer2;
						if (" ".equals(answer))
							answer = "";
					} else if ("Date".equals(questionType)) {
						answer = com.picsauditing.PICS.DateBean.toDBFormat(params.get("answer_" + qID));
					} else
						answer = params.get("answer_" + qID);
					if (null == answer)
						answer = "";
					tempQAMap.put(qID,answer);
				}//if
			}//while
			
			if(bUploadsOnly)
				return;
			
			boolean isFileUpdate = false;
			boolean bUpdateCat = true;
			iter = params.keySet().iterator();
			while (iter.hasNext()) {
				String temp = (String)iter.next();
				if (temp.startsWith("pqfQuestionID_")) {
					String qID = temp.substring(14);
					String oldAnswer = params.get("oldAnswer_"+qID);
					String oldComment = params.get("oldComment_"+qID);
					String wasChanged = params.get("wasChanged_"+qID);
					String questionType = params.get("pqfQuestionType_"+qID);					
					String dateVerified = params.get("oldDateVerified_"+qID);
					String auditorID = params.get("oldAuditorID_"+qID);
					String answer = "";
					String comment = "";
					isFileUpdate = false;
					if ("Service".equals(questionType)) {
						String tempAnswer1 = params.get("answer_"+qID+"_C");
						String tempAnswer2 = params.get("answer_"+qID+"_S");
						if (null == tempAnswer1 )
							tempAnswer1 = "";
						if (null == tempAnswer2 )
							tempAnswer2 = "";
						answer = tempAnswer1+" "+tempAnswer2;
						if (" ".equals(answer))
							answer = "";
					} else if ("Date".equals(questionType)) {						
						answer = com.picsauditing.PICS.DateBean.toDBFormat(params.get("answer_"+qID));
					} else if("File".equals(questionType)) {
						isFileUpdate = true;
						bUpdateCat = false;
					} else
						answer = params.get("answer_"+qID);
					if (null == answer)
						answer = "";
					comment = params.get("comment_"+qID);
					wasChanged = params.get("wasChanged_"+qID);
					String oldWasChanged = wasChanged;
					if ((Constants.DESKTOP_TYPE.equals(auditType) || Constants.DA_TYPE.equals(auditType) || 
							Constants.OFFICE_TYPE.equals(auditType)) && "No".equals(answer))
						wasChanged = "Yes";
					if (null == comment)
						comment = "";
					tempQAMap.put(qID,answer);
					boolean isRequired = "Yes".equals(params.get("isRequired_"+qID));
					if ("Depends".equals(params.get("isRequired_"+qID))) {
						String dependsOnQID = params.get("dependsOnQID_"+qID);
						String dependsOnAnswer = params.get("dependsOnAnswer_"+qID);
						if (dependsOnAnswer != null && dependsOnAnswer.equals((String)tempQAMap.get(dependsOnQID)))
							isRequired = true;
					}//if
					if (!answer.equals(oldAnswer) || !comment.equals(oldComment) || !wasChanged.equals(oldWasChanged)) {
						doUpdate = true;
						if ((Constants.DESKTOP_TYPE.equals(auditType) || Constants.DA_TYPE.equals(auditType) || 
								Constants.OFFICE_TYPE.equals(auditType)) && "Yes".equals(answer) && "No".equals(oldAnswer)){
							dateVerified = com.picsauditing.PICS.DateBean.getTodaysDate();
							auditorID = userID;
						}//if
						if ("".equals(wasChanged))
							wasChanged = "No";
						if(!isFileUpdate)
							insertQuery += "('"+conID+"',"+qID+",'"+eqDB(answer)+"','"+eqDB(comment)+"','"+eqDB(wasChanged)+
								"','"+com.picsauditing.PICS.DateBean.toDBFormat(dateVerified)+"',"+Utilities.intToDB(auditorID)+",'"+eqDB(verifiedAnswer)+"'),";
					}//if
					if ("Yes".equals(answer) || "NA".equals(answer))
						yesNACount++;
					if (isRequired) {
						requiredCount++;
						if (!"".equals(answer) || (isFileUpdate && !params.get("answer_"+qID).equals("")))
							requiredAnsweredCount++;
					}//if
					if (!"".equals(answer) || (isFileUpdate && !params.get("answer_"+qID).equals("")))
						answeredCount++;
	//				String qNum = params.get("pqfQuestionNum_" + qID);
	//				String question = params.get("pqfQuestion_" + qID);
	//				if (null != answer && answer.length() != 0)
	//				else if ("Check Box".equals(questionType) && null == answer)
	//					insertQuery += "('"+conID+"','"+question+"',"+qID+","+qNum+",'N'),";
				}//if
			}//while
			insertQuery = insertQuery.substring(0,insertQuery.length()-1);
			insertQuery +=";";
			if (doUpdate)
				SQLStatement.executeUpdate(insertQuery);
			
			String tempPercentCompleted = getShowPercent(requiredAnsweredCount,requiredCount);
			String tempPercentVerified = getShowPercent(yesNACount,requiredCount);
			String updateQuery = "";
			if (Constants.DESKTOP_TYPE.equals(auditType) || Constants.DA_TYPE.equals(auditType) || Constants.OFFICE_TYPE.equals(auditType))
				updateQuery = "REPLACE INTO pqfCatData (catID,conID,applies,requiredCompleted,numAnswered,numRequired,percentCompleted,"+
					"percentVerified) VALUES ("+catID+","+conID+",'Yes',"+requiredAnsweredCount+","+answeredCount+","+requiredCount+","+
					tempPercentCompleted+","+tempPercentVerified+");";
			else if(bUpdateCat)
				updateQuery = "REPLACE INTO pqfCatData (catID,conID,applies,requiredCompleted,numAnswered,numRequired,percentCompleted) VALUES ("+
					catID+","+conID+",'Yes',"+requiredAnsweredCount+","+answeredCount+","+requiredCount+","+tempPercentCompleted+");";
			
			if(!updateQuery.equals(""))
				SQLStatement.executeUpdate(updateQuery);
			
		}finally{
			DBClose();
		}//finally
	}//savePQF

	public String getQuestionIDString(String auditType) throws Exception {
		try{
			String query = "SELECT questionID FROM pqfCategories INNER JOIN pqfSubCategories ON (catID=categoryID AND "+
					"auditType='"+auditType+"') INNER JOIN pqfQuestions ON subCatID=subCategoryID;";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(query);
			String qIDs = "(";
			while (SQLResult.next())
				qIDs+=SQLResult.getString("questionID")+",";
			if (qIDs.length()>1)
				qIDs = qIDs.substring(0,qIDs.length()-1);
			qIDs+=")";
			return qIDs;
		}finally{
			DBClose();
		}//finally
	} // getQuestionIDString

/*	jj 5-18-06
	public void saveOriginalDesktopAnswers(String conID) throws Exception {
		String qIDs = getQuestionIDString(com.picsauditing.pqf.Constants.DESKTOP_TYPE);
		DBReady();
		String updateQuery = "UPDATE pqfData SET wasChanged='Yes' WHERE conID="+conID+" AND answer='No' AND questionID IN "+qIDs+";";
		SQLStatement.executeUpdate(updateQuery);
		DBClose();
	}//saveOriginalDesktopAnswers
*/
	public void deleteAuditAnswers(String conID, String auditType) throws Exception {
		String qIDs = getQuestionIDString(auditType);
		String deleteQuery = "DELETE FROM pqfData WHERE conID="+conID+" AND questionID IN "+qIDs+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
	}//deleteAuditAnswers

	public String getPercentComplete(String conID, String auditType) throws Exception {
		try{
			String selectQuery = "SELECT COUNT(*) FROM pqfCategories pc INNER JOIN pqfCatData pd ON pc.catID = pd.catID WHERE "+
					"auditType='"+auditType+"' AND conID="+conID+" AND (percentCompleted=100 OR applies='No');";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			SQLResult.next();
			int numCompleted = SQLResult.getInt(1);
			String selectQuery2 = "SELECT COUNT(*) FROM pqfCategories WHERE auditType='"+auditType+"';";
			SQLResult = SQLStatement.executeQuery(selectQuery2);
			SQLResult.next();
			int numCats = SQLResult.getInt(1);
			SQLResult.close();
			return getShowPercent(numCompleted,numCats);
		}finally{
			DBClose();
		}//finally
	}//getPercentComplete
	

	public String getPercentVerified(String conID, String auditType) throws Exception {
		try{
			String selectQuery = "SELECT COUNT(*) FROM pqfCategories pc INNER JOIN pqfCatData pd ON pc.catID = pd.catID WHERE "+
				"auditType='"+auditType+"' AND conID="+conID+" AND percentVerified='100';";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			SQLResult.next();
			int numCompleted = SQLResult.getInt(1);
			String selectQuery2 = "SELECT COUNT(*) FROM pqfCategories WHERE auditType='"+auditType+"';";
			SQLResult = SQLStatement.executeQuery(selectQuery2);
			SQLResult.next();
			int numCats = SQLResult.getInt(1);
			SQLResult.close();
			return getShowPercent(numCompleted,numCats);
		}finally{
			DBClose();
		}//finally
	}//getPercentVerified

	@SuppressWarnings("unchecked")
	public void uploadPQFFile(javax.servlet.jsp.PageContext pageContext, String conID, String catID) throws Exception {
		boolean doUpdate = false;
		int reqCount = 0;
		int reqAnsweredCount = 0;
		int answeredCount = 0;
		
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();		
		UploadConHelper helper = new UploadConHelper();
		request.setAttribute("uploader",String.valueOf(UploadProcessorFactory.PQF));
		request.setAttribute("exts",extArr);
		request.setAttribute("directory", "files");
		helper.init(request, response);
	    Map<String,String> params = (Map<String,String>)request.getAttribute("uploadfields");
		boolean catDoesNotApply = "Yes".equals(params.get("catDoesNotApply"));
		
		if (catDoesNotApply) {
			String Query = "REPLACE INTO pqfCatData (catID,conID,applies,percentCompleted,percentVerified) VALUES ("+catID+","+conID+",'No',100,100);";
			try{
				DBReady();
				SQLStatement.executeUpdate(Query);
			}finally{
				DBClose();
			}//finally
			return;
		}//if		
		
		String insertQuery = "REPLACE INTO pqfData (conID,questionID,answer) VALUES ";		
				
	    Iterator iter = params.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String> entry = (Map.Entry<String,String>)iter.next();
			String key = entry.getKey();
			String fileName = "";
			if(key.contains("answer_")){
				fileName = entry.getValue();
				if (fileName != null) 
			        fileName = FilenameUtils.getName(fileName);		        
			   
				String ext = FilenameUtils.getExtension(fileName);
				if(!checkExtension(ext, extArr ))
					continue;
				
				String qID = key.substring(7);
				String errorMsg = (String)request.getAttribute("error_" + qID);
				boolean isRequired = "Yes".equals(params.get("isRequired_" + qID))
				|| "true".equals(params.get("isRequired_" + qID));
				if (isRequired)
					reqCount++;
				if (fileName == null || fileName.equals("")) {
					boolean isUploaded = "Uploaded".equals(params.get("isUploaded_"+qID));
					if (isUploaded) {
						answeredCount++;
						if (isRequired)
							reqAnsweredCount++;
					}//if
				}else if(errorMsg != null && !errorMsg.equals("")){
					errorMessages.addElement(errorMsg.replace("$", fileName));				
				}else{
					doUpdate = true;					
					insertQuery+= "('"+conID+"',"+qID+",'"+ext+"'),";
					answeredCount++;
					if (isRequired)
						reqAnsweredCount++;
				}
			}
		}
		
		if(errorMessages.size() > 0)
			return;
		insertQuery = insertQuery.substring(0,insertQuery.length()-1);
		insertQuery +=";";
		
		try{
			DBReady();
			if (doUpdate)
				SQLStatement.executeUpdate(insertQuery);
			//		pcBean.replaceCatData(catID,conID,"Yes",""+reqCount,""+reqAnsweredCount,percentCompleted);
			String percent = getShowPercent(reqAnsweredCount,reqCount);
			String replaceQuery = "REPLACE INTO pqfCatData (catID,conID,applies,numRequired,requiredCompleted,numAnswered,percentCompleted) VALUES ("+
				catID+","+conID+",'Yes',"+reqCount+","+reqAnsweredCount+","+answeredCount+","+percent+");";
			
			SQLStatement.executeUpdate(replaceQuery);
			
		}finally{
			DBClose();
		}//fnally
	
		return;
		
	}//uploadPQFFile

	public void deletePQFFile(javax.servlet.ServletConfig config, String conID, String qID, String ext) throws Exception {
		String deleteQuery = "DELETE FROM pqfData WHERE conID="+conID+" and questionID="+qID+" LIMIT 1";
		String path = config.getServletContext().getRealPath("/");
		java.io.File fileToDelete = new java.io.File(path+"/files/pqf/qID_"+qID+"/"+qID+"_"+conID+"."+ext);
		if (fileToDelete.exists())
			fileToDelete.delete();
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
		return;
	}//deletePQFFile
	
	public String getUploadLink() throws Exception {
		String answer = getAnswer(com.picsauditing.PICS.pqf.Constants.MANUAL_PQF_QID);
		if ("".equals(answer))
			return "Not Uploaded";
		else
			return "<a href=# onClick=window.open('servlet/showpdf?id="+conID+"&file=pqf"+answer+com.picsauditing.PICS.pqf.Constants.MANUAL_PQF_QID+
				"','','scrollbars=yes,resizable=yes,width=700,height=450')>Uploaded</a>";
	} // getUploadLink()


	public void saveVerificationNoUpload(javax.servlet.http.HttpServletRequest request, String conID, String userID) throws Exception {
		try{
			DBReady();
			Enumeration e = request.getParameterNames();
			String today = com.picsauditing.PICS.DateBean.toDBFormat(com.picsauditing.PICS.DateBean.getTodaysDate());
			int questionCount = 0;
			int verifiedCount = 0;
			while (e.hasMoreElements()) {
				String temp = (String)e.nextElement();
				if (temp.startsWith("pqfQuestionID_")) {
					questionCount++;
					String qID = temp.substring(14);
					String oldVerifiedAnswer = request.getParameter("oldVerifiedAnswer_"+qID);
					String oldIsCorrect = request.getParameter("oldIsCorrect_"+qID);
					String oldComment = request.getParameter("oldComment_"+qID);
	
					String newVerifiedAnswer = request.getParameter("verifiedAnswer_"+qID);
					if (null==newVerifiedAnswer)
						newVerifiedAnswer = "";
					String originalAnswer = request.getParameter("answer_"+qID);
					String newIsCorrect = request.getParameter("isCorrect_"+qID);
					if (null==newIsCorrect)
						newIsCorrect = "";
					String newComment = request.getParameter("comment_"+qID);
					if (!"".equals(newVerifiedAnswer))
						verifiedCount++;
	
					boolean doUpdate = (!oldVerifiedAnswer.equals(newVerifiedAnswer) ||
						!oldIsCorrect.equals(newIsCorrect) ||
						!oldComment.equals(newComment));
	
					if (doUpdate){
						String replaceQuery = "REPLACE INTO pqfData (conID,questionID,auditorID,comment,answer,verifiedAnswer,"+
							"isCorrect,dateVerified) VALUES ("+
							conID+","+qID+","+userID+",'"+eqDB(newComment)+"','"+eqDB(originalAnswer)+"','"+eqDB(newVerifiedAnswer)+"','"+
							newIsCorrect+"','"+today+"');";
	
						SQLStatement.executeUpdate(replaceQuery);
					} //if
				}//if
			}//while

			String catID = request.getParameter("catID");
			String temp = getShowPercent(verifiedCount,questionCount);
			String updateQuery = "UPDATE pqfCatData SET percentVerified="+temp+" WHERE conID="+conID+" AND catID="+catID+";";
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//saveVerification

	public void saveVerification(javax.servlet.http.HttpServletRequest request, String conID, String userID) throws Exception {
		try{
			DBReady();
			Map<String,String> params = (Map<String,String>)request.getAttribute("uploadfields");
			Iterator iter = params.keySet().iterator();
			
			String today = com.picsauditing.PICS.DateBean.toDBFormat(com.picsauditing.PICS.DateBean.getTodaysDate());
			int questionCount = 0;
			int verifiedCount = 0;
			while (iter.hasNext()) {
				String temp = (String)iter.next();
				if (temp.startsWith("pqfQuestionID_")) {
					questionCount++;
					String qID = temp.substring(14);
					String oldVerifiedAnswer = params.get("oldVerifiedAnswer_"+qID);
					String oldIsCorrect = params.get("oldIsCorrect_"+qID);
					String oldComment = params.get("oldComment_"+qID);
	
					String newVerifiedAnswer = params.get("verifiedAnswer_"+qID);
					if (null==newVerifiedAnswer)
						newVerifiedAnswer = "";
					String originalAnswer = params.get("answer_"+qID);
					String newIsCorrect = params.get("isCorrect_"+qID);
					if (null==newIsCorrect)
						newIsCorrect = "";
					String newComment = params.get("comment_"+qID);
					if (!"".equals(newVerifiedAnswer))
						verifiedCount++;
	
					boolean doUpdate = (!oldVerifiedAnswer.equals(newVerifiedAnswer) ||
						!oldIsCorrect.equals(newIsCorrect) ||
						!oldComment.equals(newComment));
	
					if (doUpdate){
						String replaceQuery = "REPLACE INTO pqfData (conID,questionID,auditorID,comment,answer,verifiedAnswer,"+
							"isCorrect,dateVerified) VALUES ("+
							conID+","+qID+","+userID+",'"+eqDB(newComment)+"','"+eqDB(originalAnswer)+"','"+eqDB(newVerifiedAnswer)+"','"+
							newIsCorrect+"','"+today+"');";
	
						SQLStatement.executeUpdate(replaceQuery);
					} //if
				}//if
			}//while

			String catID = params.get("catID");
			String temp = getShowPercent(verifiedCount,questionCount);
			String updateQuery = "UPDATE pqfCatData SET percentVerified="+temp+" WHERE conID="+conID+" AND catID="+catID+";";
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//saveVerification
	
	public boolean isComplete(String conID, String auditType) throws Exception {
		if ((null == conID) || ("".equals(conID)))
			throw new Exception("Can't set isComplete from DB because conID is not set");
		String selectQuery = "SELECT * FROM pqfCategories LEFT JOIN pqfCatData ON (pqfCategories.catID=pqfCatData.catID "+
			"AND conID="+conID+") WHERE auditType='"+auditType+"' ORDER BY number;";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			isComplete = true;
			errorMessages = new Vector<String>();
			while (listRS.next()){
				if (null == listRS.getString("conID") || (!"No".equals(listRS.getString("applies")) && (100!=listRS.getInt("percentCompleted")))){
					isComplete = false;
					errorMessages.addElement("Category: "+listRS.getString("category"));
				}//if
			}//while
			return isComplete;
		}finally{
			DBClose();
		}//finally
	}//isComplete

	public boolean isClosed(String conID, String auditType) throws Exception {
		if ((null == conID) || ("".equals(conID)))
			throw new Exception("Can't set isClosed from DB because conID is not set");
		String selectQuery = "SELECT * FROM pqfCategories LEFT JOIN pqfCatData ON (pqfCategories.catID = pqfCatData.catID "+
			"AND conID="+conID+") WHERE auditType='"+auditType+"' ORDER BY number;";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			isComplete = true;
			errorMessages = new Vector<String>();
			while (listRS.next()) {
				if (null == listRS.getString("conID") || 100 != listRS.getInt("percentVerified")) {
					isComplete = false;
					errorMessages.addElement("Category "+listRS.getString("number")+"-"+listRS.getString("category"));
				}//if
			}//while
			return isComplete;
		}finally{
			DBClose();
		}//finally
	}//isClosed

	public boolean isYes(String qID) throws Exception {
// Created on 11/29/05 jj, used in pqf_redFlags.jsp
		return ("Yes".equals(getAnswer(qID)));
	}//isYes

	public boolean isNotAnswered(String qID) throws Exception {
// Created on 11/29/05 jj, used in pqf_redFlags.jsp
		return ("".equals(getAnswer(qID)));
	}//isNotAnswered

	public String getFlag(String qID) throws Exception {
// Created on 11/29/05 jj, used in pqf_redFlags.jsp
		if (isYes(qID))
			return "<img src=images/notOkCheck.gif width=19 height=15>";
		if (isNotAnswered(qID))
			return "<img src=images/notOkCheck.gif width=19 height=15 alt='Not Answered'>";
		return "<img src=images/okCheck.gif width=19 height=15 alt='OK'>";
	}//getFlag

	public String getFlagDandB(String value) throws Exception {
// Created on 12/12/05 jj, used in pqf_redFlags.jsp, flags risky D&B ratins
		if ("".equals(value))
			return "<img src=images/notOkCheck.gif width=19 height=15 alt='Not Answered'>";
		if (!Utilities.arrayContains(D_AND_B_OK, value))
			return "<img src=images/notOkCheck.gif width=19 height=15>";
		return "<img src=images/okCheck.gif width=19 height=15 alt='OK'>";
	}//getFlagDandB

	public String getFlagOver(String value, float limit) throws Exception {
// Created on 11/29/05 jj, used in pqf_redFlags.jsp
		if ("".equals(value))
			return "<img src=images/notOkCheck.gif width=19 height=15 alt='Not Answered'>";
		float num;
		try {
			num = Float.parseFloat(value);
		} catch (Exception e) {
			return "<img src=images/notOkCheck.gif width=19 height=15 alt='Invalid Number Format'>";
		}//catch
		if (num > limit)
			return "<img src=images/notOkCheck.gif width=19 height=15 alt='Exceeds "+limit+" Threshold'>";
		return "<img src=images/okCheck.gif width=19 height=15 alt='Within "+limit+" Threshold'>";
	}//getFlag

	public boolean isOK() {
		return (errorMessages.size() == 0);
	}//isOK

	public String eqDB(String temp) {
		return Utilities.escapeQuotes(temp);
	}//eqDB

	public String getShowPercent(int num, int den) {
		if (num==den)
			return "100";
		java.text.DecimalFormat decFormatter = new java.text.DecimalFormat("###,##0");
		return decFormatter.format(((float)num*100)/den);
	}//getShowPercent
	
	public boolean isFileUpload(String catID) throws Exception{
		if ((null == catID) || ("".equals(catID)))
			throw new Exception("Can't set pqfData from DB because conID is not set");
		
		String query = "select categoryID from pqfsubcategories where pqfsubcategories.subCatID IN (Select subCategoryID from pqfquestions where questionType='File');";
		ResultSet SQLResult = null;;
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(query);
			String cid = "";
			while(SQLResult.next()){
				cid = SQLResult.getString("categoryID");
			    if(catID.equals(cid))
			    	return true;
			}
		}catch(Exception ex){
			 errorMessages.addElement("Unable to get question information from database.");
		}finally{
			if(SQLResult != null)
				SQLResult.close();
			DBClose();
		}//finally
		
		return false;
	}

	public Map<String,String> getAuditAnswerMap(String conID, String auditType)throws Exception {
		Map<String,String> tempQAMap = new TreeMap<String,String>();
		try{
			DBReady();
			String selectQuery = "SELECT pqfData.questionID,answer "+
				"FROM pqfCategories INNER JOIN pqfSubCategories ON (catID=categoryID AND "+
				"auditType='"+auditType+"') INNER JOIN pqfQuestions ON subCatID=subCategoryID "+
				"LEFT JOIN pqfData ON(pqfQuestions.questionID=pqfData.questionID) "+
				"WHERE conID="+conID+" ORDER BY questionID";
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			while (rs.next())
				tempQAMap.put(rs.getString("questionID"),rs.getString("answer"));
			rs.close();
			return tempQAMap;
		}finally{
			DBClose();
		}//finally
	}//getAuditAnswerMap

	public void updatePercentageCompleted(String conID, String catID, String auditType) throws Exception {
		try{
			DBReady();
			String selectQuery = "SELECT applies FROM pqfCatData "+
				"WHERE catID="+catID+" AND conID="+conID+";";
			ResultSet rs = SQLStatement.executeQuery(selectQuery);
			if (!rs.next() || !"Yes".equals(rs.getString("applies"))){
				rs.close();
				return;
			}//if
			rs.close();
			int requiredAnsweredCount = 0;
			int answeredCount = 0;
			int requiredCount = 0;
			int yesNACount = 0;
			TreeMap<String,String> tempQAMap = (TreeMap<String,String>)getAuditAnswerMap(conID,auditType);
			selectQuery = "SELECT pqfQuestions.questionID,answer,isRequired,dependsOnQID,dependsOnAnswer "+
				"FROM pqfCategories INNER JOIN pqfSubCategories ON (catID=categoryID AND "+
				"auditType='"+auditType+"') INNER JOIN pqfQuestions ON subCatID=subCategoryID "+
				"LEFT JOIN pqfData ON(pqfQuestions.questionID=pqfData.questionID AND "+
				"(conID="+conID+" OR conID IS NULL)) WHERE catID="+catID+";";
			DBReady();
			rs = SQLStatement.executeQuery(selectQuery);
			while (rs.next()) {
				String answer = rs.getString("answer");
				if (rs.wasNull())
					answer = "";
				String tempIsRequired = rs.getString("isRequired");
				boolean isRequired = "Yes".equals(tempIsRequired);
				if ("Depends".equals(tempIsRequired)){
					String dependsOnQID = rs.getString("dependsOnQID");
					String dependsOnAnswer = rs.getString("dependsOnAnswer");
					if (dependsOnAnswer.equals(tempQAMap.get(dependsOnQID)))
						isRequired = true;
				}//if
				if ("Yes".equals(answer) || "NA".equals(answer))
					yesNACount++;
				if (isRequired){
					requiredCount++;
					if (!"".equals(answer) && !com.picsauditing.PICS.DateBean.NULL_DATE_DB.equals(answer))
						requiredAnsweredCount++;
				}//if
				if (!"".equals(answer))
					answeredCount++;
			}//while
			rs.close();
			String tempPercentCompleted = getShowPercent(requiredAnsweredCount,requiredCount);
			String tempPercentVerified = getShowPercent(yesNACount,requiredCount);
			String updateQuery = "";
			if (Constants.DESKTOP_TYPE.equals(auditType) || Constants.DA_TYPE.equals(auditType) || Constants.OFFICE_TYPE.equals(auditType))
				updateQuery = "REPLACE INTO pqfCatData (catID,conID,applies,requiredCompleted,numAnswered,numRequired,percentCompleted,"+
					"percentVerified) VALUES ("+catID+","+conID+",'Yes',"+requiredAnsweredCount+","+answeredCount+","+requiredCount+","+
					tempPercentCompleted+","+tempPercentVerified+");";
			else
				updateQuery = "REPLACE INTO pqfCatData (catID,conID,applies,requiredCompleted,numAnswered,numRequired,percentCompleted) VALUES ("+
					catID+","+conID+",'Yes',"+requiredAnsweredCount+","+answeredCount+","+requiredCount+","+tempPercentCompleted+");";
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//updatePercentageCompleted
	
	private boolean checkExtension(String ext, String exts){
		if(exts == null || exts.equals(""))
			return true;
		
		String[] list = exts.split(",");
		for(int i = 0; i < list.length; i++)
			if(list[i].equals(ext.toLowerCase()))
				return true;
		
		return false;
	}

//SQL to get all the questions and their cateogry type
//SELECT  * 
//FROM pqfSubCategories s, pqfQuestions q, pqfCategories c
//WHERE s.categoryID = c.catID AND q.subCategoryID=s.subCatID AND auditType='Desktop' ORDER BY questionID;
}//DataBean

