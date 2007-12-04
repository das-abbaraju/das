<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>
<%
	if (null == pBean) 
		pBean = new com.picsauditing.PICS.PermissionsBean();
	if (!pBean.checkAccess(com.picsauditing.PICS.PermissionsBean.NOT_CON,response))
		return;

	String Gutype = (String)session.getAttribute("usertype");
	String adminName = (String)session.getAttribute("username");
	boolean isAdmin = "admin".equals(Gutype);
	boolean isGeneral = "General".equalsIgnoreCase(Gutype);
	boolean isContractor = "Contractor".equalsIgnoreCase(Gutype);
	boolean isOperator = "Operator".equals(Gutype);
	boolean isAuditor = "Auditor".equals(Gutype);
%>