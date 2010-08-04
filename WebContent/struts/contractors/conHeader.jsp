<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:set name="auditMenu" value="auditMenu"></s:set>

<h1><s:property value="contractor.name" /><span class="sub">
<s:if test="subHeading.length() > 0">
	<s:property value="subHeading" escape="false" />
</s:if>
<s:elseif test="auditID > 0">
	<s:property value="conAudit.auditType.auditName" />
	<s:if test="conAudit.auditFor != null">for <s:property value="conAudit.auditFor"/></s:if>
	<s:else>- <s:date name="conAudit.effectiveDate" format="MMM yyyy" /></s:else>
</s:elseif>
</span></h1>
<s:if test="showHeader">
<div id="internalnavcontainer">
<ul id="navlist">
	<s:if test="!permissions.insuranceOnlyContractorUser">
		<li>
			<a class="dropdown" href="ContractorView.action?id=<s:property value="id" />" 
				onmouseover="cssdropdown.dropit(this, event, 'contractorSubMenu')">Account Details</a>
		</li>
	</s:if>
	<s:else>
		<li>
			<a class="dropdown" href="Home.action"
				onmouseover="cssdropdown.dropit(this, event, 'contractorSubMenu')">Account Details</a>
		</li>
	</s:else>

	<s:if test="!permissions.operator && !permissions.insuranceOnlyContractorUser">
		<li><a
			href="ContractorFacilities.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('contractor_facilities')">class="current"</s:if>>Facilities</a></li>
	</s:if>
	<s:if test="permissions.contractor">
		<li><a href="ContractorForms.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('con_forms')">class="current"</s:if>>Forms &amp; Docs</a></li>
	</s:if>
	<s:iterator value="#auditMenu">
		<li>
		<s:if test="children.size() > 0">
			<a id="<s:property value="nameIdSafe"/>" class="dropdown <s:if test="current == true"> current</s:if>" href="<s:property value="url" />" 
				onmouseover="cssdropdown.dropit(this, event, 'auditSubMenu<s:property value="url" />')"
				title="<s:property value="title" />"><s:property value="name" escape="false" /></a>
		</s:if>
		<s:else>
			<a id="<s:property value="nameIdSafe"/>" href="<s:property value="url" />" class="<s:if test="current == true"> current</s:if>"
			title="<s:property value="title" />"><s:property value="name" escape="false" /></a>
		</s:else>
		</li>
	</s:iterator>
</ul>
</div>

<s:if test="auditID > 0">
<div id="auditHeader" class="auditHeader">
	<fieldset>
	<ul>
		<li><label>Type:</label>
			<s:property value="conAudit.auditType.auditName" />
			 #<s:property value="conAudit.id" />
		</li>
		<li><label>Created:</label>
			<s:date name="conAudit.creationDate" format="MMM d, yyyy" />
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
		<s:if test="!conAudit.auditType.classType.policy">
			<li><label>Status:</label>
				<s:property value="conAudit.auditStatus" />
			</li>
			<li><label>Description:</label>
				<s:property value="conAudit.statusDescription" />
			</li>
			
		</s:if>
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
				<li><label>Safety Professional:</label>
					<s:if test="conAudit.auditor.id > 0"><s:property value="conAudit.auditor.name" /></s:if>
					<s:else><a href="AuditAssignments.action?auditID=<s:property value="auditID"/>">Not Assigned</a></s:else>
				</li>
				<s:if test="conAudit.closingAuditor != null && conAudit.closingAuditor.id > 0">
					<li><label>Closing Safety Professional:</label>
						<s:property value="conAudit.closingAuditor.name" />
					</li>
				</s:if>
			</s:if>
		</s:if>
		<s:if test="conAudit.auditType.showManual">
			<li><label><nobr><s:if test="conAudit.auditType.id == 96">Management Plan</s:if>
					   <s:else>Safety Manual</s:else>:</nobr></label>
				<s:if test="hasSafetyManual">
						<s:iterator value="safetyManualLink.values()">
							<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="id"/>" target="_BLANK">Uploaded (<s:date name="updateDate" format="MMM yyyy"/>)</a>
						</s:iterator>
				</s:if>
				<s:else>Not Uploaded</s:else>
			</li>
		</s:if>
		<s:if test="conAudit.auditType.classType.name().equals('IM')">
			<li><label>IM Score:</label>
				<s:property value="conAudit.printableScore"/>
			</li>
		</s:if>
		<s:if test="conAudit.requestingOpAccount">
			<li><label>For:</label>
				<s:property value="conAudit.requestingOpAccount.name" />
			</li>
		</s:if>
		<s:if test="conAudit.auditType.classType.policy">
			<s:iterator value="conAudit.operators" status="rowStatus">
				<s:if test="visible && isVisibleTo(permissions)">
					<li>
						<label>Op Status:</label>
							<a href="#cao<s:property value="id"/>"><s:property value="status"/></a>
							<span style="font-size: 10px; white-space: nowrap;"><s:property value="@com.picsauditing.util.Strings@trim(operator.name, 30)" /></span>
					</li>
				</s:if>
			</s:iterator>
		</s:if>
	</ul>
	</fieldset>
	<div class="clear"></div>
</div>
<div id="auditHeaderNav" class="auditHeaderNav noprint">
	<ul>
		<pics:permission perm="AuditEdit">
			<li><a href="ConAuditMaintain.action?auditID=<s:property value="auditID" />"
				<s:if test="requestURI.contains('audit_maintain.jsp')">class="current"</s:if>>System Edit</a></li>
		</pics:permission>
		<s:if test="conAudit.auditStatus.pendingSubmittedResubmitted || conAudit.auditStatus.incomplete && (conAudit.auditType.pqf || conAudit.auditType.annualAddendum)">
			<pics:permission perm="AuditVerification">
				<li><a href="VerifyView.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('verif')">class="current"</s:if>>Verify</a></li>
			</pics:permission>
		</s:if>
		<s:if test="conAudit.auditStatus.pending">
			<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&mode=ViewQ">Preview
			Questions</a></li>
		</s:if>
		<s:if test="conAudit.auditType.hasRequirements && conAudit.auditStatus.activeSubmitted">
			<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&onlyReq=true" 
				<s:if test="onlyReq && mode != 'Edit'">class="current"</s:if>>Print Requirements</a></li>
			<s:if test="permissions.auditor">
				<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&onlyReq=true&mode=Edit"
				 <s:if test="onlyReq && mode == 'Edit'">class="current"</s:if>>Edit Requirements</a></li>
			</s:if>
			<s:if test="permissions.admin">
				<li><a href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
			</s:if>
			<s:elseif test="permissions.onlyAuditor">
				<li><a href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
			</s:elseif>
			<s:if test="permissions.operatorCorporate">
				<li><a href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Review Requirements</a></li>
			</s:if>
		</s:if>
		<s:if test="!singleCat">
			<li><a href="Audit.action?auditID=<s:property value="auditID" />"
				<s:if test="requestURI.contains('con_audit.jsp')">class="current"</s:if>>Categories</a></li>
			<pics:permission perm="AllContractors">
				<li><a href="?auditID=<s:property value="auditID"/>&button=recalculate">Recalculate Categories</a></li>
			</pics:permission>
		</s:if>
		<s:if test="(permissions.contractor || permissions.admin) && conAudit.auditStatus.pending && conAudit.auditType.scheduled">
			<li><a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>"
					<s:if test="requestURI.contains('schedule_audit')">class="current"</s:if>>Schedule Audit</a></li>
		</s:if>
	</ul>
</div>
</s:if>
</s:if>
<s:include value="../actionMessages.jsp" />
<div class="clear"></div>

<div id="contractorSubMenu" class="auditSubMenu">
<ul>
	<s:if test="!permissions.insuranceOnlyContractorUser">
		<li><a href="ContractorView.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('con_dashboard')">class="current"</s:if>><span>Account Summary</span></a></li>
	</s:if>
	<s:if test="permissions.operator">
		<li><a href="ContractorFlag.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('flag')">class="current"</s:if>>Flag
		Status</a></li>
	</s:if>
	<li><a href="ContractorNotes.action?id=<s:property value="id" />"
		<s:if test="requestURI.contains('con_notes')">class="current"</s:if>><span>Contractor Notes</span></a></li>
	<pics:permission perm="DefineCompetencies">
		<li><a href="JobCompetencyMatrix.action?id=<s:property value="id" />"><span>HSE Competency Matrix</span></a></li>
	</pics:permission>
	<s:if test="permissions.admin">
		<li><a id="edit_contractor" href="ContractorEdit.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('edit')">class="current"</s:if>><span>Edit Account</span></a></li>
		<pics:permission perm="AuditVerification">
			<li><a href="VerifyView.action?id=<s:property value="id" />"><span>PQF Verification</span></a></li>
		</pics:permission>
		<li><a href="UsersManage.action?accountId=<s:property value="id"/>">Users</a></li>
		<li><a href="ManageEmployees.action?id=<s:property value="id"/>">Employees</a></li>
		<pics:permission perm="DefineRoles">
			<li><a href="ManageJobRoles.action?id=<s:property value="id"/>">Job Roles</a></li>
		</pics:permission>
		<s:if test="!contractor.status.demo">
			<li><a id="billing_detail" href="BillingDetail.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('billing_detail')">class="current"</s:if>><span>Billing Details</span></a></li>
			<li><a href="ContractorPaymentOptions.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('payment_options')">class="current"</s:if>><span>Payment Options</span></a></li>
		</s:if>
		<pics:permission perm="DevelopmentEnvironment">
			<li><a href="ContractorCron.action?conID=<s:property value="id" />">Contractor Cron</a></li>
		</pics:permission>
	</s:if>
	<s:elseif test="permissions.contractor">
		<pics:permission perm="ContractorAdmin">
			<li><a id="edit_contractor" href="ContractorEdit.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('edit')">class="current"</s:if>><span>Edit Account</span></a></li>
		</pics:permission>
			<li><a id="profileEditLink" href="ProfileEdit.action"
		<s:if test="requestURI.contains('profile')">class="current"</s:if>><span>Edit My Profile</span></a></li>
		<pics:permission perm="ContractorAdmin">
			<li><a href="UsersManage.action">Users</a></li>
			<li><a href="ManageEmployees.action">Employees</a></li>
			<pics:permission perm="ContractorAdmin">
				<li><a href="ManageJobRoles.action">Job Roles</a></li>
			</pics:permission>
		</pics:permission>
		<pics:permission perm="ContractorBilling">
			<li><a id="billing_detail" href="BillingDetail.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('billing_detail')">class="current"</s:if>><span>Billing Details</span></a></li>
			<li><a href="ContractorPaymentOptions.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('payment_options')">class="current"</s:if>><span>Payment Options</span></a></li>
		</pics:permission>
	</s:elseif>
	<pics:permission perm="DevelopmentEnvironment">
		<li><a href="EmployeeAssessmentResults.action?id=<s:property value="id"/>">Assessment Results</a></li>
	</pics:permission>
</ul>
</div>

<s:iterator value="#auditMenu">
<s:if test="children.size() > 0">
	<div id="auditSubMenu<s:property value="url" />" class="auditSubMenu">
	<ul>
	<s:iterator value="children">
		<li><a id="<s:property value="nameIdSafe"/>" href="<s:property value="url"/>"	class="audit <s:if test="current == true">current </s:if><s:property value="cssClass"/>"
				title="<s:property value="title" />"><span><s:property value="name" escape="false" /></span></a></li>
	</s:iterator>
	</ul>
	</div>
</s:if>
</s:iterator>
