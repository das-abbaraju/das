<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
if (!permissions.isLoggedIn()) {
	%><a href="login.jsp" style="color: red; font-weight: bold;">You must log in again</a><%
	return;
}
%>