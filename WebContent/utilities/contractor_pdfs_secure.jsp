<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>

<%	if (null == pBean) 
		pBean = new com.picsauditing.PICS.PermissionsBean();

	String utype = (String)request.getSession().getAttribute("usertype"); 
	String req_uid = request.getParameter("id"); 
	java.util.HashSet canSeeSet = (java.util.HashSet)request.getSession().getAttribute("canSeeSet");
	java.util.HashSet auditorCanSeeSet = (java.util.HashSet)session.getAttribute("auditorCanSeeSet");
	boolean isAdmin = "admin".equals(utype);
	boolean isGeneral = "General".equalsIgnoreCase(utype);
	boolean isContractor = "Contractor".equalsIgnoreCase(utype);
	boolean isOperator = "Operator".equals(utype);

	if (!pBean.checkAccess(pBean.PQF_VIEW,response))
		return;

/*	if (isContractor || 
		!(isAdmin ||
		accessID.equals(com.picsauditing.PICS.AccountBean.ALL_ACCESS) ||
		canSeeSet.contains(req_uid) || auditorCanSeeSet.contains(req_uid))
		) {
		response.sendRedirect("/logout.jsp");
		return;
	}//if
*/%>