package com.picsauditing.PICS.pqf;

import java.sql.*;
import java.util.*;
import com.picsauditing.PICS.Utilities;

public class SubCategoryBean extends com.picsauditing.PICS.DataBean {
	static String DEFAULT_SUBCATEGORY = "--Sub Category--";

	public String subCatID = "";
	public String categoryID = "";
	public String subCategory = "";
	public String number = "";

	public ArrayList<String> subCategories = null;
	public ArrayList<String> allSubCategories = null;
	public String subCatArrayCatID = null;

	ResultSet listRS = null;
	int numResults = 0;
	int count = 0;

	public void setFromDB(String subCID) throws Exception {
		subCatID = subCID;
		setFromDB();
	}//setFromDB
	
	public void setFromDB() throws Exception {
		if ((null == subCatID) || ("".equals(subCatID)))
			throw new Exception("Can't set PQF Sub Category from DB because subCatID is not set");
		String selectQuery = "SELECT * FROM pqfSubCategories WHERE subCatID="+subCatID+";";
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
		subCatID = SQLResult.getString("subCatID");
		categoryID = SQLResult.getString("categoryID");
		subCategory = SQLResult.getString("subCategory");
		number = SQLResult.getString("number");
	}//setFromResultSet

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE pqfSubCategories SET "+
			"subCategory='"+Utilities.escapeQuotes(subCategory)+
			"',number='"+number+
			"',categoryID='"+categoryID+
			"' WHERE subCatID="+subCatID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
		subCatArrayCatID = null;
	}//writeToDB

	public void writeNewToDB() throws Exception {
		String insertQuery = "INSERT INTO pqfSubCategories (subCategory,number,categoryID)" +
			" VALUES ('"+Utilities.escapeQuotes(subCategory)+"',"+number+","+categoryID+");";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
		subCatArrayCatID = null;
	}//writeNewToDB

	public void deleteSubCategory(String subCID, String rootPath) throws Exception {
		String deleteQuery = "DELETE FROM pqfSubCategories WHERE subCatID="+subCID+" LIMIT 1;"; 
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
		QuestionBean pqBean = new QuestionBean();
		pqBean.deleteAllQuestions(subCID, rootPath);
		subCatArrayCatID = null;
	}//deleteSubCategory

	public void deleteAllSubCats(String catID, String rootPath) throws Exception {
		setPQFSubCategoriesArray(catID);
		ListIterator li = subCategories.listIterator();
		while (li.hasNext()) {
			String tempSubCatID = (String)li.next();
			deleteSubCategory(tempSubCatID, rootPath);
			li.next();
		}//while
	}//deleteAllSubCats

	public void setFromRequest(javax.servlet.http.HttpServletRequest r) throws Exception {
		subCategory = r.getParameter("subCategory");
		categoryID = r.getParameter("categoryID");
		number = r.getParameter("number");
	}//setFromRequest

	public void setList(String orderBy, String catID) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String selectQuery = "SELECT * FROM pqfSubCategories WHERE categoryID="+
			catID+" ORDER BY "+orderBy+";";
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
					String subCID = temp.substring(4);
					String num = request.getParameter("num_"+subCID);
					String udpateQuery = "UPDATE pqfSubCategories SET number="+num+" WHERE subCatID="+subCID+";";
					SQLStatement.executeUpdate(udpateQuery);
				}//if
			}//while
		}finally{
			DBClose();
		}//finally
	}//updateNumbering
	
	public void renumberPQFSubCategories(String catID, String aType)  throws Exception {
		String selectQuery = "SELECT subCatID FROM pqfSubCategories WHERE categoryID="+
			catID+" ORDER BY number";
		try{
			DBReady();
			ResultSet SQLResult  = SQLStatement.executeQuery(selectQuery);
			ArrayList<String> updateQueries = new ArrayList<String>();
			int nextNumber = 1;
			while (SQLResult.next()) {
				String subCID = SQLResult.getString("subCatID");
				updateQueries.add("UPDATE pqfSubCategories SET number="+nextNumber+
						" WHERE subCatID="+subCID+";");
				nextNumber++;
			}//while
			SQLResult.close();
			for(String updateQuery : updateQueries)
				SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
		new CategoryBean().updateNumRequiredCounts(aType);
	}//renumberPQFSubCategories
	
	public boolean isOK() {
		errorMessages = new Vector<String>();
		if ((null == number) || (number.length() == 0))
			errorMessages.addElement("Please enter the sub category number");
		if ((null == subCategory) || (subCategory.length() == 0))
			errorMessages.addElement("Please enter the sub category name");
		return (errorMessages.size() == 0);
	}//isOK

	public void setPQFSubCategoriesArray(String catID) throws Exception {
		if (catID.equals(subCatArrayCatID))
			return;
		subCategories = new ArrayList<String>();
		allSubCategories = new ArrayList<String>();
		String selectQuery = "SELECT * FROM pqfSubCategories ORDER BY number";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				if (catID.equals(SQLResult.getString("categoryID"))) {
					subCategories.add(SQLResult.getString("subCatID"));
					subCategories.add(SQLResult.getString("subCategory"));
				}//if
				allSubCategories.add(SQLResult.getString("subCatID"));
				allSubCategories.add(SQLResult.getString("subCategory"));			
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		subCatArrayCatID = catID;
	}//setPQFSubCategoriesArray

	public String getPQFSubCategorySelect(String name, String classType, String selectedSubCategory, String catID) throws Exception {
		setPQFSubCategoriesArray(catID);
		return Utilities.inputSelect2First(name, classType, selectedSubCategory, 
			(String[])subCategories.toArray(new String[0]),"0",DEFAULT_SUBCATEGORY);
	}//getPQFSubCategorySelect

//	public String getPQFAllSubCategorySelect(String name, String classType, String selectedSubCategory) throws Exception {
//		setPQFSubCategoriesArray("0");
//		return Utilities.inputSelect2First(name, classType, selectedSubCategory, 
//			(String[])allSubCategories.toArray(new String[0]),"0",DEFAULT_SUBCATEGORY);
//	}//getPQFSubCategorySelect

	public String getPQFSubCategorySelectDefaultSubmit(String name, String classType, 
			String selectedSubCategory, String catID) throws Exception {
		setPQFSubCategoriesArray(catID);
		return Utilities.inputSelect2FirstSubmit(name, classType, selectedSubCategory, 
				(String[])subCategories.toArray(new String[0]),"0",DEFAULT_SUBCATEGORY);	
	}//getPQFSubCategorySelectDefaultSubmit

	public String getSubCategoryName(String subCID) throws Exception {
		setPQFSubCategoriesArray("0");
		int i = allSubCategories.indexOf(subCID);
		if (-1 == i)
			return "Does not exist";
		return (String)allSubCategories.get(i+1);
	}//getSubCategoryName
}//SubCategoryBean