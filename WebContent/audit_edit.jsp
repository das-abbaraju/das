<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%//@ page language="java" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="adBean" class="com.picsauditing.PICS.AuditDataBean" scope ="page"/>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%	
try{
	//3/5/05 if audit has not been submitted (questiosn frozen), the audit data is deleted and inserted rather than updated
	// 12/20/04 jj - added timeOutWarning, timeOut javascripts, timedOut hidden form field
	String id = request.getParameter("id");
	String action = request.getParameter("action");
	int numQuestions = 0;
	String errorMsg = "";
	String timedOut = request.getParameter("timedOut");
	boolean frozen = false;
	aBean.setFromDB(id);
	cBean.setFromDB(id);

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
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>

  <script language="JavaScript" src="js/TimeOutWarning.js"></script>
  <script language="JavaScript" src="js/ShowAudit.js"></script>
<%/*  <script language="JavaScript">
  function allYes() {
<%    for (int i=1;i<=81;i++) { %
		document.all.auditAnswer_<%=i%[0].checked = true;
		document.all.auditAnswer_33[2].checked = true;
<%	}//for %
  }//allYes
  </script>
*/%></head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"  onload="return window_onload();">
<%/*		java.util.Enumeration e = request.getParameterNames();
		int numAnswered = 0;
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("auditQuestion_")) {
				String num = temp.substring(14);
				String Tid = request.getParameter("auditId_" + num);
				String answer = request.getParameter("auditAnswer_" + num);
				if (null ==answer) 
					answer = "";
				if (!"".equals(answer)) {
					numAnswered = numAnswered + 1;
					out.println("answered: "+Tid+"<br>");
				}//if
			}//if
		}//while
*/%>
<%//timedOutSave: <%=timedOutSave%>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top" align="center">&nbsp;</td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td colspan="3" align="left">
            <form name="form1" method="post" action="audit_edit.jsp?id=<%=id%>">
              <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="center" class="blueMain">
				  <td>
<%	if (pBean.isAdmin()) {%>
	 			    <%@ include file="utilities/adminContractorNav.jsp"%>
<%	} else if (pBean.isAuditor() ) {%>
				    <%@ include file="utilities/auditorContractorNav.jsp"%>					
<%	}//if %>
                  </td>
				</tr>
                <tr align="center" class="blueMain">
                  <td class="blueHeader">Safety Audit for <%=aBean.name%>
                    <%=cBean.getValidUntilDate(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE)%>
                  </td>
                </tr>
	            <tr align="center">
                  <td class="redmain"><strong><%=errorMsg%></strong></td>
                </tr>
<%/*                <tr align="center" class="blueMain">
                  <td class="redMain">Audit Completed Date:
                    <input name="auditCompletedDate" id="auditCompletedDate" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].auditCompletedDate,'auditCompletedDate','M/d/yy','<%=cBean.auditDate'); return false;" value="<%=cBean.auditDate">
                  </td>
                </tr>
                <tr align="center" class="blueMain">
                  <td class="redMain">
	                <input name="action" type="submit" class="forms" value="Submit"> click to complete this phase of the audit & view the requirements.
                  </td>
                </tr>
*/%>                <tr align="center">
			      <td align="left">
				    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="blueMain"> 
<%//                        <td bgcolor="#003366" onclick="allYes()"><font color="#FFFFFF"><strong>Num</strong></font></td>%>
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
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
<%@ include file="includes/statcounter.jsp" %>
</html>
<%	}finally{
		adBean.closeList();
		aqBean.closeList();
	}//finally
%>