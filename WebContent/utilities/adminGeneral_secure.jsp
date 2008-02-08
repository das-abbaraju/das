<%
if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Not Contractor");
%>