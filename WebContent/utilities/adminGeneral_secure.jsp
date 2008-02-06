<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>
<%
if (null == pBean) 
	pBean = new com.picsauditing.PICS.PermissionsBean();
if (!pBean.checkAccess(com.picsauditing.PICS.PermissionsBean.NOT_CON,response))
	return;
%>