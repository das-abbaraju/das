package com.picsauditing.PICS.pqf;

import java.sql.*;
import java.util.*;

@Deprecated
/**
 * Use AuditQuestionDAO.findQuestionByType() instead
 */
public class QuestionTypeList extends com.picsauditing.PICS.DataBean {
	public static final String DEFAULT_SELECT_QUESTION_ID = "0";
	public String questionType = "";
	public ArrayList<String> questionList = new ArrayList<String>();
	public Map<Integer, String> questionMap = new TreeMap<Integer, String>();
	
	public void setFromDB(String questionType) throws Exception {
		if (isSet) 
			return;
		this.questionType = questionType;
		String selectQuery = "SELECT questionID,question FROM pqfQuestions WHERE questionType='"+questionType+"' ORDER BY question ASC;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			questionList = new ArrayList<String>();
			while (SQLResult.next()) {
				questionList.add(SQLResult.getString("questionID"));
				questionList.add(SQLResult.getString("question"));
				questionMap.put(SQLResult.getInt("questionID"), SQLResult.getString("question"));
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
		isSet = true;
	}//setFromDB

	public String getQuestionFromID(String questionType, String qID) throws Exception {
		setFromDB(questionType);
		int i = questionList.indexOf(qID);
		if (-1 == i)
			return "";
		else
			return (String)questionList.get(i+1);
	}//getTradeFromID

	public int getNumQuestionList() {
		return questionList.size()/2;
	}//getNumQuestionList

	public String getQuestion(int i) {
		if (i*2 <= questionList.size())
			return (String)questionList.get((i*2)-1);
		else
			return "";
	}//getQuestion

	public String getQuestionListQIDSelect(String questionType, String name, String classType, String selectedQuestionID, String defaultQuestion) throws Exception {
		setFromDB(questionType);
		return com.picsauditing.PICS.Inputs.inputSelect2First(name, classType, selectedQuestionID,(String[])questionList.toArray(new String[0]),
			DEFAULT_SELECT_QUESTION_ID, defaultQuestion);
	}//getQuestionListQIDSelect

	public String getQuestionListSelect(String questionType, String name, String classType, String selectedQuestion, String defaultQuestion) throws Exception {
		setFromDB(questionType);
		ArrayList<String> tempAL = new ArrayList<String>();
		for (int i=0;i < questionList.size();i+=2)
			tempAL.add((String)questionList.get(i+1));
		return com.picsauditing.PICS.Inputs.inputSelectFirst(name, classType, selectedQuestion,(String[])tempAL.toArray(new String[0]),
			defaultQuestion);
	}
	
	public Map<Integer, String> getQuestionMap(String questionType, String defaultDescription) throws Exception {
		setFromDB(questionType);
		if (defaultDescription == null || defaultDescription.length() == 0) {
			return questionMap;
		}
		Map<Integer, String> list = new TreeMap<Integer, String>();
		list.put(0, defaultDescription);
		list.putAll(questionMap);
		return list;
	}
}