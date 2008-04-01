package com.picsauditing.PICS.pqf;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.picsauditing.PICS.PermissionsBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditType;


public class CategoryBean extends com.picsauditing.PICS.DataBean {
	static String DEFAULT_CATEGORY = "--Category--";
	public static String OSHA_CATEGORY_ID = "29";
	public static String OSHA_NUM_REQUIRED = "24";
	public static String SERVICES_CATEGORY_ID = "28";

	public String catID = "";
	public String auditType = "";
	public int auditTypeID;
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
	public Set<String> categoryMatrixSet = null;
	public Set<String> opCategoryMatrixRiskSet = null;
	public TreeMap<String,String> numOfRequiredMap = null;
	boolean isNumQuestionsMapSet = false;

	ResultSet listRS = null;
	int numResults = 0;
	public int count = 0;
	boolean isJoinedWithData = false;

	public void setFromDB(String cID) throws Exception {
		catID = cID;
		setFromDB();
	}
	
	public void setFromDB() throws Exception {
		if ((null == catID) || ("".equals(catID)))
			throw new Exception("Can't set PQF Category from DB because catID is not set");
		String Query = "SELECT * FROM pqfCategories " +
			"JOIN audit_type at ON at.auditTypeID=pqfCategories.auditTypeID "+
			"WHERE pqfCategories.catID = " + catID;
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
		String Query = "SELECT * FROM pqfCategories " +
			"JOIN audit_type at ON at.auditTypeID=pqfCategories.auditTypeID "+
			"LEFT JOIN pqfCatData ON pqfCategories.catID=pqfCatData.catID "+
			"WHERE pqfCategories.catID = " + catID + " AND conID="+cID;
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
	}

	public void setList(String orderBy, String aType) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String selectQuery = "SELECT * FROM pqfCategories pqfCategories "+
				"JOIN audit_type at ON at.auditTypeID=pqfCategories.auditTypeID "+
				"WHERE legacyCode='"+aType+"' ORDER BY "+orderBy+";";
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
	}

	public void setListWithData(String orderBy, String aType, String conID) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String Query = "SELECT * FROM pqfCategories pqfCategories LEFT JOIN pqfCatData pqfCatData ON pqfCategories.catID=pqfCatData.catID "+
				"AND conID="+conID+" JOIN audit_type at ON at.auditTypeID=pqfCategories.auditTypeID "+
				"WHERE legacyCode='"+aType+"' ORDER BY "+orderBy;
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

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		catID = SQLResult.getString("catID");
		auditTypeID = SQLResult.getInt("auditTypeID");
		category = SQLResult.getString("category");
		number = SQLResult.getString("number");
		numRequired = SQLResult.getString("numRequired");
		numQuestions = SQLResult.getString("numQuestions");
		auditType = SQLResult.getString("legacyCode");
	}

	public void setDataFromResultSet(ResultSet SQLResult) throws Exception {
		conID = SQLResult.getString("conID");
		requiredCompleted = SQLResult.getString("requiredCompleted");
		dataNumRequired = SQLResult.getString("pqfCatData.numRequired");
		numAnswered = SQLResult.getString("numAnswered");
		applies = SQLResult.getString("applies");
		percentCompleted = SQLResult.getString("percentCompleted");
		percentVerified = SQLResult.getString("percentVerified");
	}

	private void setDataEmpty() {
		conID = "";
		requiredCompleted = "";
		dataNumRequired = "0";
		numAnswered = "";
		applies = "";
		percentCompleted = "0";
		percentVerified = "0";	
	}

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
		String insertQuery = "INSERT INTO pqfCategories (auditTypeID,category,number)" +
			" VALUES ('"+aType+"','"+Utilities.escapeQuotes(category)+"',"+number+");";
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
			categories = null;
		}finally{
			DBClose();
		}
	}

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
		}
		return true;
	}

	public boolean isNextRecord(PermissionsBean pBean, String cID) throws Exception {
		if (!(count <= numResults && listRS.next()))
			return false;
		count++;
		setFromResultSet(listRS);
		
		// Require users to have the ViewFullPQF to see Work History
		String catWorkHistory = "6";
		if (!catWorkHistory.equals(catID)
			|| pBean.isContractor()
			|| pBean.getPermissions().hasPermission(OpPerms.ViewFullPQF)) {
			if (isJoinedWithData) {
				catID = listRS.getString("pqfCategories.catID");
				setDataFromResultSet(listRS);
				if (listRS.wasNull() || null==conID || "null".equals(conID)) {
					setDataEmpty();
				}
			}
			return true;
		} else
			return isNextRecord(pBean,cID);
	}

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
		String selectQuery = "SELECT catID, category FROM pqfCategories WHERE auditTypeID IN (SELECT auditTypeID FROM audit_type WHERE legacyCode='"+aType+"') ORDER BY number";
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
	
	public boolean showLicenses() {
		if (this.catID.equals("21")) // CONTRACTOR'S LICENSING
			return true;
		if (this.catID.equals("27")) // STATES LICENSED IN (CONTRACTORS LICENSE)
			return true;
		return false;
	}

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
		if (null==categoryMatrixSet)
			setCategoryMatrix(auditType);
		if (categoryMatrixSet.contains(catID+"-"+qID))
			return "checked";
		return "";
	}//getMatrixChecked

	public String getOpMatrixRiskChecked(String catID,String opID, int riskLevel) throws Exception {
		if (null==opCategoryMatrixRiskSet)
			setOpCategoryMatrixRisk();
		if (opCategoryMatrixRiskSet.contains(catID+"-"+opID+"-"+riskLevel))
			return "checked";
		return "";
	}//getOpMatrixRiskChecked

	public void setCategoryMatrix(String auditType) throws Exception {
		String selectQuery = "SELECT * FROM desktopMatrix WHERE auditType='"+auditType+"';";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			categoryMatrixSet = new HashSet<String>();
			while (SQLResult.next()) {
				String catID = SQLResult.getString("catID");
				String qID = SQLResult.getString("qID");
				categoryMatrixSet.add(catID+"-"+qID);
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setCategoryMatrix

	public void setOpCategoryMatrixRisk() throws Exception {
		try{
			String selectQuery = "SELECT * FROM pqfOpMatrix";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			opCategoryMatrixRiskSet = new HashSet<String>();
			while (SQLResult.next())
				opCategoryMatrixRiskSet.add(SQLResult.getString("catID")+"-"+SQLResult.getString("opID")+"-"+SQLResult.getString("riskLevel"));
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setOpCategoryMatrixRisk

	public Set<String> getCategoryForOpRiskLevel(String opID, String riskLevel) throws Exception {
		try{
			String selectQuery = "SELECT catID FROM pqfOpMatrix WHERE opID="+opID+" AND riskLevel="+riskLevel;
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			Set<String> catIDs = new HashSet<String>();
			while (SQLResult.next())
				catIDs.add(SQLResult.getString("catID"));
			SQLResult.close();
			return catIDs;
		}finally{
			DBClose();
		}//finally
	}

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
	}

	public void regenerateAllPQFCategories() throws Exception {
		Map<String, String> list = new HashMap<String, String>();
		String selectQuery = "SELECT id, riskLevel FROM contractor_info";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery );
			while (SQLResult.next())
				list.put(SQLResult.getString("id"), SQLResult.getString("riskLevel"));
			SQLResult.close();
		} finally {
			DBClose();
		}
		for (Map.Entry<String, String> item : list.entrySet()) {
			generateDynamicCategories(item.getKey(),Constants.PQF_TYPE,item.getValue());
		}
	}

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
	
	public String getAuditType() {
		if (AuditType.DESKTOP ==  this.auditTypeID)
			return "Desktop";
		return "";
	}
}
