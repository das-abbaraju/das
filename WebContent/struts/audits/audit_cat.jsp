<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />

<SCRIPT LANGUAGE="JavaScript" SRC="js/validateForms.js"></SCRIPT>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script type="text/javascript" src="js/aim.js"></script>

<script type="text/javascript">
<s:if test="mode == 'Edit' || mode == 'Verify'">
	function changeAnswer(questionid, questionType) {
		var elm = 'answer_'+questionid;
		var value = $F($(elm));
		if (questionType == 'Radio' || questionType == 'Yes/No' || questionType == 'Yes/No/NA') {
			var selector = "input[type=radio][name='verifiedAnswer_"+questionid+"'][value='"+value+"']";
			var input = $$(selector)[0];
			input.checked = true;
		}
		else if(questionType == 'Check Box') {
			if(value = 'X') {
				$('verifiedBox_'+questionid).checked = true;
			}
		}
		else {		
			$('verifiedBox_'+questionid).value = value;
		}
		saveVerifiedAnswer(questionid, elm); 
	}
	
	function saveVerifiedAnswer(questionid, elm) {
		var pars = 'auditData.audit.id=<s:property value="conAudit.id"/>&catDataID=<s:property value="catDataID"/>&auditData.question.questionID=' + questionid + '&auditData.verifiedAnswer=' + escape($F(elm));
		var divName = 'status_'+questionid;
		var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) {
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
	}
	
	function saveComment(questionid, elm) {
		<s:if test="catDataID == 0">return;</s:if>
		var pars = 'auditData.audit.id=<s:property value="conAudit.id"/>&catDataID=<s:property value="catDataID"/>&auditData.question.questionID=' + questionid + '&auditData.comment=' + escape($F(elm));
		var divName = 'status_'+questionid;
		var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) {
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
	}
	
	
	function saveAnswer( questionid, elm ) {
		<s:if test="catDataID == 0">return;</s:if>
		var pars = 'auditData.audit.id=<s:property value="conAudit.id"/>&catDataID=<s:property value="catDataID"/>&auditData.question.questionID=' + questionid + '&auditData.answer=';
		if( elm.type == 'text' || elm.type == 'radio' || elm.type == 'textarea')
		{
			var thevalue = escape(elm.value);
			//if( thevalue != '' ) {
			// Save blanks too
				pars = pars + thevalue;

				var divName = 'status_'+questionid;
				var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
				{
					method: 'post', 
					parameters: pars,
					onSuccess: function(transport) {
						$('required_td'+questionid).innerHTML = '';
						new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					}
				});
			//}
		}
		else if( elm.type == 'checkbox')
		{
			var thevalue = '';

			if( 
				( elm.name == ('question_' + questionid + '_C') || elm.name == ('question_' + questionid + '_S') )
				&& ( document.getElementById('question_' + questionid + '_C') != undefined 
				&& document.getElementById('question_' + questionid + '_S') != undefined)  
				) {

					if( document.getElementById('question_' + questionid + '_C').checked )
					{
						thevalue = thevalue + 'C';
					}
									
					if( document.getElementById('question_' + questionid + '_S').checked )
					{
						thevalue = thevalue + 'S';
					}
			}
			else {
				if (elm.checked)
					thevalue = 'X';
				else
					thevalue = ' ';
			}	

			pars = pars + thevalue;

			var divName = 'status_'+questionid;
			var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
			{
				method: 'post', 
				parameters: pars,
				onSuccess: function(transport) {
					$('required_td'+questionid).innerHTML = '';
					new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
				}
			});
		}
		else if (elm.type == 'select-one')
		{
			var thevalue = elm.value;
			
			pars = pars + thevalue;
			
			var divName = 'status_'+questionid;
			var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
			{
				method: 'post', 
				parameters: pars,
				onSuccess: function(transport) {
					$('required_td'+questionid).innerHTML = '';
					new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
				}
			});
		}
		else
		{
			alert(elm.type + ' ' +elm.value );
		}
		return true;
	}
	
	function showFileUpload( questionid ) {
		url = 'AuditDataUpload.action?auditID=<s:property value="auditID"/>&question.questionID=' + questionid;
		title = 'Upload';
		pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
	}
	
	function reloadQuestion( questionid ) {
		var pars = 'auditID=<s:property value="conAudit.id"/>&questionID=' + questionid;
		var divName = 'td_answer_'+questionid;
		$(divName).innerHTML="<img src='images/ajax_process.gif' />";
		var myAjax = new Ajax.Updater(divName,'ReloadQuestionAjax.action',
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) {
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
	}
	
</s:if>
	
	function openOsha(logID, year) {
		url = 'servlet/showpdf?id=<s:property value="contractor.id"/>&OID='+logID+'&file=osha'+year;
		title = 'Osha300Logs';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
		window.open(url,title,pars);
	}

	function openQuestion(questionID, extension) {
		url = 'servlet/showpdf?id=<s:property value="contractor.id"/>&file=pqf'+extension+questionID;
		title = 'PICSFileUpload';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
		window.open(url,title,pars);
	}
	
	function showAnswer(questionid) {
		$('showText_'+questionid).show();
	}
</script>

</head>
<body>
<s:if test="catDataID > 0">
<s:include value="../contractors/conHeader.jsp" />
<s:include value="audit_cat_nav.jsp" />
</s:if>
<div id="auditToolbar" class="right">
<s:if test="catDataID > 0">	
	<s:if test="mode != 'View'">
		<a class="view" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=View">Switch to View Mode</a>
	</s:if>
	<s:if test="mode != 'Edit' && canEdit">
		<a class="edit" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=Edit">Switch to Edit Mode</a>
	</s:if>
	<s:if test="mode != 'Verify' && canVerify">
		<a class="verify" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=Verify">Switch to Verify Mode</a>
	</s:if>
</s:if>
	<s:if test="mode == 'View' || mode == 'ViewQ' || catDataID == 0">
		<a href="javascript:window.print()" class="print">Print</a>
	</s:if>
</div>

<br clear="all"/>

<s:iterator value="categories">
	<s:if test="catDataID == id || (catDataID == 0 && appliesB)">
		<h2>Category <s:property value="category.number"/> - <s:property value="category.category"/></h2>
		<s:if test="category.id == 29">
				<s:if test="mode == 'View'">
					<s:iterator value="contractor.oshas">
						<s:include value="audit_cat_osha.jsp"></s:include>
					</s:iterator>
				</s:if>
				<s:if test="mode == 'Edit'">
					<s:if test="permissions.admin || permissions.contractor">
						<span class="redMain">You must input at least your corporate statistics. To further assist your clients, please
						enter additional locations that you maintain OSHA/MSHA logs for that may be needed by your clients.<br/>
						</span>
					</s:if>
					<s:if test="permissions.contractor">
						<span style="font-size: 12px;color:#003768;">Provide the following numbers (excluding subcontractors) using your OSHA/MSHA 300 Forms from the past 3 years:</span><br/>
					</s:if>
					<s:if test="catDataID == 0">
						<s:include value="audit_cat_osha_edit.jsp"/>
					</s:if>
					<s:iterator value="contractor.oshas">
						<s:include value="audit_cat_osha_edit.jsp"></s:include>
					</s:iterator>
					<s:if test="catDataID != 0">
					<s:form action="OshaSave" method="POST" enctype="multipart/form-data">
						<s:hidden name="conID" value="%{conAudit.contractorAccount.id}"></s:hidden>
						<s:hidden name="auditID"></s:hidden>
						<s:hidden name="catDataID"></s:hidden>
						<s:hidden name="oshaID" value="%{id}"></s:hidden>
						<s:submit name="submit" value="Add New Location" cssStyle="padding: 6px;position: relative;left: 380px;"></s:submit>
					</s:form>
					</s:if>
				</s:if>
		</s:if>
		<s:else>
			<table class="audit">
			<s:iterator value="category.subCategories">
				<s:if test="category.subCategories.length > 1">
				<tr class="subCategory">
					<td colspan="4">Sub Category <s:property value="category.number"/>.<s:property value="number"/> - 
					<s:property value="subCategory" escape="false"/>
					</td>
				</tr>
				</s:if>
				<s:iterator value="questions">
				<s:if test="catDataID == 0 || (effectiveDate.before(conAudit.createdDate) && expirationDate.after(conAudit.createdDate))">
					<s:if test="isGroupedWithPrevious.toString() == 'No'">
						<s:set name="shaded" value="!#shaded" scope="action"/>
					</s:if>
					
					<s:if test="title.length() > 0">
						<tr class="group<s:if test="#shaded">Shaded</s:if>">
							<td class="groupTitle" colspan="4"><s:property value="title" escape="false"/></td>
						</tr>
					</s:if>
					<s:if test="mode == 'View'">
						<s:if test="onlyReq">
							<s:if test="answer.hasRequirements">
								<s:include value="audit_cat_view.jsp"></s:include>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="viewBlanks || answer.answer.length() > 0">
								<s:include value="audit_cat_view.jsp"></s:include>
							</s:if>
						</s:else>
					</s:if>
					<s:if test="mode == 'Edit'">
						<s:if test="!onlyReq || answer.hasRequirements">
							<s:include value="audit_cat_edit.jsp"></s:include>
						</s:if>
					</s:if>
					<s:if test="mode == 'Verify'">
						<s:if test="answer.answer.length() > 0">	
							<s:include value="audit_cat_verify.jsp"></s:include>
						</s:if>
					</s:if>
					<s:if test="mode == 'ViewQ'">
						<s:include value="audit_cat_questions.jsp"></s:include>
					</s:if>
				</s:if>
				</s:iterator>
			</s:iterator>
			</table>
			<span class="requiredStar">* Question is required</span>
		</s:else>
	</s:if>
</s:iterator>
<s:if test="catDataID > 0">
<br clear="all"/>
<div class="buttons" style="float: right;">
	<s:if test="nextCategory == null">
		<a href="Audit.action?auditID=<s:property value="auditID"/>" class="positive">Next &gt;&gt;</a>
	</s:if>
	<s:else>
		<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="nextCategory.id"/>&mode=<s:property value="mode"/>" class="positive">Next &gt;&gt;</a>
	</s:else>
</div>
<br clear="all"/>
<s:include value="audit_cat_nav.jsp" />
</s:if>
</body>
</html>
