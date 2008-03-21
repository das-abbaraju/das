<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%
	SelectSQL sql = new SelectSQL("useraccess");

	sql.addGroupBy("accessType");
	sql.addField("accessType");
	sql.addField("count(*) as total");

	Report report = new Report();
	report.setSql(sql);
	report.setOrderBy(request.getParameter("orderBy"), "accessType");
	report.setPageByResult(request);

	List<BasicDynaBean> searchData = report.getPage();
%>
<html>
<head>
<title>Permission Types</title>
</head>
<body>
<h2 class="blueHeader">Permission Types</h2>
<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td height="30" align="left"><%=report.getStartsWithLinks()%></td>
		<td align="right"><%=report.getPageLinks()%></td>
	</tr>
</table>
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle">
		<td width="150" colspan="2">Permission</td>
		<td align="center">Use Count</td>
	</tr>
	<%
		com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
		for (BasicDynaBean row : searchData) {
	%>
	<tr class="blueMain" <%=color.nextBgColor()%>>
		<td align="right"><%=color.getCounter()%></td>
		<td><a href="#" onclick="return false;"><%=row.get("accessType")%></a></td>
		<td><%=row.get("total")%></td>
	</tr>
	<%
		}
	%>
</table>
<p align="center"><%=report.getPageLinks()%></p>
</body>
</html>