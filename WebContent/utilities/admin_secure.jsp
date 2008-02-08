<%@page import="com.picsauditing.access.NoRightsException"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
	if (!permissions.isAdmin()) throw new NoRightsException("Admin");
%>