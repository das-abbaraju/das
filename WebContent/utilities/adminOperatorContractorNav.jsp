<%@page import="com.picsauditing.jpa.entities.ContractorAudit"%>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ContractorView.action?id=<%=id%>" class="current">Details</a></li>
	<%
		if (permissions.isAuditor() && !permissions.isAdmin()) {
	%>
	<a class="blueMain" href="contractor_list_auditor.jsp">Return to
	Contractors List</a>
	<%
		} else {
	%>
	<li><a href="accounts_edit_contractor.jsp?id=<%=id%>">Edit</a></li>
	<%
		}
	%>
	<%
		if (permissions.hasPermission(OpPerms.InsuranceCerts)) {
	%>
	<li><a href="contractor_upload_certificates.jsp?id=<%=id%>">InsureGuard</a></li>
	<%
		}
	%>
	<%
		if (permissions.isOperator()) {
	%>
	<li><a href="con_redFlags.jsp?id=<%=id%>">Flag Status</a></li>
	<%
		}
	%>
	<%
		if (permissions.isCorporate() || permissions.isAdmin()) {
	%>
	<li><a href="con_selectFacilities.jsp?id=<%=id%>">Facilities</a></li>
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
	%><a class="blueMain" href="pqf_view.jsp?auditID=<%=pqf.getId()%>"><%=pqf.getAuditType().getAuditName()%></a>
	<%
		}
		}
	%>
</ul>
</div>
