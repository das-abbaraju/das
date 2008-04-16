<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<%@page import="com.picsauditing.mail.*"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%
permissions.tryPermission(OpPerms.EmailAnnualUpdate);

String action = request.getParameter("action");
if ("Send Emails".equals(action)) {
	permissions.tryPermission(OpPerms.EmailAnnualUpdate, OpType.Edit);
	java.util.Enumeration e = request.getParameterNames();
	while (e.hasMoreElements()) {
		String temp = (String)e.nextElement();
		if (temp.startsWith("sendEmail_")) {
			String conID = temp.substring(10);
			EmailContractorBean emailer = new EmailContractorBean();
			emailer.sendMessage(EmailTemplates.annual_update, conID, permissions);
			
			emailer.getContractorBean().lastAnnualUpdateEmailDate=DateBean.getTodaysDate();
			emailer.getContractorBean().annualUpdateEmails++;
			emailer.getContractorBean().writeToDB();
		}
	}
}

SelectAccount sql = new SelectAccount();

sql.addAudit(AuditType.PQF);
sql.addAudit(AuditType.OFFICE);
sql.setType(SelectAccount.Type.Contractor);
sql.addWhere("active='Y' AND isOnlyCerts='No'");

sql.addField("a.dateCreated");
sql.addField("a.lastLogin");
sql.addField("c.lastAnnualUpdateEmailDate");
sql.addField("c.annualUpdateEmails");
//sql.addField("c.pqfSubmittedDate");
sql.addField("ca"+AuditType.PQF+".completedDate AS pqfSubmittedDate");
//sql.addField("c.auditDate");
sql.addField("ca"+AuditType.OFFICE+".completedDate AS auditDate");

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "lastLogin DESC");

report.addFilter(new SelectFilter("startsWith", "a.name LIKE '?%'", request.getParameter("startsWith")));
report.addFilter(new SelectFilterInteger("minTimes", "c.annualUpdateEmails >= '?'", request.getParameter("minTimes"), "0", "0"));
report.addFilter(new SelectFilterInteger("maxTimes", "c.annualUpdateEmails <= '?'", request.getParameter("maxTimes"), "9", ""));
report.addFilter(new SelectFilterDate("pqfDate", "ca"+AuditType.PQF+".completedDate < '?'", request.getParameter("pqfDate"), "1/1/2008", ""));
report.addFilter(new SelectFilterDate("dateCreated", "a.dateCreated < '?'", request.getParameter("dateCreated"), "1/1/2008", ""));

report.setPageByResult(request.getParameter("showPage"));
report.setLimit(50);
List<BasicDynaBean> searchData = report.getPage();

%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<html>
<head>
<title>Annual Update Emails</title>
<script language="javascript" SRC="js/checkAllBoxes.js"></script>
</head>
<body>
<center>
<span class="blueHeader">Annual Update Emails</span>

<form name="filter" method="get" action="report_annualUpdate.jsp">
	Sent Email Range: <input type="text" name="minTimes" value="<%=report.getFilterValue("minTimes")%>" size="2" class="blueSmall" />
		and <input type="text" name="maxTimes" value="<%=report.getFilterValue("maxTimes")%>" size="2" class="blueSmall" /><br />
	PQF Submitted before: <input type="text" name="pqfDate" value="<%=report.getFilterValue("pqfDate")%>" size="10" class="blueSmall" /><br />
	Registered before: <input type="text" name="dateCreated" value="<%=report.getFilterValue("dateCreated")%>" size="10" class="blueSmall" /><br />
	<input type="submit" class="buttons" value="Filter" />
</form>
<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td align="left"><%=report.getStartsWithLinks()%></td>
		<td align="right"><%=report.getPageLinks()%></td>
	</tr>
</table>
<form name="form10" id="form10" method="post" action="report_annualUpdate.jsp">
<%
for (String key : report.getFilters().keySet()) {
	if (report.getFilters().get(key).isSet()) {
		%>
		<input type="hidden" name="<%= report.getFilters().get(key).getName() %>" value="<%=report.getFilters().get(key).getValue()%>" />
		<%
	}
}
%>
<table border="0" cellpadding="1" cellspacing="1">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan="2" align="center">Email<nobr><input
			name="checkAllBox" id="checkAllBox" type="checkbox"
			onclick="checkAll(document.form10)"></nobr></td>
		<td align="center"><a
			href="?orderBy=lastAnnualUpdateEmailDate DESC" class="whiteTitle">Sent</a></td>
		<td align="center"><a href="?orderBy=annualUpdateEmails"
			class="whiteTitle">Times</a></td>
		<td width="150"><a href="?orderBy=name" class="whiteTitle">Contractor</a></td>
		<td align="center"><a href="?orderBy=dateCreated DESC"
			class="whiteTitle">Created</a></td>
		<td align="center"><a href="?orderBy=lastLogin DESC"
			class="whiteTitle">Last Login</a></td>
		<td align="center"><a href="?orderBy=ca<%=AuditType.PQF%>.completedDate DESC"
			class="whiteTitle">PQF</a></td>
		<td align="center" bgcolor="#6699CC"><a
			href="?orderBy=ca<%=AuditType.OFFICE%>.completedDate DESC" class="whiteTitle">Audit</a></td>
		<td>Preview</td>
	</tr>
	<%
	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater(report.getSql().getStartRow());
	for (BasicDynaBean row : searchData) {
	%>
	<tr class="blueMain" <%=color.nextBgColor()%>>
		<td><%=color.getCounter()%></td>
		<td align="center"><input name="sendEmail_<%=row.get("id")%>" 
				id="sendEmail_<%=row.get("id")%>" type="checkbox"></td>
		<td align="center"><%=DateBean.toShowFormat(row.get("lastAnnualUpdateEmailDate"))%></td>
		<td align="center"><%=row.get("annualUpdateEmails")%></td>
		<td><a href="contractor_detail.jsp?id=<%=row.get("id")%>"
			class="active"><%=row.get("name")%></a></td>
		<td align="center"><%=DateBean.toShowFormat(row.get("dateCreated"))%></td>
		<td align="center"><%=DateBean.toShowFormat(row.get("lastLogin"))%></td>
		<td align="center"><%=DateBean.toShowFormat(row.get("pqfSubmittedDate"))%></td>
		<td align="center"><%=DateBean.toShowFormat(row.get("auditDate"))%></td>
		<td><a href="email_templates.jsp?template=annual_update&userID=&accountID=<%=row.get("id")%>">Preview</a></td>
	</tr>
	<%	}//while %>
</table>
<br>

<% if (	permissions.hasPermission(OpPerms.EmailAnnualUpdate, OpType.Edit) ) { %>
<input name="action" type="submit" class="buttons" value="Send Emails"
	onClick="return confirm('Are you sure you want to send these emails?');">
<% } %>
</form>

<p><%=report.getPageLinks()%></p>
</center>

</body>
</html>