<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Not Contractor");
%>