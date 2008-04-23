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
<a class="blueMain" href="ContractorView.action?id=<%=id%>">Contractor Details</a> |
<% if (permissions.hasPermission(OpPerms.InsuranceCerts)) { %>
	<a class="blueMain" href="contractor_upload_certificates.jsp?id=<%=id%>">InsureGuard</a> |
<% } %>
<% if (permissions.isAuditor() && !permissions.isAdmin()) { %>
	<a class="blueMain" href="contractor_list_auditor.jsp">Return to Contractors List</a>
<% } else { %>
	<a class="blueMain" href="accounts_edit_contractor.jsp?id=<%=id%>">Edit</a> |
<% } %>
<% if (permissions.isOperator()) { %>
	<a class="blueMain" href="con_redFlags.jsp?id=<%=id%>">Red Flag Report</a>
<% } %>
<% if (permissions.isCorporate() || permissions.isAdmin()) { %>
	<a class="blueMain" href="con_selectFacilities.jsp?id=<%=id%>">Facilities</a>
<% } %>

<br/>
<a href="ConAuditList.action?id=<%=id%>">Audits</a>
<%
for(ContractorAudit pqf: cBean.getAudits()) {
	if (permissions.isAdmin() || 
		pqf.getAuditor().getId() == permissions.getUserId() ||
		permissions.canSeeAudit(pqf.getAuditType().getAuditTypeID())
		) {
		%>| <a class="blueMain" href="pqf_view.jsp?auditID=<%=pqf.getId()%>"><%=pqf.getAuditType().getAuditName() %></a>
		<%
	}
}
%>

</div>

