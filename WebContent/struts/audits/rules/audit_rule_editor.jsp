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
		});
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
	});
});
</script>
</head>

<body>
<h1><s:if test="categoryRule">Category </s:if><s:else>Audit Type </s:else>Rule Editor</h1>
<s:include value="../../actionMessages.jsp"/>

<s:if test="rule == null || button == 'edit'">
	<div class="new">
		<s:form method="post" id="rule_form">
			<s:hidden name="rule.id"/>
			<fieldset class="form">
				<h2 class="formLegend">New Category Rule</h2>
				<ol>
					<li><label>Include</label>
						<s:checkbox name="rule.include"/>
					</li>
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
					</s:if>
					<li><label>Account Type</label>
						<s:select name="rule.contractorType" list="@com.picsauditing.jpa.entities.ContractorType@values()" listValue="type" headerKey="" headerValue=""/>
					</li>
					<li><label>Operator</label>
						<input type="text" class="searchAuto" id="operator" value="<s:property value="rule.operatorAccount.name"/>"/>
						<s:hidden name="rule.operatorAccount.id" id="op_hidden"/>
						<a href="#" class="clearfield">Clear Field</a>
					</li>
					<li><label>Risk</label>
						<s:select name="rule.risk" list="#{'':'','Low':'Low','Med':'Med','High':'High'}"/>
					</li>
					<li><label>Tag</label>
						<input type="text" class="searchAuto" id="tag" value="<s:property value="rule.tag.tag"/>"/>
						<s:hidden name="rule.tag.id" id="tag_hidden"/>
						<a href="#" class="clearfield">Clear Field</a>
					</li>
					<li><label>Bid-Only</label>
						<s:select name="bidOnly" list="#{-1:'Any',0:'No',1:'Yes'}" value="bidOnly"/>
					</li>
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
	<a class="add" href="?button=edit&rule.include=false">Create New Rule</a>

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
				<td colspan="<s:if test="categoryRule">12</s:if><s:else>13</s:else>" class="center"> <s:property value="key"/> </td>
			</tr>
			<s:iterator value="value" id="r">
				<s:include value="audit_rule_view.jsp"/>
			</s:iterator>
			
			<s:if test="'Current Rule' == key && canEditDelete">
				<tr class="rule-percents">
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
	
	<s:if test="moreGranular.size() == 100">
		<div class="alert">Only displaying first 100 "More Granular" rules.</div>
	</s:if>
</s:else>

</body>
</html>