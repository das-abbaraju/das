<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h1><s:property value="contractor.name" /><span class="sub">
<s:if test="auditID > 0">
	<s:property value="conAudit.auditType.auditName" />
		- <s:date name="conAudit.effectiveDate" format="MMM yyyy" />
</s:if> <s:else>
	<s:property value="subHeading" />
</s:else></span></h1>
<s:if test="showHeader">
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ContractorView.action?id=<s:property value="id" />"
		<s:if test="requestURI.contains('con_view')">class="current"</s:if>>Details</a></li>
	<s:if test="!permissions.contractor">
		<li><a href="add_notes.jsp?id=<s:property value="id" />"
			<s:if test="requestURI.contains('note')">class="current"</s:if>>Notes</a></li>
	</s:if>
	<s:if test="permissions.admin">
		<li><a href="ContractorEdit.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('edit')">class="current"</s:if>>Edit</a></li>
	</s:if>
	<s:if test="permissions.contractor">
		<li><a href="ContractorEdit.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('edit')">class="current"</s:if>>Edit</a></li>
	</s:if>
	<s:if test="hasInsurance">
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
	<s:iterator value="activeAudits">
		<s:if test="auditStatus.toString() != 'Exempt'">
				<li><a <s:if test="id == auditID">class="current"</s:if>
					href="Audit.action?auditID=<s:property value="id"/>"
					title="<s:date
			name="effectiveDate" format="MMM yyyy" />"><s:property
					value="auditType.auditName" /></a></li>
		</s:if>	
	</s:iterator>
</ul>
</div>

<s:if test="auditID > 0">
<div id="auditHeader">
	<div style="float: left;">
		<fieldset>
		<ul>
			<li><label>Status:</label>
				<s:property value="conAudit.auditStatus" />
			</li>
			<s:if test="conAudit.auditType.hasAuditor">
				<s:if test="permissions.picsEmployee">
					<li><label>Auditor:</label>
						<s:property value="conAudit.auditor.name" default="Not Assigned" />
					</li>
				</s:if>
			</s:if>
			<li><label>Created:</label>
				<s:date name="conAudit.createdDate" format="MMM d, yyyy" />
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
		</ul>
		</fieldset>
	</div>
	<div style="float: left;">
		<fieldset>
		<ul>
			<s:if test="conAudit.requestingOpAccount">
				<li><label>For:</label>
					<s:property value="conAudit.requestingOpAccount.name" />
				</li>
			</s:if>
			<s:if test="conAudit.auditType.showManual">
				<li><label>Safety Manual:</label>
					<s:if test="hasSafetyManual">
							<s:iterator value="safetyManualLink.values()">
							<a href="#"
							onclick="window.open('servlet/showpdf?id=<s:property value="id" />&file=pqf<s:property value="answer"/>1331','','scrollbars=yes,resizable=yes,width=700,height=450'); return false;">
							Uploaded (<s:date name="audit.createdDate" format="MMM yyyy"/>)</a><br/><br/>
							</s:iterator>
					</s:if>
					<s:else>Not Uploaded</s:else>
				</li>
			</s:if>
			<s:if test="conAudit.auditType.scheduled">
				<li><label>Scheduled:</label>
					<s:date name="conAudit.scheduledDate"
						format="MMM d, yyyy" /> <s:property
						value="conAudit.auditLocation" />
				</li>
			</s:if>
			<li><label>Submitted:</label>
				<s:date name="conAudit.completedDate" format="MMM d, yyyy" />
			</li>
			<li><label>Expires:</label>
				<s:date name="conAudit.expiresDate" format="MMM d, yyyy" />
			</li>
		</ul>
		</fieldset>
	</div>
	<div style="float: left;">
		<fieldset>
		<ul>
			<pics:permission perm="AuditEdit">
				<li><a href="ConAuditMaintain.action?auditID=<s:property value="auditID" />">System Edit</a></li>
			</pics:permission>
			<s:if test="conAudit.auditType.Pqf">
				<pics:permission perm="AuditVerification">
					<li><a href="VerifyView.action?auditID=<s:property value="auditID" />">Verify PQF</a></li>
				</pics:permission>
			</s:if>
			<s:if test="conAudit.auditStatus.toString() == 'Pending'">
				<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&mode=ViewQ">Preview
				Questions</a></li>
			</s:if>
			<s:if test="conAudit.auditStatus.toString() != ('Pending','Exempt')">
				<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&onlyReq=true">View Requirements
				</a></li>
				<s:if test="permissions.auditor">
					<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&onlyReq=true&mode=Edit">Edit Requirements
					</a></li>
				</s:if>
			</s:if>
		</ul>
		</fieldset>
	</div>
	<br clear="all" style="margin-bottom: 20px"/>
</div>
</s:if>
</s:if>
<s:include value="../actionMessages.jsp" />
