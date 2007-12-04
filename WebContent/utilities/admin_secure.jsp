<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>

<%
	String adminName = (String)session.getAttribute("username");
	if (null == pBean) 
		pBean = new com.picsauditing.PICS.PermissionsBean();
	if (!pBean.checkAccess(pBean.FULL,response))
		return;
%>

