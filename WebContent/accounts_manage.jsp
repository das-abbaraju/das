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
sql.setStartsWith(request.getParameter("startsWith"));

sql.addAudit(AuditType.PQF);
sql.addAudit(AuditType.DESKTOP);
sql.addAudit(AuditType.OFFICE);
sql.addField("ca"+AuditType.PQF+".percentVerified AS ca"+AuditType.PQF+"_percentVerified");
sql.addField("ca"+AuditType.DESKTOP+".percentVerified AS ca"+AuditType.DESKTOP+"_percentVerified");
sql.addField("ca"+AuditType.OFFICE+".percentVerified AS ca"+AuditType.OFFICE+"_percentVerified");
sql.addField("c.main_trade");
sql.addField("a.industry");
sql.addField("c.certs");

Report report = new Report();
report.setSql(sql);
report.setPageByResult(request.getParameter("showPage"));
report.setOrderBy(request.getParameter("orderBy"), "a.name");

report.addFilter(new SelectFilter("name", "a.name LIKE '%?%'", request.getParameter("name"), SearchBean.DEFAULT_NAME, SearchBean.DEFAULT_NAME));
report.addFilter(new SelectFilter("industry", "a.industry = '?'", request.getParameter("industry"), SearchBean.DEFAULT_INDUSTRY, SearchBean.DEFAULT_INDUSTRY));
//report.addFilter(new SelectFilter("trade", "c.main_trade = '?'", request.getParameter("trade"), TradesBean.DEFAULT_SELECT_TRADE, SearchBean.DEFAULT_INDUSTRY));



List<BasicDynaBean> searchData = report.getPage();

TradesBean tBean = new TradesBean();
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
<tr>
<td align="left">
<input name="name" type="text" class="forms" value="<%=report.getFilterValue("name")%>" size="8" onFocus="clearText(this)">
<%=SearchBean.getSearchIndustrySelect("industry", "forms", report.getFilterValue("industry"))%>
<%=tBean.getTradesSelect("trade", "forms", report.getFilterValue("trade"))%>
<input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0" onClick="runSearch( 'form1')" onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
</tr>
</table>
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="startsWith" value="<%=sql.getStartsWith()%>"/>
	<input type="hidden" name="orderBy"  value="<%=report.getOrderBy()%>"/>
</form>
<center><%=report.getStartsWithLinksWithDynamicForm()%></center>
<table width="657" height="40" border="0" cellpadding="0" cellspacing="0">
  <tr>
	<td align="right"><%=report.getPageLinksWithDynamicForm()%></td>
  </tr>
</table>
<table width="757" border="0" cellpadding="1" cellspacing="1">
<tr bgcolor="#993300" class="whiteTitle">
	<td colspan="2">Contractor</td>
	<td></td>
	<td>Industry</td>
	<td>Trade</td>
	<td align="center" bgcolor="#6699CC"></td>
	<td align="center" bgcolor="#6699CC">PQF</td>
	<td align="center" bgcolor="#6699CC">Desktop</td>
	<td align="center" bgcolor="#6699CC">Office</td>
	<td align="center" bgcolor="#6699CC">Insur</td>
</tr>
<%
com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
for(BasicDynaBean row: searchData) {
	%>
	<tr <%=color.nextBgColor()%> class="blueMain">
		<td align="right"><%=color.getCounter()%></td>
		<td><a href="contractor_detail.jsp?id=<%=row.get("id")%>" 
			class="blueMain"><%=row.get("name")%></a>
		</td>
		<td bgcolor="#FFFFFF" align="center">
			<a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>" class="blueMain">Edit</a>
		</td>
		<td><%=row.get("industry")%></td>
		<td><%=row.get("main_trade")%></td>
		<td><a href="ConAuditList.action?id=<%=row.get("id")%>">Audits</a></td> 
		<td align="center"><%=Utilities.getPercentCompleteLink(Utilities.getInt(row, "ca"+AuditType.PQF+"_auditID"), AuditType.PQF,Utilities.getInt(row, "ca"+AuditType.PQF+"_percentComplete"), Utilities.getInt(row, "ca"+AuditType.PQF + "_percentVerified"))%></td>
	    <td align="center"><%=Utilities.getPercentCompleteLink(Utilities.getInt(row, "ca"+AuditType.DESKTOP+"_auditID"), AuditType.DESKTOP,Utilities.getInt(row, "ca"+AuditType.DESKTOP+"_percentComplete"), Utilities.getInt(row, "ca"+AuditType.DESKTOP+"_percentVerified"))%></td>
		<td align="center"><%=Utilities.getPercentCompleteLink(Utilities.getInt(row, "ca"+AuditType.OFFICE+"_auditID"), AuditType.OFFICE,Utilities.getInt(row, "ca"+AuditType.OFFICE+"_percentComplete"), Utilities.getInt(row, "ca"+AuditType.OFFICE+"_percentVerified"))%></td>
		<td align="center"><%=Utilities.getInt(row, "certs") > 0 ? "<a href=\"contractor_upload_certificates.jsp?id="+row.get("id")+"\">"+
				"<img src=\"images/icon_insurance.gif\" width=\"20\" height=\"20\" border=\"0\"></a>" : ""%></td>
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
