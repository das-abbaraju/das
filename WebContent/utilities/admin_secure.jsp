<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
	if (!permissions.isAdmin()) throw new com.picsauditing.access.NoRightsException("Admin");
%>