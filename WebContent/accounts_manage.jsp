<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.List"%>
<%@page import="com.picsauditing.search.*"%>
`<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<jsp:useBean id="questionList" class="com.picsauditing.PICS.pqf.QuestionTypeList" scope="page"/>

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
String performedBy = request.getParameter("performedBy");
String answerFilter = "";
if (TradesBean.DEFAULT_PERFORMED_BY.equals(performedBy) || performedBy == null) {
	performedBy = TradesBean.DEFAULT_PERFORMED_BY;
	answerFilter = "_%";
} else {
	if ("Sub Contracted".equals(performedBy))
		answerFilter = "%S";
	else if	("Self Performed".equals(performedBy))
		answerFilter = "C%";
}
String tradeWhere = "a.id IN (SELECT conID FROM pqfdata WHERE questionID=? AND answer LIKE '"+answerFilter+"')";
report.addFilter(new SelectFilter("trade", tradeWhere, request.getParameter("trade"), TradesBean.DEFAULT_SELECT_TRADE_ID, TradesBean.DEFAULT_SELECT_TRADE_ID));
report.addFilter(new SelectFilterInteger("generalContractorID", "a.id IN (SELECT subID FROM generalcontractors WHERE genID = ? )", request.getParameter("generalContractorID"), SearchBean.DEFAULT_GENERAL_VALUE, SearchBean.DEFAULT_GENERAL_VALUE));
report.addFilter(new SelectFilter("city", "a.city LIKE '%?%'", request.getParameter("city"), SearchBean.DEFAULT_CITY, SearchBean.DEFAULT_CITY));
report.addFilter(new SelectFilter("state", "a.state = '?'", request.getParameter("state"), "", ""));
report.addFilter(new SelectFilter("zip", "a.zip LIKE '%?%'", request.getParameter("zip"), SearchBean.DEFAULT_ZIP, SearchBean.DEFAULT_ZIP));
report.addFilter(new SelectFilter("certsOnly", "c.isOnlyCerts = '?'", request.getParameter("certsOnly"), "", ""));
report.addFilter(new SelectFilter("visible", "a.active = '?'", request.getParameter("visible"), SearchBean.DEFAULT_VISIBLE,SearchBean.DEFAULT_VISIBLE));

report.addFilter(new SelectFilter("stateLicensedIn", "a.id IN (SELECT conID FROM pqfdata WHERE questionID=? AND answer <> '')", request.getParameter("stateLicensedIn"), QuestionTypeList.DEFAULT_SELECT_QUESTION_ID, QuestionTypeList.DEFAULT_SELECT_QUESTION_ID));
report.addFilter(new SelectFilter("worksIn", "a.id IN (SELECT conID FROM pqfdata WHERE questionID=? AND answer LIKE 'Yes%')", request.getParameter("worksIn"), QuestionTypeList.DEFAULT_SELECT_QUESTION_ID, QuestionTypeList.DEFAULT_SELECT_QUESTION_ID));
report.addFilter(new SelectFilter("taxID", "c.taxID = '?'", request.getParameter("taxID"), SearchBean.DEFAULT_TAX_ID, SearchBean.DEFAULT_TAX_ID));

List<BasicDynaBean> searchData = report.getPage();

TradesBean tBean = new TradesBean();

//tBean.setFromDB();
%>
<%@page import="com.picsauditing.PICS.pqf.QuestionTypeList"%>
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
<%=Inputs.inputSelect("performedBy", "forms", performedBy, TradesBean.PERFORMED_BY_ARRAY)%>
<input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0" onClick="runSearch( 'form1')" onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
</td></tr>
<tr><td>
<%=SearchBean.getSearchGeneralSelect("generalContractorID", "forms", report.getFilterValue("generalContractorID"))%>
<input name="city" type="text" class="forms" value="<%=report.getFilterValue("city")%>" size="15" onFocus="clearText(this)">
<%=SearchBean.getStateSelect("state","forms", report.getFilterValue("state"))%>
<input name="zip" type="text" class="forms" value="<%=report.getFilterValue("zip")%>" size="5" onFocus="clearText(this)">
</td></tr>
<tr><td>
<%=Inputs.inputSelect2("certsOnly","forms",report.getFilterValue("certsOnly"), new String[] {"","- Default Certs -","Yes", "Only Certs","No","Exclude Certs"})%>
<%=Inputs.inputSelect("visible", "forms", report.getFilterValue("visible"), SearchBean.VISIBLE_SEARCH_ARRAY)%>
<%=questionList.getQuestionListQIDSelect("License","stateLicensedIn","forms", report.getFilterValue("stateLicensedIn"),SearchBean.DEFAULT_LICENSED_IN)%>
<input name="taxID" type="text" class="forms" value="<%=report.getFilterValue("taxID")%>" size="9" onFocus="clearText(this)"><span class=redMain>*must be 9 digits</span>
</td></tr>
<tr><td>
<%=questionList.getQuestionListQIDSelect("Office Location", "worksIn", "forms", report.getFilterValue("worksIn"),SearchBean.DEFAULT_WORKS_IN)%>
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
