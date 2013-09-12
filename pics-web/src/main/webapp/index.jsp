<%@page import="com.picsauditing.access.UserIndexPage"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
	response.sendRedirect(UserIndexPage.getIndexURL(permissions));
%>