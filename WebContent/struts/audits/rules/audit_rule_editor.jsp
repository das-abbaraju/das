<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
<s:include value="../../jquery.jsp"/>
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
		$(this).autocomplete('CategoryRuleSearchAjax.action', {
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
});
</script>
<style>
	tr.less-granular td {
		font-style: italic;
	}
	tr.less-granular.on {
		background-color: #9DEB95;	
	}
	tr.less-granular.off {
		background-color: #FFABA6;	
	}
	tr.current-rule td {
		font-weight: bold;
		font-size: 15px;
	}
	tr.current-rule.on {
		background-color: #9DEB95;
	}
	tr.current-rule.off {
		background-color: #FFABA6;
	}
	tr.similar-rules.on {
		background-color: maroon;	
	}
	tr.similar-rules.off {
		background-color: navy;	
	}
	tr.more-granular.on {
		background-color: #9DEB95;	
	}
	tr.more-granular.off {
		background-color: #FFABA6;	
	}
	tr.rule-header td {
		font-weight: bold;
		font-size: 120%;
		background-color: #f9f9f9;	
	}
	tr.rule-percents {
		height: 0px;
		overflow-y: auto;
	}
	tr.rule-percents td {
		vertical-align:top;
	}
</style>
</head>

<body>

<s:if test="rule == null">
	<div class="new">
		<s:form method="post" id="rule_form">
			<s:hidden name="button" value="new"/>
			<fieldset class="form">
				<h2 class="formLegend">New Category Rule</h2>
				<ol>
					<li><label>Include</label>
						<s:checkbox name="rule.include"/>
					</li>
					<li><label>Audit Type</label>
						<input type="text" class="searchAuto" id="auditType"/>
						<s:hidden name="rule.auditType.id" id="audit_hidden"/>
					</li>
					<li><label>Category</label>
						<input type="text" class="searchAuto" id="category"/>
						<s:hidden name="rule.auditCategory.id" id="cat_hidden"/>
					</li>
					<li><label>Account Type</label>
						<s:select name="rule.contractorType" list="@com.picsauditing.jpa.entities.ContractorType@values()" listValue="type" headerKey="" headerValue=""/>
					</li>
					<li><label>Operator</label>
						<input type="text" class="searchAuto" id="operator"/>
						<s:hidden name="rule.operatorAccount.id" id="operator_hidden"/>
					</li>
					<li><label>Risk</label>
						<s:select name="rule.risk" list="#{'':'','Low':'Low','Med':'Med','High':'High'}"/>
					</li>
					<li><label>Tag</label>
						<input type="text" class="searchAuto" id="tag"/>
						<s:hidden name="rule.tag" id="tag_hidden"/>
					</li>
					<li><label>Bid-Only</label>
						<s:select name="rule.acceptsBids" list="#{'':'','false':'No','true':'Yes'}"/>
					</li>
					<li><label>Question</label>
						<s:textfield name="rule.question"/>
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
				<input type="submit" class="picsbutton positive" value="Save"/>
			</fieldset>
		</s:form>
	</div>
</s:if>

<s:else>
	<table class="report">
	<thead>
		<tr>
			<td>Include</td>
			<td>Audit Type</td>
			<s:if test="categoryRule"><td>Category</td></s:if>
			<td>Account</td>
			<td>Operator</td>
			<td>Risk</td>
			<td>Tag</td>
			<td>Bid-Only</td>
			<td>Question</td>
			<td></td>
			<td>Answer</td>
			<td></td>
		</tr>
	</thead>
	
	<s:iterator value="#{'Less Granular': lessGranular, 'Current Rule': rule, 'Similar Rules': similar, 'More Granular': moreGranular}">
		<s:if test="'Current Rule' == key || value.size() > 0">
			<tr class="rule-header clickable <s:property value="key.toLowerCase().replaceAll(' ', '-')"/>">
				<td colspan="12" class="center"> <s:property value="key"/> </td>
			</tr>
			<s:iterator value="value" id="r">
				<tr class="<s:property value="key.toLowerCase().replaceAll(' ', '-')"/> clickable<s:if test="include"> on</s:if><s:else> off</s:else>" onclick="location.href='?id=<s:property value="#r.id"/>'">
					<td><s:property value="include ? 'Yes' : 'No'"/></td>
					<td><s:property value="auditTypeLabel"/></td>
					<s:if test="categoryRule"><td><s:property value="auditCategoryLabel"/></td></s:if>
					<td><s:property value="contractorTypeLabel"/></td>
					<td><s:property value="operatorAccountLabel"/></td>
					<td><s:property value="riskLabel"/></td>
					<td><s:property value="tagLabel"/></td>
					<td><s:property value="acceptsBidsLabel"/></td>
					<td><s:property value="questionLabel"/></td>
					<td><s:property value="questionComparatorLabel"/></td>
					<td><s:property value="questionAnswerLabel"/></td>
					<td>
						<s:if test="'Similar Rules' == key">
							<a href="?button=merge">Merge</a>
						</s:if>
						<s:if test="'More Granular' == key || 'Current Rule' == key">
							<a class="remove" href="?id=<s:property value="id"/>&button=delete">Delete</a>
						</s:if>
					</td>
				</tr>
			</s:iterator>
			
			<s:if test="'Current Rule' == key">
				<tr class="rule-percents">
					<td></td>
					<td></td>
					<s:if test="categoryRule">
						<td>
							<s:if test="rule.auditCategory == null">
								<table class="inner">
								<s:iterator value="getPercentOn('catID')">
									<s:if test="rule.include ? get('percentOn') < .3 : get('percentOn') > .3">
										<tr class="clickable" onclick="location.href='?id=<s:property value="id"/>&button=create&rule.include=<s:property value="!rule.include"/>&rule.auditCategory.id=<s:property value="get('catID')"/>'">
											<td class="right"><s:property value="get('catID')"/></td>
											<td class="right" title="<s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/>"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
										</tr>
									</s:if>
								</s:iterator>
								</table>
							</s:if>
						</td>
					</s:if>
					<td></td>
					<td>
						<s:if test="rule.operatorAccount == null">
							<table class="inner">
							<s:iterator value="getPercentOn('opID')">
								<s:if test="rule.include ? get('percentOn') < .3 : get('percentOn') > .3">
									<tr class="clickable" onclick="location.href='?id=<s:property value="id"/>&button=create&rule.include=<s:property value="!rule.include"/>&rule.operatorAccount.id=<s:property value="get('opID')"/>'">
										<td class="right"><s:property value="get('opID')"/></td>
										<td class="right" title="<s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/>"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
									</tr>
								</s:if>
							</s:iterator>
							</table>
						</s:if>
					</td>
					<td>
						<s:if test="rule.risk == null">
							<table class="inner">
							<s:iterator value="getPercentOn('risk')">
								<s:if test="rule.include ? get('percentOn') < .3 : get('percentOn') > .3">
									<tr class="clickable" onclick="location.href='?id=<s:property value="id"/>&button=create&rule.include=<s:property value="!rule.include"/>&rule.risk=<s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('risk'))"/>'">
										<td class="right"><s:property value="get('risk')"/></td>
										<td class="right" title="<s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/>"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
									</tr>
								</s:if>
							</s:iterator>
							</table>
						</s:if>
					</td>
					<td>
						<s:if test="rule.tag == null">
							<table class="inner">
							<s:iterator value="getPercentOn('tagID')">
								<s:if test="rule.include ? get('percentOn') < .3 : get('percentOn') > .3">
									<tr class="clickable" onclick="location.href='?id=<s:property value="id"/>&button=create&rule.include=<s:property value="!rule.include"/>&rule.tag.id=<s:property value="get('tagID')"/>'">
										<td class="right"><s:property value="get('tagID')"/></td>
										<td class="right" title="<s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/>"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
									</tr>
								</s:if>
							</s:iterator>
							</table>
						</s:if>
					</td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</s:if>
		</s:if>
	</s:iterator>
	</table>
</s:else>

</body>
</html>