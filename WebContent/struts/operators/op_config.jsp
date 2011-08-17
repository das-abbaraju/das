<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="operator.name" /> Configuration</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
$(document).ready(function() {
	var data = {
		'comparisonRule.operatorAccount.id': <s:property value="operator.id" />,
		'comparisonRule.include': false,
		showPriority: false
	};

	startThinking({ div: "excludedTypes", message: "Loading Excluded Audit Type Rules" });
	$('#excludedTypes').load("AuditTypeRuleTableAjax.action", data);

	data['comparisonRule.auditType.id'] = 1;
	startThinking({ div: "excludedCategories", message: "Loading Excluded Audit Category Rules" });
	$('#excludedCategories').load("CategoryRuleTableAjax.action", data);
	
	$('.add_rule').live('click', function(){
		var target = $(this).closest('div.included');
		var data = $(this).closest('div').find(':input').serialize();
		target.think();
		target.load('OperatorConfigurationAjax.action', data);
	});
});

function showType(typeID) {
	var data = {
		'comparisonRule.operatorAccount.id': <s:property value="operator.id" />,
		'comparisonRule.auditType.id': typeID
	};
	var checkCat;
	var cType =$('tr#type'+typeID+' .classType').text();	
	if(cType=='Policy')
		checkCat = true;

	$('tr#type'+typeID).find('.hide').show();
	$('tr#type'+typeID).find('.normal').hide();

	$('#typeTable_'+typeID).load('AuditTypeRuleTableAjax.action', data, function(){
		if(checkCat){
			$.getJSON('OperatorConfigurationAjax.action', {button: 'checkCat', auditTypeID: typeID, id:<s:property value="operator.id" /> }, function(json){
				if(json){
					if(json.addLink){
						$('#build_'+typeID).html($('<a>', 
								{'href':'OperatorConfiguration.action?id=<s:property value="operator.id"/>&button=buildCat&auditTypeID='+typeID,
								 'class':'go'}).append('Setup Insurance Category'));
					}
				}
			});
		}
	});
	
	return false;
}

function showCat(catID) {
	var data = {
		'comparisonRule.operatorAccount.id': <s:property value="operator.id" />,
		'comparisonRule.auditCategory.id': catID,
		'columnsIgnore': ['auditType','auditCategory','rootCategory']
	};

	$('tr#cat'+catID).find('.hide').show();
	$('tr#cat'+catID).find('.normal').hide();
	$('#catTable_'+catID).load('CategoryRuleTableAjax.action', data);
	
	return false;
}

function hideType(id) {
	$('tr#type'+id).find('.hide').hide();
	$('tr#type'+id).find('.normal').show();
	$('#typeTable_' + id).empty();

	return false;
}

function hideCat(id) {
	$('tr#cat'+id).find('.hide').hide();
	$('tr#cat'+id).find('.normal').show();
	$('#catTable_' + id).empty();

	return false;
}

</script>
<style type="text/css">
.auditTypeRule, .subcat-list, .hide {
	display: none;
}
#includedCategories td {
	vertical-align: top;
	width: 50%;
}
</style>
</head>
<body>
<s:include value="opHeader.jsp" />
<s:include value="../actionMessages.jsp"/>

<s:include value="../config_environment.jsp" />

<fieldset class="form">
<s:if test="operator.operator">
	<h2 class="formLegend">Parent Accounts</h2>
	<ol>
		<li><label><s:text name="Country" />:</label>
			<s:property value="operator.country.name"/>
		</li>
		<s:if test="allParents.size > 0">
			<li>
				<table class="report">
					<tbody>
							<s:set name="globalOperator" value="operator" />
							<s:set name="opID" value="%{operator.id}" />
							<s:iterator value="allParents" id="corp">
								<tr>
									<td>
										<a href="?id=<s:property value="#corp.id" />">
										<s:property value="#corp.name" /></a>
									</td>
									<s:if test="permissions.isCanAddRuleForOperator(#globalOperator)">
										<td><a href="?id=<s:property value="#opID" />&button=Remove&corpID=<s:property value="#corp.id" />" class="remove"><s:text name="button.Remove" /></a></td>
									</s:if>
								</tr>
							</s:iterator>
					</tbody>
				</table>
			</li>
		</s:if>
		<s:else>
			<tr>
				<td><div class="alert">No Parent Accounts found</div></td>
			</tr>
		</s:else>
		<s:if test="permissions.isCanAddRuleForOperator(operator)">
			<li><s:form id="includeNewParent">
					<s:hidden value="%{operator.id}" name="id" />
					<s:select list="otherCorporates" listValue="name" listKey="id" name="corpID" 
						headerValue="- Select Parent Account -" headerKey="0" />
					<s:submit method="addParentAccount" value="Add Parent Account" cssClass="picsbutton" />
				</s:form>
			</li>
		</s:if>
	</ol>
</s:if>
<s:elseif test="operator.id > 10">
	<h2 class="formLegend">Operators</h2>
	<ol>
		<li>
			<s:iterator value="operator.operatorFacilities"><a href="?id=<s:property value="operator.id"/>"><s:property value="operator.name"/></a> </s:iterator>
		</li>
	</ol>
</s:elseif>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Likely Included Audits</h2>
	<div class="included">
			<s:include value="op_config_included_audits.jsp" />
	</div>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Rules to Explicitly Remove Audit Types</h2>
	<ol>
		<li>
			<div id="excludedTypes"></div>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Likely Included PQF Categories</h2>
	<div class="included">
		<s:include value="op_config_included_cats.jsp"/>
	</div>
</fieldset>
<fieldset class="form bottom">
	<h2 class="formLegend">Rules to Explicitly Remove PQF Categories</h2>
	<ol>
		<li>
			<div id="excludedCategories"></div>
		</li>
	</ol>
</fieldset>
<a class="remove" href="?id=<s:property value="id"/>&button=Clear">Clear Cache</a>

</body>
</html>