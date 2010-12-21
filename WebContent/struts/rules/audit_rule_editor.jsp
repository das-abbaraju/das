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
	$('#moreRelated').load('<s:property value="urlPrefix"/>RuleTableAjax.action',{id: <s:property value="id"/>, button: 'moreGranular'});
	$('#lessRelated').load('<s:property value="urlPrefix"/>RuleTableAjax.action',{id: <s:property value="id"/>, button: 'lessGranular'});
	$('.hideRule').hide();
	$('#editRuleButton').click(function(){
		$('.hideRule, .showRule').toggle();
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
			$.getJSON('AuditRuleSearchAjax.action', {button: 'dAuditStatus', 'aType': $(this).val()}, 
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
	$(['dependentAudit','auditType', 'operator']).each(function(i,val) {
		if($('#'+val).val()>0){
			var link = getLink(val, $('#'+val).val());
			$('#'+val+'_display').html(link);
		}
		$('#'+val).change(function() {
			var val_id = $(this).val();
			if(val_id == 0)
				return false;
			var link = getLink(val, val_id);
			$('#'+val+'_display').html(link);
		});
	});
});

function getLink(val, val_id){
	if($.inArray(val, ['dependentAudit','auditType'])!=-1)
		return $('<a>',{'href':'ManageAuditType.action?id='+val_id, 'class':'go'}).append('Go to Audit');
	else
		return $('<a>',{'href':'OperatorConfiguration.action?id='+val_id, 'class':'go'}).append('Go to Operator');
}
</script>
<style>
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
.requiredOperator {
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
	<s:if test="rule.id > 0">
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
						<li>
							<h4><s:property value="rule.toString()"/></h4>
							<a class="edit showPointer" id="editRuleButton"><span class="hideRule">Cancel Edit</span><span class="showRule">Edit Rule</span></a>
						</li>
					</ol>
				</fieldset>
			</s:if>
			<fieldset class="form hideRule">
				<h2 class="formLegend">Rule</h2>
				<ol>
					<s:if test="rule.id > 0">
						<li><label>Created By</label>
							<s:property value="rule.createdBy"/>
						</li>
						<li title="<s:date name="rule.updateDate" nice="true"/>"><label>Created</label>
							<s:date name="rule.creationDate"/>
						</li>
						<li><label>Updated By</label>
							<s:property value="rule.updatedBy"/>
						</li>
						<li title="<s:date name="rule.updateDate" nice="true"/>"><label>Updated</label>
							<s:date name="rule.updateDate"/>
						</li>
					</s:if>
					<li><label>Include</label>
						<s:radio theme="pics" list="#{true:'Yes',false:'No'}" name="rule.include"/>
					</li>
					<s:if test="auditTypeRule">
						<li><label>Manually Added</label>
							<s:radio theme="pics" list="#{true:'Yes',false:'No'}" name="rule.manuallyAdded"/>
						</li>
					</s:if>
					<li><label>Level</label>
						<s:property value="rule.level" default="0"/> + <s:textfield name="rule.levelAdjustment" />
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
							<div id="operator_display"></div>
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
			<fieldset class="form submit" style="margin-bottom: 0px;">
				<input type="submit" class="picsbutton positive" name="button" value="Save"/>
				<s:if test="'New' != button">
					<input type="submit" class="picsbutton" name="button" value="Copy"/>
					<input type="submit" class="picsbutton negative" name="button" value="Delete"/>
				</s:if>
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
		<script>
			$(function() {
				$('#detail').load('<s:property value="urlPrefix"/>RuleTableAjax.action', {id: <s:property value="id"/>});
			});
		</script>
	</s:elseif>
</div>

</body>
</html>