<%@page import="com.picsauditing.jpa.entities.ContractorAudit"%>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ContractorView.action?id=<%=id%>">Details</a></li>
	<%
		if (permissions.isAuditor() && !permissions.isAdmin()) {
	%>
	<a class="blueMain" href="contractor_list_auditor.jsp">Return to
	Contractors List</a>
	<%
		} else {
	%>
	<li><a href="accounts_edit_contractor.jsp?id=<%=id%>" 
		<%= request.getRequestURI().contains("accounts_edit_contractor") ? "class=\"current\"" : ""%>>Edit</a></li>
	<%
		}
	%>
	<%
		if (permissions.hasPermission(OpPerms.InsuranceCerts)) {
	%>
	<li><a href="contractor_upload_certificates.jsp?id=<%=id%>"
		<%= request.getRequestURI().contains("contractor_upload_certificates") ? "class=\"current\"" : ""%>>InsureGuard</a></li>
	<%
		}
	%>
	<%
		if (permissions.isOperator()) {
	%>
	<li><a href="con_redFlags.jsp?id=<%=id%>"
	>Flag Status</a></li>
	<%
		}
	%>
	<%
		if (permissions.isCorporate() || permissions.isAdmin()) {
	%>
	<li><a href="con_selectFacilities.jsp?id=<%=id%>"
		<%= request.getRequestURI().contains("con_selectFacilities") ? "class=\"current\"" : ""%>>Facilities</a></li>
	<%
		}
	%>
	<li><a href="ConAuditList.action?id=<%=id%>">Audits</a></li>
	<%
		for (ContractorAudit pqf : cBean.getAudits()) {
			if (permissions.isAdmin()
					|| pqf.getAuditor().getId() == permissions.getUserId()
					|| permissions.canSeeAudit(pqf.getAuditType()
							.getAuditTypeID())) {
				boolean selected = false;
				if (request.getRequestURI().contains("pqf")) {
					String rAuditID = request.getParameter("auditID");
					selected = pqf.getId() == Integer.parseInt(rAuditID);
				}
	%><li><a <%= selected ? "class=\"current\"" : "" %>href="pqf_view.jsp?auditID=<%=pqf.getId()%>"><%=pqf.getAuditType().getAuditName()%></a></li>
	<%
		}
		}
	%>
</ul>
</div>
