<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.access.*"%>
<%@include file="includes/main.jsp" %>
<%
SearchAccounts search = new SearchAccounts();

String accountType = request.getParameter("type");
if (accountType == null) accountType = "Operator";

if (accountType.equals("Operator")) {
	permissions.tryPermission(OpPerms.ManageOperators);
	pageBean.setTitle("List Operators");
} else if (accountType.equals("Corporate")) {
	permissions.tryPermission(OpPerms.ManageCorporate);
	pageBean.setTitle("List Corporate Accounts");
} else {
	throw new Exception("invalid parameter type");
}

String orderBy = request.getParameter("orderBy");
if (orderBy != null) {
	search.sql.addOrderBy(orderBy);
}
search.sql.addOrderBy("a.name");

search.sql.addWhere("active='Y'");
search.sql.addWhere("a.type='" + accountType + "'");

search.setPageByResult(request);
search.startsWith(request.getParameter("startsWith"));
search.setLimit(50);

List<BasicDynaBean> searchData = search.doSearch();
%>
<%@ include file="includes/header.jsp" %>
<table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td height="70" colspan="2" align="center" class="buttons"><%@ include
			file="includes/selectReport.jsp"%> <span
			class="blueHeader"><%=pageBean.getTitle() %></span></td>
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
	</tr>
	<%
	int counter = search.getStartRow();
	for (BasicDynaBean row : searchData) {
	%>
	<tr id="auditor_tr<%=row.get("id")%>" class="blueMain"
		<% if ((counter%2)==1) out.print("bgcolor=\"#FFFFFF\""); %>>
		<td align="right"><%=counter%></td>
		<td><a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>"><%=row.get("name")%></a></td>
	</tr>
	<%
		counter++;
	} // end foreach loop
	%>
</table>

<%@ include file="includes/footer.jsp" %>
