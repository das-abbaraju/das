package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

//1-17-05 BJ  Added multiple requirements, class/program
public class AuditDataBean extends DataBean {
//conID, qID, num, Question, Category, Answer, OK, textAnswer/Comment
	
	public String conID = "";
	public String num = "";
	public String id = "";
	public String question = "";
	public String links = "";
	public String category = "";
	public String answer = "";  // SET 'Yes','No','NA'
	public String ok = "";  // ENUM 'Yes','No'
	public String textAnswer = "";
	public String requirement = "";
	public String whichreq = "";
	public String reqclass = "";
	public String reqprogram = "";
	public String reqComplete = "";
	public String dateReqComplete = "";
	public String classComplete = "";
	public String dateClassComplete = "";
	public String programComplete = "";
	public String dateProgramComplete = "";
	public int numAnswered =  0;
	public int numReqComplete = 0;
	
	public TreeMap<String,String> questionAnswerMap = null;
	public TreeMap<String,String> questionTextAnswerMap = null;
	public TreeMap<String,String> requirementMap = null;
	public TreeMap<String,String> whichreqMap = null;
	public TreeMap<String,String> reqclassMap = null;
	public TreeMap<String,String> reqprogramMap = null;
	public TreeMap<String,String> reqCompleteMap = null;
	public TreeMap<String,String> dateReqCompleteMap = null;
	public TreeMap<String,String> classCompleteMap = null;
	public TreeMap<String,String> dateClassCompleteMap = null;
	public TreeMap<String,String> programCompleteMap = null;
	public TreeMap<String,String> dateProgramCompleteMap = null;
//	TreeMap questionOKMap = null;
	public TreeMap<String,String> reqOkMap = null;

	public static String[] OFFICE_REQS_ARRAY = {"Class","Program","Both"};
	
	ResultSet listRS = null;
	int numResults = 0;
	int count = 0;

	public void setFromDB() throws Exception {;}//setFromDB
	public void setFromDB(String cID) throws Exception {
		if ((null == cID) || ("".equals(cID)))
			throw new Exception("Can't set audit from DB because conID is not set");
		questionAnswerMap = new TreeMap<String,String>();
		questionTextAnswerMap = new TreeMap<String,String>();
		requirementMap = new TreeMap<String,String>();
		whichreqMap = new TreeMap<String,String>();
		reqclassMap = new TreeMap<String,String>();
		reqprogramMap = new TreeMap<String,String>();
		reqCompleteMap = new TreeMap<String,String>();
		dateReqCompleteMap = new TreeMap<String,String>();
		classCompleteMap = new TreeMap<String,String>();
		dateClassCompleteMap = new TreeMap<String,String>();
		programCompleteMap = new TreeMap<String,String>();
		dateProgramCompleteMap = new TreeMap<String,String>();
		reqOkMap = new TreeMap<String,String>();
		String selectQuery = "SELECT * FROM auditData WHERE con_id='"+cID+"' ORDER BY num;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()){
				setFromResultSet(SQLResult);
				questionAnswerMap.put(id,answer);
				questionTextAnswerMap.put(id,textAnswer);
				requirementMap.put(id,requirement);
				whichreqMap.put(id,whichreq);
				reqclassMap.put(id,reqclass);
				reqprogramMap.put(id,reqprogram);
				reqCompleteMap.put(id,reqComplete);
				dateReqCompleteMap.put(id,dateReqComplete);
				classCompleteMap.put(id,classComplete);
				dateClassCompleteMap.put(id,dateClassComplete);
				programCompleteMap.put(id,programComplete);
				dateProgramCompleteMap.put(id,dateProgramComplete);
				reqOkMap.put(id,ok);
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setFromDB

	public void setList(String cID) throws Exception {
		if ((null == cID) || ("".equals(cID)))
			throw new Exception("Can't set audit from DB because con_id is not set");
		String selectQuery = "SELECT * FROM auditData WHERE con_id='"+cID+"' ORDER BY num;";
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

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		conID = SQLResult.getString("con_id");
		num = SQLResult.getString("num");
		id = SQLResult.getString("id");
		category = SQLResult.getString("category");
		question = SQLResult.getString("question");
		links = SQLResult.getString("links");
		answer = SQLResult.getString("answer");
		ok = SQLResult.getString("ok");
		textAnswer = SQLResult.getString("textAnswer");
		requirement = SQLResult.getString("requirement");
		whichreq = SQLResult.getString("whichreq");
		reqclass = SQLResult.getString("reqclass");
		reqprogram = SQLResult.getString("reqprogram");
		reqComplete = SQLResult.getString("reqComplete");
		dateReqComplete = DateBean.toShowFormat(SQLResult.getString("dateReqComplete"));
		classComplete = SQLResult.getString("classComplete");
		dateClassComplete = DateBean.toShowFormat(SQLResult.getString("dateClassComplete"));
		programComplete = SQLResult.getString("programComplete");
		dateProgramComplete = DateBean.toShowFormat(SQLResult.getString("dateProgramComplete"));	
	}//setFromResultSet

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
		if (null != listRS){
			listRS.close();
			listRS = null;
		}//if
		DBClose();
	}//closeList

	public String getBGColor(){
		if ((count % 2) == 1)	return " bgcolor=FFFFFF";
		else	return "";
	}//getBGColor

	public String checkedAnswer(String id, String answer) throws Exception {
		if (questionAnswerMap == null)
			throw new Exception("questionAnswerMap is null");
		if (questionAnswerMap.containsKey(id)){
			String temp = (String)questionAnswerMap.get(id);
			if (temp.equals(answer))
				return "checked";
		}//if
		return "";
	}//checkedAnswer

	public String checkedWhichreq(String id, String whichreq) throws Exception {
		if (whichreqMap == null)
			throw new Exception("whichreqMap is null");
		if (whichreqMap.containsKey(id)){
			String temp = (String)whichreqMap.get(id);
			if (temp.equals(whichreq))
				return "checked";
		}//if
		return "";
	}//checkedWhichreq

	public String checkedReqComplete(String num, String answer) throws Exception {
		if (reqCompleteMap == null)
			throw new Exception("reqCompleteMap is null");
		if (reqCompleteMap.containsKey(num)){
			String temp = (String)reqCompleteMap.get(num);
			if (temp.equals(answer))
				return "checked";
		}//if
		return "";
	}//checkedReqComplete

	public String checkedClassComplete(String num, String answer) throws Exception {
		if (classCompleteMap == null)
			throw new Exception("classCompleteMap is null");
		if (classCompleteMap.containsKey(num)){
			String temp = (String)classCompleteMap.get(num);
			if (temp.equals(answer))
				return "checked";
		}//if
		return "";
	}//checkedClassComplete
	
		public String checkedProgramComplete(String num, String answer) throws Exception {
		if (programCompleteMap.containsKey(num)){
			String temp = (String)programCompleteMap.get(num);
			if (temp.equals(answer))
				return "checked";
		}//if
		return "";
	}//checkedProgramComplete

	public String getAnswerFromID(String id){
		if (questionAnswerMap.containsKey(id))
			return (String)questionAnswerMap.get(id);
		return "";
	}//getAnswerFromID

	public String getRequirementFromID(String id){
		if (whichreqMap.containsKey(id))
			return (String)whichreqMap.get(id);
		return "";
	}//getRequirementFromID

	public String getTextAnswerFromID(String id){
		if (questionTextAnswerMap.containsKey(id))
			return (String)questionTextAnswerMap.get(id);
		return "";
	}//getTextAnswerFromID

	public boolean questionAnsweredFromID(String id){
	if (questionAnswerMap.containsKey(id) && !questionAnswerMap.get(id).equals("") && !questionAnswerMap.get(id).equals(null))
		return true;	
	else 
		return false;
	}//questionAnsweredFromID
	
	public String getReqClassFromID(String id){
		if (reqclassMap.containsKey(id))
			return (String)reqclassMap.get(id);
		return "";
	}//getReqClassFromID
	
	public String getReqProgramFromID(String id){
		if (reqprogramMap.containsKey(id))
			return (String)reqprogramMap.get(id);
		return "";
	}//getReqProgramFromID


	public String getWhichReqFromID(String id){
		if (whichreqMap.containsKey(id))
			return (String)whichreqMap.get(id);
		return "";
	}//getWhichReqFromID

	public String getReqCompleteFromID(String id){
		if (reqCompleteMap.containsKey(id))
			return (String)reqCompleteMap.get(id);
		return "";
	}//getReqCompleteFromID

	public String getDateReqCompleteFromID(String id){
		if (dateReqCompleteMap.containsKey(id))
			return (String)dateReqCompleteMap.get(id);
		return "";
	}//getDateReqCompleteFromID

	public String getClassCompleteFromID(String id){
		if (classCompleteMap.containsKey(id))
			return (String)classCompleteMap.get(id);
		return "";
	}//getClassCompleteFromID

	public String getDateClassCompleteFromID(String id){
		if (dateClassCompleteMap.containsKey(id))
			return (String)dateClassCompleteMap.get(id);
		return "";
	}//getDateClassCompleteFromID

	public String getProgramCompleteFromID(String id){
		if (programCompleteMap.containsKey(id))
			return (String)programCompleteMap.get(id);
		return "";
	}//getProgramCompleteFromID

	public String getDateProgramCompleteFromID(String id){
		if (dateProgramCompleteMap.containsKey(id))
			return (String)dateProgramCompleteMap.get(id);
		return "";
	}//getDateProgramCompleteFromID

	public String[] saveAudit(javax.servlet.http.HttpServletRequest request, String conID, boolean frozen) throws Exception {
		ArrayList<String> noAnswer = new ArrayList<String>();
		Enumeration e = request.getParameterNames();
		AuditQuestionBean aqBean = new AuditQuestionBean();
		aqBean.setOKMapFromDB();
		try{
			DBReady();
			if (!frozen){
				String deleteQuery = "DELETE FROM auditData WHERE con_id="+conID+";";
				SQLStatement.executeUpdate(deleteQuery);
			}//if
			String insertQuery = "INSERT INTO auditData (con_id,num,id,category,question,links,answer,OK,textAnswer,requirement,whichreq,reqclass,reqprogram) VALUES ";
			String updateQuery = "";
			boolean toInsert = false;
			while (e.hasMoreElements()){
				String temp = (String)e.nextElement();
				if (temp.startsWith("auditQuestion_")){	
					String num = temp.substring(14);
					String id = request.getParameter("auditId_" + num);
					String question = request.getParameter("auditQuestion_" + num);
					String links = request.getParameter("auditLinks_" + num);
					String category = request.getParameter("auditCategory_" + num);
					String answer = request.getParameter("auditAnswer_" + num);
					String answerOld =  request.getParameter("auditAnswerOld_" + num);
					String ok = "";
					String textAnswer = request.getParameter("auditTextAnswer_" + num);
					String textAnswerOld = request.getParameter("auditTextAnswerOld_" + num);
					String requirement = "";
					String whichreq  =  request.getParameter("auditWhichReq_" + num);
					String whichreqOld  =  request.getParameter("auditWhichReqOld_" + num);
					String reqclass =  "";
					String reqprogram =  "";
	//				ResultSet tempRS = SQLStatement.executeQuery(Query);
	//				String tempOKAnswer = tempRS.getStirng
					if (null==answerOld)
						answerOld = "";		
					if (null ==answer) 
						answer = "";
					if (null==textAnswerOld)
						answerOld = "";		
					if (null ==textAnswer) 
						answer = "";
					if (null ==whichreqOld) 
						whichreqOld = "";
					if (null ==whichreq) 
						whichreq = "None";
					//  if answer is blank, ignore question
					if (!"".equals(answer)){
						numAnswered = numAnswered + 1;
						if (aqBean.isAnswerOK(id, answer)){
							ok = "Yes";
							if (!whichreq.equals("NA"))
								whichreq = "None";
						} else {
							ok = "No";
							aqBean.setFromDB(id);
							//see if multiple or single class, and which selected BJ 1-17-05
							if ("NA".equals(whichreq))
								requirement = aqBean.requirement;
							else {
								if ("Class".equals(whichreq) || "Both".equals(whichreq))
									reqclass = aqBean.reqclass;
								if ("Program".equals(whichreq) || "Both".equals(whichreq)) 
									reqprogram = aqBean.reqprogram;
								//if multireq and none selected, add to no answer list bj 3-14-05
								if (!("Class".equals(whichreq) || "Program".equals(whichreq) || "Both".equals(whichreq))){
									noAnswer.add(num);
									numAnswered = numAnswered - 1;
								} //if none selected
							}//else - multireq							
						}//else - isAnswerOK	
						//if no old answer, or if not frozen, insert question into auditData
						if (!frozen || ("".equals(answerOld))){
							toInsert = true;
							insertQuery+="('"+conID+"','"+num+"','"+id+"','"+eqDB(category)+
								"','"+eqDB(question)+"','"+eqDB(links)+"','"+eqDB(answer)+
								"','"+ok+"','"+eqDB(textAnswer)+"','"+ eqDB(requirement)+
								"','"+eqDB(whichreq)+"','"+eqDB(reqclass)+"','"+eqDB(reqprogram)+"'),";
					//else if answer changed or if whichreq changed, update row
						} else if ((!answer.equals(answerOld)) || (!textAnswer.equals(textAnswerOld)) || (!whichreq.equals(whichreqOld))){
							updateQuery = "UPDATE auditData SET answer='"+eqDB(answer)+"',textAnswer='"+eqDB(textAnswer)+"',ok='"+ok+
								"', requirement='"+eqDB(requirement)+"',whichreq='"+whichreq+"',reqprogram='"+eqDB(reqprogram)+"',reqclass='"+eqDB(reqclass)+
								"' WHERE con_id="+conID+" AND id="+id+";";
							SQLStatement.executeUpdate(updateQuery);
						} //if
					} else
						noAnswer.add(num);
				}//if (temp.startsWith("auditQuestion_"
			}//while
			insertQuery = insertQuery.substring(0,insertQuery.length()-1);
			insertQuery +=";";
			if (toInsert)
				SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally		
		Collections.sort(noAnswer);
		return (String[])noAnswer.toArray(new String[0]);
	}//saveAudit

	public String updateRequirements(javax.servlet.http.HttpServletRequest request, String conID) throws Exception {
		String updateQuery = "";
		Enumeration e = request.getParameterNames();
		try{
			DBReady();
			while (e.hasMoreElements()){
				String temp = (String)e.nextElement();
				if (temp.startsWith("requirement_")){
					String num = temp.substring(12);
					//String num = temp;
					String requirement = request.getParameter("requirement_" + num);
					String reqclass = request.getParameter("reqclass_" + num);
					String reqprogram = request.getParameter("reqprogram_" + num);
					String reqComplete = request.getParameter("reqComplete_" + num);
					String dateReqComplete = request.getParameter("dateReqComplete_" + num);
					String classComplete = request.getParameter("classComplete_" + num);
					String dateClassComplete = request.getParameter("dateClassComplete_" + num);
					String programComplete = request.getParameter("programComplete_" + num);
					String dateProgramComplete = request.getParameter("dateProgramComplete_" + num);
					updateQuery = "UPDATE auditData "+
							"SET requirement='"+eqDB(requirement)+"',reqclass='"+eqDB(reqclass)+"',reqprogram='"+eqDB(reqprogram)+"',reqComplete='"+reqComplete+"',dateReqComplete='"+DateBean.toDBFormat(dateReqComplete)+"',"+
							"classComplete='"+classComplete+"',dateClassComplete='"+DateBean.toDBFormat(dateClassComplete)+"',programComplete='"+programComplete+"',dateProgramComplete='"+DateBean.toDBFormat(dateProgramComplete)+"'"+
							" WHERE con_id='"+conID+"' AND id='"+num+"';" ;
					SQLStatement.executeUpdate(updateQuery);
					if ("Yes".equals(reqComplete) ||"Yes".equals(classComplete))				 
						numReqComplete++;
					if ("Yes".equals(programComplete))
						numReqComplete++;
				}//if
			}//while
		}finally{
			DBClose();
		}//finally		
		return updateQuery;
	}//updateRequirements

/*	public void clearTreeMaps(){
//		if (!questionAnswerMap.isEmpty()) 
	//		questionAnswerMap.clear();
		//questionTextAnswerMap.clear();
		//requirementMap.clear();
		//reqCompleteMap.clear();
		//dateReqCompleteMap.clear();
	}//clearTreeMaps
*/
	public void deleteAudit(String deleteID, String delName, String auditType, String compDate, String closedDate) throws Exception {
		String auditID = "0";
		String insertQuery = "INSERT INTO archivedAudits (auditType,conID,auditCompletedDate,auditClosedDate,conName) "+
			"VALUES ('"+auditType+"','"+deleteID+"','"+DateBean.toDBFormat(compDate)+"','"+DateBean.toDBFormat(closedDate)+
			"','"+eqDB(delName)+"');";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ResultSet SQLResult = SQLStatement.getGeneratedKeys();
			if (SQLResult.next())
				auditID = SQLResult.getString("GENERATED_KEY");
			else {
				SQLResult.close();
				DBClose();
				throw new Exception("No auditID returned after inserting new archived audit");
			}//else
			SQLResult.close();
	
			setList(deleteID);
			insertQuery = "INSERT INTO archivedAuditData (auditID,con_id,num,id,category,question,answer,OK,textAnswer,"+
				"requirement,whichreq,reqclass,reqprogram,reqComplete,dateReqComplete,classComplete,dateClassComplete,"+
				"programComplete,dateProgramComplete) VALUES ";
			boolean doInsert = false;
			while (isNextRecord()){
				doInsert = true;
				insertQuery+="("+auditID+","+conID+","+num+","+id+",'"+eqDB(category)+"','"+eqDB(question)+"','"+
						eqDB(answer)+"','"+ok+"','"+eqDB(textAnswer)+"','"+ eqDB(requirement)+"','"+eqDB(whichreq)+"','"+
						eqDB(reqclass)+"','"+eqDB(reqprogram)+"','"+eqDB(reqComplete)+"','"+DateBean.toDBFormat(dateReqComplete)+
						"','"+eqDB(classComplete)+"','"+DateBean.toDBFormat(dateClassComplete)+"','"+eqDB(programComplete)+
						"','"+DateBean.toDBFormat(dateProgramComplete)+"'),";
			}//while
			insertQuery = insertQuery.substring(0,insertQuery.length()-1);
			insertQuery +=";";
			if (doInsert)
				SQLStatement.executeUpdate(insertQuery);
	
			String deleteQuery = "DELETE FROM auditData WHERE con_id="+deleteID+";";
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally		
	}//deleteAudit

	public String getOfficeRequirementsRadio(String name, String classType, String selected){
		return Inputs.getRadioInput(name, classType, selected, OFFICE_REQS_ARRAY);
	}//getOfficeRequirementsRadio

	public String eqDB(String temp){
		return Utilities.escapeQuotes(temp);
	}//eqDB
}//AuditDataBean