package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

public class AuditQuestionBean extends DataBean {
	public static String DEFAULT_AUDIT_TYPE = "- Audit Type -";
	public static String[] AUDIT_TYPE_ARRAY = {DEFAULT_AUDIT_TYPE,"Office","Field"};

	public String questionID = "";
	public String num = "";
	public String question = "";
	public String okAnswer = "";  // SET 'Yes','No','NA'
	public String requirement = "";
	public String multireq = "No";
	public String reqclass = "";
	public String reqprogram = "";
	public String categoryID = "";
	public String auditType = "";
	public ArrayList<String> links = new ArrayList<String>();
	public ArrayList<String> qIDs = new ArrayList<String>();

	public TreeMap<String,String> questionOKAnswerMap = null;

	public static ArrayList<String> categoryIDNameTypeAL = null;

	ResultSet listRS = null;
	int numResults = 0;
	int count = 0;

	public String getAllRequirements() {
		String temp = "";
		if (!"".equals(requirement))
			temp+=requirement+"<br>";
		if (!"".equals(reqclass))
			temp+=reqclass+"<br>";
		if (!"".equals(reqprogram))
			temp+=reqprogram;
		return temp;
	}

	public void addLink(String linkURL, String linkText) {
		if (null != linkURL && linkURL.length() != 0 && null != linkText&& linkText.length() != 0 ) {
			links.add(linkURL);
			links.add(linkText);
		} //if
	}//addLink

	public String getLinkURL(int i) {
		i = (i-1)*2;
		if (i < (links.size()))	return (String)links.get(i);
		else	return "";
	}//getLinkURL

	public String getLinkText(int i) {
		i = (i*2)-1;
		if (i < (links.size()))	return (String)links.get(i);
		else	return "";
	}//getLinkText

	public int getNumOfLinks() {
		return links.size()/2;
	}//getNumOfLinks
	
	public String getRequirement(){
		String reqString = "";
		if  ("Yes".equals(multireq)) {
			if (!"".equals(reqclass))
				reqString = "Class: " + reqclass + "<br>";
			if (!"".equals(reqprogram)) 
				reqString += "Program: " + reqprogram;
		} else
			reqString = requirement;
		return reqString;
	}//getRequirement
	
	public void setOKMapFromDB() throws Exception {
		if (questionOKAnswerMap != null)
			return;
		String selectQuery = "SELECT * FROM auditQuestions ORDER BY questionID;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			questionOKAnswerMap = new TreeMap<String,String>();
			while (SQLResult.next()) {
				setFromResultSet(SQLResult);
				questionOKAnswerMap.put(questionID,okAnswer);
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setOKMapFromDB

	public boolean isAnswerOK(String questionID, String answer) throws Exception {
		if (questionOKAnswerMap == null)
			throw new Exception("questionOKAnswerMap is null");
		if ("".equals(answer))
			return false;
		if (questionOKAnswerMap.containsKey(questionID)) {
			String temp = (String)questionOKAnswerMap.get(questionID);
			if (temp.indexOf(answer) != -1)
				return true;
		}//if
		return false;
	}//isAnswerOK

	public void setFromDB(String qID) throws Exception {
		questionID = qID;
		setFromDB();
	}//setFromDB
	
	public void setFromDB() throws Exception {
		if ((null == questionID) || ("".equals(questionID)))
			throw new Exception("Can't set auditQuestions from DB because questionID is not set");
		String selectQuery = "SELECT * FROM auditQuestions WHERE questionID='"+questionID+"';";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setFromDB

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		questionID = SQLResult.getString("questionID");
		num = SQLResult.getString("num");
		question = SQLResult.getString("question");
		okAnswer = SQLResult.getString("okAnswer");
		requirement = SQLResult.getString("requirement");
		multireq = SQLResult.getString("multireq");
		reqclass = SQLResult.getString("reqclass");
		reqprogram = SQLResult.getString("reqprogram");
		categoryID = SQLResult.getString("categoryID");
		auditType = SQLResult.getString("auditType");
		links.clear();
		for (int i=1;i<=6;i++)
			addLink(SQLResult.getString("linkURL"+i),SQLResult.getString("linkText"+i));
	}//setFromResultSet

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE auditQuestions SET num="+Utilities.intToDB(num)+",question='"+Utilities.escapeQuotes(question)+
				"',okAnswer='"+okAnswer+"',requirement='"+Utilities.escapeQuotes(requirement)+
				"',multireq='"+Utilities.escapeQuotes(multireq)+
				"',reqclass='"+Utilities.escapeQuotes(reqclass)+
				"',reqprogram='"+Utilities.escapeQuotes(reqprogram)+
				"',categoryID="+Utilities.intToDB(categoryID)+
				",auditType='"+Utilities.escapeQuotes(auditType);
		for (int i=1;i<=6;i++) {
			updateQuery+="',linkURL"+i+"='"+Utilities.escapeQuotes(getLinkURL(i));
			updateQuery+="',linkText"+i+"='"+Utilities.escapeQuotes(getLinkText(i));				
		}//for
		updateQuery+="' WHERE questionID="+questionID+";";
		System.out.println(updateQuery);
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally		
	}//writeToDB

	public void writeNewToDB() throws Exception {
		String insertQuery = "INSERT INTO auditQuestions (question,requirement,reqclass,reqprogram) VALUES ('"+
				Utilities.escapeQuotes(question)+"','','','');"; 
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ResultSet SQLResult = SQLStatement.getGeneratedKeys();
			SQLResult.next();
			questionID = SQLResult.getString("GENERATED_KEY");
			writeToDB();
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//writeNewToDB

	public void deleteQuestion(String questionID) throws Exception {
		String updateQuery = "UPDATE auditQuestions SET visible='No' WHERE questionID="+questionID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally				
	}//deleteQuestion

	public void setFromRequest(javax.servlet.http.HttpServletRequest r) throws Exception {
		num = r.getParameter("num");
		question = r.getParameter("question");
		String okYes = r.getParameter("okYes");;
		String okNo = r.getParameter("okNo");;
		String okNA = r.getParameter("okNA");;
		okAnswer = "";
		if (okYes != null && !"".equals(okYes))
			okAnswer += "Yes,";
		if (okNo != null && !"".equals(okNo))
			okAnswer += "No,";
		if (okNA != null && !"".equals(okNA))
			okAnswer += "NA,";
		if (okAnswer.length() != 0)
			okAnswer = okAnswer.substring(0,okAnswer.length()-1);
		multireq = r.getParameter("multireq");
		requirement = r.getParameter("requirement");
		reqclass = r.getParameter("reqclass");
		reqprogram = r.getParameter("reqprogram");
		categoryID = r.getParameter("categoryID");
		auditType = r.getParameter("auditType");
		links.clear();
		for (int i=1;i<=6;i++)
			addLink(r.getParameter("linkURL"+i),r.getParameter("linkText"+i));
	}//setFromRequest

	//Changed function to take in parameter to determine if ordered by category or number BJ 11-1-04
	//Added number sub ordering when ordering by category JJ 11/24/04
	public void setList(String orderBy, String auditType) throws Exception {
		setCategoryIDNameTypeALFromDB();
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "num";
		if ("category".equals(orderBy))
			orderBy = "category, num";
		String selectQuery = "SELECT * FROM auditQuestions WHERE auditType='"+auditType+
			"' AND visible='Yes' ORDER BY "+orderBy+";";
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
		}//catch
	}//setList

	public boolean isNextRecord() throws Exception {
		if (!(count <= numResults && listRS.next()))
			return false;
		count++;
		setFromResultSet(listRS);
		return true;
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

	public void updateNumbering(javax.servlet.http.HttpServletRequest request) throws Exception {
		Enumeration e = request.getParameterNames();
		try{
			DBReady();
			while (e.hasMoreElements()) {
				String temp = (String)e.nextElement();
				if (temp.startsWith("num_")) {
					String qID = temp.substring(4);
					String num = request.getParameter("num_"+qID);
					String updateQuery = "UPDATE auditQuestions SET num="+num+" WHERE questionID="+qID+";";
					SQLStatement.executeUpdate(updateQuery);
				}//if
			}//while
		}finally{
			DBClose();
		}//finally		
	}//updateNumbering
	
	//added 1-10-05 to keep question numbers sequential after changes to order
	public void renumberAudit(String auditType)  throws Exception {
		String selectQuery = "SELECT questionID FROM auditQuestions WHERE auditType='"+auditType+
			"' AND visible='Yes' ORDER BY num";
		try{
			DBReady();
			ResultSet SQLResult  = SQLStatement.executeQuery(selectQuery);
			String updateQuery = "";
			int nextNumber = 1;
			while (SQLResult.next())
				qIDs.add(SQLResult.getString("questionID"));
			SQLResult.close();
			ListIterator li = qIDs.listIterator();
			while (li.hasNext()) {
				String tempQIDs = (String)li.next();
				updateQuery  = "UPDATE auditQuestions SET num="+nextNumber+
						" WHERE questionID="+tempQIDs+";";
				nextNumber = nextNumber+1;
				SQLStatement.executeUpdate(updateQuery);
			}//while
		}finally{
			DBClose();
		}//finally		
	}//renumberAudit
		
	public boolean isOK() {
		errorMessages = new Vector<String>();
		if ((null == question) || (question.length() == 0))
			errorMessages.addElement("Please enter the audit question");
		if ("Office".equals(auditType)) {
			if ("No".equals(multireq) || ("".equals(multireq))) {
				if ((null == requirement) || (requirement.length() == 0))
					errorMessages.addElement("Please enter the requirement");
			} else {
				if (((null == reqclass) || (reqclass.length() == 0)) && ((null == reqprogram) || (reqprogram.length() == 0)))
					errorMessages.addElement("Please enter the required class and/or program");
			}//if
		}//if
		if ((null == okAnswer) || (okAnswer.length() == 0))
			errorMessages.addElement("Please select at least one OK answer");
		return (errorMessages.size() == 0);
	}//isOK
	
	public void addCategory(String newCategory, String auditType) throws Exception {
		String insertQuery = "INSERT INTO auditCategories (category,auditType) VALUES ('"+
			Utilities.escapeQuotes(newCategory)+"','"+auditType+"');"; 
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally		
		setCategoryIDNameTypeALFromDB();				
	}//addCategory

	public void deleteCategory(String deleteCategoryID) throws Exception {
		String deleteQuery = "DELETE FROM auditCategories WHERE catID="+deleteCategoryID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally		
		setCategoryIDNameTypeALFromDB();				
	}//deleteCategory

/*	public String[] getAuditCategoriesArray2(String auditType) throws Exception {
		ArrayList tempAL = new ArrayList();
		DBReady();
		String Query = "SELECT * FROM auditCategories WHERE auditType='"+auditType+"'ORDER BY category;";
		ResultSet SQLResult = SQLStatement.executeQuery(Query);
		while (SQLResult.next()) {
			tempAL.add(SQLResult.getString("catID"));
			tempAL.add(SQLResult.getString("category"));
		}//while
  		return (String[])tempAL.toArray(new String[0]);
	}//getActiveGeneralsArray
*/
	public String getAuditCategoriesSelect2(String name, String classType, 
					String selectedCategory, String auditType) throws Exception {
		setCategoryIDNameTypeALFromDB();
		ArrayList<String> tempAL = new ArrayList<String>();
		ListIterator li = categoryIDNameTypeAL.listIterator();
		while (li.hasNext()) {
			String tempID = (String)li.next();
			String tempName = (String)li.next();
			String tempType = (String)li.next();
			if (auditType.equals(tempType)) {
				tempAL.add(tempID);
				tempAL.add(tempName);
			}//if
		}//while
		return Inputs.inputSelect2(name, classType, selectedCategory, (String[])tempAL.toArray(new String[0]));
	}//getAuditCategoriesSelect2

	public void setCategoryIDNameTypeALFromDB() throws Exception {
		if (null != categoryIDNameTypeAL)
			return;
		categoryIDNameTypeAL = new ArrayList<String>();
		String selectQuery = "SELECT * FROM auditCategories ORDER BY category;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				categoryIDNameTypeAL.add(SQLResult.getString("catID"));
				categoryIDNameTypeAL.add(SQLResult.getString("category"));
				categoryIDNameTypeAL.add(SQLResult.getString("auditType"));
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setCategoryIDNameTypeALFromDB

	public String isOKAnswerChecked(String s) {
		if (okAnswer.indexOf(s) != -1)
			return "checked";
		return "";
	}//isOKAnswerChecked

	public String getCategoryName() throws Exception {
		setCategoryIDNameTypeALFromDB();
		if (!categoryIDNameTypeAL.contains(categoryID))
			throw new Exception("No Category in categoryIDNameTypeAL with ID: "+ categoryID);
		return (String)categoryIDNameTypeAL.get(categoryIDNameTypeAL.indexOf(categoryID)+1);		
	}//getCategoryName

	public String getAuditTypeSelectSubmit(String name, String classType, String selectedType) {
		return Inputs.inputSelectSubmit(name, classType, selectedType, AUDIT_TYPE_ARRAY);
	}//getAuditTypeSelectSubmit

	public String getAuditTypeFromCategoryID(String catID) throws Exception {
		setCategoryIDNameTypeALFromDB();
		if (!categoryIDNameTypeAL.contains(catID))
			throw new Exception("No Category in categoryIDNameTypeAL with ID: "+ catID);
		return (String)categoryIDNameTypeAL.get(categoryIDNameTypeAL.indexOf(catID)+2);
	}//getAuditTypeFromCategoryID

	public String getLinksShow() {
		String temp = "";
		for (int i=1;i<=6;i++) {
			temp+="<a href=http://"+getLinkURL(i)+" target=_blank>"+getLinkText(i)+"</a> ";
		} //for
		return temp;
	}//getLinksShow
	public void resetCategoryIDNameTypeAL(){
		categoryIDNameTypeAL = null;
	}//resetCategoryIDNameTypeAL
}//AuditQuestionBean