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

sql.setAuditTypeID(AuditType.DA);
sql.addField("createdDate");
sql.addField("auditorID");
sql.addField("assignedDate");
sql.addField("completedDate");
sql.addField("closedDate");
sql.addWhere("auditStatus='Pending'");
//sql.addField("c.pqfSubmittedDate");
//sql.addField("c.daAuditor_id");
//sql.addField("c.daAssignedDate");
//sql.addField("c.daSubmittedDate");//sql.addField("c.daClosedDate");

//TODO make some logic in a cron job or something that creates the DA audits
//  according to the criteria below, ie they answered yes on question 894
//sql.addPQFQuestion(894, false, "requiredAnswer"); //q318.answer
//sql.addWhere("q894.answer = 'Yes' OR c.daRequired IS NULL OR c.daRequired = 'Yes'");

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "createdDate");

report.setPageByResult(request.getParameter("showPage"));
report.setLimit(50);

List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>Schedule Drug &amp; Alcohol Audits</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script type="text/javascript">
function selectAuditor(auditID) {
	var form = $('auditor_form'+auditID);
	var auditor = form['auditorID'];
	pars = 'action=saveAuditor&auditID='+auditID+'&auditorID='+$F(auditor);
	
	var divName = 'auditor_td'+auditID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'report_daAudit.jsp', {method: 'post', parameters: pars});
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
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle"> 
			    <td><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
 			    <td align="center"><a href="?orderBy=createdDate DESC" class="whiteTitle">Submitted</a></td>
 			    <td align="center"><a href="?orderBy=completedDate DESC" class="whiteTitle">Submitted</a></td>
 			    <td align="center"><a href="?orderBy=closedDate DESC" class="whiteTitle">Closed</a></td>
 			    <td align="center"><a href="?orderBy=auditorID DESC,name" class="whiteTitle">Auditor</a></td>
 			    <td align="center"><a href="?orderBy=assignedDate DESC" class="whiteTitle">Assigned</a></td>
 			    <td align="center">Not Required</td>
	</tr>
<%	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
	for(BasicDynaBean row: searchData) {
		String auditorID;
		if (null==row.get("auditorID"))
			auditorID = "";
		else
			auditorID = row.get("auditorID").toString();
%>
	<tr id="auditor_tr<%=row.get("auditID")%>" class="blueMain" <%=color.nextBgColor()%>>
			    <td><a href="accounts_edit_contractor.jsp?id=<%=row.get("conID")%>"><%=row.get("name")%></a></td>
			    <td><%=DateBean.toShowFormat(row.get("createdDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("completedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("closedDate"))%></td>
			    <td>
			    	<form class="auditselect" id="auditor_form<%=row.get("auditID")%>">
			    		<%=AUDITORS.getAuditorsSelect("auditorID","forms",auditorID,"selectAuditor("+row.get("auditID")+")")%>
			    	</form>
			    </td>
			    <td id="auditor_td<%=row.get("auditID")%>"><%=DateBean.toShowFormat(row.get("assignedDate"))%></td>
			    <td>
			    	<form class="auditselect" id="">
			    		<input type="button" class="blueMain" value="Not Required" onclick="notRequired(<%=row.get("auditID")%>)" name="required_button<%=row.get("auditID")%>" />
			    	</form>
			    </td>
	</tr>
<%	}%>
</table>
<p align="center"><%=report.getPageLinks()%></p>
</body>
</html>