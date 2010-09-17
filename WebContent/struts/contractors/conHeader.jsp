<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<script type="text/javascript">
$(function(){
	$('.singleButton').click(function() {
		var sID = $(this).children('.bStepID').val();
		var cID = $(this).children('.bCaoID').val();
		var data = {
				auditID: $('#auditID').val(), button: 'statusLoad', 
				caoID: cID, stepID: sID
		};
		loadResults(data, $('#addToNotes').val());
	});
	$('.clearOnce').live('click',function(){
		if($('#clearOnceField').val()==1){
			$('#clearOnceField').val(0);
			$(this).val('');		
		}
	});
});

function loadResults(data, noteText){
	$('#noteAjax').load('CaoSaveAjax.action', data, function(){
        $.blockUI({ message:$('#noteAjax'), css: { width: '350px'} }); 
        if($('.clearOnce').val()=='')
			$('#clearOnceField').val(0);
	    $('#yesButton').click(function(){
	        $.blockUI({message: 'Saving Status, please wait...'});
	        data.button = 'caoAjaxSave';
	        data.note = noteText;
	        $('#caoTable').load('CaoSaveAjax.action', data, function(){
	            $.unblockUI();
	        });
	    });
	     
	    $('#noButton').click(function(){
	        $.unblockUI();
	        return false;
	    });
	});
}
</script>

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
<s:hidden name="auditID" id="auditID" />
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
				onmouseover="cssdropdown.dropit(this, event, 'auditSubMenu<s:property value="nameIdSafe" />')"
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
	<div id="fieldsHead" style="width: 95%; margin-left: auto; margin-right:auto;">
		<fieldset>
		<ul>
			<li><label>ID:</label>
				 <s:property value="conAudit.id" />
			</li>
			<s:if test="conAudit.auditType.classType.name().equals('IM')">
				<li><label>IM Score:</label>
					<s:property value="conAudit.printableScore"/>
				</li>
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
		</ul>
		</fieldset>
		<fieldset>
		<ul>
			<s:if test="conAudit.expiresDate != null">
				<li><label>Expires:</label>
					<s:date name="conAudit.expiresDate" format="MMM d, yyyy" />
				</li>
			</s:if>			
			<s:if test="permissions.picsEmployee">
				<s:if test="conAudit.closingAuditor != null && conAudit.closingAuditor.id > 0 && conAudit.closingAuditor.name != conAudit.auditor.name">
					<li><label>Closing Auditor:</label>
						<s:property value="conAudit.closingAuditor.name" />
					</li>
				</s:if>
			</s:if>
		</ul>
		</fieldset>
		<fieldset>
		<ul>
			<s:if test="permissions.picsEmployee">
				<s:if test="conAudit.auditType.hasAuditor">
					<li><label>Safety Professional:</label>
						<s:if test="conAudit.auditor.id > 0"><s:property value="conAudit.auditor.name" /></s:if>
						<s:else><a href="AuditAssignments.action?auditID=<s:property value="auditID"/>">Not Assigned</a></s:else>
					</li>
				</s:if>
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
	</div>
	<div class="clear"></div>
	<div id="caoTable" class="center">	
		<s:include value="caoTable.jsp"/>
	</div>
	<span style="float: right; padding-right: 25px;"><a href="#" class="refresh">Refresh</a></span>
	<div class="clear"></div>
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
<div id="noteAjax" class="blockDialog">
	
</div>

<s:iterator value="#auditMenu">
<s:if test="children.size() > 0">
	<div id="auditSubMenu<s:property value="nameIdSafe" />" class="auditSubMenu">
	<ul>
	<s:iterator value="children">
		<li><a id="<s:property value="nameIdSafe"/>" href="<s:property value="url"/>"	class="audit <s:if test="current == true">current </s:if><s:property value="cssClass"/>"
				title="<s:property value="title" />"><span><s:property value="name" escape="false" /></span></a></li>
	</s:iterator>
	</ul>
	</div>
</s:if>
</s:iterator>
