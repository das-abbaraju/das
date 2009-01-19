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
	<s:iterator value="auditMenu">
		<li>
		<s:if test="children.size() > 1">
			<a class="dropdown <s:if test="current == true"> current</s:if>" href="<s:property value="url" />" 
				onmouseover="cssdropdown.dropit(this, event, 'auditSubMenu<s:property value="url" />')">
				<s:property value="name" /></a>
		</s:if>
		<s:else>
			<a href="<s:property value="url" />" class="<s:if test="current == true"> current</s:if>">
			<s:property value="name" /></a>
		</s:else>
		</li>
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
		<s:if test="conAudit.auditType.annualAddendum && conAudit.auditFor != null">
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
							<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&question.id=<s:property value="question.id"/>" target="_BLANK">Uploaded (<s:date name="audit.createdDate" format="MMM yyyy"/>)</a>
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
		<s:if test="conAudit.auditStatus.pendingSubmittedResubmitted && (conAudit.auditType.Pqf || conAudit.auditType.AnnualAddendum)">
			<pics:permission perm="AuditVerification">
				<li><a href="VerifyView.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('verif')">class="current"</s:if>>Verify</a></li>
			</pics:permission>
		</s:if>
		<s:if test="conAudit.auditStatus.toString() == 'Pending'">
			<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&mode=ViewQ">Preview
			Questions</a></li>
		</s:if>
		<s:if test="conAudit.auditType.hasRequirements && conAudit.auditStatus.activeSubmitted">
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

<s:iterator value="auditMenu">
<s:if test="children.size() > 1">
	<div id="auditSubMenu<s:property value="url" />" class="auditSubMenu">
	<ul>
	<s:iterator value="children">
		<li><a href="<s:property value="url"/>" <s:if test="current == true">class="current"</s:if>><span><s:property value="name" escape="false" /></span></a></li>
	</s:iterator>
	</ul>
	</div>
</s:if>
</s:iterator>
