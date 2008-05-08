<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%@page import="com.picsauditing.search.SelectContractorAudit"%>
<%@page import="com.picsauditing.search.Report"%>
<%@page import="java.util.List"%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<%@page import="org.apache.commons.beanutils.BasicDynaBean"%>
<%
permissions.tryPermission(OpPerms.AuditVerification);

SelectContractorAudit sql = new SelectContractorAudit();
sql.setAuditTypeID(AuditType.PQF);

sql.addField("c.notes");
sql.addWhere("a.active='Y'");

sql.addField("ca.scheduledDate");
sql.addField("ca.completedDate");
sql.addField("ca.percentVerified");

sql.addJoin("LEFT JOIN osha os ON os.conID = a.id AND os.location = 'Corporate'");
sql.addField("os.verifiedDate1");
sql.addField("os.verifiedDate2");
sql.addField("os.verifiedDate3");

if ("on".equals(request.getParameter("osha1"))) sql.addWhere("os.verifiedDate1 IS NULL");
if ("on".equals(request.getParameter("osha2"))) sql.addWhere("os.verifiedDate2 IS NULL");
if ("on".equals(request.getParameter("osha3"))) sql.addWhere("os.verifiedDate3 IS NULL");

sql.addWhere("ca.auditStatus='"+AuditStatus.Submitted+"'");

/*
sql.addQuestion(1617);
sql.addQuestion(1519);
sql.addQuestion(889);
sql.addField("q1617.dateVerified as dateVerified07");
sql.addField("q1519.dateVerified as dateVerified06");
sql.addField("q889.dateVerified as dateVerified05");

if ("on".equals(request.getParameter("emr07"))) sql.addWhere("q1617.dateVerified IS NULL OR q1617.dateVerified='0000-00-00'");
if ("on".equals(request.getParameter("emr06"))) sql.addWhere("q1519.dateVerified IS NULL OR q1519.dateVerified='0000-00-00'");
if ("on".equals(request.getParameter("emr05"))) sql.addWhere("q889.dateVerified IS NULL OR q889.dateVerified='0000-00-00'");
*/

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "ca.scheduledDate");

report.setPageByResult(request.getParameter("showPage"));
report.setLimit(50);

List<BasicDynaBean> searchData = report.getPage();

%>
<%@page import="com.picsauditing.jpa.entities.AuditStatus"%>
<html>
<head>
<title>PQF Verification</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>PQF Verification</h1>

<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<form id="form1" name="form1" style="display: none">
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="startsWith" value="<%= request.getParameter("startsWith") == null ? "" : request.getParameter("startsWith") %>"/>
	<input type="hidden" name="orderBy"  value="<%=request.getParameter("orderBy") == null ? "" : request.getParameter("orderBy") %>"/>
	<p class="blueMain">Show Contractors that need:</p>
	
	<table border="0">
		<tr>
			<td>
	<table border="0" cellpadding="2" cellspacing="0" class="blueMain">
		<tr>
			<td></td>
			<td>2007</td>
			<td>2006</td>
			<td>2005</td>
		</tr>
		<tr>
			<td>OSHA</td>
			<td><input type="checkbox" name="osha1" <%="on".equals(request.getParameter("osha1")) ? "checked" : "" %>></td>
			<td><input type="checkbox" name="osha2" <%="on".equals(request.getParameter("osha2")) ? "checked" : "" %>></td>
			<td><input type="checkbox" name="osha3" <%="on".equals(request.getParameter("osha3")) ? "checked" : "" %>></td>
		</tr>
		<tr>
			<td>EMR</td>
			<td><input type="checkbox" name="emr07" <%="on".equals(request.getParameter("emr07")) ? "checked" : "" %>></td>
			<td><input type="checkbox" name="emr06" <%="on".equals(request.getParameter("emr06")) ? "checked" : "" %>></td>
			<td><input type="checkbox" name="emr05" <%="on".equals(request.getParameter("emr05")) ? "checked" : "" %>></td>
		</tr>
	</table>
	</td>
	<td>
	<input type="image" src="images/button_search.gif" width="70" height="23" />
	</td>
	</tr>
	</table>
	<div class="alphapaging">
	<%=report.getStartsWithLinksWithDynamicForm()%>
	</div>
</form>
</div>

<div>
<%=report.getPageLinksWithDynamicForm()%>
</div>

<table class="report">
	<thead>
	<tr>
		<td></td>
		<td><a href="#" onclick="changeOrderBy('form1','a.name'); return false;" >Contractor</a></td>
		<td ><a href="javascript: changeOrderBy('form1','ca<%=AuditType.PQF%>.completedDate');" >Submitted</a></td>
		<td ><a href="javascript: changeOrderBy('form1','scheduledDate');" >Followup</a></td>
		<td >% Verified</td>
		<td >Notes</td>
	</tr>
	</thead>
	<%
	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();

	for(BasicDynaBean row: searchData) {
		String notes = row.get("notes") == null ? "" : row.get("notes").toString().substring(0, 50)+"...";
		
	%>
	<tr id="auditor_tr<%=row.get("auditID")%>" <%=color.nextBgColor()%>>
		<td class="right"><%=color.getCounter()%></td>
		<td><a href="VerifyView.action?auditID=<%=row.get("auditID")%>"><%=row.get("name")%></a></td>
		<td><%=DateBean.toShowFormat(row.get("completedDate"))%></td>
		<td><%=DateBean.toShowFormat(row.get("scheduledDate"))%></td>
		<td align="right"><%=row.get("percentVerified")%>%</td>
		<td><%=notes%></td>
	</tr>
	<%
		}
	%>
</table>
<div><center>
<%=report.getPageLinksWithDynamicForm()%>
</center></div>
</body>
</html>
