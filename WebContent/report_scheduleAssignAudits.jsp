<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>	
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<%
permissions.tryPermission(OpPerms.AssignAudits);
boolean canEdit = permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit);

if (request.getParameter("action") != null) {
	out.print("**");
	return;
}
/* TODO update for multiaudit
String action = request.getParameter("action");
if (action != null) {
	String outputText = "<span=\"color: red\">no permission</span>";
	if (canEdit) {
		String conID = request.getParameter("conID");
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		if (action.equals("saveAuditor")) {
			String auditorID = request.getParameter("auditorID");
			cBean.daAuditor_id = auditorID;
			cBean.daAssignedDate = DateBean.getTodaysDate();
			if (auditorID.equals("0")) cBean.daAssignedDate = "";
			outputText = "<b>" + cBean.daAssignedDate + "</b>";
		}
		if (action.equals("notRequired")) {
			cBean.daRequired = "No";
			outputText = "<i>not required</i>";
		}
		cBean.writeToDB();
	}
	out.print(outputText);
	return;
}
*/
SelectContractorAudit sql = new SelectContractorAudit();

//sql.setAuditTypeID(AuditType.DA);
sql.addField("createdDate");
sql.addField("auditorID");
sql.addField("assignedDate");
sql.addField("completedDate");
sql.addField("closedDate");
sql.addField("conID");

sql.addField("isScheduled");
sql.addField("hasAuditor");
sql.addWhere("auditStatus='Pending'");
//sql.addField("c.pqfSubmittedDate");
//sql.addField("c.daAuditor_id");
//sql.addField("c.daAssignedDate");
//sql.addField("c.daSubmittedDate");
//sql.addField("c.daClosedDate");

//TODO make some logic in a cron job or something that creates the DA audits
//  according to the criteria below, ie they answered yes on question 894
//sql.addPQFQuestion(894, false, "requiredAnswer"); //q318.answer
//sql.addWhere("q894.answer = 'Yes' OR c.daRequired IS NULL OR c.daRequired = 'Yes'");

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "createdDate");
report.setPageByResult(request.getParameter("showPage"));
report.addFilter(new SelectFilter("name", "a.name LIKE '%?%'", request.getParameter("name"), SearchBean.DEFAULT_NAME, SearchBean.DEFAULT_NAME));
report.addFilter(new SelectFilterInteger("operatorID", "a.id IN (SELECT subID FROM generalcontractors WHERE genID = ? )", request.getParameter("operatorID"), SearchBean.DEFAULT_GENERAL_VALUE, SearchBean.DEFAULT_GENERAL_VALUE));
report.setLimit(50);

List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>Schedule Drug &amp; Alcohol Audits</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script type="text/javascript">
function saveAudit(auditID) {
	var form = $('assignScheduleAuditsForm');
	var auditorID = form['auditorID_'+auditID];
	
	pars = 'audit.id=<%=auditID%>&audit.scheduledDate=&auditID='+auditID+'&auditorID='+$F(auditorID);
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=&audit.auditor=
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=01/01/2008&audit.auditor.id=907
	
	var divName = 'dateAssigned_td'+auditID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'AuditSaveAjax.action', {method: 'post', parameters: pars});
	new Effect.Highlight($('auditor_tr'+auditID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}
function notRequired(auditID) {
	pars = 'action=notRequired&auditID='+auditID;
	
	var divName = 'auditor_td'+auditID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'report_daAudit.jsp', {method: 'post', parameters: pars});
	new Effect.Highlight($('auditor_tr'+auditID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}
</script>
<style>
.auditselect {
	margin: 0px;
	padding: 0px;
}	
</style>
<s:head theme="ajax"/>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr>
    <td height="70" colspan="2" align="center" class="buttons"> 
      <%@ include file="includes/selectReport.jsp"%>
      <span class="blueHeader">Schedule Drug &amp; Alcohol Audits</span><br>
      <a href="report_daAudit.jsp" class="blueMain">Refresh</a>
    </td>
  </tr>
</table>
<form id="form1" name="form1" method="post" action="report_scheduleAssignAudits.jsp">
<table border="0" align="center" cellpadding="2" cellspacing="0">
  <tr>
    <td align="left">
      <input name="name" type="text" class="forms" value="<%=report.getFilterValue("name")%>" size="8" onFocus="clearText(this)">
      <%=SearchBean.getSearchGeneralSelect("operatorID", "forms", report.getFilterValue("operatorID"))%>
      <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0" onClick="runSearch( 'form1')" onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
    </td>
  </tr>
</table>
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="startsWith" value="<%=sql.getStartsWith()%>"/>
	<input type="hidden" name="orderBy"  value="<%=report.getOrderBy()%>"/>
</form>
<form class="forms" id="assignScheduleAuditsForm">
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle"> 
			    <td><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
 			    <td align="center"><a href="?orderBy=auditType" class="whiteTitle">Type</a></td>
 			    <td align="center"><a href="?orderBy=createdDate DESC" class="whiteTitle">Created</a></td>
 			    <td align="center"><a href="?orderBy=completedDate DESC" class="whiteTitle">Submitted</a></td>
 			    <td align="center"><a href="?orderBy=closedDate DESC" class="whiteTitle">Closed</a></td>
 			    <td align="center"><a href="?orderBy=auditorID DESC,name" class="whiteTitle">Auditor</a></td>
 			    <td align="center"><a href="?orderBy=assignedDate DESC" class="whiteTitle">Assigned</a></td>
 			    <td align="center"><a href="?orderBy=scheduledDAte,name" class="whiteTitle">Scheduled</a></td>
	</tr>
<%	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
	for(BasicDynaBean row: searchData) {
		String auditID = row.get("auditID").toString();
		String auditorID;
		if (null==row.get("auditorID"))
			auditorID = "";
		else
			auditorID = row.get("auditorID").toString();
		boolean hasAuditor = (0 != Integer.parseInt(row.get("hasAuditor").toString()));
		boolean isScheduled = (0 != Integer.parseInt(row.get("isScheduled").toString()));
		pageContext.setAttribute("auditID",auditID);
%>
	<tr id="auditTr_<%=auditID%>" class="blueMain" <%=color.nextBgColor()%>>
			    <td><a href="accounts_edit_contractor.jsp?id=<%=row.get("conID")%>"><%=row.get("name")%></a></td>
			    <td><a href="pqf_view.jsp?auditID=<%=auditID%>"><%=row.get("auditName")%></a></td>
			    <td><%=DateBean.toShowFormat(row.get("createdDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("completedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("closedDate"))%></td>
			    <td>
<%		if (hasAuditor){ %>
			    		<%=AUDITORS.getAuditorsSelect("auditorID_"+auditID,"forms",auditorID,"")%>
<%		}//if%>
			    </td>
			    <td id="auditor_td<%=auditID%>"><%=DateBean.toShowFormat(row.get("assignedDate"))%></td>
				<td align="center" id="scheduleAuditTd_<%=auditID%>
<%		if (isScheduled){ %>
					<s:datetimepicker name="scheduledDate+#attr.auditID" displayFormat="M/d/yy"/>
					<s:datetimepicker name="scheduledTime+#attr.auditID" displayFormat="hh:mm a zzz" type="time""/>
<%		}//if%>
				</td>
				<td>
				<input type="submit" name="submitAudit_<%=auditID%>" value="Save" onClick="saveAudit(<%=auditID%>);">
				</td>

	</tr>
<%	}%>
</table>
</form>
<p align="center"><%=report.getPageLinks()%></p>
</body>
</html>