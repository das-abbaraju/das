<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>	
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<%
boolean canEdit = true;

Report report = new Report();
List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>Schedule Drug &amp; Alcohol Audits</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script type="text/javascript">
function saveAuditor(auditID) {
	var form = $('assignScheduleAuditsForm');
	var auditorID = form['auditorID_'+auditID];
	auditorID = $F(auditorID);
	var auditorPars = '&audit.auditor=';
	if (0 != auditorID)
		auditorPars = '&audit.auditor.id='+auditorID
	
	pars = 'audit.id='+auditID+auditorPars;

	var divName = 'assignedDateTd_'+auditID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';

	var myAjax = new Ajax.Updater(divName, 'AuditSaveAjax.action', {method: 'post', parameters: pars});
	new Effect.Highlight($('auditTr_'+auditID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});

//	${'pars'}.innerHTML = pars;
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=&audit.auditor=
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=01/01/2008&audit.auditor.id=907
	
}
function saveAudit(auditID) {
	var form = $('assignScheduleAuditsForm');
	var auditorID = form['auditorID_'+auditID];
	auditorID = $F(auditorID);
	var auditorPars = '&audit.auditor=';
	if (0 != auditorID)
		auditorPars = '&audit.auditor.id='+auditorID
	var scheduledDate = form['scheduledDate_'+auditID];
	if (null == scheduledDate)
		scheduledDate='';
	else
		scheduledDate = $F(scheduledDate);
	var scheduledTime = form['scheduledTime_'+auditID];
	if (null == scheduledTime)
		scheduledTime='';
	else
		scheduledTime = $F(scheduledTime);
	
	pars = 'audit.id='+auditID+'&audit.scheduledDate='+scheduledDate+auditorPars;

	var divName = 'status_'+auditID;
//	$(divName).innerHTML = auditorID;

	var myAjax = new Ajax.Updater(divName, 'AuditSaveAjax.action', {method: 'post', parameters: pars});
//	new Effect.Highlight($('auditTr_'+auditID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});

	${'pars'}.innerHTML = pars;
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=&audit.auditor=
// AuditSaveAjax.action?audit.id=3260&audit.scheduledDate=01/01/2008&audit.auditor.id=907
	
}
// TODO support setting D&A audits to Not required
function notRequired(auditID) {
	pars = 'audit.status=Exempt&audit.id='+auditID;
	
	var divName = 'auditor_td'+auditID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'AuditSaveAjax.action', {method: 'post', parameters: pars});
	new Effect.Highlight($('auditor_tr'+auditID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}
</script>
<style>
.auditselect {
	margin: 0px;
	padding: 0px;
}	
</style>
<s:head theme="ajax" debug="true"/>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr>
    <td height="70" colspan="2" align="center" class="buttons"> 
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
 			    <td id="pars"></td>
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
		pageContext.setAttribute("scheduledDate","scheduledDate_"+auditID);
		pageContext.setAttribute("scheduledTime","scheduledTime_"+auditID);
%>
	<tr id="auditTr_<%=auditID%>" class="blueMain" <%=color.nextBgColor()%>>
			    <td><a href="accounts_edit_contractor.jsp?id=<%=row.get("conID")%>"><%=row.get("name")%></a></td>
			    <td><a href="Audit.action?auditID=<%=auditID%>"><%=row.get("auditName")%></a></td>
			    <td><%=DateBean.toShowFormat(row.get("createdDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("completedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("closedDate"))%></td>
			    <td>
<%		if (hasAuditor){ %>
			    		<%=AUDITORS.getAuditorsSelect("auditorID_"+auditID,"forms",auditorID,"saveAuditor("+auditID+")")%>
<%		}//if%>
			    </td>
			    <td id="'assignedDateTd_<%=auditID%>"><%=DateBean.toShowFormat(row.get("assignedDate"))%></td>
				<td align="center" id="scheduleAuditTd_<%=auditID%>">
<%		if (isScheduled){ %>
					<s:datetimepicker id="%{#attr.scheduledDate}" name="%{#attr.scheduleDate}"/>
					<s:datetimepicker id="%{#attr.scheduledTime}" name="%{#attr.scheduleTime}"  type="time"/>
<%		}//if%>
				</td>
				<td id="status_<%=auditID%>">
				<input class="forms" type="button" value="Save" onClick="saveAudit(<%=auditID%>);">
				</td>
	</tr>
<%	}%>
</table>
</form>
<p align="center"><%=report.getPageLinks()%></p>
</body>
</html>