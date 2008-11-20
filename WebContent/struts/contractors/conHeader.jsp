<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h1><s:property value="contractor.name" /><span class="sub">
<s:if test="auditID > 0">
	<s:property value="conAudit.auditType.auditName" />
	<s:if test="conAudit.auditFor != null">for <s:property value="conAudit.auditFor"/></s:if>
	<s:else>- <s:date name="conAudit.effectiveDate" format="MMM yyyy" /></s:else>
</s:if> <s:else>
	<s:property value="subHeading" />
</s:else></span></h1>
<s:if test="showHeader">
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a id="conDetailLink" href="ContractorView.action?id=<s:property value="id" />"
		<s:if test="requestURI.contains('con_view')">class="current"</s:if>>Details</a></li>
	<s:if test="!permissions.contractor">
		<li><a href="add_notes.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('note')">class="current"</s:if>>Notes</a></li>
	</s:if>
	<s:if test="permissions.admin">
		<li><a id="conEditLink" href="ContractorEdit.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('edit')">class="current"</s:if>>Edit</a></li>
	</s:if>
	<s:if test="permissions.contractor">
		<li><a href="ContractorEdit.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('edit')">class="current"</s:if>>Edit</a></li>
	</s:if>
	<s:if test="requiresInsurance">
		<li><a
			href="contractor_upload_certificates.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('cert')">class="current"</s:if>>InsureGuard</a></li>
	</s:if>
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
			<s:if test="requestURI.contains('con_viewForms')">class="current"</s:if>>Forms & Docs</a></li>
	</s:if>
	<li><a <s:if test="requestURI.contains('contractor_audits')">class="current"</s:if> href="ConAuditList.action?id=<s:property value="id" />">Audits</a></li>
	<s:iterator value="activeAudits" status="stat">
		<s:if test="auditStatus.toString() != 'Exempt'">
		<li>
			<s:if test="#stat.index < 4">
				<a <s:if test="id == auditID">class="current"</s:if>
					href="Audit.action?auditID=<s:property value="id"/>"
					title="<s:date
			name="effectiveDate" format="MMM yyyy" />"><s:property
					value="auditType.auditName" /></a>
			</s:if>
			<s:else>
				<a <s:if test="id == auditID">class="current"</s:if>
					href="Audit.action?auditID=<s:property value="id"/>"
					title="<s:date
			name="effectiveDate" format="MMM yyyy" />"><s:property
					value="auditType.auditName.substring(0,4)" />...</a>
			</s:else>
		</li>
		</s:if>
	</s:iterator>
</ul>
</div>

<s:if test="auditID > 0">
<div id="auditHeader">
	<fieldset>
	<ul>
		<li><label>Type:</label>
			<s:property value="conAudit.auditType.auditName" />
			 #<s:property value="conAudit.id" />
		</li>
		<li><label>Created:</label>
			<s:date name="conAudit.createdDate" format="MMM d, yyyy" />
		</li>
		<s:if test="conAudit.expiresDate != null">
			<li><label>Expires:</label>
				<s:date name="conAudit.expiresDate" format="MMM d, yyyy" />
			</li>
		</s:if>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label>Status:</label>
			<s:property value="conAudit.auditStatus" />
		</li>
		<s:if test="conAudit.auditStatus.name() == 'Submitted'">
			<s:if test="conAudit.auditType.PQF">
				<li><label>Verified:</label>
					<s:property value="conAudit.percentVerified" />%
				</li>
			</s:if>
			<s:else>
				<li><label>% Closed:</label>
					<s:property value="conAudit.percentVerified" />%
				</li>
			</s:else>
		</s:if>
		<s:if test="conAudit.auditStatus.name() == 'Pending'">
			<li><label>% Complete:</label>
				<s:property value="conAudit.percentComplete" />%
			</li>
		</s:if>
		<s:if test="conAudit.completedDate != null">
			<li><label>Submitted:</label>
				<s:date name="conAudit.completedDate" format="MMM d, yyyy" />
			</li>
		</s:if>
		<s:if test="conAudit.auditType.scheduled && conAudit.scheduledDate != null">
			<li><label>Scheduled:</label>
				<s:date name="conAudit.scheduledDate"
					format="MMM d, yyyy" /> <s:property
					value="conAudit.auditLocation" />
			</li>
		</s:if>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<s:if test="conAudit.auditFor != null">
			<li><label>For:</label>
				<s:property value="conAudit.auditFor"/>
			</li>
		</s:if>
		<s:if test="permissions.picsEmployee">
			<s:if test="conAudit.auditType.hasAuditor">
				<li><label>Auditor:</label>
					<s:if test="conAudit.auditor.id > 0"><s:property value="conAudit.auditor.name" /></s:if>
					<s:else><a href="AuditAssignments.action?auditID=<s:property value="auditID"/>">Not Assigned</a></s:else>
				</li>
			</s:if>
		</s:if>
		<s:if test="conAudit.auditType.showManual">
			<li><label>Safety Manual:</label>
				<s:if test="hasSafetyManual">
						<s:iterator value="safetyManualLink.values()">
						<a href="#"
						onclick="window.open('servlet/showpdf?id=<s:property value="id" />&file=pqf<s:property value="answer"/>1331','','scrollbars=yes,resizable=yes,width=700,height=450'); return false;">
						Uploaded (<s:date name="audit.createdDate" format="MMM yyyy"/>)</a>
						</s:iterator>
				</s:if>
				<s:else>Not Uploaded</s:else>
			</li>
		</s:if>
		<s:if test="conAudit.requestingOpAccount">
			<li><label>For:</label>
				<s:property value="conAudit.requestingOpAccount.name" />
			</li>
		</s:if>
	</ul>
	</fieldset>
	<div class="clear"></div>
</div>
<div id="auditHeaderNav">
	<ul>
		<pics:permission perm="AuditEdit">
			<li><a href="ConAuditMaintain.action?auditID=<s:property value="auditID" />"
				<s:if test="requestURI.contains('audit_maintain.jsp')">class="current"</s:if>>System Edit</a></li>
		</pics:permission>
		<s:if test="conAudit.auditType.Pqf">
			<pics:permission perm="AuditVerification">
				<li><a href="VerifyView.action?auditID=<s:property value="auditID" />"
				<s:if test="requestURI.contains('pqf_verif')">class="current"</s:if>>Verify PQF</a></li>
			</pics:permission>
		</s:if>
		<s:if test="conAudit.auditStatus.toString() == 'Pending'">
			<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&mode=ViewQ">Preview
			Questions</a></li>
		</s:if>
		<s:if test="conAudit.auditType.auditTypeID > 1 && conAudit.auditType.hasRequirements && (conAudit.auditStatus.toString().equals('Submitted') || conAudit.auditStatus.toString().equals('Active'))">
			<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&onlyReq=true" 
				<s:if test="onlyReq">class="current"</s:if>>View Requirements</a></li>
			<s:if test="permissions.auditor">
				<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&onlyReq=true&mode=Edit">Edit Requirements
				</a></li>
			</s:if>
		</s:if>
		<li><a href="Audit.action?auditID=<s:property value="auditID" />"
			<s:if test="requestURI.contains('con_audit.jsp')">class="current"</s:if>>Categories</a></li>
	</ul>
</div>
</s:if>
</s:if>
<s:include value="../actionMessages.jsp" />
<div class="clear"></div>
