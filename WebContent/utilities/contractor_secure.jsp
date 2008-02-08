<%
if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Not Contractor");
if(!pBean.checkAccess(com.picsauditing.PICS.PermissionsBean.OP_VIEW, response))
	return;
%>