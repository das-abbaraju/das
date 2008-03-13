<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.mail.EmailSender"%>
<%
permissions.tryPermission(OpPerms.AssignAudits);
boolean canEdit = permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit);

pageBean.setTitle("Schedule Office Audits");
pageBean.includeScriptaculous(true);

String action = request.getParameter("action");
if (action != null) {
	if (action.equals("saveAudit")) {
		if (!canEdit) {
			%>no permission<%
			return;
		}
		String newAuditDate = request.getParameter("auditDate");
		String conID = request.getParameter("conID");
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		if (!cBean.auditDate.equals(newAuditDate))
			cBean.lastAuditDate = cBean.auditCompletedDate;
		cBean.auditDate = newAuditDate;
		cBean.auditHour = request.getParameter("auditHour");
		cBean.auditAmPm = request.getParameter("auditAmPm");
		cBean.auditLocation= request.getParameter("auditLocation");
		if (!cBean.auditor_id.equals(request.getParameter("auditor")))
			cBean.assignedDate = DateBean.getTodaysDate();
		cBean.auditor_id = request.getParameter("auditor");
		
		//check to see if audit doublescheduled bj 4-5-05
		String check = cBean.checkDoubleAudit(conID);
		if (check.length() > 0) {
			%><%=check%><%
			return;
		}
		cBean.writeToDB();
		%><%=cBean.assignedDate%><%
		return;
	}

	if (action.equals("sendEmail")) {
		EmailSender mailer = new EmailSender();
		
		EmailBean.sendAuditEmail(aBean,cBean,false);
		if (!"0".equals(cBean.auditor_id))
			EmailBean.sendAuditEmail(aBean,cBean,true);
		cBean.writeAuditEmailDateToDB(actionID, permissions.getUsername());
		%>Sent<%
		return;
	}
}
SelectAccount sql = new SelectAccount();
sql.setType(SelectAccount.Type.Contractor);

sql.addField("c.lastPayment");

sql.addWhere("active='Y'");
sql.addWhere("c.desktopSubmittedDate='0000-00-00' OR c.desktopSubmittedDate < DATE_ADD(CURDATE(),INTERVAL -34 MONTH)");
sql.addWhere("!(auditCompletedDate<>'0000-00-00' AND auditCompletedDate<'"+DateBean.OLD_OFFICE_CUTOFF+"' AND auditCompletedDate>DATE_ADD(CURDATE(),INTERVAL -3 YEAR))");

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "pqfSubmittedDate");

report.setPageByResult(request);
report.setLimit(50);

List<BasicDynaBean> searchData = report.getPage();

%>
<%@ include file="includes/header.jsp" %>
<script type="text/javascript">
function selectAuditor(conID) {
	var form = $('auditor_form'+conID);
	var auditor = form['auditorID'];
	pars = 'action=saveAuditor&conID='+conID+'&auditorID='+$F(auditor);
	
	var divName = 'auditor_td'+conID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'report_desktop.jsp', {method: 'post', parameters: pars});
	new Effect.Highlight($('auditor_tr'+conID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}
</script>
<style>
.auditselect {
	margin: 0px;
	padding: 0px;
}
</style>
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>

<%
try {
%>
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
				<td align="center"><nobr><input name="auditDate" id="auditDate" type="text" class="forms" size="8" onClick="cal1.select(document.form_<%=sBean.aBean.id%>.auditDate,'auditDate','M/d/yy'); return false;" value="<%=sBean.cBean.auditDate%>">
				  &nbsp;|&nbsp;<%=Inputs.getHourSelect("auditHour","forms",sBean.cBean.auditHour)%>
				    <%=Inputs.getAMPMSelect("auditAmPm","forms",sBean.cBean.auditAmPm)%></nobr>
				</td>
			    <td align="left"><%=Inputs.getRadioInput("auditLocation","blueMain",sBean.cBean.auditLocation,cBean.AUDIT_LOCATION_ARRAY)%></td>
			    <td><%=AUDITORS.getAuditorsSelect("auditor","blueMain",sBean.cBean.auditor_id)%></td>
				<td><input name="action" type="submit" class="buttons" value="Schedule"></td>
		  	  </tr>
	<% } %>
</table>
<center><%=sBean.getLinks()%></center>
<center><%@ include file="utilities/contractor_key.jsp"%></center>

<%@ include file="includes/footer.jsp"%>
