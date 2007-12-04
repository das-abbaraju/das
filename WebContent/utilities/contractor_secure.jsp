<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>

<%	if (null == pBean)
		pBean = new com.picsauditing.PICS.PermissionsBean();
	pBean.thisPageID = request.getParameter("id"); 
	
	String userID = (String)session.getAttribute("userid");
	String utype = (String)session.getAttribute("usertype");
	String uid = (String)session.getAttribute("userid");
	String req_uid = request.getParameter("id");
	String adminName = (String)session.getAttribute("username");
	java.util.HashSet canSeeSet = pBean.canSeeSet;
	java.util.HashSet auditorCanSeeSet = pBean.auditorCanSeeSet;
	boolean isAdmin = "admin".equals(utype);
	boolean isGeneral = "General".equalsIgnoreCase(utype);
	boolean isContractor = "Contractor".equalsIgnoreCase(utype);
	boolean isOperator = "Operator".equals(utype);
	boolean isAuditor = "Auditor".equals(utype);
	
	if(pBean.isContractor() && !pBean.checkAccess(com.picsauditing.PICS.PermissionsBean.OP_VIEW, response))
		return;


/*	if (null == uid) {
		response.sendRedirect("/logout.jsp");
		return;
	}//if
	if (!(isAdmin ||
			canSeeSet.contains(req_uid) || auditorCanSeeSet.contains(req_uid)
			)) {
		response.sendRedirect("/logout.jsp");
		return;
	}//if
*/
%>