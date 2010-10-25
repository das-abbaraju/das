<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:if test="categoryRule">Category </s:if><s:else>Audit Type </s:else>Rule Editor</title>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
<s:include value="../../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function() {
	$('.searchAuto').each(function(){
		var field =  $(this).attr('id');
		if(field=='operator')
			var num = 100;
		else
			var num = 10;
		$(this).autocomplete('<s:if test="categoryRule">Category</s:if><s:else>AuditType</s:else>RuleSearchAjax.action', {
			extraParams: {fieldName: field, button: 'searchAuto'},
			max: num,
			width: 200,
			formatItem : function(data,i,count){
				return data[1]
			},
			formatResult: function(data,i,count){
				return data[1];
			}
		}).result(function(event, data){
			event.preventDefault();
			$('#'+data[0]+'_hidden').val(data[2]);
			if(data[0]=='dAuditType'){
				if(data[2] > 0){
					$.getJSON('AuditTypeRuleSearchAjax.action',{button: 'dAuditStatus', 'aType': $('#dAuditType_hidden').val()}, 
						function(json){
							if(json){
								$('#dAuditSelect').html('');
								var options = json.options;
								for(var i=0; i<options.length; i++){
									$('#dAuditSelect').append($('<option>').attr('value', options[i].option).text(options[i].option));
								}
								$('#dAuditSelectli').show();
							}
						}
					);	
				}
			}else if(data[0]=='op'){
				if(data[2] > 0){
					$.getJSON('AuditRuleSearchAjax.action',{button: 'opTagFind', 'opID': $('#op_hidden').val()}, 
						function(json){
							if(json){
								$('#tag').html('');
								var tags = json.tags;
								for(var i=0; i<tags.length; i++){
									$('#tag').append($('<option>').attr('value', tags[i].tagID).text(tags[i].tag));
								}
								$('#opTagli').show();
							} 
						}
					);	
				}
			}
		});
	});
	$('.hide-rule-percents').click(function(){
		if($('.rule-percents').is(':hidden')){
			$('.rule-percents').show();
			$('.hide-rule-percents > td').text('Click to hide Rules to Create');
		} else{
			$('.rule-percents').hide();
			$('.hide-rule-percents > td').text('Click to show Rules to Create');
		}
	});
});

$(function() {
	$('.rule-header.less-granular').click(function() {
		$('tr.less-granular').not('.rule-header').toggle();
	});
	$('.rule-header.similar-rules').click(function() {
		$('tr.similar-rules').not('.rule-header').toggle();
	});
	$('.rule-header.more-granular').click(function() {
		$('tr.more-granular').not('.rule-header').toggle();
	});
});

$(function() {
	$('#rule_form').submit(function() {
		$('input[name]',this).each(function() {if ($(this).blank()) $(this).remove()});
	});

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
<h1><s:if test="categoryRule">Category </s:if><s:else>Audit Type </s:else>Rule Editor</h1>
<s:include value="../../actionMessages.jsp"/>

<s:if test="rule == null || button == 'edit'">
	<a href="<s:property value="categoryRule ? 'Category' : 'AuditType' " />RuleEditor.action?id=<s:property value="id" />">Back to List</a>
	<div class="new">
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
						<input type="text" class="searchAuto" id="auditType"  value="<s:property value="rule.auditType.auditName"/>"/>
						<s:hidden name="rule.auditType.id" id="auditType_hidden"/>
						<a href="#" class="clearfield">Clear Field</a>
					</li>
					<s:if test="categoryRule">
						<li><label>Category</label>
							<input type="text" class="searchAuto" id="category" value="<s:property value="rule.auditCategory.name"/>"/>
							<s:hidden name="rule.auditCategory.id" id="cat_hidden"/>
							<a href="#" class="clearfield">Clear Field</a>
						</li>
						<li><label>Top or Sub Category</label>
							<% //Do not change, if subcat is true then we set rootcat to be false and vice versa %>
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
					<li><label>Operator</label>
						<input type="text" class="searchAuto" id="operator" value="<s:property value="rule.operatorAccount.name"/>"/>
						<s:hidden name="rule.operatorAccount.id" id="op_hidden"/>
						<a href="#" class="clearfield">Clear Field</a>
					</li>					
					<li id="opTagli" <s:if test="rule.tag==null">style="display: none;"</s:if>><label>Tag</label>
						<s:select list="OpTagList" name="rule.tag.id" listKey="id" listValue="tag" id="tag" />
					</li>
					<s:if test="!categoryRule">
						<li><label>Dependent Audit</label>
							<input type="text" class="searchAuto" id="dAuditType" value="<s:property value="rule.dependentAuditType.auditName"/>"/>
							<s:hidden name="rule.dependentAuditType.id" id="dAuditType_hidden"/>
							<a href="#" class="clearfield">Clear Field</a>
						</li>					
						<li id="dAuditSelectli" <s:if test="rule.dependentAuditStatus==null">style="display: none;"</s:if>><label>Dependent Status</label>
							<s:select list="DAuditStatus" name="rule.dependentAuditStatus" id="dAuditSelect" />
							<a href="#" class="clearfield">Clear Field</a>
						</li>					
					</s:if>
					<li><label>Question</label>
						<input type="text" class="searchAuto" id="question" value="<s:property value="rule.question.name"/>"/>
						<s:hidden name="rule.question.id" id="question_hidden"/>
						<a href="#" class="clearfield">Clear Field</a>
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
	</div>
</s:if>

<s:else>
	| <a href="CategoryRuleSearch.action">Search</a> | <a href="?id=1">Top</a> |
	<a class="add" href="?button=edit&rule.include=true">Create New Rule</a> |
	<a class="refresh" href="RecalculateRules.action">Recalculate Priority for all <s:if test="categoryRule">Category</s:if><s:else>Audit Type</s:else> Rules</a> |

	<table class="report">

	<s:if test="canEditDelete">
		<s:set name="showAction" value="true"/>
	</s:if>
	<s:else>
		<s:set name="showAction" value="false"/>
	</s:else>
	<s:include value="audit_rule_header.jsp"/>
	
	<s:iterator value="#{'Less Granular': lessGranular, 'Current Rule': rule, 'More Granular': moreGranular}">
		<s:if test="'Current Rule' == key || value.size() > 0">
			<s:set name="ruleclass" value="%{key.toLowerCase().replaceAll(' ', '-')}"/>
			<tr class="rule-header clickable <s:property value="#ruleclass"/>">
				<td colspan="<s:if test="categoryRule">13</s:if><s:else>14</s:else>" class="center"> <s:property value="key"/> </td>
			</tr>
			<s:iterator value="value" id="r">
				<s:include value="audit_rule_view.jsp"/>
			</s:iterator>
			
			<s:if test="'Current Rule' == key && canEditDelete">
				<tr class="hide-rule-percents clickable">
					<td colspan="<s:if test="categoryRule">13</s:if><s:else>14</s:else>" class="center">Click to show Rules to Create</td>
				</tr>
				<tr class="rule-percents" style="display: none;">
					<td></td> <!-- delete column -->
					<s:iterator value="columns" id="col"> <!-- all columns -->
						<td>
							<s:if test="#col.value!=null">
								<s:iterator value="#col.value" id="entry"> <!-- iterate over value, is a map with 1 entry -->
									<table class="inner">
										<s:iterator value="getPercentOn(#entry.key)">
											<tr>
												<td class="right"><a href="?button=create&id=<s:property value="id"/>&rule.include=<s:property value="get('percentOn') > .5"/>&<s:property value="#entry.value"/><s:property value="get(#entry.key)"/>"><s:property value="%{get(#entry.key)}"/></a></td>
												<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
												<td><s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/></td>
											</tr>
										</s:iterator>
									</table>
								</s:iterator>
							</s:if>
						</td>
					</s:iterator>
					<td></td> <!-- delete column -->
				</tr>
			</s:if>
		</s:if>
	</s:iterator>
	</table>
	
	<s:if test="moreGranular.size() == 250">
		<div class="alert">Only displaying first 250 "More Granular" rules.</div>
	</s:if>
</s:else>

</body>
</html>