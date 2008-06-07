<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@page import="javax.naming.NoPermissionException"%>
<%@page import="com.picsauditing.search.SelectAccount"%>
<%@page import="com.picsauditing.search.Report"%>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
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
sql.addWhere("a.type='" + accountType + "'");
sql.setStartsWith(request.getParameter("startsWith"));

search.setSql(sql);
search.setPageByResult(request.getParameter("showPage"));
search.setLimit(50);

List<BasicDynaBean> searchData = search.getPage();
%>
<html>
<head>
<title><%=title%></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Manage <%=accountType%> Accounts</h1>
<form id="form1" name="form1" style="display: none">
<input type="hidden" name="actionID" value="0">
			<input type="hidden" name="action" value="">
			<input type="hidden" name="showPage" value="1"/>
			<input type="hidden" name="orderBy"  value="<%=sBean.orderBy == null ? "dateCreated DESC" : sBean.orderBy %>"/>
</form>
<div>
<%= search.getPageLinksWithDynamicForm() %>
<%
if (canEdit) {
  if(accountType.equals("Corporate")) {%>
<div><a href="accounts_new_operator.jsp?type=<%=accountType%>">Create New Corporate</a></div>
<%
  }	else {
%>
<div><a href="accounts_new_operator.jsp?type=<%=accountType%>">Create New</a></div>
<%		
	}
	}
%>
</div>

<table class="report">
	<thead>
	<tr>
		<td colspan=2><a href="?type=<%=accountType%>&orderBy=a.name">Name</a></td>
		<td>Industry</td>
		<td>City</td>
		<td>State</td>
		<td>Primary Contact</td>
		<td><%=(accountType.startsWith("O"))?"Contractors":"Operators"%></td>
		<td>&nbsp;</td>
	</tr>
	</thead>
	<%
	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater(search.getSql().getStartRow());
	for (BasicDynaBean row : searchData) {
		%>
		<% if(row.get("active").equals("Y")) { %>
		<tr id="auditor_tr<%=row.get("id")%>" <%= color.nextBgColor()%>>
		<% } else { %>
		<tr id="auditor_tr<%=row.get("id")%>" <%= color.nextBgColor()%> class="na">	
		<% } %>
		<td class="right"><%=color.getCounter()%></td>
		<% if(row.get("active").equals("Y")) { %>	
			<td><a href="accounts_edit_operator.jsp?id=<%=row.get("id")%>"><%=row.get("name")%></a></td>
		<% } else { %>
			<td class="inactive"><a href="accounts_edit_operator.jsp?id=<%=row.get("id")%>"><%=row.get("name")%>*</a></td>
		<% } %>	
			<td><%=row.get("industry")%></td>
			<td><%=row.get("city")%></td>
			<td><%=row.get("state")%></td>
			<td><%=row.get("contact")%></td>
			<td class="right"><%=row.get("subCount")%></td>
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

</body>
</html>