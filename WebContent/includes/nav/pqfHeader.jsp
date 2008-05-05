<%@page import="com.picsauditing.jpa.entities.ContractorAudit"%>
<%@page import="com.picsauditing.jpa.entities.User"%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<%@page import="com.picsauditing.access.OpPerms"%>
<%@page import="com.picsauditing.PICS.Utilities"%>
<%@page import="com.picsauditing.PICS.DateBean"%>

<%
	ContractorAudit conAudit = action.getAudit();
	User auditor = conAudit.getAuditor();
	String id = conAudit.getContractorAccount().getId().toString();
%>
<style>
td.label {
	text-align: right;
	padding-left: 6px;
	padding-right: 4px;
	font-weight: bold;
	background-color: #BBBBBB;
}
</style>
<%@ include file="/utilities/adminOperatorContractorNav.jsp"%>
<table border="0" cellspacing="0" cellpadding="1">
	<tr valign="top">
		<td align="right">
		<table border="0" cellspacing="2">
			<tr class="blueMain">
				<td class="label">Status:</td>
				<td><%=conAudit.getAuditStatus()%></td>
			</tr>
			<%
				if (conAudit.getAuditType().isHasAuditor()) {
			%>
			<tr class="blueMain">
				<td class="label">Auditor:</td>
				<td><%=(conAudit.getAuditor() == null) ? "Not Assigned"
						: conAudit.getAuditor().getName()%></td>
			</tr>
			<%
				}
			%>
			<tr class="blueMain">
				<td class="label">Created:</td>
				<td><%=DateBean.toShowFormat(conAudit.getCreatedDate())%></td>
			</tr>
			<%
				if (conAudit.getAuditType().isHasRequirements()) {
			%>
			<tr class="blueMain">
				<td class="label">Closed:</td>
				<td><%=DateBean.toShowFormat(conAudit.getCompletedDate())%> <%=conAudit.getPercentVerified()%>%</td>
			</tr>
			<%
				}
			%>
		</table>
		</td>
		<td>
		<table border="0" cellspacing="2">
			<%
				if (conAudit.getRequestingOpAccount() != null) {
			%>
			<tr class="blueMain">
				<td class="label">For:</td>
				<td><%=conAudit.getRequestingOpAccount().getName()%></td>
			</tr>
			<%
				}
			%>
			<%
				if (conAudit.getAuditType().isScheduled()) {
			%>
			<tr class="blueMain">
				<td class="label">Scheduled:</td>
				<td><%=DateBean.toShowFormat(conAudit.getScheduledDate())%> <%=conAudit.getAuditLocation()%></td>
			</tr>
			<%
				}
			%>
			<tr class="blueMain">
				<td class="label">Submitted:</td>
				<td><%=DateBean.toShowFormat(conAudit.getCompletedDate())%> <%=conAudit.getPercentComplete()%>%</td>
			</tr>
			<tr class="blueMain">
				<td class="label">Expires:</td>
				<td><%=DateBean.toShowFormat(conAudit.getExpiresDate())%></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<%
	if (conAudit.isCanView(permissions)) {
%>
<a class="blueMain" href="pqf_view.jsp?auditID=<%=conAudit.getId()%>">View</a>
|
<%
	}
	if(conAudit.isCanEdit(permissions)) {
%>
<a class="blueMain"
	href="pqf_editMain.jsp?auditID=<%=conAudit.getId()%>">Edit</a>
|
<%
	} if(conAudit.isCanVerify(permissions)) {
%>
<a class="blueMain" href="pqf_verify.jsp?auditID=<%=conAudit.getId()%>">Verify</a>
|
<%
	}
	if (permissions.hasPermission(OpPerms.AuditVerification)
			&& conAudit.getAuditType().getAuditTypeID() == AuditType.PQF) {
%>
<a class="blueMain"
	href="VerifyView.action?auditID=<%=conAudit.getId()%>">Verify PQF</a>
|
<%
	}
	if (conAudit.isCanView(permissions)) {
%>
<a class="blueMain" href="pqf_viewAll.jsp?auditID=<%=conAudit.getId()%>">View
All</a>
|
<%
	}
%>
<a class="blueMain"
	href="pqf_printAll.jsp?auditID=<%=conAudit.getId()%>">Print</a>
</ul>
