<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>
<%
	if (null == pBean) 
		pBean = new com.picsauditing.PICS.PermissionsBean();
	if (!pBean.checkAccess(com.picsauditing.PICS.PermissionsBean.NOT_CON, response))
		return;

	String Gutype = (String)session.getAttribute("usertype");
	String Guid = (String)session.getAttribute("userid");

	boolean isContractor = "Contractor".equalsIgnoreCase(Gutype);
	if (pBean.isContractor()) {
		response.sendRedirect("contractor_detail.jsp?id=" + Guid);
		return;
	}//if

	boolean isAdmin = "admin".equals(Gutype);
	boolean isGeneral = "General".equalsIgnoreCase(Gutype);
	boolean isOperator = "Operator".equals(Gutype);
%>