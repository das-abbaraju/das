package com.picsauditing.PICS.pqf;

import java.sql.*;
import java.util.*;
import com.picsauditing.PICS.*;


public class CategoryBean extends com.picsauditing.PICS.DataBean {
	static String DEFAULT_CATEGORY = "--Category--";
	public static String OSHA_CATEGORY_ID = "29";
	public static String OSHA_NUM_REQUIRED = "24";
	public static String SERVICES_CATEGORY_ID = "28";

	public String catID = "";
	public String auditType = "";
	public String category = "";
	public String number = "";
	public String numRequired = "";
	public String numQuestions = "";

	//pqfcatData
	public String conID = "";
	public String requiredCompleted = "";
	public String dataNumRequired = "";
	public String numAnswered = "";
	public String applies = "";
	public String percentCompleted = "";
	public String percentVerified = "";
	public String riskLevel = "1";

	public ArrayList<String> categories = null;
	public ArrayList<String> allCategories = null;
	public ArrayList<String> categoryMatrixAL = null;
	public ArrayList<String> opCategoryMatrixHighRiskAL = null;
	public ArrayList<String> opCategoryMatrixLowRiskAL = null;
	public TreeMap<String,String> numOfRequiredMap = null;
	boolean isNumQuestionsMapSet = false;
//	public TreeMap allCategoriesMap = null;

	ResultSet listRS = null;
	int numResults = 0;
	public int count = 0;
	boolean isJoinedWithData = false;

	public void setFromDB(String cID) throws Exception {
		catID = cID;
		setFromDB();
	}//setFromDB
	
	public void setFromDB() throws Exception {
		if ((null == catID) || ("".equals(catID)))
			throw new Exception("Can't set PQF Category from DB because catID is not set");
		String Query = "SELECT * FROM pqfCategories WHERE catID = " + catID + ";";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

	public void setFromDBWithData(String catID, String cID) throws Exception {
		if ((null == catID) || ("".equals(catID)))
			throw new Exception("Can't set PQF Category from DB because catID is not set");
		String Query = "SELECT * FROM pqfCategories LEFT JOIN pqfCatData ON pqfCategories.catID=pqfCatData.catID "+
			"AND conID="+cID+" WHERE pqfCategories.catID="+catID+";";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next()) {
				setFromResultSet(SQLResult);
				setDataFromResultSet(SQLResult);
				isJoinedWithData = true;
				if (SQLResult.wasNull() || null==conID || "null".equals(conID)){
					setDataEmpty();
					isJoinedWithData = false;
				}//if
			}//if
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDBWithData

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		catID = SQLResult.getString("catID");
		auditType = SQLResult.getString("auditType");
		category = SQLResult.getString("category");
		number = SQLResult.getString("number");
		numRequired = SQLResult.getString("pqfCategories.numRequired");
		numQuestions = SQLResult.getString("numQuestions");
	}//setFromResultSet

	public void setDataFromResultSet(ResultSet SQLResult) throws Exception {
		conID = SQLResult.getString("conID");
		requiredCompleted = SQLResult.getString("requiredCompleted");
		dataNumRequired = SQLResult.getString("pqfCatData.numRequired");
		numAnswered = SQLResult.getString("numAnswered");
		applies = SQLResult.getString("applies");
		percentCompleted = SQLResult.getString("percentCompleted");
		percentVerified = SQLResult.getString("percentVerified");
	}//setDataFromResultSet

	public void setDataEmpty() {
		conID = "";
		requiredCompleted = "";
		dataNumRequired = "0";
		numAnswered = "";
		applies = "";
		percentCompleted = "0";
		percentVerified = "0";	
	}//setDataEmpty

	public void writeToDB() throws Exception {
		String Query = "UPDATE pqfCategories SET "+
			"category='"+Utilities.escapeQuotes(category)+
			"',number='"+number+
			"' WHERE catID="+catID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(Query);
			categories = null;
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public void writeNewToDB(String aType) throws Exception {
		String insertQuery = "INSERT INTO pqfCategories (auditType,category,number)" +
			" VALUES ('"+aType+"','"+Utilities.escapeQuotes(category)+"',"+number+");";
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
			categories = null;
		}finally{
			DBClose();
		}//finally
	}//writeNewToDB

	public void deleteCategory(String cID, String rootPath) throws Exception {
		String deleteQuery = "DELETE FROM pqfCategories WHERE catID="+cID+" LIMIT 1;";
		try {
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
			new SubCategoryBean().deleteAllSubCats(cID, rootPath);
			categories = null;
		}finally{
			DBClose();
		}//finally
	}//deleteCategory

	public void setFromRequest(javax.servlet.http.HttpServletRequest r) throws Exception {
		category = r.getParameter("category");
		number = r.getParameter("number");
	}//setFromRequest

	public void setList(String orderBy, String aType) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String selectQuery = "SELECT * FROM pqfCategories WHERE auditType='"+aType+"' ORDER BY "+orderBy+";";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		} catch (Exception e) {
			DBClose();
			throw e;
		}//catch		
	}//setList

	public void setListWithData(String orderBy, String aType, String conID) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String Query = "SELECT * FROM pqfCategories LEFT JOIN pqfCatData ON pqfCategories.catID=pqfCatData.catID "+
				"AND conID="+conID+" WHERE auditType='"+aType+"' ORDER BY "+orderBy+";";
		try {
			DBReady();
			listRS = SQLStatement.executeQuery(Query);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
			isJoinedWithData = true;
		} catch (Exception e) {
			DBClose();
			throw e;
		}//catch		
	}//setListWithData

	public void replaceCatData(String t_catID, String t_conID, String t_applies, String t_reqComp, String t_numReq, String t_percComp) throws Exception {
		String query = "REPLACE INTO pqfCatData (catID,conID,applies,requiredCompleted,numRequired,percentCompleted) VALUES ("+
				t_catID+","+t_conID+",'"+t_applies+"',"+t_reqComp+","+t_numReq+","+t_percComp+");";
		try {
			DBReady();
			SQLStatement.executeUpdate(query);
		}finally{
			DBClose();
		}//finally
	}//replaceCatData

	public void resetList() throws Exception {
		listRS.beforeFirst();	
		count = 0;
	}//resetList

	public boolean isNextRecord() throws Exception {
		if (!(count <= numResults && listRS.next()))
			return false;
		count++;
		setFromResultSet(listRS);
		if (isJoinedWithData) {
			catID = listRS.getString("pqfCategories.catID");
			setDataFromResultSet(listRS);
			if (listRS.wasNull() || null==conID || "null".equals(conID))
				setDataEmpty();
		}//if
		return true;
	}//isNextRecord

	public boolean isNextRecord(PermissionsBean pBean, String cID) throws Exception {
		if (!(count <= numResults && listRS.next()))
			return false;
		count++;
		setFromResultSet(listRS);
		if (pBean.canSeeAuditCategory(catID, cID)) {
			if (isJoinedWithData) {
				catID = listRS.getString("pqfCategories.catID");
				setDataFromResultSet(listRS);
				if (listRS.wasNull() || null==conID || "null".equals(conID)) {
					conID = "";
					requiredCompleted = "";
					dataNumRequired = "";
					numAnswered = "";
					applies = "";
					percentCompleted = "0";
					percentVerified = "0";
				}//if
			}//if
			return true;
		} else
			return isNextRecord(pBean,cID);
	}//isNextRecord

	public void closeList() throws Exception {
		count = 0;
		numResults = 0;
		isJoinedWithData = false;
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
		ArrayList<String> updateQueriesAL = new ArrayList<String>();
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("num_")) {
				String id = temp.substring(4);
				String num = request.getParameter("num_" + id);
				updateQueriesAL.add("UPDATE pqfCategories SET number="+num+" WHERE catID="+id+";");
			}//if
		}//while
		
		try{
			DBReady();
			for(String updateQuery : updateQueriesAL)
				SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//updateNumbering
	
	public void renumberPQFCategories(String aType) throws Exception {
		ArrayList<String> updateQueriesAL = new ArrayList<String>(); 
		String selectQuery = "SELECT catID FROM pqfCategories WHERE auditType='"+aType+"' ORDER BY number";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			int nextNumber = 1;
			while (SQLResult.next()) {
				updateQueriesAL.add("UPDATE pqfCategories SET number="+nextNumber+" WHERE catID="+SQLResult.getInt("catID")+";");
				nextNumber = nextNumber+1;
			}//while
			SQLResult.close();
			for(String updateQuery : updateQueriesAL)
				SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//renumberPQFCategories
	
	public boolean isOK() {
		errorMessages = new Vector<String>();
		if ((null == number) || (number.length() == 0))
			errorMessages.addElement("Please enter the category number");
		if ((null == category) || (category.length() == 0))
			errorMessages.addElement("Please enter the category name");
		return (errorMessages.size() == 0);
	}//isOK

	public void setPQFCategoriesArray(String aType) throws Exception {
		if (null != categories)
			return;
		categories = new ArrayList<String>();
		String selectQuery = "SELECT catID,category FROM pqfCategories WHERE auditType='"+aType+"' ORDER BY number";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()){
				categories.add(SQLResult.getString("catID"));
				categories.add(SQLResult.getString("category"));
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setPQFCategoriesArray

	public String getPQFCategorySelect(String name, String classType, String selectedCategory, String aType) throws Exception {
		setPQFCategoriesArray(aType);
		return Utilities.inputSelect2First(name, classType, selectedCategory, 
			(String[])categories.toArray(new String[0]),"0",DEFAULT_CATEGORY);
	}//getPQFCategorySelect

	public String getPQFCategorySelectDefaultSubmit(String name, String classType, 
			String selectedCategory, String aType) throws Exception {
		setPQFCategoriesArray(aType);
		return Utilities.inputSelect2FirstSubmit(name, classType, selectedCategory, 
				(String[])categories.toArray(new String[0]),"0",DEFAULT_CATEGORY);
	}//getPQFCategorySelectDefaultSubmit

	public String getCategoryName(String cID, String aType) throws Exception {
		setPQFCategoriesArray(aType);
		int i = categories.indexOf(cID);
		if (-1 == i)
			return "Does not exist";
		return (String)categories.get(i+1);
	}//getCategoryName

	public void setNumRequiredMap() throws Exception {
		if (isNumQuestionsMapSet)
			return;
		String Query = "SELECT catID,COUNT(*) AS total FROM pqfCategories "+
							"INNER JOIN pqfSubCategories ON (catID=categoryID) "+
							"INNER JOIN pqfQuestions ON (subCatID=subCategoryID) "+
							" WHERE isRequired='Yes' GROUP BY catID";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			numOfRequiredMap = new TreeMap<String,String>();
			while (SQLResult.next())
				numOfRequiredMap.put(SQLResult.getString("catID"),SQLResult.getString("total"));
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		isNumQuestionsMapSet = true;
	}//setNumRequiredMap

	public String getNumRequired(String catID) throws Exception {
		setNumRequiredMap();
		if (null==numOfRequiredMap)
			throw new Exception("numOfRequiredMap is null");
		if (OSHA_CATEGORY_ID.equals(catID))
			return OSHA_NUM_REQUIRED;
		if (numOfRequiredMap.containsKey(catID))			
			return (String)numOfRequiredMap.get(catID);
		return "0";
	}//getNumRequired

	public void updateNumRequiredCounts(String aType) throws Exception {
		setNumRequiredMap();
		setPQFCategoriesArray(aType);
		ArrayList<String> updateQueries = new ArrayList<String>();
		try {
			DBReady();
			for (ListIterator li = categories.listIterator();li.hasNext();){
				String catID = (String)li.next();
				li.next();
				String total = getNumRequired(catID);
				SQLStatement.executeUpdate("UPDATE pqfCategories SET "+"numRequired="+
						total+" WHERE catID="+catID+";");
			}//for
			String selectQuery = "SELECT catID,COUNT(*) AS total FROM pqfCategories "+
					"INNER JOIN pqfSubCategories ON (catID=categoryID) "+
					"INNER JOIN pqfQuestions ON (subCatID=subCategoryID) "+
					"GROUP BY catID";
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				String catID = SQLResult.getString("catID");
				String total = SQLResult.getString("total");
				updateQueries.add("UPDATE pqfCategories SET "+"numQuestions="+total+" WHERE catID="+catID+";");
			}//while
			SQLResult.close();
			for(String updateQuery : updateQueries)
				SQLStatement.executeUpdate(updateQuery);
			isNumQuestionsMapSet = true;
		}finally{
			DBClose();
		}//finally
	}//updateNumRequiredCounts

	public boolean isRequired() {
		if ("0".equals(numRequired))
			return false;
		return true;
	}//isRequired

	public int getCatSize() {
		return numResults;
	}

	public void saveMatrix(java.util.Enumeration<String> e,String auditType) throws Exception {
//		String tableName = auditType.toLowerCase()+"Matrix";
		String deleteQuery = "DELETE FROM desktopMatrix WHERE auditType='"+auditType+"';";
		try {
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
			if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)){
				deleteQuery = "DELETE FROM pqfOpMatrix;";
				SQLStatement.executeUpdate(deleteQuery);
			}//if
			boolean doDesktopInsert = false;
			boolean doOpInsert = false;
			String desktopInsertQuery = "INSERT INTO desktopMatrix (auditType,catID,qID)"+"VALUES ";
			String opInsertQuery = "INSERT INTO pqfOpMatrix (catID,opID,riskLevel)"+"VALUES ";
			while (e.hasMoreElements()){
				String temp = (String)e.nextElement();
				if (temp.startsWith("checked_")) {
					doDesktopInsert = true;
					int begCID = temp.indexOf("_cID")+5;
					int endCID = temp.indexOf("_qID");
					int begQID = temp.indexOf("_qID")+5;
					String cID = temp.substring(begCID, endCID);
					String qID = temp.substring(begQID);
					//pcBean.writeToDB(subCID, subQID);
					desktopInsertQuery += "('"+auditType+"',"+cID+","+qID+"),";
				}//if
				if (temp.startsWith("opChecked_")) {
					doOpInsert = true;
					int endRiskLevel = temp.indexOf("_cID");
					int begCID = temp.indexOf("_cID")+5;
					int endCID = temp.indexOf("_oID");
					int begOID = temp.indexOf("_oID")+5;
					String riskLevel = temp.substring(10, endRiskLevel);
					System.out.println(riskLevel);
					String cID = temp.substring(begCID, endCID);
					String oID = temp.substring(begOID);
					//pcBean.writeToDB(subCID, subQID);
					opInsertQuery += "("+cID+","+oID+","+riskLevel+"),";
				}//if
			}//while
			if (doDesktopInsert){
				desktopInsertQuery = desktopInsertQuery.substring(0,desktopInsertQuery.length()-1);
				SQLStatement.executeUpdate(desktopInsertQuery);
			}//if
			if (doOpInsert){
				opInsertQuery = opInsertQuery.substring(0,opInsertQuery.length()-1);
				SQLStatement.executeUpdate(opInsertQuery);
			}//if
		}finally{
			DBClose();
		}//finally
	}//saveMatrix

	public String getMatrixChecked(String catID,String qID,String auditType) throws Exception {
		if (null==categoryMatrixAL)
			setCategoryMatrix(auditType);
		if (categoryMatrixAL.contains(catID+"-"+qID))
			return "checked";
		return "";
	}//getMatrixChecked

	public String getOpMatrixHighRiskChecked(String catID,String opID) throws Exception {
		if (null==opCategoryMatrixHighRiskAL)
			setOpCategoryMatrixHighRisk();
		if (opCategoryMatrixHighRiskAL.contains(catID+"-"+opID))
			return "checked";
		return "";
	}//getOpMatrixHighRiskChecked
	public String getOpMatrixLowRiskChecked(String catID,String opID) throws Exception {
		if (null==opCategoryMatrixLowRiskAL)
			setOpCategoryMatrixLowRisk();
		if (opCategoryMatrixLowRiskAL.contains(catID+"-"+opID))
			return "checked";
		return "";
	}//getOpMatrixLowRiskChecked

	public void setCategoryMatrix(String auditType) throws Exception {
		String selectQuery = "SELECT * FROM desktopMatrix WHERE auditType='"+auditType+"';";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			categoryMatrixAL = new ArrayList<String>();
			while (SQLResult.next()) {
				String catID = SQLResult.getString("catID");
				String qID = SQLResult.getString("qID");
				categoryMatrixAL.add(catID+"-"+qID);
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setCategoryMatrix

	public void setOpCategoryMatrixHighRisk() throws Exception {
		try{
			String selectQuery = "SELECT * FROM pqfOpMatrix WHERE riskLevel="+ContractorBean.RISK_LEVEL_VALUES_ARRAY[1]+";";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			opCategoryMatrixHighRiskAL = new ArrayList<String>();
			while (SQLResult.next())
				opCategoryMatrixHighRiskAL.add(SQLResult.getString("catID")+"-"+SQLResult.getString("opID"));
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setOpCategoryMatrixHighRisk
	public void setOpCategoryMatrixLowRisk() throws Exception {
		try{
			String selectQuery = "SELECT * FROM pqfOpMatrix WHERE riskLevel="+ContractorBean.RISK_LEVEL_VALUES_ARRAY[0]+";";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			opCategoryMatrixLowRiskAL = new ArrayList<String>();
			while (SQLResult.next())
				opCategoryMatrixLowRiskAL.add(SQLResult.getString("catID")+"-"+SQLResult.getString("opID"));
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setOpCategoryMatrixLowRisk

	public void generateDynamicCategories(String conID, String auditType, String riskLevel) throws Exception {
		try{
			DBReady();
			HashSet<String> catIDSet = new HashSet<String>();
			if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType)){
				String selectQuery = "SELECT catID FROM desktopMatrix INNER JOIN pqfData ON (desktopMatrix.qID=pqfData.questionID "+
						"AND auditType='"+auditType+"') INNER JOIN "+
						"pqfQuestions ON (pqfQuestions.questionID=desktopMatrix.qID) WHERE conID="+conID+
						" AND (questionType='Service' AND answer IN ('C','C S') OR "+
						"questionType IN ('Industry','Main Work') AND answer='X') GROUP BY catID;";
				ResultSet SQLResult = SQLStatement.executeQuery(selectQuery );
				while (SQLResult.next())
					catIDSet.add(SQLResult.getString("catID"));
				SQLResult.close();
			}//if
			if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)){
				String selectQuery = "SELECT catID FROM pqfOpMatrix INNER JOIN generalContractors ON "+
					"(pqfOpMatrix.opID=generalContractors.genID "+
					"AND generalContractors.subID="+conID+" "+
					"AND riskLevel="+riskLevel+")";
				ResultSet SQLResult = SQLStatement.executeQuery(selectQuery );
				while (SQLResult.next())
					catIDSet.add(SQLResult.getString("catID"));
				SQLResult.close();
			}//if
			String replaceQuery = "REPLACE pqfCatData (catID,conID,applies,requiredCompleted,numAnswered,numRequired,percentCompleted) VALUES ";
			setListWithData("number",auditType,conID);
	
			boolean doInsert = false;
			while (isNextRecord()){
				if (!"Yes".equals(applies) && catIDSet.contains(catID)){
					replaceQuery += "("+catID+","+conID+",'Yes',0,0,"+numRequired+",0),";
					doInsert = true;
				} else if (doesCatApply() && !catIDSet.contains(catID)){
					replaceQuery += "("+catID+","+conID+",'No',0,0,0,100),";
					doInsert = true;
				}//if
			}//while
	//		closeList();
			replaceQuery = replaceQuery.substring(0,replaceQuery.length()-1);
			replaceQuery +=";";
			if (doInsert)
				SQLStatement.executeUpdate(replaceQuery);
		}finally{
			DBClose();
		}//finally
	}//generateDynamicCategories

	public void regenerateAllPQFCategories() throws Exception {
		ArrayList<String> conIDsAL = new ArrayList<String>();
		String selectQuery = "SELECT id FROM accounts WHERE type='Contractor'";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery );
			while (SQLResult.next())
				conIDsAL.add(SQLResult.getString("id"));
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		for (String conID : conIDsAL){
			generateDynamicCategories(conID,Constants.PQF_TYPE,ContractorBean.RISK_LEVEL_VALUES_ARRAY[0]);
			generateDynamicCategories(conID,Constants.PQF_TYPE,ContractorBean.RISK_LEVEL_VALUES_ARRAY[1]);
		}//for
	}//regenerateDAllPQFCategories

	public String getPercentShow(String percent) {
		if ("No".equals(applies))
			return "NA";
		return percent+"%";
	}//getPercentShow

	public String getPercentCheck(String percent) {
		if ("100".equals(percent))
			return "<img src=images/okCheck.gif width=19 height=15 alt='100%'>";
		return "<img src=images/notOkCheck.gif width=19 height=15 alt='Not Complete'>";
	}//getPercentCheck

	public boolean doesCatApply() {
		return !"No".equals(applies);
	}//doesCatApply
/*
SELECT  * 
FROM pqfCategories AS cat
JOIN pqfSubCategories AS sub
JOIN pqfQuestions AS ques
LEFT  JOIN pqfData ON ( ques.questionID = pqfData.questionID ) 
WHERE categoryid = catID AND subCategoryID = subCatID AND isRequired =  'Yes'
ORDER  BY category, sub.number, ques.number
*/
}//CategoryBean