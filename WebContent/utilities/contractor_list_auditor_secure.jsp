<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>

<%	if (null == pBean) 
		pBean = new com.picsauditing.PICS.PermissionsBean();

	String Gutype = (String)session.getAttribute("usertype"); 
	String Guid = (String)session.getAttribute("userid"); 
	String 	req_uid = request.getParameter("id"); 
	boolean isAdmin = "admin".equals(Gutype);
	boolean isGeneral = "General".equalsIgnoreCase(Gutype);
	boolean isContractor = "Contractor".equalsIgnoreCase(Gutype);
	boolean isOperator = "Operator".equals(Gutype);
	boolean isAuditor = "Auditor".equals(Gutype);
	
	if (pBean.isContractor()) {
		response.sendRedirect("contractor_detail.jsp?id=" + Guid);
		return;
	}//if
	if (!pBean.checkAccess(pBean.NOT_CON,response))
		return;

/*	if (!(isOperator || isAdmin || isGeneral || isAuditor)) {
		response.sendRedirect("/logout.jsp");
		return;
	}//if
*/	if (pBean.isAdmin()) {
		response.sendRedirect("accounts_manage.jsp");
		return;
	}//if	
%>

