<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Question</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function copyQuestion(atypeID) {
	$('#copy_audit').load('ManageQuestionCopyAjax.action', {button: 'text', 'id': atypeID},
		function() {
			$(this).dialog({
				modal: true, 
				title: 'Copy Question',
				width: '55%',
				close: function(event, ui) {
					$(this).dialog('destroy');
					location.reload();
				},
				buttons: {
					Cancel: function() {
						$(this).dialog('close');
					},
					'Copy Question': function() {
						var data = $('form#textForm').serialize();
						data += "&button=Copy&originalID="+atypeID;
						startThinking( {div: 'copy_audit', message: 'Copying Question...' } );
						$.ajax(
							{
								url: 'ManageQuestionCopyAjax.action',
								data: data,
								complete: function() {
									stopThinking( {div: 'copy_audit' } );
									$(this).dialog('close');
									location.reload();
								}
							}
						);
					}
				}
			});
		}
	);
}

function moveQuestion(atypeID) {
	$('#copy_audit').load('ManageQuestionMoveAjax.action', {button: 'text', 'id': atypeID},
		function() {
			$(this).dialog({
				modal: true, 
				title: 'Move Question',
				width: '55%',
				close: function(event, ui) {
					$(this).dialog('destroy');
				},
				buttons: {
					Cancel: function() {
						$(this).dialog('close');
					},
					'Move Question': function() {
						var data = $('form#textForm').serialize();
						data += "&button=Move&originalID="+atypeID;
						startThinking( {div: 'copy_audit', message: 'Moving Question...' } );
						$.ajax(
							{
								url: 'ManageQuestionMoveAjax.action',
								data: data,
								complete: function() {
									stopThinking( {div: 'copy_audit' } );
									$(this).dialog('close');
									location.reload();
								}
							}
						);
					}
				}
			});
		}
	);
}
</script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<s:form id="save">
	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{question.category.id}"/>
	<fieldset class="form">
	<h2 class="formLegend">Question</h2>
	<ol>
		<li><label>ID:</label>
			<s:if test="question.id > 0">
				<s:property value="question.id" />
			</s:if>
			<s:else>
				NEW
			</s:else>
		</li>
		<li><label>Name:</label>
			<s:textarea name="question.name" />
		</li>		
		<li><label>Effective Date:</label>
			<s:textfield name="question.effectiveDate" value="%{ question.effectiveDate && getText('short_dates', {question.effectiveDate})}"/>
		</li>
		<li><label>Expiration Date:</label>
			<s:textfield name="question.expirationDate" value="%{ question.expirationDate && getText('short_dates', {question.expirationDate})}"/>
		</li>
		<s:if test="question.id > 0">
			<li><label>Added:</label>
				<s:date name="question.creationDate" />
			</li>
			<li><label>Updated:</label>
				<s:date name="question.updateDate" />
			</li>
		</s:if>
		<li><label>Column Header:</label>
			<s:textfield name="question.columnHeader" size="20" maxlength="30"/>
		</li>	
		<li><label>Field Identifier:</label>
			<s:textfield name="question.uniqueCode" size="20" maxlength="50"/>
		</li>
		<li><label>Has Requirement:</label>
			<s:checkbox name="question.hasRequirement"/>
		</li>
		<li><label>OK Answer:</label>
			<s:textfield name="question.okAnswer" />
		</li>
		<li><label>Requirement</label>
			<s:textfield name="question.requirement" />
		</li>
		<li><label>Flaggable:</label>
			<s:checkbox name="question.flaggable"/>
		</li>
		<li><label>Required:</label>
			<s:checkbox name="question.required" />
		</li>
		<li><label>Required by Question:</label>
			<s:textfield name="requiredQuestionID" />
			<s:if test="requiredQuestionID > 0"><a href="?id=<s:property value="requiredQuestionID" />">Show</a></s:if>
			<div class="fieldhelp">
				<h3>Required by Question</h3>
				<p>The question that determines whether or not this question is required.</p>
			</div>
		</li>
		<li><label>Required Answer:</label>
			<s:textfield name="question.requiredAnswer" />
			<div class="fieldhelp">
				<h3>Required Answer</h3>
				<p>If the "Required by Question" has this answer, this question will become a required question.</p>
			</div>
		</li>
		<li><label>Visible Question:</label>
			<s:textfield name="visibleQuestionID" />
			<div class="fieldhelp">
				<h3>Visible Question</h3>
				<p>The question that determines whether or not this question is visible.</p>
			</div>
		</li>
		<li><label>Visible Answer:</label>
			<s:textfield name="question.visibleAnswer" />
			<div class="fieldhelp">
				<h3>Visible Answer</h3>
				<p>If the "Visible" has this answer, this question will become a required question.</p>
			</div>
		</li>
		<li><label>Question Type:</label>
			<s:select list="questionTypes" name="question.questionType" />
		</li>
		<li><label>Risk Level:</label>
			<s:select list="@com.picsauditing.jpa.entities.LowMedHigh@values()" name="question.riskLevel" />
		</li>		
		<li><label>Title:</label>
			<s:textfield name="question.title" size="65"/>
		</li>
		<li><label>Grouped with Previous:</label>
			<s:checkbox name="question.groupedWithPrevious"/>
		</li>
		<li><label>Show Comments:</label>
			<s:checkbox name="question.showComment" value="question.showComment"/>
		</li>
		<li><label>Help Page:</label>
			<div>
				<s:textfield name="question.helpPage" size="30" maxlength="100" />
				<s:if test="question.helpPage.length() > 0"><a href="http://help.picsauditing.com/wiki/<s:property value="question.helpPage"/>">Help Center</a></s:if>
				<s:else>help.picsauditing.com/wiki/???</s:else>
			</div>
		</li>		
	</ol>
	</fieldset>
	<fieldset class="form submit">
		<div>
			<button class="picsbutton positive" name="button" type="submit" value="save">Save</button>
			<input type="button" class="picsbutton" value="Copy" onclick="copyQuestion(<s:property value="id"/>)"/>
			<input type="button" class="picsbutton" value="Move" onclick="moveQuestion(<s:property value="id"/>)"/>
		<s:if test="question.id > 0">
			<button name="button" class="picsbutton negative" type="submit" value="delete">Delete</button>
		</s:if>
		</div>
	</fieldset>
</s:form>

<div id="copy_audit"></div>
</body>
</html>