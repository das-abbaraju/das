<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	//3/5/05 if audit has not been submitted (questiosn frozen), the audit data is deleted and inserted rather than updated
	// 12/20/04 jj - added timeOutWarning, timeOut javascripts, timedOut hidden form field
	String id = request.getParameter("id");
	id = "91";
	int numQuestions = 0;
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	
	String orderby = "num";
%>
<html>
<head>
  <title>Audit Field</title>
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
                  <td class="blueHeader">Safety Audit for <%=aBean.name%></td>
                </tr>
	            <tr align="center">
                  <td class="redmain"></td>
                </tr>
                <tr align="center" class="blueMain">
                  <td class="redMain">&nbsp;</td>
                </tr>
                <tr align="center">
			      <td align="left">
				    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="blueMain"> 
                        <td bgcolor="#003366"><font color="#FFFFFF"><strong>Num</strong></font></td>
                        <td bgcolor="#003366"><font color="#FFFFFF"><strong>Question</strong></font></td>
                        <td width="50" bgcolor="#003366"><font color="#FFFFFF"><strong>Answer</strong></font></td>
                      </tr>
<%		aqBean.setList(orderby,"Field");
		numQuestions = 0;
		while (aqBean.isNextRecord()) {
			numQuestions = numQuestions + 1;%>
                      <tr class="blueMain" <%=aqBean.getBGColor()%>> 
                        <td valign="top" width="30"><%=numQuestions%></td>
                        <td valign="top">(<%=aqBean.getCategoryName()%>) <%=aqBean.question%></td>
                        <td valign="top" width="130">
						  <%=com.picsauditing.PICS.Inputs.getYesNoNARadio("auditAnswer_"+numQuestions,"forms","")%>
						  <br><input type="text" name="auditTextAnswer_<%=numQuestions%>" class="forms" size="25" value="">
						</td>
                      </tr>
<%		}//while%>
                    </table>
                    <br><span align="left">
    				<input name="action" type="submit" class="forms" value="Save"> click to save your work. You may still edit this audit later.
	                <p><input name="action" type="submit" class="forms" value="Submit"> click to complete this phase of the audit & view the requirements.
  	                <p><input name="action" type="submit" class="forms" value="Reset"> click to reset status of this audit to "Scheduled." This will delete all audit data.  
                    </span>
                  </td>
                </tr>
              </table>
              <input type="hidden" name="timedOut" value="">
              <input type="hidden" name="action" value="timeoutsave">
			</form>
</body>
</html>
<%	}finally{
		aqBean.closeList();
	}//finally
%>