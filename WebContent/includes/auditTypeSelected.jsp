<%	permissions.tryPermission(OpPerms.ManageAudits);
	String thisPageAuditType = request.getParameter("auditType");
	if (null != thisPageAuditType)
		session.setAttribute("auditType", thisPageAuditType);
	String auditType= (String)session.getAttribute("auditType");
	if (null == auditType) {
		response.sendRedirect("audit_selectType.jsp?from="+request.getServletPath());
		return;
	}//if
%>