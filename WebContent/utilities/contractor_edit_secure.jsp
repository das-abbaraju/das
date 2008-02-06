<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>
<%	if (null == pBean)
		pBean = new com.picsauditing.PICS.PermissionsBean();

	pBean.thisPageID = request.getParameter("id"); 
	if (!pBean.checkAccess(pBean.CON_EDIT,response))
		return;
%>

