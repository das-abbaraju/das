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
	EmailQueueDAO emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
	ContractorAccountDAO conAccountDAO = (ContractorAccountDAO)SpringUtils.getBean("ContractorAccountDAO");
	EmailBuilder emailBuilder = new EmailBuilder();
	java.util.Enumeration e = request.getParameterNames();
	while (e.hasMoreElements()) {
		String temp = (String)e.nextElement();
		if (temp.startsWith("sendEmail_")) {
			String conID = temp.substring(10);
			ContractorAccount contractor = conAccountDAO.find(Integer.parseInt(conID));
			emailBuilder.clear();
			emailBuilder.setTemplate(4); // Annual Update
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(contractor);
			EmailQueue email = emailBuilder.build();
			email.setPriority(30);
			emailQueueDAO.save(email);
			ContractorBean.addNote(contractor.getId(), permissions,
					"Sent PICS annual update email to " + emailBuilder.getSentTo());
			contractor.setAnnualUpdateEmails(contractor.getAnnualUpdateEmails() + 1);
			contractor.setLastAnnualUpdateEmailDate(new Date());
			conAccountDAO.save(contractor);
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
sql.addField("ca"+AuditType.PQF+".completedDate AS pqfSubmittedDate");
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
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="com.picsauditing.dao.ContractorAccountDAO"%>
<%@page import="com.picsauditing.jpa.entities.ContractorAccount"%>
<%@page import="com.picsauditing.dao.EmailQueueDAO"%>
<%@page import="com.picsauditing.jpa.entities.EmailQueue"%>
<html>
<head>
<title>Annual Update Emails</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script language="javascript" SRC="js/checkAllBoxes.js"></script>
</head>
<body>
<h1>Annual Update Emails</h1>

<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<form id="form1" name="form1" method="post" style="display: none">
	Sent Email Range: <input type="text" name="minTimes" value="<%=report.getFilterValue("minTimes")%>" size="2" class="blueSmall" />
		and <input type="text" name="maxTimes" value="<%=report.getFilterValue("maxTimes")%>" size="2" class="blueSmall" /><br />
	PQF Submitted before: <input type="text" name="pqfDate" value="<%=report.getFilterValue("pqfDate")%>" size="10" class="blueSmall" /><br />
	Registered before: <input type="text" name="dateCreated" value="<%=report.getFilterValue("dateCreated")%>" size="10" class="blueSmall" /><br />
	<input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0" >
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="startsWith" />
	<input type="hidden" name="orderBy"  value="<%=report.getOrderBy() %>"/>
	<div class="alphapaging">
	<%=report.getStartsWithLinksWithDynamicForm()%>
	</div>
</form>
</div>
<div>
<%=report.getPageLinksWithDynamicForm()%></div>
<form name="form10" id="form10" method="post">
<table class="report">
	<thead>
	<tr>
		<td></td>
		<td>Email<nobr><input
			name="checkAllBox" id="checkAllBox" type="checkbox"
			onclick="checkAll(document.form10)"></nobr></td>
		<td><a
			href="?orderBy=lastAnnualUpdateEmailDate DESC" >Sent</a></td>
		<td><a href="?orderBy=annualUpdateEmails"
			>Times</a></td>
		<td><a href="?orderBy=name" >Contractor</a></td>
		<td><a href="?orderBy=dateCreated DESC"
			>Created</a></td>
		<td><a href="?orderBy=lastLogin DESC"
			>Last Login</a></td>
		<td><a href="?orderBy=ca<%=AuditType.PQF%>.completedDate DESC"
			>PQF</a></td>
		<td><a
			href="?orderBy=ca<%=AuditType.OFFICE%>.completedDate DESC" >Office</a></td>
		<td>Preview</td>
	</tr>
	</thead>
	<%
	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater(report.getSql().getStartRow());
	for (BasicDynaBean row : searchData) {
	%>
	<tr class="blueMain" <%=color.nextBgColor()%>>
		<td><%=color.getCounter()%></td>
		<td class="center"><input name="sendEmail_<%=row.get("id")%>" 
				id="sendEmail_<%=row.get("id")%>" type="checkbox"></td>
		<td class="center"><%=DateBean.toShowFormat(row.get("lastAnnualUpdateEmailDate"))%></td>
		<td class="center"><%=row.get("annualUpdateEmails")%></td>
		<td><a href="ContractorView.action?id=<%=row.get("id")%>"
			class="active"><%=row.get("name")%></a></td>
		<td class="center"><%=DateBean.toShowFormat(row.get("dateCreated"))%></td>
		<td class="center"><%=DateBean.toShowFormat(row.get("lastLogin"))%></td>
		<td class="center"><%=DateBean.toShowFormat(row.get("pqfSubmittedDate"))%></td>
		<td class="center"><%=DateBean.toShowFormat(row.get("auditDate"))%></td>
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

<div>
<%=report.getPageLinksWithDynamicForm()%></div>

</body>
</html>