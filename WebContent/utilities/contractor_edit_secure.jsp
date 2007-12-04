<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>

<%	if (null == pBean) 
		pBean = new com.picsauditing.PICS.PermissionsBean();

	String utype = (String)session.getAttribute("usertype"); 
	String uid = (String)session.getAttribute("userid");
	String req_uid = request.getParameter("id"); 
	String adminName = (String)session.getAttribute("username");
	boolean isAdmin = "admin".equals(utype);
	boolean isGeneral = "General".equalsIgnoreCase(utype);
	boolean isContractor = "Contractor".equalsIgnoreCase(utype);
	boolean isOperator = "Operator".equals(utype);
	boolean isAuditor = "Auditor".equals(utype);
	java.util.HashSet canSeeSet = (java.util.HashSet)session.getAttribute("canSeeSet");

	pBean.thisPageID = request.getParameter("id"); 
	if (!pBean.checkAccess(pBean.CON_EDIT,response))
		return;
%>

