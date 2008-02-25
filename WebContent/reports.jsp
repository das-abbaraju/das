<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>

<%
String report = request.getParameter("report");
if (null != report && !"-- Select a report --".equals(report)){
	response.sendRedirect(report);
	return;
}
Menu reportMenu = new Menu();
reportMenu.fillPicsMenu(permissions);
pageBean.setTitle("Reports");
%>
<%@ include file="includes/header.jsp" %>
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
<%@ include file="includes/footer.jsp" %>
