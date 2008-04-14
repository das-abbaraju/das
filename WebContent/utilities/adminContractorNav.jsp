<%@page import="com.picsauditing.jpa.entities.ContractorAudit"%>
<%
String thisPage = "";
if (!request.getServletPath().contains("contractor_detail")){
	com.picsauditing.PICS.AccountBean acctBean = new com.picsauditing.PICS.AccountBean();
	%>
	<h2 class="blueHeader" style="text-align: center"><%=acctBean.getName(id)%></h2>
	<%
}
%>
<div class="blueMain" style="text-align: center">
<a class="blueMain" href="contractor_detail.jsp?id=<%=id%>">Contractor Details</a> |
<% if (permissions.hasPermission(OpPerms.InsuranceCerts)) { %>
	<a class="blueMain" href="contractor_upload_certificates.jsp?id=<%=id%>">Insurance Certificates</a> |
<% } %>
<% if (permissions.isAuditor() && !permissions.isAdmin()) { %>
	<a class="blueMain" href="contractor_list_auditor.jsp">Return to Contractors List</a>
<% } else { %>
	<a class="blueMain" href="accounts_edit_contractor.jsp?id=<%=id%>">Edit Account</a> |
<% } %>
<% if (permissions.isOperator()) { %>
	<a class="blueMain" href="con_redFlags.jsp?id=<%=id%>">Red Flag Report</a>
<% } %>
<% if (permissions.isCorporate() || permissions.isAdmin()) { %>
	<a class="blueMain" href="con_selectFacilities.jsp?id=<%=id%>">View Facilities</a>
<% } %>

<br/>
<%
for(int key : cBean.getAudits().keySet()) {
	ContractorAudit pqf = cBean.getAudits().get(key);

	if (permissions.isAdmin()
		|| pqf.getAuditor().getId() == permissions.getUserId()
		|| permissions.canSeeAudit(pqf.getAuditType().getAuditTypeID()) 
		) {
		%><strong><%=pqf.getAuditType().getAuditName() %></strong>:
		<%
		if (permissions.isPicsEmployee()) {
			%>
			<a class="blueMain" href="pqf_editMain.jsp?auditID=<%=pqf.getId()%>">Edit</a> |
			<a class="blueMain" href="pqf_verify.jsp?auditID=<%=pqf.getId()%>">Verify</a> |
			<%
		}
		%>
		<a class="blueMain" href="pqf_view.jsp?auditID=<%=pqf.getId()%>">View</a> |
		<a class="blueMain" href="pqf_viewAll.jsp?auditID=<%=pqf.getId()%>">View All</a> |
		<a class="blueMain" href="pqf_printAll.jsp?auditID=<%=pqf.getId()%>">Print</a>
		<br/>
		<%
	}
}

String tempAuditType = com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE;
if (pBean.canVerifyAudit(tempAuditType, id) && !cBean.isAuditClosed()) {
	%>
	<strong>Office</strong>:
	<a class="blueMain" href="audit_edit.jsp?id=<%=id%>">Edit</a>
	<%
	if (cBean.isAuditCompleted()) { %>
		| <a class="blueMain" href="audit_view.jsp?id=<%=id%>">View</a> |
		<a class="blueMain" href="audit_editRequirements.jsp?id=<%=id%>">Edit RQs</a> |
		<a class="blueMain" href="audit_viewRequirements.jsp?id=<%=id%>">View RQs</a>
	<%
	}
}
%>

</div>

