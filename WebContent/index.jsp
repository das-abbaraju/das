<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
if (permissions.isLoggedIn())
	response.sendRedirect("Home.action");
else
	response.sendRedirect("Login.action");
%>