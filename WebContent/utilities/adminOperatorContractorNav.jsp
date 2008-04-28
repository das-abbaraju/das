<%@page import="com.picsauditing.jpa.entities.ContractorAudit"%>
<%@page import="com.picsauditing.jpa.entities.AuditStatus"%>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ContractorView.action?id=<%=id%>">Details</a></li>
	<%
		if (permissions.isAdmin()) {
	%>
	<li><a href="accounts_edit_contractor.jsp?id=<%=id%>" 
		<%= request.getRequestURI().contains("accounts_edit_contractor") ? "class=\"current\"" : ""%>>Edit</a></li>
	<%
		} 
	%>	
	<%
		if (permissions.isContractor()) {
	%>
	<li><a href="contractor_edit.jsp?id=<%=id%>"
	<%= request.getRequestURI().contains("contractor_edit") ? "class=\"current\"" : ""%>>Edit</a></li>
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
	<%=request.getRequestURI().contains("con_redFlags") ? "class=\"current\"" : ""%>>Flag Status</a>
	<%
		} else {
	%>
	<a href="con_selectFacilities.jsp?id=<%=id%>"
		<%= request.getRequestURI().contains("con_selectFacilities") ? "class=\"current\"" : ""%>>Facilities</a></li>
	<%
		}
	%>
	<%
		if (permissions.isContractor()) {
	%>
	<li><a href="con_viewForms.jsp?id=<%=id%>"
	<%=request.getRequestURI().contains("con_viewForms") ? "class=\"current\"" : ""%>>Forms & Docs</a></li>
	<%
		}
	%>
	<li><a href="ConAuditList.action?id=<%=id%>">Audits</a></li>
	<%
		for (ContractorAudit pqf : cBean.getAudits()) {
			if (!pqf.getAuditStatus().equals(AuditStatus.Expired)) {
				if (permissions.isPicsEmployee() || permissions.isContractor()
					|| permissions.canSeeAudit(pqf.getAuditType().getAuditTypeID())) {
					boolean selected = false;
					if (request.getRequestURI().contains("pqf")) {
						String rAuditID = request.getParameter("auditID");
						selected = pqf.getId() == Integer.parseInt(rAuditID);
					}
	%><li><a <%= selected ? "class=\"current\"" : "" %>href="pqf_view.jsp?auditID=<%=pqf.getId()%>"><%=pqf.getAuditType().getAuditName()%></a></li>
	<%
				}
			}
		}
	%>
</ul>
</div>
