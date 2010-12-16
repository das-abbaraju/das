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
		if ($(this).blank())
			$('#question_display').html('');
	}).autocomplete('AuditQuestionAutocomplete.action', {
		formatItem  : function(data,i,count) {
			return data[1];
		},
		formatResult: function(data,i,count) {
			return data[0];
		}
	}).result(function(event, data) {
		$('#question_display').html(data[1]);
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
		if ($.trim($(this).val()).length == 0) {
			$('#tag').html('');
			$('#opTagli').hide();
		} else {
			$.getJSON('AuditRuleSearchAjax.action',{button: 'opTagFind', 'opID': $('#operator').val()}, 
				function(json) {
					if (json) {
						$('#tag').html('');
						var tags = json.tags;
						$('#tag').append($('<option>').attr('value', -1).text("- Any -"));
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
		if ($.trim($(this).val()).length == 0) {
			$('#dAuditSelect').html('');
			$('#dAuditSelectli').hide();
		} else {
			$.getJSON('AuditTypeRuleSearchAjax.action', {button: 'dAuditStatus', 'aType': $(this).val()}, 
				function(json) {
					if (json) {
						$('#dAuditSelect').html('');
						var options = json.options;
						$('#dAuditSelect').append($('<option>').attr('value', '').text("- Any -"));
						for(var i=0; i<options.length; i++) {
							$('#dAuditSelect').append($('<option>').attr('value', options[i].option).text(options[i].option));
						}
						$('#dAuditSelectli').show();
					}
				}
			);
		}
	});
	$('#question').change(function() {
		if ($.trim($(this).val()).length == 0) {
			$('#question_display').text('');
		}
	});
});

$(function() {
	$('a.clearfield').click(function(e) {
		e.preventDefault();
		$('input', $(this).parent()).val('');
		if($('input', $(this).parent()).attr('id')=='operator'){
			$('#tag').html('');
			$('#opTagli').hide();
		} else if($('input', $(this).parent()).attr('id')=='dAuditType'){
			$('#dAuditSelect').html('');
			$('#dAuditSelectli').hide();
		}
	});
});
</script>
</head>
<body>
<h1><s:property value="ruleType"/> Rule Editor</h1>
<s:include value="../actionMessages.jsp"/>

<div id="summary">
	<s:property value="rule.toString()"/>
	<ol>
		<li><label>Created By</label>
			<s:property value="rule.createdBy"/>
		</li>
		<li><label>Creation Date</label>
			<s:property value="rule.creationDate"/>
		</li>
		<li><label>Updated By</label>
			<s:property value="rule.updatedBy"/>
		</li>
		<li><label>Updated Date</label>
			<s:property value="rule.updateDate"/>
		</li>
	</ol>
</div>

<div id="detail">
	<s:if test="canEditRule">
		<s:form method="post" id="rule_form">
			<s:hidden name="rule.id"/>
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
						<s:select id="auditType" name="rule.auditType.id" list="{}" headerKey="" headerValue=" - Audit Type - ">
							<s:iterator value="auditTypeMap" var="aType">
								<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
							</s:iterator>
						</s:select>
					</li>
					<s:if test="!auditTypeRule">
						<li><label>Category</label>
							<s:textfield cssClass="autocomplete" id="category" name="rule.auditCategory.id"/>
							<div id="category_display"></div>
							<s:if test="rule.auditCategory.id != null">
								<div><a href="ManageCategory.action?id=<s:property value="rule.auditCategory.id"/>">Go To Category</a></div>
							</s:if>
						</li>
						<li><label>Top or Sub Category</label>
							<s:select list="#{-1:'Any',0:'Sub Categories',1:'Top Categories'}" name="rootCat"/> 
						</li>
					</s:if>
					<li><label>Bid-Only</label>
						<s:select name="bidOnly" list="#{-1:'Any',0:'No',1:'Yes'}" value="bidOnly"/>
					</li>
					<li><label>Account Type</label>
						<s:select name="rule.contractorType" list="@com.picsauditing.jpa.entities.ContractorType@values()" listValue="type" headerKey="" headerValue="Any"/>
					</li>
					<li><label>Risk</label>
						<s:select name="rule.risk" list="#{'':'Any','Low':'Low','Med':'Medium','High':'High'}"/>
					</li>
					<li <s:if test="operatorRequired">class="required"</s:if>>
						<label>Operator</label>
						<s:select id="operator" name="rule.operatorAccount.id" list="operatorList" headerKey="" headerValue="- Operator -" listKey="id" listValue="name"></s:select>
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
					<li id="opTagli" <s:if test="rule.operatorAccount==null">style="display: none;"</s:if>><label>Tag</label>
						<s:select list="operatorTagList" name="tagID" listKey="id" listValue="tag" id="tag" headerKey="0" headerValue="- Any -"
						value="rule.tag.id" />
					</li>
					<s:if test="auditTypeRule">
						<li><label>Dependent Audit</label>
							<s:select id="dependentAudit" name="rule.dependentAuditType.id" list="{}" headerKey="" headerValue=" - Audit Type - ">
								<s:iterator value="auditTypeMap" var="aType">
									<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
								</s:iterator>
							</s:select>
						</li>					
						<li id="dAuditSelectli" <s:if test="rule.dependentAuditStatus==null">style="display: none;"</s:if>><label>Dependent Status</label>
							<s:select list="dependentAuditStatus" name="rule.dependentAuditStatus" id="dAuditSelect" headerKey="" headerValue="- Any -" />
						</li>					
					</s:if>
					<li><label>Question</label>
						<s:textfield cssClass="autocomplete" id="question" name="rule.question.id"/>
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
					<li><label>Question Comparator</label>
						<s:select name="rule.questionComparator" list="@com.picsauditing.jpa.entities.QuestionComparator@values()" headerKey="" headerValue=""/>
					</li>
					<li><label>Answer</label>
						<s:textfield name="rule.questionAnswer" />
					</li>
				</ol>
			</fieldset>
			<fieldset class="form submit">
				<input type="submit" class="picsbutton positive" name="button" value="Save"/>
			</fieldset>
		</s:form>
	</s:if>
	<s:else>
		
	</s:else>
</div>

</body>
</html>