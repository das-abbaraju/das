<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">

$(function(){
	
	$('.sysEditDate').datepicker({
			changeMonth: true,
			changeYear:true,
			yearRange: '2008:'+new Date(),
			showOn: 'button',
			buttonImage: './images/icon_calendar.gif',
			buttonImageOnly: true,
			buttonText: 'Choose a date...',
			constrainInput: true,
			showAnim: 'fadeIn'
	});

	$('.statusOpBox :visible').live('change', function(){
		$(this).parents('tr').addClass('dirtyCao');
	});

	$('#saveEdit_cao').live('click', function(){
		$('.clean').hide();
		$('.dirty').show();
		$('#auditHeader').addClass('dirty');
		var caoMap = $('.dirtyCao :input').serialize();
		$.post('ConAuditMaintainAjax.action', 'button=caoSave&systemEdit=true&'+'auditID='+$('input[name=auditID]').val()+'&'+caoMap, function(data){
			$('#caoTable').html(data);
			$('#auditHeader').removeClass('dirty');
			$('.clean').show();
			$('.dirty').hide();
		});
		
	});
	
	$('.singleButton').live('click', function() {
		var data = {
				auditID: $('#auditID').val(), button: 'statusLoad', 
				caoID: $(this).children('.bCaoID').val(), 
				status: $(this).children('.bStatus').val()
		};
		$('#caoTable').block({message: 'Loading...'});
		loadResults(data);
	});

	$('#multiStatusChange').live('change', function(){
		var status = $(this).val();
		if(status==-1)
			return false;
		var caoString = $('#h_'+status).val();
		var caoIDs = caoString.substring(1, caoString.length-1).replace(/\s/g,'').split(',');
		var data = {
				auditID: $('#auditID').val(), button: 'statusLoad',
				caoIDs: caoIDs,
				status: status
		}
		$('#caoTable').block({message: 'Loading...'});
		loadResults(data);		
	});

	<s:if test="canEditCao && !systemEdit">
	$('td.caoStatus a.edit').live('click', function(e) {
		e.preventDefault();
		$(this).parents('td.caoStatus:first').toggleClass('edit');
	});

	$('#caoTable').delegate('span.caoEdit', 'change', function() {
		var data = {
				button: 'caoSave',
				auditID: $('#auditID').val(), 
				'caosSave[0].id': $('.caoID', this).val(), 
				'caosSave[0].status': $('.status', this).val(),
				systemEdit: false
		};
		$.post('ConAuditMaintainAjax.action', data, function(data){
			$('#caoTable').html(data);
		});
	});
	</s:if>
});

function loadResults(data, noteText){
	$.ajax({
		url: 'CaoSaveAjax.action',
		data: data,
		headers: {'refresh':'true'},
		type: 'get',
		success: function(response, status, xhr){
			if(status == 'success'){
				$('#caoAjax').html(response);
				$('#caoTable').unblock();
				if($('#noteRequired').val()=='true'){
					$('#yesButton').addClass('disabled');
					$('#addToNotes').live('keyup', function(){
						if($(this).val()!='')
							$('#yesButton').removeClass('disabled');
						else
							$('#yesButton').addClass('disabled');
					});
				}
				$('#yesButton').click(function(){
					if($(this).hasClass('disabled'))
						return false;
			        $.blockUI({message: 'Saving Status, please wait...'});
			        data.button = 'caoAjaxSave';
			        if($('#addToNotes').val())
			        	data.note =  $('#addToNotes').val();
			        $('#caoTable').load('CaoSaveAjax.action', data, function(){
			            $.unblockUI();
			        });
			    });		     
			    $('#noButton').click(function(){
			        $.unblockUI();
			        return false;
			    });
				if($('#noteRequired').val()=='true')
		        	$.blockUI({ message:$('#caoAjax')});
				else  
					$('#yesButton').click();
			    
			} else {
				$('#caoTable').block({message: 'Error with request, please try again',
					timeout: 1500	
				});
			}
		}
	});
}

function loadStatus(caoID){
	$('#caoAjax').load('CaoSaveAjax.action', {auditID: $('#auditID').val(), button: 'statusHistory', caoID: caoID}, function(){
		$.blockUI({message: $('#caoAjax'), css: {width:'575px'}});
		$('.editNote').click(function(){
			var that = $(this);
			that.hide();
			var parent = that.closest('tr').attr('id');
			var note_div = $('#'+parent+' .ac_cao_notes');
			var oldNote = note_div.text();
			note_div.html($('<textarea>').append(oldNote).attr({
				'rows':'4',
				'cols':'24'}))
					.append($('<a>').append('Change').addClass('showPointer edit saveEdited'))
					.append($('<a>').append('Cancel').addClass('showPointer remove noCancel'));
			$('#'+parent+' .ac_cao_notes > .saveEdited').click(function(){
				$.post('CaoSaveAjax.action', {
					auditID: $('#auditID').val(), button: 'updateEditNote', 
					caoID: caoID, noteID: parent,
					note: $('#'+parent+' .ac_cao_notes textarea').val()}, function(){
						$.unblockUI();
						return false;
				});
			});
			$('#'+parent+' .ac_cao_notes > .noCancel').click(function(){
				that.show();
				note_div.html('');
				note_div.text(oldNote);
			});
		});
		$('#noButton').click(function(){
	        $.unblockUI();
	        return false;
	    });			
	});
}
</script>
<style>
#caoTable a.edit {
	display: none
}

#caoTable span.right {
	width: 20px;
}

#caoTable td.hover a.edit {
	display: inline;
}

#caoTable .caoEdit {
	display: none;
}

#caoTable .systemEdit .caoEdit {
	display: inline;
}

#caoTable td.edit .caoDisplay {
	display: none;
}

#caoTable td.edit .caoEdit {
	display: inline;
}
</style>

<s:set name="auditMenu" value="auditMenu"></s:set>

<h1><s:property value="contractor.name" />
<span class="sub">
<s:if test="auditID > 0">
	<s:text name="%{conAudit.auditType.getI18nKey('name')}" />
	<s:if test="conAudit.auditFor != null && conAudit.auditFor.length() > 0">for <s:property value="conAudit.auditFor"/></s:if>
	<s:elseif test="!conAudit.auditType.pqf">- <s:date name="conAudit.effectiveDateLabel" format="MMM yyyy" /></s:elseif>
</s:if>
<s:elseif test="subHeading.length() > 0">
	<s:property value="subHeading" escape="false" />
</s:elseif>
</span>
</h1>

<s:if test="showHeader">
<s:hidden name="auditID" id="auditID" />

<div id="internalnavcontainer">
<ul id="navlist">
	<s:if test="!permissions.insuranceOnlyContractorUser">
		<li>
			<a class="dropdown" href="ContractorView.action?id=<s:property value="id" />" 
				onmouseover="cssdropdown.dropit(this, event, 'contractorSubMenu')"><s:text name="ContractorView.title" /></a>
		</li>
	</s:if>
	<s:else>
		<li>
			<a class="dropdown" href="Home.action"
				onmouseover="cssdropdown.dropit(this, event, 'contractorSubMenu')"><s:text name="ContractorView.title" /></a>
		</li>
	</s:else>

	<s:if test="!permissions.operator && !permissions.insuranceOnlyContractorUser">
		<li><a
			href="ContractorFacilities.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('contractor_facilities')">class="current"</s:if>><s:text name="global.Facilities" /></a></li>
	</s:if>
	<s:if test="permissions.contractor">
		<li><a href="ContractorForms.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('con_forms')">class="current"</s:if>><s:text name="global.FormsAndDocs" /></a></li>
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
			<li><label><s:text name="global.id" />:</label>
				 <s:property value="conAudit.id" />
			</li>
			<s:if test="conAudit.auditType.scoreable">
				<li><label><s:text name="Audit.message.Score" />:</label>
					<s:if test="conAudit.auditType.classType.im">
						<div id="auditScore"><s:property value="conAudit.printableScore"/></div>
					</s:if>
					<s:else>
						<div id="auditScore"><s:property value="conAudit.score"/></div>
					</s:else>
				</li>
			</s:if>
			<s:if test="conAudit.auditType.showManual">
				<li><label><nobr><s:if test="conAudit.auditType.id == 96"><s:text name="Audit.message.ManagementPlan" />Management Plan</s:if>
						   <s:else><s:text name="Audit.message.SafetyManual" /></s:else>:</nobr></label>
					<s:if test="hasSafetyManual">
							<s:iterator value="safetyManualLink.values()">
								<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&auditData.question.id=<s:property value="question.id"/>" target="_BLANK"><s:text name="Audit.message.Uploaded"><s:param><s:date name="updateDate" format="MMM yyyy"/></s:param></s:text></a>
							</s:iterator>
					</s:if>
					<s:else><s:text name="Audit.message.NotUploaded" /></s:else>
				</li>
			</s:if>
		</ul>
		</fieldset>
		<fieldset>
		<ul>
			<s:if test="conAudit.expiresDate != null">
				<li><label><s:text name="Audit.message.Expires" />:</label>
					<s:date name="conAudit.expiresDate" format="MMM d, yyyy" />
				</li>
			</s:if>			
			<s:if test="permissions.picsEmployee">
				<s:if test="conAudit.closingAuditor != null && conAudit.closingAuditor.id > 0 && conAudit.closingAuditor.name != conAudit.auditor.name">
					<li><label><s:text name="Audit.ClosingAuditor" />:</label>
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
					<li><label>
						<s:if test="conAudit.auditType.classType.name().equals('PQF') || conAudit.auditType.classType.name().equals('Policy') || conAudit.auditType.annualAddendum"><s:text name="global.CSR" />:</s:if>
						<s:else><s:text name="global.SafetyProfessional" />:</s:else></label>
						<s:if test="conAudit.auditor.id > 0"><s:property value="conAudit.auditor.name" /></s:if>
						<s:else><a href="AuditAssignments.action?auditID=<s:property value="auditID"/>"><s:text name="Audit.message.NotAssigned" /></a></s:else>
					</li>
				</s:if>
			</s:if>			
			<s:if test="conAudit.auditType.scheduled && conAudit.scheduledDate != null">
				<li><label><s:text name="Audit.message.Scheduled" />:</label>
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
	<s:if test="systemEdit">
		<span class="refresh"><a class="clickable save" id="saveEdit_cao"><span class="clean"><s:text name="button.Save" /></span><span class="dirty"><s:text name="Audit.message.SavingNow" /></span></a></span>
	</s:if>
	<s:else>
		<span class="refresh"><a class="clickable refresh" id="refresh_cao"><span class="clean"><s:text name="button.Refresh" /></span><span class="dirty"><s:text name="Audit.message.RefreshingStatus" /></span></a></span>
	</s:else>
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
	<s:if test="permissions.admin || permissions.hasPermission('ContractorWatch')">
		<li><a href="ReportActivityWatch.action?conID=<s:property value="id" />"
			<s:if test="requestURI.contains('report_activity_watch')">class="current"</s:if>><span>Activity</span></a></li>
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
			<li><a href="AuditBuilder.action?id=<s:property value="id" />">Audit Builder</a></li>
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
			<s:if test="permissions.requiresCompetencyReview">
				<li><a href="ManageJobRoles.action">Job Roles</a></li>
			</s:if>
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
<div id="caoAjax" class="blockDialog">
	
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
