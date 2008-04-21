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
<h1>Reports</h1>
<h3>Click Report Name to View</h3>
<ol>
	<%
	for(com.picsauditing.access.MenuItem item : reportMenu.getValidItems()) {
		%>
		<li><a href="<%=item.getUrl()%>"><%=item.getPrompt()%></a></li>
		<%
	}
	%>
</ol>
</body>
</html>