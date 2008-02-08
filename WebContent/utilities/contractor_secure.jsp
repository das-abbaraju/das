<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>
<%
pBean.thisPageID = request.getParameter("id");

if(!pBean.checkAccess(PermissionsBean.BASIC, response))
	return; // make sure people are logged in at least
if(pBean.isContractor() && !pBean.checkAccess(PermissionsBean.OP_VIEW, response))
	return;
%>