<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.List"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<%
if (!permissions.isAdmin()) throw new com.picsauditing.access.NoRightsException("Admin");

SelectAccount sql = new SelectAccount();

sql.setType(SelectAccount.Type.Contractor);
sql.startsWith(request.getParameter("startsWith"));

sql.addAudit(AuditType.PQF);
sql.addAudit(AuditType.DESKTOP);
sql.addAudit(AuditType.OFFICE);

sql.addField("c.main_trade");
sql.addField("a.industry");

Report report = new Report();
report.setSql(sql);
report.setPageByResult(request.getParameter("showPage"));
report.setOrderBy(request.getParameter("orderBy"), "a.name DESC");

List<BasicDynaBean> searchData = report.getPage();

//com.picsauditing.PICS.pqf.QuestionTypeList statesLicensedInList = new com.picsauditing.PICS.pqf.QuestionTypeList();
//tBean.setFromDB();
%>
<html>
<head>
<title>Accounts Manage</title>
<meta name="header_gif" content="header_manageAccounts.gif" />
<script language="JavaScript" SRC="js/Search.js"></script>
</head>
<body>
<%@ include file="includes/selectReport.jsp"%>
<form id="form1" name="form1" method="post" action="accounts_manage.jsp">
<table border="0" align="center" cellpadding="2" cellspacing="0">
</table>
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="startsWith" value="<%=sBean.selected_startsWith == null ? "" : sBean.selected_startsWith %>"/>
	<input type="hidden" name="orderBy"  value="<%=sBean.orderBy == null ? "" : sBean.orderBy %>"/>
</form>
<center><%=report.getStartsWithLinksWithDynamicForm()%></center>
<table width="657" height="40" border="0" cellpadding="0" cellspacing="0">
  <tr>
	<td align="right"><%=report.getPageLinksWithDynamicForm()%></td>
  </tr>
</table>
<table width="757" border="0" cellpadding="1" cellspacing="1">
<tr bgcolor="#003366" class="whiteTitle">
	<td height="25" colspan="2" align="center" bgcolor="#993300"></td>
	<td colspan="2">Contractor</td>
	<td>Industry</td>
	<td>Trade</td>
	<td align="center" bgcolor="#336699">PQF</td>
	<td align="center" bgcolor="#993300">Desktop</td>
	<td align="center" bgcolor="#6699CC">Office</td>
	<td align="center" bgcolor="#993300">Insur</td>
</tr>
<%
com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
for(BasicDynaBean row: searchData) {
	%>
	<tr <%=color.nextBgColor()%> class="blueMain">
		<td bgcolor="#FFFFFF" align="center">
			<a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>" class="blueMain">Edit</a>
		</td>
		<td align="right"><%=color.getCounter()%></td>
		<td><a href="contractor_detail.jsp?id=<%=row.get("id")%>" 
			class="blueMain"><%=row.get("name")%></a>
		</td>
		<td><%=row.get("industry")%></td>
		<td><%=row.get("main_trade")%></td>
		<td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.PQF_TYPE)%></td>
		<td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE)%></td>
		<td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE)%></td>
		<td align="center"><%=sBean.getCertsAdminLink()%></td>
	</tr>
	<%
}
%>
</table>
<center>
<%=report.getPageLinksWithDynamicForm()%>
<br><br>
<%@ include file="utilities/contractor_key.jsp"%>
</center>
</body>
</html>
