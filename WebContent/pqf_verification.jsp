<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%@page import="com.picsauditing.search.SelectAccount"%>
<%@page import="com.picsauditing.search.Report"%>
<%@page import="java.util.List"%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<%@page import="org.apache.commons.beanutils.BasicDynaBean"%>
<%
permissions.tryPermission(OpPerms.AuditVerification);

SelectAccount sql = new SelectAccount();
sql.setType(SelectAccount.Type.Contractor);
sql.addAudit(AuditType.PQF);

sql.addField("c.notes");
sql.addWhere("active='Y'");

sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id AND ca.auditTypeID = " + AuditType.PQF);
sql.addField("ca.auditID");
sql.addField("ca.scheduledDate");
//sql.addWhere("c.pqfSubmittedDate >= '2008-01-01'");
sql.addWhere("ca"+AuditType.PQF+".completedDate >= '2008-01-01'");
//sql.addField("c.pqfSubmittedDate");
sql.addField("ca"+AuditType.PQF+".completedDate AS pqfSubmittedDate");

sql.addJoin("LEFT JOIN osha os ON os.conID = a.id AND os.location = 'Corporate'");
sql.addField("os.verifiedDate1");
sql.addField("os.verifiedDate2");
sql.addField("os.verifiedDate3");

if ("on".equals(request.getParameter("osha1"))) sql.addWhere("os.verifiedDate1 IS NULL");
if ("on".equals(request.getParameter("osha2"))) sql.addWhere("os.verifiedDate2 IS NULL");
if ("on".equals(request.getParameter("osha3"))) sql.addWhere("os.verifiedDate3 IS NULL");

sql.addPQFQuestion(1617);
sql.addPQFQuestion(1519);
sql.addPQFQuestion(889);
sql.addField("q1617.dateVerified as dateVerified07");
sql.addField("q1519.dateVerified as dateVerified06");
sql.addField("q889.dateVerified as dateVerified05");

if ("on".equals(request.getParameter("emr07"))) sql.addWhere("q1617.dateVerified IS NULL OR q1617.dateVerified='0000-00-00'");
if ("on".equals(request.getParameter("emr06"))) sql.addWhere("q1519.dateVerified IS NULL OR q1519.dateVerified='0000-00-00'");
if ("on".equals(request.getParameter("emr05"))) sql.addWhere("q889.dateVerified IS NULL OR q889.dateVerified='0000-00-00'");

sql.setStartsWith(request.getParameter("startsWith"));

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "ca"+AuditType.PQF+".completedDate");

report.setPageByResult(request.getParameter("showPage"));
report.setLimit(50);

List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>PQF Verification</title>
<script src="js/Search.js" type="text/javascript"></script>
</head>
<body>
<h1>PQF Verification</h1>

<form id="form1" name="form1" action="pqf_verification.jsp">
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
	<td align="center" valign="bottom">
	<input type="image" src="images/button_search.gif" width="70" height="23" />
	</td>
	</tr>
	</table>
</form>

<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td align="center"><%=report.getStartsWithLinksWithDynamicForm()%><br />
		<%=report.getPageLinksWithDynamicForm()%></td>
	</tr>
</table>
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan=2><a href="#" onclick="changeOrderBy('form1','a.name'); return false;" class="whiteTitle">Contractor</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','ca<%=AuditType.PQF%>.completedDate');" class="whiteTitle">Submitted</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','scheduledDate');" class="whiteTitle">Followup</a></td>
		<td align="center">Verification Status</td>
		<td align="center">Notes</td>
	</tr>
	<%
	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();

	for(BasicDynaBean row: searchData) {
		int verified = 0;
		if (DateBean.toShowFormat(row.get("verifiedDate1")).length() > 0) verified++;
		if (DateBean.toShowFormat(row.get("verifiedDate2")).length() > 0) verified++;
		if (DateBean.toShowFormat(row.get("verifiedDate3")).length() > 0) verified++;
		if (DateBean.toShowFormat(row.get("dateVerified07")).length() > 0) verified++;
		if (DateBean.toShowFormat(row.get("dateVerified06")).length() > 0) verified++;
		if (DateBean.toShowFormat(row.get("dateVerified05")).length() > 0) verified++;
	%>
	<tr id="auditor_tr<%=row.get("auditID")%>" class="blueMain"
		<%=color.nextBgColor()%>>
		<td align="right"><%=color.getCounter()%></td>
		<td><a href="VerifyView.action?auditID=<%=row.get("auditID")%>"><%=row.get("name")%></a></td>
		<td><%=DateBean.toShowFormat(row.get("pqfSubmittedDate"))%></td>
		<td><%=DateBean.toShowFormat(row.get("scheduledDate"))%></td>
		<td><%=Math.round(100*(float)verified/6)%>%</td>
		<td><%=row.get("notes").toString().substring(0, 50)%>...</td>
	</tr>
	<%
		}
	%>
</table>
<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td align="right"><%=report.getPageLinksWithDynamicForm()%></td>
	</tr>
</table>
</body>
</html>
