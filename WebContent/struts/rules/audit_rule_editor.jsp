<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="title"/> Rule Editor</title>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function() {
	$('#question').change(function() {
		if ($(this).blank()) {
			$('#question_display').html('');
			$('.requiresQuestion').hide();
		}
	}).autocomplete('AuditQuestionAutocomplete.action', {
		formatItem  : function(data,i,count) {
			return data[1];
		},
		formatResult: function(data,i,count) {
			return data[0];
		}
	}).result(function(event, data) {
		$('#question_display').html(data[1]);
		$('.requiresQuestion').show();
	});
	$('#category').change(function() {
		if ($(this).blank())
			$('#category_display').html('');
	}).autocomplete('AuditCategoryAutocomplete.action', {
		extraParams: {auditTypeID: $('#auditType').val()},
		formatItem  : function(data,i,count) {
			return data[1];
		},
		formatResult: function(data,i,count) {
			return data[0];
		}
	}).result(function(event, data) {
		$('#category_display').html(data[1]);
	});
	$('#operator').change(function() {
		if ($(this).blank()) {
			$('#tag').find('option').remove();
			$('#opTagli').hide();
		} else {
			$.getJSON('AuditRuleSearchAjax.action',{button: 'opTagFind', 'opID': $('#operator').val()}, 
				function(json) {
					if (json) {
						$('#tag').html('');
						var tags = json.tags;
						$('#tag').append($('<option>').attr('value', 0).text("Any"));
						for(var i=0; i<tags.length; i++) {
							$('#tag').append($('<option>').attr('value', tags[i].tagID).text(tags[i].tag));
						}
						$('#opTagli').show();
					} 
				}
			);
		}
	});
	$('#dependentAudit').change(function() {
		if ($(this).blank()) {
			$('#dAuditSelect').html('');
			$('.requiresDependentAudit').hide();
		} else {
			$.getJSON('AuditTypeRuleSearchAjax.action', {button: 'dAuditStatus', 'aType': $(this).val()}, 
				function(json) {
					if (json) {
						$('#dAuditSelect').html('');
						var options = json.options;
						$('#dAuditSelect').append($('<option>').attr('value', '').text("Any"));
						for(var i=0; i<options.length; i++) {
							$('#dAuditSelect').append($('<option>').attr('value', options[i].option).text(options[i].option));
						}
						$('.requiresDependentAudit').show();
					}
				}
			);
		}
	});
	$('#comparator').change(function() {
		if ($(this).blank()) {
			$('.requiresComparator').hide().find('input').val('');
		} else {
			$('.requiresComparator').show();
		}
	});
});
</script>
<style>
<s:if test="rule.question == null">
.requiresQuestion {
	display: none;
}
</s:if>
<s:if test="rule.dependentAuditStatus == null">
.requiresDependentAudit {
	display: none;
}
</s:if>
<s:if test="rule.operatorAccount == null">
.requiredOperator {
	display: none;
}
</s:if>
<s:if test="rule.questionComparator == null">
.requiresComparator {
	display: none;
}
</s:if>
</style>
</head>
<body>
<h1><s:property value="ruleType"/> Rule Editor</h1>
<s:include value="../actionMessages.jsp"/>

<div id="detail">
	<s:if test="canEditRule">
		<s:form method="post" id="rule_form">
			<s:hidden name="id"/>
			<fieldset class="form">
				<h2 class="formLegend">Summary</h2>
				<ol>
					<li>
						<s:property value="rule.toString()"/>
					</li>
					<li><label>Created By</label>
						<s:property value="rule.createdBy"/>
					</li>
					<li><label>Created</label>
						<s:date name="rule.creationDate" nice="true"/>
					</li>
					<li><label>Updated By</label>
						<s:property value="rule.updatedBy"/>
					</li>
					<li><label>Updated</label>
						<s:date name="rule.updateDate" nice="true"/>
					</li>
				</ol>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">Rule</h2>
				<ol>
					<li><label>Include</label>
						<s:checkbox name="rule.include"/>
					</li>
					<li><label>Level</label>
						<s:property value="rule.level"/> + <s:textfield name="rule.levelAdjustment" />
					</li>
					<li><label>Priority</label>
						<s:property value="rule.priority"/>
					</li>
				</ol>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">Options</h2>
				<ol>
					<li><label>Audit Type</label>
						<s:select  name="ruleAuditTypeId" value="rule.auditType.id" list="{}" headerKey="0" headerValue="Any Audit Type">
							<s:iterator value="auditTypeMap" var="aType">
								<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
							</s:iterator>
						</s:select>
					</li>
					<s:if test="!auditTypeRule">
						<li><label>Category</label>
							<s:textfield cssClass="autocomplete" id="category" value="%{rule.auditCategory.id}" name="ruleAuditCategoryId"/>
							<div id="category_display">
								<s:if test="rule.auditCategory != null">
									<s:iterator value="rule.auditCategory.ancestors" status="stat">
										<a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="name"/></a>
										<s:if test="!stat.last">
											&gt;
										</s:if>
									</s:iterator>
								</s:if>
							</div>
						</li>
						<li><label>Top or Sub Category</label>
							<s:select list="#{'':'Any',false:'Sub Categories',true:'Top Categories'}" name="rule.rootCategory"/> 
						</li>
					</s:if>
					<li><label>Bid-Only</label>
						<s:select name="ruleAcceptsBids" list="#{'':'Any',false:'No',true:'Yes'}" value="rule.acceptsBids"/>
					</li>
					<li><label>Account Type</label>
						<s:select name="rule.contractorType" list="@com.picsauditing.jpa.entities.ContractorType@values()" listValue="type" headerKey="" headerValue="Any"/>
					</li>
					<li><label>Risk</label>
						<s:select name="rule.risk" list="#{'':'Any','Low':'Low','Med':'Medium','High':'High'}"/>
					</li>
					<li <s:if test="operatorRequired">class="required"</s:if>>
						<label>Operator</label>
						<s:select id="operator" name="ruleOperatorAccountId" value="rule.operatorAccount.id" list="operatorList" headerKey="" 
							headerValue="Any Operator" listKey="id" listValue="name"></s:select>
						<s:if test="rule.operatorAccount.id != null">
							<div><a href="FacilitiesEdit.action?id=<s:property value="rule.operatorAccount.id"/>">Go To Operator</a></div>
						</s:if>
						<s:if test="operatorRequired"> 
							<div class="fieldhelp">
							<h3>Operator</h3>
							<p>You must specify the Operator that this rule will apply to</p>
							</div>
						</s:if>
					</li>
					<li id="opTagli" class="requiresOperator"<s:if test="rule.operatorAccount==null">style="display: none;"</s:if>><label>Tag</label>
						<s:select list="operatorTagList" name="ruleOperatorTagId" listKey="id" listValue="tag" id="tag" headerKey="0" headerValue="Any"
						value="rule.tag.id" />
					</li>
					<s:if test="auditTypeRule">
						<li><label>Dependent Audit</label>
							<s:select id="dependentAudit" name="ruleDependentAuditTypeId" value="rule.dependentAuditType.id" list="{}" headerKey="" headerValue="Any Audit Type">
								<s:iterator value="auditTypeMap" var="aType">
									<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
								</s:iterator>
							</s:select>
						</li>
						<li class="requiresDependentAudit"><label>Dependent Status</label>
							<s:select list="dependentAuditStatus" name="rule.dependentAuditStatus" id="dAuditSelect" headerKey="" headerValue="Any" />
						</li>					
					</s:if>
					<li><label>Question</label>
						<s:textfield cssClass="autocomplete" id="question" name="ruleQuestionId" value="%{rule.question.id}"/>
						<div id="question_display">
							<s:if test="rule.question != null">
								<a href="ManageAuditType.action?id=<s:property value="rule.question.auditType.id"/>"><s:property value="rule.question.auditType.auditName"/></a> &gt;
								<s:iterator value="rule.question.category.ancestors">
									<a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="name"/></a> &gt;
								</s:iterator>
								<a href="ManageQuestion.action?id=<s:property value="rule.question.id"/>"><s:property value="rule.question.name"/></a>
							</s:if>
						</div>
					</li>
					<li class="requiresQuestion"><label>Question Comparator</label>
						<s:select id="comparator" name="rule.questionComparator" list="@com.picsauditing.jpa.entities.QuestionComparator@values()" headerKey="" headerValue="- Comparator -"/>
					</li>
					<li class="requiresComparator"><label>Answer</label>
						<s:textfield name="rule.questionAnswer" />
					</li>
				</ol>
			</fieldset>
			<fieldset class="form submit">
				<input type="submit" class="picsbutton positive" name="button" value="Save"/>
				<input type="submit" class="picsbutton" name="button" value="Copy"/>
				<input type="submit" class="picsbutton negative" name="button" value="Delete"/>
			</fieldset>
		</s:form>
	</s:if>
	<s:else>
		
	</s:else>
</div>

</body>
</html>