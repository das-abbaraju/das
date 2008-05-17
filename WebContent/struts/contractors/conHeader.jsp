<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h1><s:property value="contractor.name" /><span class="sub">
<s:if test="auditID > 0">
	<s:property value="conAudit.auditType.auditName" />
		- <s:date name="conAudit.effectiveDate" format="MMM yyyy" />
</s:if> <s:else>
	<s:property value="subHeading" />
</s:else></span></h1>

<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ContractorView.action?id=<s:property value="id" />"
		<s:if test="requestURI.contains('con_view')">class="current"</s:if>>Details</a></li>
	<s:if test="!permissions.contractor">
		<li><a href="add_notes.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('note')">class="current"</s:if>>Notes</a></li>
	</s:if>
	<s:if test="permissions.admin">
		<li><a
			href="accounts_edit_contractor.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('edit')">class="current"</s:if>>Edit</a></li>
	</s:if>
	<s:if test="permissions.contractor">
		<li><a href="contractor_edit.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('edit')">class="current"</s:if>>Edit</a></li>
	</s:if>
	<pics:permission perm="InsuranceCerts">
		<li><a
			href="contractor_upload_certificates.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('cert')">class="current"</s:if>>InsureGuard</a></li>
	</pics:permission>
	<s:if test="permissions.operator">
		<li><a href="ContractorFlag.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('flag')">class="current"</s:if>>Flag
		Status</a></li>
	</s:if>
	<s:else>
		<li><a
			href="con_selectFacilities.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('con_selectFacilities')">class="current"</s:if>>Facilities</a></li>
	</s:else>
	<s:if test="permissions.contractor">
		<li><a href="con_viewForms.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('con_viewForms')">class="current"</s:if>>Forms
		& Docs</a></li>
	</s:if>
	<li><a <s:if test="requestURI.contains('contractor_audits')">class="current"</s:if> href="ConAuditList.action?id=<s:property value="id" />">Audits</a></li>
	<s:iterator value="activeAudits">
		<li><a <s:if test="id == auditID">class="current"</s:if>
			href="Audit.action?auditID=<s:property value="id"/>"><s:property
			value="auditType.auditName" /></a></li>
	</s:iterator>
</ul>
</div>

<s:if test="auditID > 0">
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
						<td><s:if test="conAudit.closedDate">
							<s:date name="conAudit.closedDate" format="MMM d, yyyy" />
						</s:if><s:else>
							<s:property value="conAudit.percentVerified" />
						</s:else></td>
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
						<td><s:date name="conAudit.scheduledDate"
							format="MMM d, yyyy" /><s:property
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

	<div><a
		href="pqf_viewAll.jsp?auditID=<s:property value="auditID" />">View
	All</a> | <a href="pqf_printAll.jsp?auditID=<s:property value="auditID" />">Print</a>
	<s:if test="conAudit.auditType.PQF">
		<pics:permission perm="AuditVerification">
	| <a href="VerifyView.action?auditID=<s:property value="auditID" />">Verify
			PQF</a>
		</pics:permission>
	</s:if></div>
</s:if>
