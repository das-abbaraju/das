<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%
permissions.tryPermission(OpPerms.AssignAudits);
boolean canEdit = permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit);

String action = request.getParameter("action");
if (action != null && action.equals("saveAuditor")) {
	if (!canEdit) {
		%>no permission<%
		return;
	}
	String auditorID = request.getParameter("auditorID");
	String conID = request.getParameter("conID");
	ContractorBean cBean = new ContractorBean();
	cBean.setFromDB(conID);
	cBean.desktopAuditor_id = auditorID;
	cBean.desktopAssignedDate = DateBean.getTodaysDate();
	cBean.writeToDB();
	%><%=cBean.desktopAssignedDate%><%
	return;
}
SelectAccount sql = new SelectAccount();
sql.setType(SelectAccount.Type.Contractor);

sql.addField("c.lastPayment");
sql.addField("c.pqfSubmittedDate");
sql.addField("c.desktopAuditor_id");
sql.addField("c.desktopAssignedDate");
sql.addField("c.desktopSubmittedDate");
sql.addField("c.desktopClosedDate");
sql.addField("c.desktopPercent");
sql.addField("c.desktopVerifiedPercent");

sql.addWhere("active='Y'");
sql.addWhere("isExempt='No'");
sql.addWhere("c.desktopSubmittedDate='0000-00-00' OR c.desktopSubmittedDate < DATE_ADD(CURDATE(),INTERVAL -34 MONTH)");
sql.addWhere("!(auditCompletedDate<>'0000-00-00' AND auditCompletedDate<'"+DateBean.OLD_OFFICE_CUTOFF+"' AND auditCompletedDate>DATE_ADD(CURDATE(),INTERVAL -34 YEAR))");
String startsWith = request.getParameter("startsWith"); 
if( startsWith != null && startsWith.length() > 0 )
{
	sql.addWhere("a.name like '" + startsWith + "%'");
}

//Get from the PQF if they uploaded their manual and what the revision data of it was
//Please upload your manual as a single pdf or word document.
int manualQID = Integer.parseInt(com.picsauditing.PICS.pqf.Constants.MANUAL_PQF_QID);
sql.addPQFQuestion(manualQID, true, "manualUploaded");

//What is the documented last date of revision of your safety manual?
int revisionQID = Integer.parseInt(com.picsauditing.PICS.pqf.Constants.MANUAL_REVISION_QID);
sql.addPQFQuestion(revisionQID, false, "revisionDate");

sql.setStartsWith(request.getParameter("startsWith"));

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "c.desktopSubmittedDate DESC");

report.setPageByResult(request.getParameter("showPage"));
report.setLimit(50);

List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>Schedule Desktop Audits</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script src="js/Search.js" type="text/javascript"></script>
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
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0"
	align="center">
	<tr>
		<td height="70" colspan="2" align="center" class="buttons"><%@ include
			file="includes/selectReport.jsp"%> <span
			class="blueHeader">Schedule Desktop Audits</span></td>
	</tr>
</table>

<form id="form1" name="form1" action="report_desktop.jsp">
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="startsWith" value="<%= request.getParameter("startsWith") == null ? "" : request.getParameter("startsWith") %>"/>
	<input type="hidden" name="orderBy"  value="<%=request.getParameter("orderBy") == null ? "c.desktopSubmittedDate DESC" : request.getParameter("orderBy") %>"/>
</form>


<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td height="30" align="left"><%=report.getStartsWithLinksWithDynamicForm()%></td>
		<td align="right"><%=report.getPageLinksWithDynamicForm()%></td>
	</tr>
</table>
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan=2><a href="javascript: changeOrderBy('form1','a.name');" class="whiteTitle">Contractor</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','lastPayment DESC');"
			class="whiteTitle">Paid</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','pqfSubmittedDate DESC');"
			class="whiteTitle">PQF</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','desktopSubmittedDate DESC');"
			class="whiteTitle">Submitted</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','desktopClosedDate DESC');"
			class="whiteTitle">Closed</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','revisionDate DESC');"
			class="whiteTitle">Revision</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','desktopAuditor_id');"
			class="whiteTitle">Auditor</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','desktopAssignedDate DESC');"
			class="whiteTitle">Assigned</a></td>
	</tr>
	<%
	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();

	for(BasicDynaBean row: searchData) {
	%>
	<tr id="auditor_tr<%=row.get("id")%>" class="blueMain"
		<%=color.nextBgColor()%>>
		<td align="right"><%=color.getCounter()%></td>
		<td><a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>"><%=row.get("name")%></a></td>
		<td><%=DateBean.toShowFormat(row.get("lastPayment"))%></td>
		<td><%=DateBean.toShowFormat(row.get("pqfSubmittedDate"))%></td>
		<td><%=DateBean.toShowFormat(row.get("desktopSubmittedDate"))%></td>
		<td><%=DateBean.toShowFormat(row.get("desktopClosedDate"))%></td>
		<td><%=DateBean.toShowFormat(row.get("revisionDate"))%></td>
		<td>
		<form class="auditselect" id="auditor_form<%=row.get("id")%>">
		<%=AUDITORS.getAuditorsSelect("auditorID","forms",row.get("desktopAuditor_id").toString(),"selectAuditor("+row.get("id")+")")%>
		</form>
		</td>
		<td id="auditor_td<%=row.get("id")%>"><%=DateBean.toShowFormat(row.get("desktopAssignedDate"))%></td>
	</tr>
	<%
	}
	%>
</table>

</body>
</html>