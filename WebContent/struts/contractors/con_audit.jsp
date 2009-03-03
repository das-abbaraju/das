<%@ taglib prefix="s" uri="/struts-tags"%>
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
	
	function editCao( caoId ) {
		startThinking( {div: 'caoThinking' } );
		
		var pars= 'cao.id=' + caoId;
		var myAjax = new Ajax.Updater($('caoSection'),'CaoEditAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				if (transport.status == 200) {
					$('caoSection').show();
				}
				stopThinking( {div: 'caoThinking' } );
			}
		});
	
		return false;
	}

	function saveCao() {
		startThinking( {div: 'caoThinking' } );
		var pars= $('caoForm').serialize();
		
		var myAjax = new Ajax.Updater('caoSection','CaoEditAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) {
				if (transport.status == 200) {
					new Effect.Highlight($('caoSection'),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					setMainFields();
				}
				stopThinking( {div: 'caoThinking' } );
			}
		});
	
		return false;
	}
	
	function setMainFields( ) {
		var caoStatusElm = $('caoForm')['cao.status'];
		var caoId = $F($('cao.id'));
		var noteValue = $F($('cao.notes'));

		var caoStatus;
		for( var i = 0; i < caoStatusElm.length; i++  ) {
			var current = caoStatusElm[i];
			if( current.checked ) {
				caoStatus = current.value;
				break;
			}
		}

		
		$('caoStatusMain_' + caoId).innerHTML = caoStatus;
		

		if( noteValue != '' ) {
			$('caoNotesMain_' + caoId).show();
			$('caoNotesMain_' + caoId).innerHTML = '&nbsp;&nbsp;' + noteValue;
		}
		else {
			$('caoNotesMain_' + caoId).hide();
		}
	
		return;	
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
			<a href="AuditCat.action?auditID=<s:property value="auditID" />&onlyReq=true" title="Click to see ALL Open Requirements">Open Requirements</a>
			that need your attention. Please refer to the <a href="help/c/default.htm?turl=HTMLDocuments%2Fdesktopaudit.htm" target="_BLANK" title="Click Here to View the Help Guide in a new Window">Help Guide</a> 
			for additional instructions on how to close out your audit.
		</div>
	</s:if>
	<s:if test="conAudit.auditStatus.name() == 'Pending' && !conAudit.contractorAccount.paymentMethodStatusValid">
		<div id="info">Before you will be able to submit your information for review, you must <a href="ContractorEdit.action?id=<s:property value="conAudit.contractorAccount.id"/>"> update your payment method</a>.</div>		
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
	<s:if test="canResubmitPolicy">
		<div id="alert" class="buttons" style="">
			<s:submit id="submit" value="Resubmit" name="button"/>
			 I have reviewed and updated information for : 
			 <s:iterator value="conAudit.operators">
			 	<s:if test="status.name() == 'Rejected'"> 
			 		<s:property value="operator.name"/> ,
			 	</s:if>
			 </s:iterator>
		</div>
	</s:if>
	<s:if test="canClose">
		<div id="alert" class="buttons" style="">
			<s:hidden name="auditStatus" value="Active" />
			<s:submit value="%{'Close '.concat(conAudit.auditType.auditName)}"/>
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
	<tbody>
	<s:iterator value="conAudit.operators" status="rowStatus">
		<s:if test="permissions.operator || permissions.corporate">
			<s:if test="operator.id == permissions.accountId">
				<tr>
					<td><s:property value="operator.name"/></td>
					<td><a href="#" id="caoStatusMain_<s:property value="id"/>" class="edit" onclick="javascript: return editCao(<s:property value="id"/>);"><s:property value="status"/></a></td>
				</tr>
				<s:if test="notes != null && notes.length() > 0">
				<tr>
					<td colspan="2" id="caoNotesMain_<s:property value="id"/>">&nbsp;&nbsp;<s:property value="notes"/></td>
				</tr>
				</s:if>
			</s:if>
		</s:if>
		<s:else>
			<tr>
				<td><s:property value="operator.name"/></td>
			<td>
				<s:property value="status"/>
			</td>
			</tr>

			<s:if test="notes != null && notes.length() > 0">
				<tr>
					<td colspan="2">&nbsp;&nbsp;<s:property value="notes"/></td>
				</tr>
			</s:if>

			
		</s:else>
	</s:iterator>
	</tbody>
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
		<s:if test="conAudit.auditType.id == 17">
			<th>Score</th>
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
			<s:if test="conAudit.auditType.id == 17">
				<th><s:property value="printableScore"/></th>
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
<br clear="both"/>
<div id="caoThinking"></div>
<div id="caoSection"></div>
<br clear="both"/>
</body>
</html>
