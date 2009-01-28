\<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<script src="js/prototype.js" type="text/javascript"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script type="text/javascript">
	function resubmitPqf(verifyCheckBox) {
		var data = verifyCheckBox.checked;
		if(data) {
			$('submit').disabled = false;	
		}
		else
			$('submit').disabled = true;		
	}
</script>
</head>
<body>
<s:include value="conHeader.jsp" />

<s:if test="permissions.contractor">
	<s:if test="conAudit.auditStatus.activeSubmitted || conAudit.auditStatus.activeResubmittedExempt">
		<s:iterator value="activeAudits">
			<s:if test="auditStatus.pending && auditType.canContractorEdit">
				<div id="info">Please complete the <a href="Audit.action?auditID=<s:property value="id"/>"><s:property value="auditFor"/> <s:property value="auditType.auditName"/></a>.</div>
			</s:if>
		</s:iterator>
	</s:if>
	<s:if test="conAudit.auditType.pqf && conAudit.auditStatus.activeSubmitted && conAudit.aboutToExpire">
		<div id="alert">Your PQF is about to expire, please review every section and re-submit it.</div>
	</s:if>
	<s:if test="conAudit.auditType.desktop && conAudit.auditStatus.submitted">
		<div id="info">The PICS auditor has submitted your Desktop Audit. There are 
			<a href="?auditID=<s:property value="auditID" />&onlyReq=true" title="Click to see ALL Open Requirements">Open Requirements</a>
			that need your attention. Please refer to the <a href="help/c/default.htm?turl=HTMLDocuments%2Fdesktopaudit.htm" target="_BLANK" title="Click Here to View the Help Guide in a new Window">Help Guide</a> 
			for additional instructions on how to close out your audit.
		</div>
	</s:if>
	<s:if test="conAudit.auditStatus.name() == 'Pending' && !conAudit.contractorAccount.paymentMethodStatusValid">
		<div id="info">Before you will be able to submit your information for review, you must <a href="ContractorEdit.action?id=<s:property value="conAudit.contractorAccount.id"/>"> provide a valid payment method</a>.</div>		
	</s:if>
</s:if>

<s:form>
	<s:hidden name="auditID" />
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
			<s:else>
				<s:checkbox name="''" onchange="resubmitPqf(this);"/>
				 I have reviewed and updated my previously submitted data and verified its accuracy.<br/>
				<s:submit id="submit" value="Submit" name="button" disabled="true"></s:submit>
			</s:else>
		</div>
	</s:if>
	<s:if test="canClose">
		<div id="alert" class="buttons" style="">
			<s:if test="conAudit.auditType.classType.toString().equals('Policy') ">
				<s:submit name="button" value="Approve" cssStyle="font-size: 16px; padding: 8px; margin: 5px; color: darkgreen; font-weight: bold;" />
				<s:submit name="button" value="Reject" cssStyle="font-size: 16px; padding: 8px; margin: 5px; color: #d12f19;; font-weight: bold;" />
				Click Approve when you have verified the <s:property value="conAudit.auditType.auditName"/>.  		
			</s:if>
			<s:else>
				<s:hidden name="auditStatus" value="Active" />
				<s:submit value="%{'Close '.concat(conAudit.auditType.auditName)}"/>
			</s:else>
		</div>
	</s:if>
</s:form>

<s:if test="conAudit.auditType.classType.toString() == 'Policy'">
<table class="report" style="float: right;">
	<thead>
		<tr>
			<th>Operator</th>
			<th>Status</th>
		</tr>
	</thead>
	<s:iterator value="conAudit.operators" status="rowStatus">
		<s:if test="permissions.operator || permissions.corporate">
			<s:if test="operator.id == permissions.accountId">
				<tr>
					<td><s:property value="operator.name"/></td>
					<td><s:property value="status"/></td>
				</tr>
				<tr>
					<td colspan="2"><s:property value="notes"/></td>
				</tr>
			</s:if>
		</s:if>
	</s:iterator>
</table>
</s:if>

<table class="report" style="clear: none;">
	<thead>
		<tr>
			<th>Num</th>
			<th>Category</th>
		<s:if test="conAudit.auditStatus.name() == 'Pending' || conAudit.auditType.pqf">
			<th colspan="2">Complete</th>
		</s:if>
		<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements">
			<th colspan="2">Requirements</th>
		</s:if>
		<s:if test="canApply">
			<th>Apply</th>
		</s:if>
		</tr>
	</thead>
	<s:iterator value="categories" status="rowStatus">
		<s:if test="appliesB">
			<tr>
				<td class="right"><a name="<s:property value="id" />"><s:property value="category.number" /></a></td>
				<td><a href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.category" /></a></td>
			<s:if test="conAudit.auditStatus.name() == 'Pending' || conAudit.auditType.pqf">
				<td class="right"><s:property value="percentCompleted" />%</td>
				<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements">
				<td class="right"><s:property value="percentVerified" />%</td>
				<td><s:if test="percentVerified == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="canApply">
				<td><s:form method="POST"><s:hidden name="auditID" value="%{auditID}" /><s:hidden name="removeCategoryID" value="%{id}" /><s:submit value="Remove"></s:submit></s:form></td>
			</s:if>
			</tr>
		</s:if>
	</s:iterator>
	<s:iterator value="categories" status="rowStatus">
		<s:if test="!appliesB && permissions.picsEmployee">
			<tr class="notapp">
				<td class="right"><a name="<s:property value="id" />"><s:property value="category.number" /></a></td>
				<td><a href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.category" /></a></td>
				<s:if test="conAudit.auditStatus.name() == 'Pending' || conAudit.auditType.pqf">
					<td class="center" colspan="2">N/A</td>
				</s:if>
				<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements">
					<td colspan="2"></td>
				</s:if>
				<s:if test="canApply">
					<td>
					<s:form method="POST"><s:hidden name="auditID" value="%{auditID}" /><s:hidden name="applyCategoryID" value="%{id}"></s:hidden><s:submit value="Add"></s:submit></s:form>
					</td>
				</s:if>
			</tr>
		</s:if>
	</s:iterator>
</table>
<s:if test="conAudit.auditType.pqf">
	<div id="info">
		The OSHA and EMR categories have been moved to the Annual Update.
	</div>
</s:if>

</body>
</html>
