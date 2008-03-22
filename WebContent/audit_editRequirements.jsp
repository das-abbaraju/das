<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="adBean" class="com.picsauditing.PICS.AuditDataBean" scope ="page"/>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%
try{
	String id = request.getParameter("id");
	String action = request.getParameter("action");
	String reqStyleClass = "blueMain";
	int numReq = 0;
	int displayNumReq = 0;
	String errorMsg = "";
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	
	if ("Save".equals(action))
		adBean.updateRequirements(request, id);
	if ("Submit".equals(action)) {
		adBean.updateRequirements(request, id);
		//Make sure all requirements are complete before submitting
		String numReqString = request.getParameter("numReq");
		if (numReqString != null)
  	    	numReq = Integer.parseInt(numReqString);
		if (numReq==adBean.numReqComplete) {
			cBean.closeAudit(id, pBean.userName);
			response.sendRedirect("audit_view.jsp?id=" + id +"&msg=All the requirements on this audit have been closed.");
		} else {
//			errorMsg = numReq + "==" + adBean.numReqComplete + "Please complete all requirements before submitting";
			errorMsg = "Please complete all requirements before submitting";
		} //else
	}//if
	adBean.setFromDB(id);
	adBean.setList(id);
%>
<html>
<head>
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>

  <title>CHANGEME</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr> 
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <table border="0" cellspacing="0" cellpadding="1" class="blueMain" align="center">
        <tr align="center" class="blueMain">
          <td><%@ include file="includes/nav/secondNav.jsp"%></td>
        </tr>
        <tr> 
          <td align="center" class="blueHeader">Office Audit for <%=aBean.name%></td>
        </tr>
        <tr align="center"> 
          <td class="redmain"><%=errorMsg%></td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
        </tr>
        <tr align="center">
          <td>
		    <form name="form1" method="post" action="audit_editRequirements.jsp?id=<%=id%>">
<%/*              <table>
                <tr> 
                  <td>
                    <input name="auditClosedDate" id="auditClosedDate" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].auditClosedDate,'auditClosedDate','M/d/yy','<%=cBean.auditClosedDate%'); return false;" value="<%=cBean.auditClosedDate%">
              <br><input name="action" type="submit" class="forms" value="Submit">
              click to close this audit
				  </td>
                </tr>
              </table>
*/%>
              <table width="657" border="0" cellpadding="1" cellspacing="1">
                <tr class="whiteTitle"> 
                  <td width="30" bgcolor="#003366">#</td>
                  <td bgcolor="#003366">Question</td>
                  <td bgcolor="#003366">Answer</td>
                </tr>
<%	aqBean.setOKMapFromDB();
	aqBean.setList("num","Office");
	numReq = 0;
	while (adBean.isNextRecord()) {
		if (adBean.ok.equals("No")) {
			displayNumReq += 1;
			if (!"".equals(adBean.checkedReqComplete(adBean.id,"Yes"))) 
				reqStyleClass = "blueMain";
			else
				reqStyleClass = "redMain";
%>
                <tr class="blueMain" <%=com.picsauditing.PICS.Utilities.getBGColor(numReq)%>> 
                  <td><%=adBean.num%></td>
                  <td>(<%=adBean.category%>) <%=adBean.question%> <%=adBean.links%><br>
                    <strong> <%=adBean.getTextAnswerFromID(adBean.id)%></strong></td>
                  <td><%=adBean.getAnswerFromID(adBean.id)%></td>
                </tr>
                <tr class="blueMain" <%=com.picsauditing.PICS.Utilities.getBGColor(numReq)%>> 
                  <td valign="top"><nobr><em>Req <%=displayNumReq%>:</em></nobr></td>
                  <td colspan="2">
<%			if ("NA".equals(adBean.whichreq) || "None".equals(adBean.whichreq) || "".equals(adBean.whichreq)) {
				if (!"".equals(adBean.checkedReqComplete(adBean.id,"Yes"))) 
					reqStyleClass = "blueMain";
				else
					reqStyleClass = "redMain";
%>
				    <span class="<%=reqStyleClass%>">
				    <textarea name="requirement_<%=adBean.id%>" cols="110" rows="3" class="forms" id="requirement_<%=adBean.id%>"><%=adBean.requirement%></textarea>
		      	    <strong>Requirement Closed? </strong>
                    <input name="reqComplete_<%=adBean.id%>" type="radio" value="Yes" <%=adBean.checkedReqComplete(adBean.id,"Yes")%> onClick="document.all.dateReqComplete_<%=adBean.id%>.value = '<%=com.picsauditing.PICS.DateBean.getTodaysDate()%>';">Yes 
                    <input name="reqComplete_<%=adBean.id%>" type="radio" value="No" <%=adBean.checkedReqComplete(adBean.id,"No")%>>No &nbsp;&nbsp; &nbsp;&nbsp; 
				      Date Requirement Closed 
                    <input type="text" size="8" name="dateReqComplete_<%=adBean.id%>" value="<%=adBean.getDateReqCompleteFromID(adBean.id)%>"></span>
<%				numReq += 1;
			} else { %>
				    <strong>
                    <input type="hidden" size="115" name="requirement_<%=adBean.id%>" value="<%=adBean.requirement%>">
<%				if ("Class".equals(adBean.whichreq) || "Both".equals(adBean.whichreq)) { 
					if (!"".equals(adBean.checkedClassComplete(adBean.id,"Yes"))) 
						reqStyleClass = "blueMain";
					else
						reqStyleClass = "redMain";
%>
					<span class="<%=reqStyleClass%>">
					  Class: <textarea name="reqclass_<%=adBean.id%>" cols="110" rows="3" class="forms" id="reqclass_<%=adBean.id%>"><%=adBean.reqclass%></textarea>
					<strong> Class Requirement Closed? </strong>
                    <input name="classComplete_<%=adBean.id%>" type="radio" value="Yes" <%=adBean.checkedClassComplete(adBean.id,"Yes")%> onClick="document.all.dateClassComplete_<%=adBean.id%>.value = '<%=com.picsauditing.PICS.DateBean.getTodaysDate()%>';">Yes 
                    <input name="classComplete_<%=adBean.id%>" type="radio" value="No" <%=adBean.checkedClassComplete(adBean.id,"No")%>>
                      No &nbsp;&nbsp; &nbsp;&nbsp; Date Class Requirement Closed 
                    <input type="text" size="8" name="dateClassComplete_<%=adBean.id%>" value="<%=adBean.getDateClassCompleteFromID(adBean.id)%>"></span><br>
<% 					numReq += 1;
				}//if class
				if ("Program".equals(adBean.whichreq) || "Both".equals(adBean.whichreq)) {
					if (!"".equals(adBean.checkedProgramComplete(adBean.id,"Yes"))) 
						reqStyleClass = "blueMain";
					else
						reqStyleClass = "redMain";
%>
					<span class="<%=reqStyleClass%>">
                      Program: <textarea name="reqprogram_<%=adBean.id%>" cols="110" rows="3" class="forms" id="reqprogram_<%=adBean.id%>"><%=adBean.reqprogram%></textarea>
                    <strong> Program Requirement Closed? 
                    <input name="programComplete_<%=adBean.id%>" type="radio" value="Yes" <%=adBean.checkedProgramComplete(adBean.id,"Yes")%> onClick="document.all.dateProgramComplete_<%=adBean.id%>.value = '<%=com.picsauditing.PICS.DateBean.getTodaysDate()%>';">Yes 
                    <input name="programComplete_<%=adBean.id%>" type="radio" value="No" <%=adBean.checkedProgramComplete(adBean.id,"No")%>>
                    No &nbsp;&nbsp; &nbsp;&nbsp; Date Program Requirement Closed 
 	                <input type="text" size="8" name="dateProgramComplete_<%=adBean.id%>" value="<%=adBean.getDateProgramCompleteFromID(adBean.id)%>"><br>
<%					numReq += 1;
				} //if program
			}//else - if single req
%>
                    </strong></span>
				  </td>
                </tr>
<%		}//if (adBean.ok.equals("No")) 			
	}//while
	aqBean.closeList();
%>
              </table>
              <br>
              <span align="left"> 
              <input type="hidden" name="numReq" value=<%=numReq%>>
              <input name="action" type="submit" class="forms" value="Save">
              click to save these requirements and edit later 
              <input name="action" type="submit" class="forms" value="Submit">
              click to close this audit </span> 
            </form>
            <br> <br> <br> </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br> <br>
	</td>
  </tr>
  <tr> 
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
<%	}finally{
		aqBean.closeList();
	}//finally
%>