<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />

<script type="text/javascript" src="js/prototype.js"></script>
<s:if test="mode == 'Edit' || mode == 'Verify'">
	<script type="text/javascript"
		src="js/scriptaculous/scriptaculous.js?load=effects"></script>
	<script type="text/javascript" src="js/validateForms.js"></script>
	<script type="text/javascript" src="js/audit_cat_edit.js"></script>
</s:if>
<script type="text/javascript">
	var auditID = <s:property value="auditID"/>;
	var catDataID = <s:property value="catDataID"/>;
	var conID = <s:property value="conAudit.contractorAccount.id"/>;
	var mode = '<s:property value="#parameters.mode"/>';

	function verifyReject(status, opID) {
		var caoNotes = $('notes_'+opID) != null ? $('notes_'+opID).value : "";
		startThinking( {div:'cao_' + opID, type: 'large'} );
		var pars = {
			auditID: auditID,
			button: status,
			caoNotes: caoNotes,
			opID: opID			
		};
		
		var myAjax = new Ajax.Updater('cao_verification', 'PolicySaveAjax.action',
				{
					method: 'post',
					parameters: pars
				});
	}

	function submitPolicy(opID, isResubmit) {
		startThinking( {div: 'submit_'+opID, type: 'large'} );
		var pars = {
			opID: opID,
			auditID: auditID,
			button: (isResubmit)? 'Resubmit' : 'Submit'
		};

		var myAjax = new Ajax.Updater('cao_submit', 'AuditCatSubmitAjax.action',
				{
					method: 'post',
					parameters: pars
				});
	}
	
	function editCao( caoId, act ) {
		var pars;
		if ("save" == act)
			pars= $('caoForm').serialize();
		else
			pars= 'cao.id=' + caoId;
		startThinking( {div: 'caoSection', type: 'large' } );
		
		var myAjax = new Ajax.Updater($('caoSection'),'CaoEditAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				if (transport.status == 200) {
					$('caoSection').show();
				}
			}
		});
		
		return false;
	}

	function changeButton(button, checked) {
		$(button).disabled = !checked;
	}

	function openOsha(logID) {
		url = 'DownloadOsha.action?id='+logID;
		title = 'Osha300Logs';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
		window.open(url,title,pars);
	}
</script>
</head>
<body>

<s:include value="audit_catHeader.jsp"/>

<s:if test="auditID > 0">
	<s:if test="!singleCat">
		<s:include value="audit_cat_nav.jsp" />
	</s:if>
	<div id="caoSection"></div>
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
<s:if test="mode == 'Edit'">
	<div
		<s:if test="'done' == button">
			id="alert"
		</s:if>
	>
	<div class="requiredLegend">Starred questions are required to continue from this page</div>
	</div>
</s:if>
<s:iterator value="categories">
	<s:if test="catDataID == id || (catDataID == 0 && appliesB)">
		<s:if test="category.id in { 151, 157, 158 }">
			<s:include value="audit_cat_sha.jsp"></s:include>
		</s:if>
		<s:else>
			<s:if test="category.validSubCategories.size() > 0">
				<h2>Category <s:property value="category.number"/> - <s:property value="category.category"/></h2>
				<s:set name="shaded" value="true" scope="action"/>
				<s:iterator value="category.validSubCategories">
					<div class="subCategory">
						<s:if test="category.validSubCategories.size() > 1">
							<h3 class="subCategory">
								Sub Category <s:property value="category.number"/>.<s:property value="number"/> - 
								<s:property value="subCategory" escape="false"/>
							</h3>
							<s:if test="helpText != null && helpText.length() > 0">
								<div id="alert"><s:property value="helpText"/></div>
							</s:if>
						</s:if>
						<s:iterator value="questions">
							<s:if test="parentQuestion == null">
								<s:if test="title != null && title.length() > 0">
									<h4 class="groupTitle">
										<s:property value="title" escape="false"/>
									</h4>
								</s:if>
								<s:if test="mode == 'ViewQ'">
									<div class="question <s:if test="shaded">shaded</s:if>">
										<s:include value="audit_cat_questions.jsp"></s:include>
									</div>
								</s:if>
													
								<s:if test="valid">
									<s:set name="q" value="[0]" />
									<s:if test="#q.allowMultipleAnswers">
										<!-- Tuple Anchor Question -->
										<div id="tuple_<s:property value="#q.id"/>">
											<s:include value="audit_cat_tuples.jsp"></s:include>
										</div>
									</s:if>
									<s:else>
										<!-- Single Leaf Question -->
										<s:set name="a" value="answerMap.get(#q.id)" />
										<s:set name="visible" value="#q.visible" />
										<s:if test="onlyReq && !#a.hasRequirements">
											<s:set name="visible" value="false" />
										</s:if>
										<s:if test="!viewBlanks && (#a == null || #a.answer == null || #a.answer.length() == 0)">
											<s:set name="visible" value="false" />
										</s:if>
										<s:if test="#visible">
											<s:if test="#q.isGroupedWithPrevious.toString() == 'No'">
												<s:set name="shaded" value="!#shaded" scope="action"/>
											</s:if>
	
											<s:if test="#a.parentAnswer != null">
												<s:set name="paid" value="#a.parentAnswer.id"/>
											</s:if>
											<s:else>
												<s:set name="paid" value="0"/>
											</s:else>
											
											<div id="node_<s:property value="#attr.paid"/>_<s:property value="#q.id"/>" class="question <s:if test="#shaded">shaded</s:if>">
												<s:include value="audit_cat_question.jsp"></s:include>
											</div>
										</s:if>
									</s:else>
								</s:if>
							</s:if>
						</s:iterator>
					</div>
				</s:iterator>

			</s:if>
		</s:else>
	</s:if>
</s:iterator>
<s:if test="catDataID > 0">
	<br clear="all"/>
	<pics:permission perm="InsuranceVerification">
		<s:if test="conAudit.auditType.classType.policy">
			<s:if test="hasSubmittedCaos">
				<div id="cao_verification">
					<s:include value="audit_cat_cao_verification.jsp"/>
				</div>
			</s:if>
			<div class="buttons" style="float:right">
				<s:if test="needsNextPolicyForContractor">
					<a class="button" href="AuditCat.action?auditID=<s:property value="nextPolicy.id"/>&catDataId=<s:property value="nextPolicy.categories.get(0).id"/>"> Next Policy &gt;</a>
				</s:if>
				<a class="button" href="PolicyVerification.action?filter.visible=Y&filter.caoStatus=Submitted&button=getFirst"> Oldest Policy &gt;&gt;</a>
			</div>
			<br clear="all"/>
		</s:if>
	</pics:permission>
	<s:if test="!singleCat">
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
	<s:else>
		<s:if test="conAudit.auditType.classType.policy && canSubmitPolicy">
			<div id="cao_submit">
				<s:include value="audit_cat_policy_submit.jsp"/>
			</div>
		</s:if>
		<s:if test="conAudit.percentComplete < 100 && conAudit.auditStatus.pending && !conAudit.auditType.classType.policy">
			<div id="info" class="buttons" style="">
			<s:if test="conAudit.auditType.annualAddendum">
				<a href="Audit.action?auditID=<s:property value="auditID"/>" class="positive">Done</a>
			</s:if>
			Click Done when you're ready to submit the <s:property value="conAudit.auditType.auditName"/>
			</div>
		</s:if>
	</s:else>
</s:if>
</body>
</html>
