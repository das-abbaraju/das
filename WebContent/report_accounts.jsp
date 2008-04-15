<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@page import="javax.naming.NoPermissionException"%>
<%@page import="com.picsauditing.search.SelectAccount"%>
<%@page import="com.picsauditing.search.Report"%>
<%

String action = request.getParameter("action");
if ("Delete".equals(action)) {
	AccountBean aBean = new AccountBean();
	aBean.setFromDB(request.getParameter("accountID"));
	
	if (aBean.isOperator())
		permissions.tryPermission(OpPerms.ManageOperators, OpType.Delete);
	else if (aBean.isCorporate())
		permissions.tryPermission(OpPerms.ManageCorporate, OpType.Delete);
	else throw new NoPermissionException("Delete Account");
	
	aBean.deleteAccount(aBean.id, config.getServletContext().getRealPath("/"));
}

SelectAccount sql = new SelectAccount();
Report search = new Report();
search.setSql(sql);

String accountType = request.getParameter("type");
if (accountType == null) accountType = "Operator";

boolean canEdit = false;
boolean canDelete = false;
String title = "";
if (accountType.equals("Operator")) {
	permissions.tryPermission(OpPerms.ManageOperators);
	canEdit = permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit);
	canDelete = permissions.hasPermission(OpPerms.ManageCorporate, OpType.Delete);
	sql.addJoin("LEFT JOIN (SELECT genID, count(*) as subCount FROM generalContractors GROUP BY genID) sub ON sub.genID = a.id");
	sql.addField("subCount");
	title = "List Operators";
} else if (accountType.equals("Corporate")) {
	permissions.tryPermission(OpPerms.ManageCorporate);
	canEdit = permissions.hasPermission(OpPerms.ManageCorporate, OpType.Edit);
	canDelete = permissions.hasPermission(OpPerms.ManageCorporate, OpType.Delete);
	sql.addJoin("LEFT JOIN (SELECT corporateID, count(*) as subCount FROM facilities GROUP BY corporateID) sub ON sub.corporateID = a.id");
	sql.addField("subCount");
	title = "List Corporate Accounts";
} else {
	throw new Exception("invalid parameter type");
}

search.setOrderBy(request.getParameter("orderBy"), "a.name");

sql.addField("a.industry");
sql.addField("a.city");
sql.addField("a.state");
sql.addField("a.contact");

sql.addWhere("active='Y'");
sql.addWhere("a.type='" + accountType + "'");
sql.startsWith(request.getParameter("startsWith"));

search.setSql(sql);
search.setPageByResult(request.getParameter("showPage"));
search.setLimit(50);

List<BasicDynaBean> searchData = search.getPage();
%>
<html>
<head>
<title><%=title%></title>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td height="70" colspan="2" align="center" class="buttons"><%@ include
			file="includes/selectReport.jsp"%> <span
			class="blueHeader">Manage <%=accountType%> Accounts</span></td>
	</tr>
</table>
<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td height="30" align="left"><%=search.getStartsWithLinks("&type=" + accountType)%></td>
		<td align="right"><%=search.getPageLinks("&type=" + accountType)%></td>
	</tr>
</table>
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan=2><a href="?type=<%=accountType%>&orderBy=a.name" class="whiteTitle">Name</a></td>
		<td>Industry</td>
		<td>City</td>
		<td>State</td>
		<td>Primary Contact</td>
		<td><%=(accountType.startsWith("O"))?"Contractors":"Operators"%></td>
		<td>&nbsp;</td>
	</tr>
	<%
	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater(search.getSql().getStartRow());
	for (BasicDynaBean row : searchData) {
		%>
		<tr id="auditor_tr<%=row.get("id")%>" class="blueMain"
			<%= color.nextBgColor()%>>
			<td align="right"><%=color.getCounter()%>.</td>
			<td><a href="accounts_edit_operator.jsp?id=<%=row.get("id")%>"><%=row.get("name")%></a></td>
			<td><%=row.get("industry")%></td>
			<td><%=row.get("city")%></td>
			<td><%=row.get("state")%></td>
			<td><%=row.get("contact")%></td>
			<td align="right"><%=row.get("subCount")%></td>
			<td><% if (canDelete && row.get("subCount") == null) { %>
				<form method="post" action="report_accounts.jsp" style="margin: 0px; padding: 0px;">
					<input name="action_id" type="hidden" value="<%=row.get("id")%>">
					<input id="delete<%=row.get("id")%>" name="action" type="submit" class="buttons" value="Delete" onClick="return confirm('Are you sure you want to delete this account?');">
				</form>
              <% } %>
			</td>
		</tr>
		<%
	}
	%>
</table>
<%
if (canEdit) {
%><p align="center"><a href="accounts_new_operator.jsp?type=<%=accountType%>">Create New</a></p><%
}
%>
</body>
</html>