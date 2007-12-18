<%@ page language="java" import="com.picsauditing.PICS.*" %>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>

<%	if (null == pBean)
		pBean = new PermissionsBean();
	pBean.thisPageID = request.getParameter("id");
	
	if(!pBean.checkAccess(PermissionsBean.BASIC, response))
		return; // make sure people are logged in at least
	if(pBean.isContractor() && !pBean.checkAccess(PermissionsBean.OP_VIEW, response))
		return;

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
%>