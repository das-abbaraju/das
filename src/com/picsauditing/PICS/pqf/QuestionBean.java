package com.picsauditing.PICS.pqf;

import java.sql.*;
import java.util.*;
import java.io.*; 
import com.picsauditing.PICS.*;


public class QuestionBean extends com.picsauditing.PICS.DataBean {
	public DataBean data = new DataBean();
	boolean isJoinedWithData = false;

	public static final String DEFAULT_TYPE = "--Type--";
	static final String[] TYPE_ARRAY = {DEFAULT_TYPE,"Check Box","Country","Date","Decimal Number","Drop Down",
		"File","Industry","License","Main Work","Manual","Money","Office","Office Location","Radio","Service","State","Text",
		"Text Area","Yes/No","Yes/No/NA"};	// must match ENUM in db
	static final String[] YES_NO_OPTIONS_ARRAY = {"Yes","No"};
	static final String[] YES_NO_NA_OPTIONS_ARRAY = {"Yes","No","NA"};
	public static final String[] YES_NO_OFFICE_ARRAY = {"No","Yes","Yes with Office"};
	public static final String[] YES_NO_DEPENDS_OPTIONS_ARRAY = {"Yes","No","Depends"};
	public static final String[] DEPENDS_ANSWER_ARRAY = {"Yes","Yes","No","No","X","Checked"};
//	public static final boolean VERIFIED_ANSWER = true;
//	public static final boolean ORIGINAL_ANSWER = false;
//	static final String[] BUSINESS_TYPE_OPTIONS_ARRAY = {"Sole Owner","Partnership","Corporation"};
//	static final String[] EMR_OPTIONS_ARRAY = {"Interstate rate","Intrastate rate",
//			"Monopolistic state rate","Dual rate"};

	public String questionID = "";
	public String subCategoryID = "";
	public String number = "0";
	public String question = "";
	public String hasRequirement = "";
	public String okAnswer = ""; // SET Yes,No,NA
	public String requirement = "";
	public String isRequired = "";
	public String dependsOnQID = "";
	public String dependsOnAnswer = "";
	public String questionType = ""; // ENUM Text, Radio, Check Boxes, Text Area, Title, Drop Down, Yes/No
	public String lastModified = "";
	public String title = "";
	public String linkURL1 = "";
	public String linkText1 = "";
	public String linkURL2 = "";
	public String linkText2 = "";
	public String linkURL3 = "";
	public String linkText3 = "";
	public String linkURL4 = "";
	public String linkText4 = "";
	public String linkURL5 = "";
	public String linkText5 = "";
	public String linkURL6 = "";
	public String linkText6 = "";
	public String isGroupedWithPrevious = "No";
	public String isRedFlagQuestion = "No";
	public ArrayList<String> links = new ArrayList<String>();

	ArrayList<String> questionIDs = null;
	String questionsArraySubCatID = null;
	public TreeMap<String,String> QMap = null;
	public TreeMap<String,String> QToCatIDMap = null;

	ResultSet listRS = null;
	int numResults = 0;
	public int count = 0;
	int groupCount = 0;
	public boolean highlightRequired = true;

	public void setFromDB(String qID) throws Exception {
		questionID = qID;
		setFromDB();
	}//setFromDB
	
	public void setFromDB() throws Exception {
		if ((null == questionID) || ("".equals(questionID)))
			throw new Exception("Can't set PQF Question from DB because questionID is not set");
		String Query = "SELECT * FROM pqfQuestions WHERE questionID = " + questionID + ";";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		questionID = SQLResult.getString("questionID");
		subCategoryID = SQLResult.getString("subCategoryID");
		number = SQLResult.getString("number");
		question = SQLResult.getString("question");
		hasRequirement = SQLResult.getString("hasRequirement");
		okAnswer = SQLResult.getString("okAnswer");
		requirement = SQLResult.getString("requirement");
		isRequired = SQLResult.getString("isRequired");
		dependsOnQID = SQLResult.getString("dependsOnQID");
		dependsOnAnswer = SQLResult.getString("dependsOnAnswer");
		questionType = SQLResult.getString("questionType");
		lastModified = com.picsauditing.PICS.DateBean.toShowFormat(SQLResult.getString("lastModified"));
		title = SQLResult.getString("title");
		isGroupedWithPrevious = SQLResult.getString("isGroupedWithPrevious");
		isRedFlagQuestion = SQLResult.getString("isRedFlagQuestion");
		links.clear();
		for (int i=1;i<=6;i++)
			addLink(SQLResult.getString("linkURL"+i),SQLResult.getString("linkText"+i));
	}//setFromResultSet

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE pqfQuestions SET "+
			"subCategoryID='"+subCategoryID+
			"',number="+Utilities.intToDB(number)+
			",question='"+Utilities.escapeQuotes(question)+
			"',hasRequirement='"+hasRequirement+
			"',okAnswer='"+okAnswer+
			"',requirement='"+Utilities.escapeQuotes(requirement)+
			"',isRequired='"+isRequired+
			"',dependsOnQID="+Utilities.intToDB(dependsOnQID)+
			",dependsOnAnswer='"+dependsOnAnswer+
			"',questionType='"+questionType+
			"',isGroupedWithPrevious='"+isGroupedWithPrevious+
			"',isRedFlagQuestion='"+isRedFlagQuestion+
			"',title='"+Utilities.escapeQuotes(title);
		for (int i=1;i<=6;i++) {
			updateQuery+="',linkURL"+i+"='"+Utilities.escapeQuotes(getLinkURL(i));
			updateQuery+="',linkText"+i+"='"+Utilities.escapeQuotes(getLinkText(i));				
			}//for
		updateQuery+="',lastModified=NOW() WHERE questionID="+questionID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
		questionsArraySubCatID = null;
	}//writeToDB

	public void writeNewToDB(javax.servlet.jsp.PageContext pageContext) throws Exception {
		String folderPath;
		String insertQuery = "INSERT INTO pqfQuestions (subCategoryID,number,question,hasRequirement,okAnswer,requirement,isRequired,dependsOnQID,"+
			"dependsOnAnswer,questionType,lastModified,"+
			"title,linkURL1,linkText1,linkURL2,linkText2,linkURL3,linkText3,linkURL4,linkText4,linkURL5,linkText5,"+
			"linkURL6,linkText6,isGroupedWithPrevious,isRedFlagQuestion,dateCreated)" +
			" VALUES ('"+subCategoryID+"',"+Utilities.intToDB(number)+",'"+Utilities.escapeQuotes(question)+
			"','"+hasRequirement+"','"+okAnswer+"','"+Utilities.escapeQuotes(requirement)+
			"','"+isRequired+"',"+Utilities.intToDB(dependsOnQID)+",'"+dependsOnAnswer+"','"+questionType+"',NOW(),'"+Utilities.escapeQuotes(title);
		for (int i=1;i<=6;i++)
			insertQuery+="','"+Utilities.escapeQuotes(getLinkURL(i))+"','"+Utilities.escapeQuotes(getLinkText(i));
		insertQuery+="','"+isGroupedWithPrevious+"','"+isRedFlagQuestion+"',NOW());";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			if ("File".equals(questionType)) {
				ResultSet SQLResult = SQLStatement.getGeneratedKeys();
				if (SQLResult.next()) {
					String ftpDir = pageContext.getSession().getServletContext().getInitParameter("FTP_DIR");
					String temp = File.separator;
					folderPath = ftpDir+temp+"files"+temp+"pqf"+temp+"qID_"+SQLResult.getString("GENERATED_KEY");
					File newFolderFile = new File(folderPath);
					newFolderFile.mkdirs();
				}//if
			}//if
		}finally{
			DBClose();
		}//finally
		questionsArraySubCatID = null;
	}//writeNewToDB
	
	public void deleteQuestion(String qID, String rootPath) throws Exception {
		try{
			DBReady();
			String deleteQuery = "DELETE FROM pqfQuestions WHERE questionID="+qID+" LIMIT 1;";
			SQLStatement.executeUpdate(deleteQuery);
			String deleteQuery2 = "DELETE FROM pqfData WHERE questionID="+qID+";";
			SQLStatement.executeUpdate(deleteQuery2);
			String deleteQuery3 = "DELETE FROM pqfOptions WHERE questionID="+qID+";";
			SQLStatement.executeUpdate(deleteQuery3);
		}finally{
			DBClose();
		}//finally
		String temp = File.separator;
		String folderPath = rootPath+"files"+temp+"pqf"+temp+"qID_"+qID;
		File folderFile = new File(folderPath);
		deleteDirectory(folderFile);

		questionsArraySubCatID = null;
	}//deleteQuestion

	public void deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i=0; i< files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}//else
			}//for
			path.delete();
		}//if
	}//deleteDirectory


	public void deleteAllQuestions(String subCatID, String rootPath) throws Exception {
		setPQFQuestionsArray(subCatID);
		ListIterator li = questionIDs.listIterator();
		while (li.hasNext()) {
			String tempquestionID = (String)li.next();
			deleteQuestion(tempquestionID, rootPath);
		}//while
	}//deleteAllQuestions

	public void setFromRequest(javax.servlet.http.HttpServletRequest r) throws Exception {
		subCategoryID = r.getParameter("subCategoryID");
		number = r.getParameter("number");
		question = r.getParameter("question");
		hasRequirement = r.getParameter("hasRequirement");
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
		if (0 != okAnswer.length())
			okAnswer = okAnswer.substring(0,okAnswer.length()-1);
		requirement = r.getParameter("requirement");
		isRequired = r.getParameter("isRequired");
		dependsOnQID = r.getParameter("dependsOnQID");
		dependsOnAnswer = r.getParameter("dependsOnAnswer");
		questionType = r.getParameter("questionType");
		title = r.getParameter("title");
		isGroupedWithPrevious = r.getParameter("isGroupedWithPrevious");
		isRedFlagQuestion = r.getParameter("isRedFlagQuestion");
		links.clear();
		for (int i=1;i<=6;i++)
			addLink(r.getParameter("linkURL"+i),r.getParameter("linkText"+i));
	}//setFromRequest

	/*public void setList(String orderBy, String catID) throws Exception {
		DBReady();
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String Query = "SELECT * FROM pqfQuestions WHERE isVisible='Yes' AND categoryID = " +
			catID + " ORDER BY " + orderBy + ";";
		listRS = SQLStatement.executeQuery(Query);
		numResults = 0;
		while (listRS.next())
			numResults++;
		listRS.beforeFirst();
		count = 0;
	}//setList*/

	public void setList(String orderBy, String qType) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
			String selectQuery = "SELECT * FROM pqfQuestions WHERE questionType='"+qType+"' ORDER BY "+orderBy+";";
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

	public void setSubList(String orderBy, String subCatID) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String selectQuery = "SELECT * FROM pqfQuestions WHERE subCategoryID="+
			subCatID+" ORDER BY "+orderBy+";";
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
	}//setSubList

	public void setSubListWithData(String orderBy, String subCatID, String conID) throws Exception {
		if ("".equals(orderBy) || null == orderBy)
			orderBy = "number";
		String selectQuery = "SELECT * FROM pqfQuestions LEFT JOIN pqfData ON pqfQuestions.questionID = pqfData.questionID"+
			" AND conID="+conID+" WHERE subCategoryID="+subCatID+" ORDER BY "+orderBy+";";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
			isJoinedWithData = true;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setSubListWithData

	public boolean isNextRecord() throws Exception {
		try{
			if (!(count <= numResults && listRS.next()))
				return false;
			count++;
			setFromResultSet(listRS);
			if (isJoinedWithData) {
				questionID = listRS.getString("pqfQuestions.questionID");
				data.setFromResultSet(listRS);
				if (listRS.wasNull() || null==data.conID || "null".equals(data.conID))
					data = new DataBean();
			}//if
			if (!"Yes".equals(isGroupedWithPrevious))
				groupCount++;
			return true;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch		
	}//isNextRecord

	public void closeList() throws Exception {
		count = 0;
		numResults = 0;
		isJoinedWithData = false;
		if (null != listRS){
			listRS.close();
			listRS = null;
		}//if
		DBClose();
	}//closeList

	public String getBGColor() {
		if ((count % 2) == 1)	return " bgcolor=FFFFFF";
		else	return "";
	}//getBGColor

	public String getGroupBGColor() {
		if ((groupCount % 2) == 1)	return " bgcolor=FFFFFF";
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
					String updateQuery = "UPDATE pqfQuestions SET number="+num+" WHERE questionID="+qID+";";
					SQLStatement.executeUpdate(updateQuery);
				}//if
			}//while
		}finally{
			DBClose();
		}//finally
	}//updateNumbering
	
	public void renumberPQF(String subCatID, int auditTypeID)  throws Exception {
		String selectQuery = "SELECT questionID FROM pqfQuestions WHERE subCategoryID="+
			subCatID+" ORDER BY number";
		ArrayList<String> updateQueries = new ArrayList<String>();
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			int nextNumber = 1;
			while (SQLResult.next()) {
				String qID = SQLResult.getString("questionID");
				updateQueries.add(" UPDATE pqfQuestions SET number="+nextNumber+
						" WHERE questionID="+qID+";");
				nextNumber = nextNumber+1;
			}//while
			SQLResult.close();
			for(String updateQuery:updateQueries)
				SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
		new CategoryBean().updateNumRequiredCounts(auditTypeID);
	}
		
	public boolean isOK() {
		errorMessages = new Vector<String>();
			if (null==questionType || DEFAULT_TYPE.equals(questionType))
				errorMessages.addElement("Please select the question Type");
			if (null==subCategoryID || 0==subCategoryID.length() || "0".equals(subCategoryID))
				errorMessages.addElement("Please select the Sub Category");
			if (null==hasRequirement || 0==hasRequirement.length())
				errorMessages.addElement("Please select if question Has Requirement or not");
			if ("Yes".equals(hasRequirement) && (null==okAnswer || 0==okAnswer.length()))
				errorMessages.addElement("Please select at least one OK answer");
			if ("Yes".equals(hasRequirement) && (null==requirement || 0==requirement.length()))
				errorMessages.addElement("Please enter the Requirement");
			if (null==isRequired || 0==isRequired.length())
				errorMessages.addElement("Please select Required or not");
			if ("Depends".equals(isRequired) && (null==dependsOnQID || 0==dependsOnQID.length() || "0".equals(dependsOnQID)))
				errorMessages.addElement("Please enter Depends on ID");
			if (null==question || 0==question.length())
				errorMessages.addElement("Please enter the Question");
			return (errorMessages.size() == 0);
	}//isOK

	public void addSubCategory(String newSubCategory, String number) throws Exception {
		String Query = "INSERT INTO pqfSubCategories (subCategory,number) VALUES ('"+
				Utilities.escapeQuotes(newSubCategory)+"',"+number+");"; 
		try{
			DBReady();
			SQLStatement.executeUpdate(Query);
		}finally{
			DBClose();
		}//finally
	}//addCategory

	public String getPQFTypeSelect(String name, String classType, String selectedType) throws Exception {
		return Utilities.inputSelect(name, classType, selectedType, TYPE_ARRAY);
	}//getPQFTypeSelect

	public void addOption(String qID, String newOption) throws Exception {
		String insertQuery = "INSERT INTO pqfOptions (questionID,option)" +
			" VALUES ('"+qID+"','"+Utilities.escapeQuotes(newOption)+"');";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//addOption

	public void deleteOption(String optionID) throws Exception {
		String deleteQuery = "DELETE FROM pqfOptions WHERE optionID="+optionID+" LIMIT 1;"; 
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
	}//deleteOption

	public boolean calcIsRequired(DataBean pdBean) throws Exception {
		if (null == pdBean)
			return ("Yes".equals(isRequired));
		return ("Yes".equals(isRequired) || ("Depends".equals(isRequired) && dependsOnAnswer.equals(pdBean.getAnswer(dependsOnQID))));
	}//calcIsRequired

	public String getClassAttribute(DataBean pdBean) throws Exception {
		if (highlightRequired && calcIsRequired(pdBean))
			return " class=required";
		else
			return " class=blueMain";
	}//getClassAttribute

//	public String getInputElement(boolean isVerifiedAnswer) throws Exception {
	public String getInputElement() throws Exception {
		String inputName = "answer_"+questionID;
		String value = data.answer;
		String comment = data.comment;
		if ("Yes/No".equals(questionType))
			return Inputs.getYesNoRadio(inputName,"forms",value);
		if ("Yes/No/NA".equals(questionType))
			return Inputs.getYesNoNARadio(inputName,"forms",value);
		if ("Manual".equals(questionType)) {
				return Inputs.getYesNoNARadio(inputName,"forms",value)+
					"<br>Comments:<input type=text name=comment_"+questionID+" class=forms size=30 value=\""+comment+"\">";
		}//if
		if ("Office".equals(questionType))
			return Inputs.getYesNoNARadio(inputName,"forms",value);
		if ("Office Location".equals(questionType))
			return Inputs.inputSelect(inputName,"forms",value,QuestionBean.YES_NO_OFFICE_ARRAY);
		if ("Check Box".equals(questionType) || "Industry".equals(questionType) || "Main Work".equals(questionType))
			return Inputs.getCheckBoxInput(inputName,"forms",value, "X");
		if ("Service".equals(questionType)) {
			String temp = "<nobr>";
			String tempValue = "";
			if (-1 != value.indexOf("C"))
				tempValue = "C";
			temp+=Inputs.getCheckBoxInput(inputName+"_C","forms",tempValue, "C")+" C  ";
			if (-1 != value.indexOf("S"))
				tempValue = "S";
			temp+=Inputs.getCheckBoxInput(inputName+"_S","forms",tempValue, "S")+" S";
			temp+="</nobr>";
			return temp;
		}//if
		if ("State".equals(questionType))
			return Inputs.getLongStateSelect(inputName,"forms",value);
		if ("Country".equals(questionType))
			return Inputs.getCountrySelect(inputName,"forms",value);
		if ("Date".equals(questionType))
			return Inputs.getDateInput(inputName,"forms",DateBean.toShowFormat(value),"formEdit");
		if ("Text Area".equals(questionType))
			return "</td></tr><tr class=forms "+getGroupBGColor()+"><td>&nbsp;</td><td colspan=2>"+
				Inputs.getTextAreaInput(inputName,"forms",value,"70","4");
		if ("Radio".equals(questionType)) {
			OptionBean oBean = new OptionBean();
			String[] optionsArray = oBean.getOptionsArray(questionID);
			return Inputs.getRadioInput(inputName,"forms",value,optionsArray);
		}//if
		if ("Drop Down".equals(questionType)) {
			OptionBean oBean = new OptionBean();
			String[] optionsArray = oBean.getOptionsArray(questionID);
			return Inputs.inputSelect(inputName,"forms",value,optionsArray);
		}//if
		if ("Money".equals(questionType))
			return "<nobr>$<"+"input type=text name="+inputName+" class=forms size=19 value=\""+value+"\" "+
				"onBlur=\"validateNumber('"+inputName+"','Question "+number+"');\">";	
		if ("Decimal Number".equals(questionType))
			return "<"+"input type=text name="+inputName+" class=forms size=19 value=\""+value+"\" "+
				"onBlur=\"validateNumber('"+inputName+"','Question "+number+"');\">";
		if ("File".equals(questionType)) {
			if ("".equals(value))
				value = "Not Uploaded";
			else
				value = "Uploaded";
			return "<"+"input name="+inputName+" type=file class=forms size=19>";
		}//if
		return "<input type=text name="+inputName+" class=forms size=20 value=\""+value+"\">";
	}//getInputElement

	public String getVerifiedInputElement() throws Exception {
		String qID = questionID;
		String temp = "<input type=text name=verifiedAnswer_"+qID+" class=forms size=60 value=\""+data.verifiedAnswer+"\">";
		return temp;
	}//getVerifiedInputElement

	public boolean hasOptions() {
		return ("Radio".equals(questionType) || "Check Boxes".equals(questionType) || "Drop Down".equals(questionType));
	}//hasOptions

	public String getTitleLine(String classType) {
		if (0 == title.length())
			return "";
		return "<tr class="+classType+" "+getGroupBGColor()+"><td colspan=3><strong>"+title+"</strong></td></tr>";
	}//getTitleLine

	public String getTitleShort() {
		int l = title.length();
		if (0 == l)			return "";
		else if (l < 10)	return "("+title.substring(0,l)+") ";
		return "("+title.substring(0,10)+") ";
	}//getTitleShort

	public void setPQFQuestionsArray(String subCatID) throws Exception {
		if (subCatID.equals(questionsArraySubCatID))
			return;
		questionIDs = new ArrayList<String>();
		String selectQuery = "SELECT * FROM pqfQuestions WHERE subCategoryID="+subCatID+" ORDER BY number";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				questionIDs.add(SQLResult.getString("questionID"));
			questionsArraySubCatID = subCatID;
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setPQFQuestionsArray

	public String getVerifiedAnswerView() throws Exception {
		String value = data.verifiedAnswer;
		if ("".equals(value) || (data.answer.equals(data.verifiedAnswer)))
			return "";
		String temp = "<br>Verified Answer: <strong><span class=greenMain>"+value+"</span></strong>";
		temp += "<img src=images/okCheck.gif width=19 height=15><span class=greenMain>Verified on "+data.dateVerified+"</span>";
		return temp;
	}//getVerifiedAnswerView

	public String getCommentView() throws Exception {
		String comment = data.comment;
		if ("".equals(comment))
			return "";
		return "<br>Comment: <span class=greenMain>"+comment+"</span>";
	}//getCommentView

	public String getAnswerView(String value) throws Exception {
		if ("Date".equals(questionType) && !"".equals(value))
			value = com.picsauditing.PICS.DateBean.toShowFormat(value);
		if ("File".equals(questionType) && "".equals(value))
			value = "Not Uploaded";
		else if ("File".equals(questionType) && !"".equals(value))
			value = "<a href=# onClick=window.open('servlet/showpdf?id="+data.conID+"&file=pqf"+value+questionID+
				"','','scrollbars=yes,resizable=yes,width=700,height=450')>Uploaded</a>";
		return value;
	}//getAnswerView

	public String getOriginalAnswerView() throws Exception {
		String value = getAnswerView(data.answer);
		if (!"".equals(value))
			value = "<strong><span class=blueMain>"+value+"</span></strong>";
		if ("Yes".equals(data.isCorrect))
			return value+"<img src=images/okCheck.gif width=19 height=15><span class=greenMain>Verified on "+data.dateVerified+"</span>";
		else if ("No".equals(data.isCorrect))
			return value+"<img src=images/notOkCheck.gif width=19 height=15><span class=redMain>Inaccurate Data</span>";
		else
			return value;
	}//getOriginalAnswerView

	public void setQMapFromDB() throws Exception {
		if (null != QMap)
			return;
		String selectQuery = "SELECT * FROM pqfQuestions INNER JOIN pqfSubCategories ON (subCatID=subCategoryID);";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			QMap = new TreeMap<String,String>();
			QToCatIDMap = new TreeMap<String,String>();
			while (SQLResult.next()) {
				setFromResultSet(SQLResult);
				QMap.put(questionID,question);
				QToCatIDMap.put(questionID,SQLResult.getString("categoryID"));
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

	public String getQuestion(String qID) throws Exception {
		setQMapFromDB();
		if (QMap.containsKey(qID))			
			return (String)QMap.get(qID);
		throw new Exception("No pqf question with questionID: "+qID);
	}//getQuestion

	public String getCategoryID(String qID) throws Exception {
		setQMapFromDB();
		if (QToCatIDMap.containsKey(qID))
			return (String)QToCatIDMap.get(qID);
		throw new Exception("No pqf question with questionID: "+qID);
	}//getCategoryID

	public String getLinks() {
		String temp = "";
		for (int i=1;i<=6;i++) {
			if (0 < getLinkURL(i).length())
				temp+="<a class=blueMain href=http://"+getLinkURL(i)+" target=_blank>"+getLinkText(i)+"</a> ";
		}//for
		return temp;
	}//getLinks
	
	public String getLinksWithBreak() {
		String temp = "";
		for (int i=1;i<=6;i++) {
			if (0 < getLinkURL(i).length())
				temp+="<br><a class=blueMain href=http://"+getLinkURL(i)+" target=_blank>"+getLinkText(i)+"</a> ";
		}//for
		return temp;
	}//getLinksWithBreak

	public String getLinksWithCommas() {
		String temp = " ";
		for (int i=1;i<=6;i++) {
			if (0 < getLinkURL(i).length())
				temp+=" <a class=blueMain href=http://"+getLinkURL(i)+" target=_blank>"+getLinkText(i)+"</a>,";
		}//for
		temp = temp.substring(0,temp.length()-1);
		return temp;
	}//getLinksWithCommas

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

	public String isOKAnswerChecked(String s) {
		if (okAnswer.indexOf(s) != -1)
			return "checked";
		return "";
	}//isOKAnswerChecked

	public String onClickCorrectYesNoRadio(String qID, String questionType, String classType, String selected) throws Exception {
		String name = "isCorrect_"+qID;
		StringBuffer temp = new StringBuffer();
		temp.append("<label><input name=").append(name).append(" class=").append(classType);
		temp.append(" type=radio value=Yes");
		if ("Yes".equals(selected))
			temp.append(" checked");
		temp.append(" onClick='document.all.verifiedAnswer_"+qID+".value=document.all.answer_"+qID+".value");
		temp.append("'>Yes</label>");
		temp.append("<label><input name=").append(name).append(" class=").append(classType);
		temp.append(" type=radio value=No");
		if ("No".equals(selected))
			temp.append(" checked");
		temp.append(">No</label>");
		return temp.toString();
	}//onClickCorrectYesNoRadio

	public boolean hasReq() {
		return "Yes".equals(data.wasChanged);
	}//hasReq
	public String getRequirementShow() throws Exception {
		if ("Yes".equals(hasRequirement) && hasReq())
			if (-1 != okAnswer.indexOf(data.answer))
				return "Status: <span class=blueMain><strong>Closed on "+data.dateVerified+"<br>"+requirement+"</strong></span>";
			else
				return "Status: <span class=redMain><strong>Open<br>"+requirement+"</strong></span>";
		return "";
	}//getRequirementShow
}//pqfQuestionBean