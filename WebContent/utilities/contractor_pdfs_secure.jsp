<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>
<%
if (null == pBean) 
	pBean = new com.picsauditing.PICS.PermissionsBean();
if (!pBean.checkAccess(pBean.PQF_VIEW,response))
	return;
%>