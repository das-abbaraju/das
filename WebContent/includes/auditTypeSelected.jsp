<%	permissions.tryPermission(OpPerms.ManageAudits);
	String auditTypeID = request.getParameter("auditTypeID");
	if (null == auditTypeID) {
		response.sendRedirect("AuditTypeChoose.action");
		return;
	}
%>