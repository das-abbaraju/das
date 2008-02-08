<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>

<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>

<%	try{
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	String which = request.getParameter("which");
	sBean.orderBy = request.getParameter("orderBy");
	String doubleAuditOK = request.getParameter("doubleAuditOK");
	String msg = "";
	if (null==sBean.orderBy)
		sBean.orderBy = "pqfSubmittedDate";
	if (null==which)
		which = sBean.NEW_AUDITS;
	if ("Send".equals(action)) {
		aBean.setFromDB(actionID);
		cBean.setFromDB(actionID);
		EmailBean.sendAuditEmail(aBean,cBean,false);
		if (!"0".equals(cBean.auditor_id))
			EmailBean.sendAuditEmail(aBean,cBean,true);
		cBean.writeAuditEmailDateToDB(actionID, permissions.getUsername());
	}//if
	if ("Update".equals(action)) {
		cBean.setFromDB(actionID);
		String newAuditDate = request.getParameter("auditDate_"+actionID);
		if (!cBean.auditDate.equals(newAuditDate))
			cBean.lastAuditDate = cBean.auditCompletedDate;
		cBean.auditDate = newAuditDate;
		cBean.auditHour = request.getParameter("auditHour_"+actionID);
		cBean.auditAmPm = request.getParameter("auditAmPm_"+actionID);
		cBean.auditLocation= request.getParameter("auditLocation_"+actionID);
		if (!cBean.auditor_id.equals(request.getParameter("auditor_"+actionID)))
			cBean.assignedDate = DateBean.getTodaysDate();
		cBean.auditor_id = request.getParameter("auditor_"+actionID);
		//check to see if audit doublescheduled bj 4-5-05
		if ("".equals(cBean.checkDoubleAudit(actionID)) || "true".equals(doubleAuditOK)) {
//			if (!"".equals(cBean.auditDate)) {
//				if (cBean.STATUS_SCHEDULING.equals(cBean.status)) {
//					cBean.setStatus(cBean.STATUS_PENDING);
//					cBean.triggerAuditStatusChange(cBean.STATUS_PENDING);
//				}//if
//			}//if
			cBean.writeToDB();
		} else 
			msg = cBean.checkDoubleAudit(actionID);	
	}//if "Update"
	sBean.setWhichScheduleAuditsReport(which); 
 	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);

%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="1" cellpadding="0" cellspacing="0">
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
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
	  </table>
	  <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr> 
          <td>&nbsp;</td>
		  <td colspan="3">
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr> 
                <td height="70" colspan="2" align="center" class="buttons"> 
                  <%@ include file="includes/selectReport.jsp"%>
                  <span class="blueHeader">
<%	if (sBean.RESCHEDULE_AUDITS.equals(which)) 
		out.println("Re -");
%>
				    Schedule Audits Report </span><br>
				    <a href="audit_calendar.jsp?format=popup" target="_blank" class="blueMain">Audit Calendar</a>
                </td>
              </tr>
<%	if (!"".equals(msg)) { %>
			  <tr>
			    <td colspan="2" class="blueMain"><%=msg%><br>Would you still like to schedule <b><%=request.getParameter("con_name")%></b> 
<%		if (!(null==request.getParameter("auditHour_" + actionID)) && !"".equals(request.getParameter("auditHour_" + actionID)))
			out.println(" at <b>" + request.getParameter("auditHour_" + actionID) +" " + request.getParameter("auditAmPm_" + actionID) + "</b>");
%>
				  on this date?
				  <form name="form_override" id="form_override" method="post" action="report_scheduleAudits.jsp">
					<input type="hidden" name="doubleAuditOK" value="true">
					<input type="hidden" name="action" value="Update">
					<input type="hidden" name="actionID" value="<%=actionID%>">
					<input type="hidden" name="auditDate_<%=actionID%>" value="<%=request.getParameter("auditDate_" + actionID)%>">
					<input type="hidden" name="auditAmPm_<%=actionID%>" value="<%=request.getParameter("auditAmPm_" + actionID)%>">
					<input type="hidden" name="auditHour_<%=actionID%>" value="<%=request.getParameter("auditHour_" + actionID)%>">
					<input type="hidden" name="auditor_<%=actionID%>" value="<%=request.getParameter("auditor_" + actionID)%>">
					<input type="submit" value="Schedule Anyway"> 
					<input type="button" value="Cancel" onClick="javascript:window.location.replace('report_scheduleAudits.jsp');">
				  </form>
<%		} //if %>
			     
			    </td>
			  </tr>
			  <tr>
                <td colspan="2" align="center"><%@ include file="includes/reportsSearch.jsp"%></td>
			  </tr>
              <tr> 
                <td height="30" align="left"><%=sBean.getStartsWithLinks()%></td>
                <td align="right"><%=sBean.getLinks()%></td>
              </tr>
            </table>
            <table width="800" border="0" cellpadding="1" cellspacing="1" align="center">
              <tr bgcolor="#003366" class="whiteTitle"> 
                <td align="center">Email</td>
                <td align="center">Sent</td>
                <td width="150"><a href="?orderBy=name&which=<%=which%>" class="whiteTitle">Contractor</a></td>
                <td align="center"><a href="?orderBy=pqfSubmittedDate&which=<%=which%>" class="whiteTitle">PQF</a></td>
                <td align="center"><a href="?orderBy=pqfSubmittedDate&which=<%=which%>" class="whiteTitle">Desktop</a></td>
 			    <td align="center" bgcolor="#336699">Payment</td>
<%	if (sBean.RESCHEDULE_AUDITS.equals(which)) { %>
				<td align="center" bgcolor="#6699CC"><nobr>Last Audit</nobr></td>
<%	} //if %>
				<td width="130" align="center" bgcolor="#6699CC"><a href="?orderBy=auditDate&which=<%=which%>" class="whiteTitle">Audit 
                  Date</a> | Time</td>
                <td  align="center" bgcolor="#6699CC">Location</td>
                <td  align="center" bgcolor="#6699CC">Office Auditor</td>
  			  </tr>
<%	while (sBean.isNextRecord()) {
%>			  <tr  <%=sBean.getBGColor()%> class="blueMain">
		      <form name="form_<%=sBean.aBean.id%>" id="form_<%=sBean.aBean.id%>" method="post" action="report_scheduleAudits.jsp">
				<td align="center">
				  <input name="action" type="submit" class="buttons" value="Send" onClick="return confirm('Are you sure you want to send this email?');"> 
				</td>
				<td align="center"><%=sBean.cBean.lastAuditEmailDate%></td>
				<td><a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=sBean.getTextColor()%>"><%=sBean.aBean.name%></a></td>
				<td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.PQF_TYPE)%></td>
				<td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE)%></td>
                <td class="blueMain"><%=sBean.cBean.lastPayment%></td>
<%		if (sBean.RESCHEDULE_AUDITS.equals(which)) { %>
			    <td align=center><%=sBean.cBean.lastAuditDate%></td>
<%		}//if %>
				<td align="center"><nobr><input name="auditDate_<%=sBean.aBean.id%>" id="auditDate_<%=sBean.aBean.id%>" type="text" class="forms" size="8" onClick="cal1.select(document.form_<%=sBean.aBean.id%>.auditDate_<%=sBean.aBean.id%>,'auditDate_<%=sBean.aBean.id%>','M/d/yy'); return false;" value="<%=sBean.cBean.auditDate%>">
				  &nbsp;|&nbsp;<%=Inputs.getHourSelect("auditHour_"+sBean.aBean.id,"forms",sBean.cBean.auditHour)%>
				    <%=Inputs.getAMPMSelect("auditAmPm_"+sBean.aBean.id,"forms",sBean.cBean.auditAmPm)%></nobr>
				</td>
			    <td align="left"><%=Inputs.getRadioInput("auditLocation_"+sBean.aBean.id,"blueMain",sBean.cBean.auditLocation,cBean.AUDIT_LOCATION_ARRAY)%></td>
			    <td align="center"><nobr>
				  <%=AUDITORS.getAuditorsSelect("auditor_"+sBean.aBean.id,"blueMain",sBean.cBean.auditor_id)%>
				  <input name="action" type="submit" class="buttons" value="Update">
				  <!--onClick="javascript:window.open('checkDoubleAudit.jsp?actionID=<%=sBean.aBean.id%>')">-->	
				</td>
                <input name="actionID" type="hidden" value="<%=sBean.aBean.id%>">
                <input name="con_name" type="hidden" value="<%=sBean.aBean.name%>">
                <input name="which" type="hidden" value="<%=which%>">
	          </form>
		  	  </tr>
<%	}//while %>
		    </table>
		    <br><center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
      <center><%@ include file="utilities/contractor_key.jsp"%><br><br><br></center>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>