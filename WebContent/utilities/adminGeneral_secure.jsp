<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
if (permissions.isContractor()) return;
%>