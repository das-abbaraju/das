<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript">
	function showAuditFiles(auditID) {
		url = 'ContractorAuditFileUpload.action?auditID='+auditID;
		title = 'Upload';
		pars = 'scrollbars=yes,resizable=yes,width=550,height=550,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
	}
</script>

<s:if test="auditID > 0">
	<s:include value="../contractors/conHeader.jsp" />
	
	<s:if test="conAudit.auditType.classType.policy">
		<s:if test="permissions.operatorCorporate && policyWithOtherOperators">
			<div id="alert">More than one facility has access to this data. Please contact PICS if any changes to existing data are needed.</div>
		</s:if>
		<s:if test="conAudit.willExpireSoon() && !conAudit.auditStatus.expired">
			<div id="alert">This policy is about to Expire and is currently locked for editing. Please use the up coming policy to record any changes 
				<s:iterator value="conAudit.contractorAccount.audits" id="newPending">
					<s:if test="#newPending.auditType.classType.policy && conAudit.id != #newPending.id">
						<s:if test="conAudit.auditType == #newPending.auditType && #newPending.auditStatus.pending">
							<a href="Audit.action?auditID=<s:property value="#newPending.id"/>"><s:property value="#newPending.auditType.auditName"/></a>
						</s:if>
					</s:if>
				</s:iterator>
			</div>
		</s:if>
	</s:if>
</s:if>


<s:if test="permissions.contractor">
	<s:if test="conAudit.auditStatus.activeSubmitted || conAudit.auditStatus.activeResubmittedExempt">
		<s:if test="activePendingEditableAudits.size > 0">
			<div id="alert">
				You have <strong><s:property value="activePendingEditableAudits.size"/></strong> Audits to complete. To see your list of open tasks go to your <a href="Home.action">home page</a>.
				<ul>
					<s:iterator value="activePendingEditableAudits">
						<li>Please complete the <a href="Audit.action?auditID=<s:property value="id"/>"><s:property value="auditFor"/> <s:property value="auditType.auditName"/></a>.</li>
					</s:iterator>
				</ul>
			</div>
			<div class="buttons">
				<a href="Audit.action?auditID=<s:property value="activePendingEditableAudits.get(0).getId()"/>" class="picsbutton">Next Audit &gt;</a>
			</div>
		</s:if>
	</s:if>
	<s:if test="conAudit.auditType.pqf && conAudit.auditStatus.activeSubmitted && conAudit.aboutToExpire">
		<div id="alert">Your PQF is about to expire, please review every section and re-submit it.</div>
	</s:if>
	<s:if test="conAudit.auditType.desktop && conAudit.auditStatus.submitted">
		<div id="info">The PICS auditor has submitted your Desktop Audit. There are 
			<a href="AuditCat.action?auditID=<s:property value="auditID" />&onlyReq=true" title="Click to see ALL Open Requirements">Open Requirements</a>
			that need your attention. Please refer to the <a href="help/c/default.htm?turl=HTMLDocuments%2Fdesktopaudit.htm" target="_BLANK" title="Click Here to View the Help Guide in a new Window">Help Guide</a> 
			for additional instructions on how to close out your audit.
		</div>
	</s:if>
	<s:if test="conAudit.auditStatus.name() == 'Pending' && !conAudit.contractorAccount.paymentMethodStatusValid">
		<div id="info">Before you will be able to submit your information for review, you must <a href="ContractorPaymentOptions.action?id=<s:property value="conAudit.contractorAccount.id"/>"> update your payment method</a>.</div>		
	</s:if>
</s:if>

<s:if test="conAudit.auditType.hasRequirements">
	<div id="info">
		Please use  
		<input type="button" class="picsbutton positive" value="Additional Attachments" onclick="showAuditFiles(<s:property value="auditID" />)" title="Opens in new window (please disable your popup blocker)"/>
		to upload any additional documents that your client's require.	
	</div>	
</s:if>	

<s:form>
	<s:hidden name="auditID" />
	<s:if test="!conAudit.auditType.classType.policy">
		<s:if test="canSubmit">
			<div id="alert" class="buttons" style="">
				<s:if test="conAudit.auditStatus.pendingExpired">
					<s:submit id="submit" value="Submit" name="button" cssStyle="font-size: 16px; padding: 8px; margin: 5px; color: darkgreen; font-weight: bold;"></s:submit>
					<s:if test="conAudit.auditType.pqf">
						<br />You're almost done! Please take another opportunity to double check your information.
						<br />Click Submit when you're ready to send your information to PICS for review.
						<br />You MUST click this button before your PQF can become Activated.
					</s:if>
					<s:else>
						<br />Click Submit when you're ready to finalize the <s:property value="conAudit.auditType.auditName"/>.
					</s:else>
				</s:if>
				<s:elseif test="conAudit.auditStatus.incomplete">
					<s:submit id="submit" value="Submit" name="button" cssStyle="font-size: 16px; padding: 8px; margin: 5px; color: darkgreen; font-weight: bold;"></s:submit>
					<br/>Click Submit when you have completed all the pending requirements.				
				</s:elseif>
				<s:else>
					<s:checkbox name="''" onchange="resubmitPqf(this);"/>
					 I have reviewed and updated my previously submitted data and verified its accuracy.<br/>
					<s:submit id="submit" value="Submit" name="button" disabled="true"></s:submit>
				</s:else>
			</div>
		</s:if>
	</s:if>
	<s:if test="canClose">
		<div id="alert" class="buttons" style="">
			<s:hidden name="auditStatus" value="Active" />
			<s:submit value="%{'Close '.concat(conAudit.auditType.auditName)}"/>
		</div>
	</s:if>
</s:form>
