<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%
if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Not Contractor");

String report = request.getParameter("report");
if (null != report && !"-- Select a report --".equals(report)){
	response.sendRedirect(report);
	return;
}
Menu reportMenu = new Menu();
reportMenu.fillPicsMenu(permissions);
%>
<html>
<head>
<title>Reports</title>
</head>
<body>
<table border="0" cellpadding="1" cellspacing="1">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan="2">Click Report Name to Access</td>
	</tr>
	<%
	int rowCount = 0;
	for(com.picsauditing.access.MenuItem item : reportMenu.getValidItems()) {
		rowCount++;
		%>
		<tr class="blueMain" <%=Utilities.getBGColor(rowCount)%>>
			<td align="right"><%=rowCount%>.</td>
			<td><a href="<%=item.getUrl()%>"><%=item.getPrompt()%></a></td>
		</tr>
		<%
	}
	%>
</table>
</body>
</html>