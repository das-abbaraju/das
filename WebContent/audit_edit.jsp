<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="adBean" class="com.picsauditing.PICS.AuditDataBean" scope ="page"/>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%	
try{
	String id = request.getParameter("id");
	String action = request.getParameter("action");
	int numQuestions = 0;
	String errorMsg = "";
	String timedOut = request.getParameter("timedOut");
	boolean frozen = false;
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	
	if (ContractorBean.AUDIT_STATUS_RQS.equals(cBean.auditStatus) || ContractorBean.AUDIT_STATUS_CLOSED.equals(cBean.auditStatus))
		frozen = true;
					 
	//if submitted via timeout warning, save data
	if ("timeoutsave".equals(action))
	//if ("save".equals(request.getParameter("save")))
		adBean.saveAudit(request, id, frozen);
	if ("Yes".equals(timedOut)) {
		adBean.saveAudit(request, id,frozen);
		response.sendRedirect("logout.jsp");
		return;
	}//if
	if ("Save".equals(action)) {
		String[] noAnswerArray = adBean.saveAudit(request, id,frozen);
		String numQuestionsString = request.getParameter("numQuestions");
		if ((numQuestionsString != null)  && (!numQuestionsString.equals(""))) 
  	    	numQuestions = Integer.parseInt(numQuestionsString);
		if (numQuestions!=adBean.numAnswered || "0".equals(cBean.auditor_id)) {
			errorMsg = "The following questions are still incomplete:<br>  ";
			if ("0".equals(cBean.auditor_id))
				errorMsg += "Choose an auditor, ";
			for (int x = 0; x < noAnswerArray.length; x++)
				errorMsg += noAnswerArray[x] + ", ";
			errorMsg = errorMsg.substring(0,errorMsg.length()-2);
		} else
			errorMsg = "Every question has been answered.";
	}//if
	if ("Submit".equals(action)) {
		String[] noAnswerArray = adBean.saveAudit(request, id,frozen);
		//Make sure all questions are answered before submitting
		String numQuestionsString = request.getParameter("numQuestions");
		if ((numQuestionsString != null)  && (!numQuestionsString.equals("")))
  	    	numQuestions = Integer.parseInt(numQuestionsString);
		if (numQuestions==adBean.numAnswered && !"0".equals(cBean.auditor_id)) {
			cBean.submitAudit(id, pBean.userName);
			response.sendRedirect("audit_editRequirements.jsp?id="+id);
			return;
		} else {
			errorMsg = "Please answer the following questions and select applicable requirements before submitting:<br>  ";
			if ("0".equals(cBean.auditor_id))
				errorMsg += "Choose an auditor, ";
			for (int x = 0; x < noAnswerArray.length; x++) { 
				errorMsg = errorMsg + noAnswerArray[x] + ", ";
			} //for
			errorMsg = errorMsg.substring(0,errorMsg.length()-2);
		}//else
	}//if
	if ("Reset".equals(action)) {
		adBean.deleteAudit(id, aBean.name, "Office", cBean.auditCompletedDate, cBean.auditClosedDate);
		cBean.auditCompletedDate = com.picsauditing.PICS.DateBean.NULL_DATE;
		cBean.auditClosedDate = com.picsauditing.PICS.DateBean.NULL_DATE;
		cBean.writeToDB();
	} //if reset
	adBean.setFromDB(id);
	adBean.setList(id);
%>

<html>
<head>
  <title>Office Audit</title>
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
  <script language="JavaScript" src="js/TimeOutWarning.js"></script>
  <script language="JavaScript" src="js/ShowAudit.js"></script>
</head>
<body onload="return window_onload();">
            <form name="form1" method="post" action="audit_edit.jsp?id=<%=id%>">
              <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="center" class="blueMain">
				  <td><%@ include file="includes/nav/secondNav.jsp"%></td>
				</tr>
                <tr align="center" class="blueMain">
                  <td class="blueHeader">Safety Audit for <%=aBean.name%>
                    <%=cBean.getValidUntilDate(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE)%>
                  </td>
                </tr>
	            <tr align="center">
                  <td class="redmain"><strong><%=errorMsg%></strong></td>
                </tr>
                <tr align="center">
			      <td align="left">
				    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="blueMain"> 
                        <td bgcolor="#003366"><font color="#FFFFFF"><strong>Num</strong></font></td>
                        <td bgcolor="#003366"><font color="#FFFFFF"><strong>Question</strong></font></td>
                        <td width="50" bgcolor="#003366"><font color="#FFFFFF"><strong>Answer</strong></font></td>
                      </tr>
<%	//FROZEN: if audit has been submitted, look at auditData table (adBean) to get questions from date audit was submitted
	if (cBean.isAuditCompleted()) {
		numQuestions = 0;
		while (adBean.isNextRecord()) {
			numQuestions = numQuestions + 1;%>
                      <tr class="blueMain" <%=adBean.getBGColor()%>> 
                        <td valign="top" width="30"><%=adBean.num%></td>
                        <td valign="top">(<%=adBean.category%>) <%=adBean.question%>  <%=adBean.links%></td>
                        <td valign="top" width="130">
						  <%=com.picsauditing.PICS.Inputs.getYesNoNARadio("auditAnswer_"+adBean.num,"forms",adBean.getAnswerFromID(adBean.id))%>
						  <br><input type="text" name="auditTextAnswer_<%=adBean.num%>" class="forms" size="25" value="<%=adBean.getTextAnswerFromID(adBean.id)%>">
<%			//1-17-05 BJ if multiple req, display class/program for this question
			if (!("NA".equals(adBean.whichreq) || "None".equals(adBean.whichreq))) {
%>						  <strong>Requirement:</strong>
						  <%=adBean.getOfficeRequirementsRadio("auditWhichReq_"+adBean.num,"forms",adBean.getRequirementFromID(adBean.id))%>
<%			} else { //multiple requirements not available %>
						  <input type="hidden" name="auditWhichReq_<%=adBean.num%>" value="NA">
<%			}//else %>
						  <input type="hidden" name="auditAnswerOld_<%=adBean.num%>"  value="<%=adBean.getAnswerFromID(adBean.id)%>">
						  <input type="hidden" name="auditTextAnswerOld_<%=adBean.num%>"  value="<%=adBean.getTextAnswerFromID(adBean.id)%>">
						  <input type="hidden" name="auditWhichReqOld_<%=adBean.num%>"  value="<%=adBean.getWhichReqFromID(adBean.id)%>">
						  <input type="hidden" name="auditId_<%=adBean.num%>" value="<%=adBean.id%>">
						  <input type="hidden" name="auditQuestion_<%=adBean.num%>" value="<%=adBean.question%>">
						  <input type="hidden" name="auditLinks_<%=adBean.num%>" value="<%=adBean.links%>">
						  <input type="hidden" name="auditCategory_<%=adBean.num%>" value="<%=adBean.category%>">
						</td>
                      </tr>
<%		}//while
		adBean.closeList();					  
	} else { //audit not yet submitted; get questions from auditQuestions table (aqBean)
		aqBean.setList("num","Office");
		numQuestions = 0;
		while (aqBean.isNextRecord()) {
			numQuestions = numQuestions + 1;
%>
                      <tr class="blueMain" <%=aqBean.getBGColor()%>> 
                        <td width="30" valign="top"><%=aqBean.num%></td>
                        <td  valign="top">(<%=aqBean.getCategoryName()%>) <%=aqBean.question%> <%=aqBean.getLinksShow()%></td>
                        <td valign="top" width="130">
						  <%=com.picsauditing.PICS.Inputs.getYesNoNARadio("auditAnswer_"+aqBean.num,"forms",adBean.getAnswerFromID(aqBean.questionID))%>
						  <br><input type="text" name="auditTextAnswer_<%=aqBean.num%>" class="forms" size="25" value="<%=adBean.getTextAnswerFromID(aqBean.questionID)%>">
<%	//1-17-05 BJ if multiple req, display class/program for this question
			if ("Yes".equals(aqBean.multireq)) { %>
						  <strong>Requirement:</strong>
<%				if (!"".equals(aqBean.reqclass)) { %>
						  <input name="auditWhichReq_<%=aqBean.num%>" type="radio" value="Class" <%=adBean.checkedWhichreq(aqBean.questionID,"Class")%>>Class 
<%				}//if
				if (!"".equals(aqBean.reqprogram)) { %>
						  <input name="auditWhichReq_<%=aqBean.num%>" type="radio" value="Program" <%=adBean.checkedWhichreq(aqBean.questionID,"Program")%>>Program
<%				}//if
				if (!"".equals(aqBean.reqprogram) && (!"".equals(aqBean.reqclass))) { %>
						  <input name="auditWhichReq_<%=aqBean.num%>" type="radio" value="Both" <%=adBean.checkedWhichreq(aqBean.questionID,"Both")%>>Both
						  <br>
<%				}//if
			} else { // multiple requirements not available %>
						  <input type="hidden" name="auditWhichReq_<%=aqBean.num%>" value="NA">
<%			}//else %>
						  <input type="hidden" name="auditAnswerOld_<%=aqBean.num%>"  value="<%=adBean.getAnswerFromID(aqBean.questionID)%>"> 
						  <input type="hidden" name="auditTextAnswerOld_<%=aqBean.num%>"  value="<%=adBean.getTextAnswerFromID(aqBean.questionID)%>"> 
						  <input type="hidden" name="auditWhichReqOld_<%=aqBean.num%>"  value="<%=adBean.getWhichReqFromID(aqBean.questionID)%>">
						  <input type="hidden" name="auditId_<%=aqBean.num%>" value="<%=aqBean.questionID%>">
						  <input type="hidden" name="auditQuestion_<%=aqBean.num%>" value="<%=aqBean.question%>">
						  <input type="hidden" name="auditLinks_<%=aqBean.num%>" value="<%=aqBean.getLinksShow()%>">
						  <input type="hidden" name="auditCategory_<%=aqBean.num%>" value="<%=aqBean.getCategoryName()%>">
						</td>
                      </tr>
<%		}//while
		aqBean.closeList();
	}//else
%>
                    </table>
                    <br><span align="left">
					<input type="hidden" name="numQuestions" value=<%=numQuestions%>>
    				<input name="action" type="submit" class="forms" value="Save"> click to save your work. You may still edit this audit later.
	                <p><input name="action" type="submit" class="forms" value="Submit"> click to complete this phase of the audit & view the requirements.
<%	if (pBean.isAdmin()) { %>
  	                <p><input name="action" type="submit" class="forms" value="Reset"> click to reset status of this audit to "Scheduled." This will delete all audit data.  
<%	}//if %>
                    </span>
                  </td>
                </tr>
              </table>
              <input type="hidden" name="timedOut" value="">
              <input type="hidden" name="action" value="timeoutsave">
			</form>
<%	}finally{
		adBean.closeList();
		aqBean.closeList();
	}//finally
%>
</body>
</html>