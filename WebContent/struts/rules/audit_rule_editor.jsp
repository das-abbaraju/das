<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="ruleType"/> Rule Editor</title>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function() {
	startThinking({div:'moreRelated', message: "Loading"});
	startThinking({div:'lessRelated', message: "Loading"});
	$('#moreRelated').load('<s:property value="urlPrefix"/>RuleTableAjax.action',{id: <s:property value="id"/>, button: 'moreGranular'});
	$('#lessRelated').load('<s:property value="urlPrefix"/>RuleTableAjax.action',{id: <s:property value="id"/>, button: 'lessGranular'});
	$('#operator').change(function() {
		if ($(this).blank()) {
			$('#operator_display').html('');
			$('#tag').find('option').remove();
			$('#opTagli').hide();
		}
	}).autocomplete('OperatorAutocomplete.action', {
		formatItem  : function(data,i,count) {
			return data[1];
		},
		formatResult: function(data,i,count) {
			return data[0];
		},
		max			: 50
	}).result(function(event, data) {
		$('#operator_display').html("<a target='_BLANK' href=\"OperatorConfiguration.action?id=" + data[0] + "\">" + data[1] + " Configuration</a>");
		$.getJSON('AuditRuleSearchAjax.action',{button: 'opTagFind', 'opID': data[0]},
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
	});
	$('#question').change(function() {
		if ($(this).blank()) {
			$('#question_display').html('');
			$('.requiresQuestion').hide();
			$('.requiresComparator').hide();
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
	<s:if test="!auditTypeRule">
	$('#category').change(function() {
		if ($(this).blank())
			$('#category_display').html('');
	}).autocomplete('CategoryAutocomplete.action', {
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
	</s:if>
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
	$('#ruleEditCheckbox').change(function() {
		if ($(this).is(':checked'))
			$("div.buttons .picsbutton").removeAttr("disabled");
		else
			$("div.buttons .picsbutton").attr("disabled", "disabled");
	});
});
</script>
<style>
.hideRule {
	display: none;
}
<s:if test="rule == null || rule.question == null">
.requiresQuestion {
	display: none;
}
</s:if>
<s:if test="rule == null || rule.dependentAuditStatus == null">
.requiresDependentAudit {
	display: none;
}
</s:if>
<s:if test="rule == null || rule.operatorAccount == null">
.requiresOperator {
	display: none;
}
</s:if>
<s:if test="rule == null || rule.questionComparator == null">
.requiresComparator {
	display: none;
}
</s:if>
#related {
	display: none;
}
</style>
</head>
<body>
<h1><s:property value="ruleType"/> Rule Editor</h1>
<s:include value="../actionMessages.jsp"/>
<div>
	<s:if test="rule.id > 0 && canEditRule">
			<a class="add" href="<s:property value="urlPrefix"/>RuleEditor.action?button=New">Create new rule</a>
	</s:if>
	<s:else>
		<script type="text/javascript">$(function(){$('.hideRule').show();});</script>
	</s:else>
</div>
<s:if test="rule != null">
	<s:if test="rule.effectiveDate.after(new java.util.Date())">
		<div class="alert">
			This rule will not go into effect until <s:date name="rule.effectiveDate" format="MM/dd/yyyy"/>.
		</div>
	</s:if>
	<s:elseif test="!rule.current">
		<div class="alert">
			This rule is no longer in effect, it was removed by <s:property value="rule.updatedBy.name"/>.
		</div>
	</s:elseif>
</s:if>
<div id="detail">
	<s:if test="canEditRule && rule.current">
		<s:form method="post" id="rule_form">
			<s:hidden name="id"/>
			<s:if test="rule.id > 0">
				<fieldset class="form lessGran">
					<h2 class="formLegend">Less Granular</h2>
					<div id="lessRelated" style="padding-top:10px;"></div>
				</fieldset>
				<fieldset class="form">
					<h2 class="formLegend">Summary</h2>
					<ol>
						<s:if test="rule.id > 0">
							<li>
								<h4><s:property value="rule.toString()"/></h4>
							</li>
							<li>
								Created by: <s:set var="o" value="rule" />
								<s:include value="../who.jsp" />
							</li>
							<li>
								<s:if test="rule.operatorAccount != null">
									<a href="FacilitiesEdit.action?id=<s:property value="rule.operatorAccount.id" />"
										><s:property value="rule.operatorAccount.name" /></a><br />
								</s:if>
								<s:if test="rule.auditType != null">
									<a href="ManageAuditType.action?id=<s:property value="rule.auditType.id" />"
										><s:property value="rule.auditType.name" /></a><br />
								</s:if>
								<s:if test="rule.auditCategory != null">
									<a href="ManageCategory.action?id=<s:property value="rule.auditCategory.id" />"
										><s:property value="rule.auditCategory.name" /></a><br />
								</s:if>
							</li>
							<s:if test="permissions.canEditAuditRules">
								<li>
									<a href="#edit" class="edit showPointer" id="editRuleButton" onclick="$(this).hide(); $('.hideRule').show();">Edit Rule</a>
								</li>
							</s:if>
						</s:if>
					</ol>
				</fieldset>
			</s:if>
			<fieldset class="form hideRule">
				<a name="edit"></a>
				<h2 class="formLegend">Rule</h2>
				<ol>
					<li><label>Include</label>
						<div class="nobr"><s:radio theme="pics" list="#{true:'Include',false:'Exclude'}" name="ruleInclude" value="rule.include"/></div>
					</li>
					<li><label>Level</label>
						<s:property value="%{rule.level-rule.levelAdjustment}" default="0"/> + <s:textfield name="rule.levelAdjustment" />
						<div class="fieldhelp">
						<h3>Level Adjustment</h3>
						<p>Level and priority values are auto calculated by the system. These cannot be modified by the user.</p>
						<p>Enter a positive number to have this rule run earlier or more important.</p>
						<p>Enter a negative number to have this rule run later or less important.</p>
						</div>
					</li>
					<li><label>Priority</label>
						<s:property value="rule.priority"/>
					</li>
				</ol>
			</fieldset>
			<fieldset class="form hideRule">
				<h2 class="formLegend">Options</h2>
				<ol>
					<li><label>Audit Type</label>
						<s:select id="auditType" name="ruleAuditTypeId" value="rule.auditType.id" list="{}" headerKey="0" headerValue="Any Audit Type">
							<s:iterator value="auditTypeMap" var="aType">
								<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
							</s:iterator>
						</s:select>
						<div id="auditType_display"></div>
					</li>
					<s:if test="auditTypeRule">
						<li><label>Auto Add Audit</label>
							<div class="nobr"><s:radio theme="pics" list="#{false:'Auto Add',true:'Manually Added'}" name="rule.manuallyAdded"/></div>
							<div class="fieldhelp">
							<h3>Auto Add Audit</h3>
							<p>Auto Add (default) - a single audit is added to each contractor account that matches this rule.</p>
							<p>Manually Added - audits are available to be manually added to a contractor's account. Examples include: Field Audit and Integrity Management</p>
							</div>
						</li>
					</s:if>
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
							<div class="fieldhelp">
							<h3>Top or Sub Category</h3>
							<p>Only set this field if category is blank. This is auto selected by the system if a category has been selected.</p>
							</div>
						</li>
					</s:if>
					<li><label>Bid Only</label>
						<div class="nobr"><s:radio theme="pics" name="ruleAcceptsBids" list="#{'':'Any',false:'Full Account',true:'Bid Only'}" value="rule.acceptsBids"/></div>
						<div class="fieldhelp">
						<h3>Bid Only</h3>
						<p>Full Account (default) - Regular paying contractor account.</p>
						<p>Bid Only - A trial contractor account that is used for bidding.</p>
						</div>
					</li>
					<li><label>Account Type</label>
						<s:select name="rule.contractorType" list="@com.picsauditing.jpa.entities.ContractorType@values()" listValue="type" headerKey="" headerValue="Any"/>
					</li>
					<li><label>Risk</label>
						<div class="nobr"><s:radio theme="pics" name="rule.risk" list="#{'':'Any','Low':'Low','Med':'Medium','High':'High'}"/></div>
					</li>
					<li <s:if test="operatorRequired">class="required"</s:if>>
						<label>Operator</label>
						<s:textfield cssClass="autocomplete" id="operator" name="ruleOperatorAccountId" value="%{rule.operatorAccount.id}"/>
						<div id="operator_display">
							<s:if test="rule.operatorAccount != null">
								<a href="OperatorConfiguration.action?id=<s:property value="rule.operatorAccount.id"/>"><s:property value="rule.operatorAccount.name"/> Configuration</a>
							</s:if>
						</div>
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
							<div id="dependentAudit_display"></div>
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
						<s:select id="comparator" name="rule.questionComparator" list="@com.picsauditing.jpa.entities.QuestionComparator@values()" headerKey="" headerValue="Comparator"/>
					</li>
					<li class="requiresComparator"><label>Answer</label>
						<s:textfield name="rule.questionAnswer" />
					</li>
				</ol>
			</fieldset>
			<fieldset class="form hideRule submit" style="margin-bottom: 0px;">
				<s:if test="((!auditTypeRule && rule.priority < 300) || (auditTypeRule && rule.priority < 230)) && button != 'New'">
					<s:checkbox label="label" id="ruleEditCheckbox" name="ruleEditCheckbox" value="false" fieldValue="false" />I understand that I am changing a rule with potentially broad reaching affects.<br />
					<div class="buttons">
						<input type="submit" class="picsbutton positive" name="button" value="Save" disabled="disabled"/>
						<s:if test="'New' != button">
							<input type="submit" class="picsbutton" name="button" value="Copy" disabled="disabled"/>
							<input type="submit" class="picsbutton negative" name="button" value="Delete" disabled="disabled"/>
						</s:if>
					</div>
				</s:if>
				<s:else>
					<input type="submit" class="picsbutton positive" name="button" value="Save"/>
					<s:if test="'New' != button">
						<input type="submit" class="picsbutton" name="button" value="Copy"/>
						<input type="submit" class="picsbutton negative" name="button" value="Delete"/>
					</s:if>
				</s:else>
			</fieldset>
			<s:if test="rule.id > 0">
				<fieldset class="form moreGran">
					<h2 class="formLegend">More Granular</h2>
					<div id="moreRelated" style="padding-top:10px;"></div>
				</fieldset>
			</s:if>
		</s:form>
	</s:if>
	<s:elseif test="id > 0">
		<fieldset class="form lessGran">
			<h2 class="formLegend">Less Granular</h2>
			<div id="lessRelated"></div>
		</fieldset>
		<fieldset class="form">
			<h2 class="formLegend">Summary</h2>
			<ol>
				<li>
					<h4><s:property value="rule.toString()"/></h4>
				</li>
				<s:if test="rule.id > 0">
					<li>
						<s:property value="rule.getWhoString()"/>
					</li>
				</s:if>
			</ol>
			<s:if test="permissions.hasGroup(@com.picsauditing.jpa.entities.User@GROUP_MARKETING)">
				<div class="info">
					This rule is not specific to one of your Accounts. Please contact one of the Audit Rule Administrators if this rule needs to be changed.
				</div>
			</s:if>
		</fieldset>
		<fieldset class="form moreGran">
			<h2 class="formLegend">More Granular</h2>
			<div id="moreRelated"></div>
		</fieldset>
	</s:elseif>	
</div>

</body>
</html>