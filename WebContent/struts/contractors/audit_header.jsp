<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="com.picsauditing.access.OpPerms"%>
<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<style>
td.label {
	text-align: right;
	padding-left: 6px;
	padding-right: 4px;
	font-weight: bold;
	background-color: #BBBBBB;
}
</style>
<h1><s:property value="conAudit.contractorAccount.name" /> <span
	class="sub"><s:property value="conAudit.auditType.auditName" />
- <s:date name="conAudit.effectiveDate" format="MMM yyyy" /></span></h1>

<%
	String id = request.getParameter("id");
	cBean.setFromDB(id);
%>
<%@ include file="/utilities/adminOperatorContractorNav.jsp"%>

<table>
	<tr>
		<td style="vertical-align: top">
		<table class="forms">
			<tr>
				<th>Status:</th>
				<td><s:property value="conAudit.auditStatus" /></td>
			</tr>
			<s:if test="conAudit.auditType.hasAuditor">
				<tr>
					<th>Auditor:</th>
					<td><s:property value="conAudit.auditor.name"
						default="Not Assigned" /></td>
				</tr>
			</s:if>
			<tr>
				<th>Created:</th>
				<td><s:date name="conAudit.createdDate" format="MMM d, yyyy" /></td>
			</tr>
			<s:if test="conAudit.auditType.hasRequirements">
				<tr>
					<th>Verified:</th>
					<td><s:if test="conAudit.closedDate"><s:date name="conAudit.closedDate" format="MMM d, yyyy" />
					</s:if><s:else><s:property value="conAudit.percentVerified" /></s:else></td>
				</tr>
			</s:if>
		</table>
		</td>
		<td style="vertical-align: top">
		<table class="forms">
			<s:if test="conAudit.requestingOpAccount">
				<tr>
					<th>For:</th>
					<td><s:property value="conAudit.requestingOpAccount.name" /></td>
				</tr>
			</s:if>
			<s:if test="conAudit.auditType.showManual">
				<tr>
					<th>Safety Manual:</th>
					<td><s:if test="hasSafetyManual">
						<a href="servlet/showpdf?id=">Uploaded</a>
					</s:if><s:else>Not Uploaded</s:else></td>
				</tr>
			</s:if>
			<s:if test="conAudit.auditType.scheduled">
				<tr>
					<th>Scheduled:</th>
					<td><s:date name="conAudit.scheduledDate" format="MMM d, yyyy" /><s:property
						value="conAudit.auditLocation" /></td>
				</tr>
			</s:if>
			<tr>
				<th>Submitted:</th>
				<td><s:date name="conAudit.completedDate" format="MMM d, yyyy" /></td>
			</tr>
			<tr>
				<th>Expires:</th>
				<td><s:date name="conAudit.expiresDate" format="MMM d, yyyy" /></td>
			</tr>
		</table>
		</td>
	</tr>
</table>

<div>
<s:if test="conAudit.auditType.auditTypeID == AuditType.PQF">
	if (permissions.hasPermission(OpPerms.AuditVerification)
	<a href="VerifyView.action?auditID=<s:property value="auditID" />">Verify PQF</a> |
</s:if>
<a href="pqf_viewAll.jsp?auditID=<s:property value="auditID" />">View All</a> |
<a href="pqf_printAll.jsp?auditID=<s:property value="auditID" />">Print</a>
</div>
