<%@page language="java" errorPage="exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" /> 
<script src="js/validate_contractor.js" type="text/javascript"></script>
	<script type="text/javascript"
		src="js/scriptaculous/scriptaculous.js?load=effects"></script>

<script type="text/javascript">
	function setComment(auditId, questionId, catDataId ) {
		var comment = $F($('comment_' + questionId));
		
		startThinking({div:'status_'+questionId});

		var pars = 'auditData.audit.id='+auditId+'&catDataID='+catDataId+'&auditData.question.id=' + questionId + '&auditData.comment=' + comment;
		var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function(transport) {
			
				new Effect.Highlight($('comment_' + questionId),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			
				stopThinking({div:'status_'+questionId});
			}
		});
		return false;
	}
	
	 function changeAuditStatus(id, auditStatus) {
		var pars = 'auditID='+id+'&auditStatus='+auditStatus;
		var myAjax = new Ajax.Updater('','ContractorAuditSaveAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function() {
			$('verification_audit').innerHTML='';
			$('auditHeader').scrollTo();
			}
		});
		return false;
	 }

	function previewEmail() {
		pars = 'id='+<s:property value="contractor.id" />;
		$('emailTemplate').innerHTML ="<img src='images/ajax_process.gif' />";
		var myAjax = new Ajax.Updater('emailTemplate', 'VerifyPreviewEmailAjax.action', {method: 'post', parameters: pars});
	}
	 
	function sendEmail() {
		pars = 'id='+<s:property value="contractor.id" />;
		if($('body') != null && $('subject') != null) {
			pars +='&emailBody=' +escape($F($('body')));
			pars +='&emailSubject=' +escape($F($('subject')));
		}	
		var myAjax = new Ajax.Updater('emailStatus', 'VerifySendEmailAjax.action', {method: 'post', parameters: pars});
		new Effect.Highlight($('emailStatus'), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
	}
	 
</script>
</head>
<body >
<s:if test="verificationAudits.size() == 0">
	<div id="alert">No Audits found to verify</div>
</s:if>
<s:else>
	<s:form id="verify">
		<table class="report">
			<s:iterator value="verificationAudits">
				<thead>
					<tr><td><s:property value="auditType.auditName"/> - Questions</td>
						<td>Answer</td>
					</tr>	
				</thead>
				<s:iterator
					value="@com.picsauditing.actions.audits.ApproveInsurance@getQuestionAnswer(id).values">
					<tr>
						<td><s:property
							value="question.question" /></td>
						<td class="center"><s:if
							test="question.questionType.startsWith('File')">
							<s:if test="id > 0 && answer.length() > 0">
								<a
									href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.id=<s:property value="id"/>"
									target="_BLANK">View File</a>
							</s:if>
							<s:else>File Not Uploaded</s:else>
						</s:if> <s:else>
							<s:property value="answer" />
						</s:else></td>
					</tr>
				</s:iterator>
				<tr><td colspan="2">Notes: <s:textfield name="" size="70"/></td></tr>
				<tr><td colspan="2" style="text-align: center;">
				<div class="buttons">
					<button class="negative" name="button" onclick="return changeAuditStatus(<s:property value="conAudit.id"/>,'Reject');">Reject</button>
					<button class="positive" name="button" onclick="return changeAuditStatus(<s:property value="conAudit.id"/>,'Active');">Approve</button>
				</div>
				</td></tr>
			</s:iterator>
		</table>	
	</s:form>
</s:else>

<div class="clear"></div>
	<table>
		<tr class="blueMain">
			<td><div class="buttons">
					<button name="button" onclick="previewEmail();">Preview Email</button>
					<button class="positive" name="button" onclick="sendEmail();">Send Email</button>
				</div>
			</td>
		</tr>
		<tr>
			<td id="emailTemplate"></td>
		</tr>
		<tr>
			<td><div id="emailStatus" style="font-style: italic; color: red;"></div></td>
		</tr>
	</table>
</body>
</html>