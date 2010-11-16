<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="operator.name" /> Configuration</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
$(document).ready(function() {
	var data = {
		'operator.id': <s:property value="operator.id" />,
		showPriority: false,
		showWho: false,
		excluded: true
	};

	startThinking({ div: "excludedTypes", message: "Loading Excluded Audit Type Rules" });
	$('#excludedTypes').load("AuditRuleTableAjax.action", data);

	data.type = 'Category';
	data.where = 'r.auditType.id = 1';
	startThinking({ div: "excludedCategories", message: "Loading Excluded Audit Category Rules" });
	$('#excludedCategories').load("AuditRuleTableAjax.action", data);

	// GL = 13, WC = 14, Auto = 15
	data.excluded = false;
	data.type = 'AuditType';
	for (i = 13; i <= 15; i++) {
		data.auditTypeID = i;
		startThinking({ div: "insurance_" + i, message: "Loading Insurance Rules" });
		$('#insurance_' + i).load("AuditRuleTableAjax.action", data, function() {
			var j = $(this).attr('id').substring(10);
			$(this).append('<a href="AuditTypeRuleEditor.action?button=edit&rule.include=true&rule.auditType.id='
					+ j + '&rule.operatorAccount.id=' + <s:property value="operator.id" /> 
					+ '" class="add" target="_blank">Add Rule</a>');
		});
	}
});

function toggleCategory(catID) {
	var ol = "#subcat_" + catID;
	var arrow = ".arrow_" + catID;
	$(ol).slideToggle();
	$(arrow).toggle();

	return false;
}

function showType(typeID) {
	var data = {
		'operator.id': <s:property value="operator.id" />,
		showPriority: false,
		showWho: false,
		auditTypeID: typeID
	};

	$('tr#'+typeID).find('.hidden').show();
	$('tr#'+typeID).find('.normal').hide();

	startThinking({ div: "typeTable_" + typeID, message: "Loading Audit Type Rules" });
	$('#typeTable_' + typeID).load("AuditRuleTableAjax.action", data);

	return false;
}

function hideType(typeID) {
	$('tr#'+typeID).find('.hidden').hide();
	$('tr#'+typeID).find('.normal').show();
	$('#typeTable_' + typeID).empty();

	return false;
}

function loadCatRules(catID, divCatID, name) {
	$('.catTable').empty();

	var data = {
		'operator.id': <s:property value="operator.id" />,
		showPriority: false,
		showWho: false,
		categoryID: catID
	};

	startThinking({ div: "catTable_" + divCatID, message: "Loading Category Rules" });
	$('#catTable_' + divCatID).load("AuditRuleTableAjax.action", data, function() {
		$(this).prepend('<b>' + name + '</b><br /><a href="ManageCategory.action?id=' + catID 
				+ '">Edit Category</a> <a href="#" onclick="return loadCatRules(' + catID + ',' + divCatID 
				+ ', \'' + name + '\');" class="refresh">Refresh</a>');
	});

	return false;
}
</script>
<style type="text/css">
.auditTypeRule, .subcat-list, .hidden {
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
<fieldset class="form">
	<h2 class="formLegend">General</h2>
	<ol>
		<li><label>Parent Accounts</label>
			<table class="report">
				<tbody>
					<s:if test="allParents.size > 0">
						<s:set name="opID" value="%{operator.id}" />
						<s:iterator value="allParents" id="corp">
							<tr>
								<td>
									<a href="FacilitiesEdit.action?id=<s:property value="#corp.id" />">
									<s:property value="#corp.name" /></a>
								</td>
								<td><a href="OperatorConfiguration.action?id=<s:property value="#opID" />&button=Remove&corpID=<s:property value="#corp.id" />" class="remove">Remove</a></td>
							</tr>
						</s:iterator>
					</s:if>
					<s:else>
						<tr>
							<td>No Parent Accounts found</td>
						</tr>
					</s:else>
				</tbody>
			</table>
			<s:form>
				<s:hidden value="%{operator.id}" name="id" />
				<s:select list="otherCorporates" listValue="name" listKey="id" name="corpID" 
					headerValue="- Add Parent Account -" headerKey="0" />
				<input type="submit" name="button" value="Add" class="picsbutton positive" />
			</s:form>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Likely Included Audits</h2>
	<ol>
		<li>
			<s:if test="typeList.size > 0">
				<table class="report">
					<thead>
						<tr>
							<th>Category</th>
							<th>Audit</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="typeList" id="type">
							<tr id="<s:property value="#type.id" />">
								<td><s:property value="#type.classType" /></td>
								<td><s:property value="#type.auditName" /></td>
								<td>
									<a href="#" onclick="return showType(<s:property value="#type.id" />);" class="normal">Show Rules</a>
									<a href="#" onclick="return hideType(<s:property value="#type.id" />);" class="hidden">Hide Rules</a>
									<a href="#" onclick="return showType(<s:property value="#type.id" />);" class="hidden refresh">Refresh</a>
									<div id="typeTable_<s:property value="#type.id" />"></div>
									<a href="AuditTypeRuleEditor.action?button=edit&rule.include=true&rule.auditType.id=<s:property value="#type.id" />&rule.operatorAccount.id=<s:property value="operator.id" />&rule.operatorAccount.name=<s:property value="operator.name" />"
										target="_blank" class="hidden add">Add Rule</a>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
		</li>
		<li>
			<s:form id="includeNewAudit">
				<s:hidden value="%{operator.id}" name="id" />
				<s:hidden value="Include" name="button" />
				<s:select list="otherAudits" headerKey="0" headerValue="- Include Another Audit -" 
					listKey="id" listValue="auditName" name="auditTypeID" onchange="$('#includeNewAudit').submit();" />
			</s:form>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Rules to Explicitly Remove Audits</h2>
	<ol>
		<li>
			<div id="excludedTypes"></div>
			<a href="AuditTypeRuleEditor.action?button=edit&rule.include=false&rule.operatorAccount.id=<s:property value="operator.id" />&rule.operatorAccount.name=<s:property value="operator.name" />"
				target="_blank" class="add">Add Rule</a>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Likely Included PQF Categories</h2>
	<s:if test="categoryList.size > 0">
		<table style="width: 100%;" id="includedCategories">
			<s:iterator value="categoryList" id="cat">
				<tr>
					<td>
						<ol>
							<li>
								<a href="#" onclick="return loadCatRules(<s:property value="#cat.id" />, <s:property value="#cat.id" />, '<s:property value="#cat.name" />');"><s:property value="#cat.name" /></a>
								<s:if test="#cat.subCategories.size > 0">
									<a href="#" onclick="return toggleCategory(<s:property value="#cat.id" />);">
										<img src="images/arrow-blue-down.png" class="arrow_<s:property value="#cat.id" />" alt="Expand" />
										<img src="images/arrow-blue-right.png" class="arrow_<s:property value="#cat.id" />" alt="Collapse" style="display: none;" />
									</a>
								</s:if>
								<s:if test="#cat.subCategories.size > 0">
									<s:set name="subcat" value="%{#cat}" />
									<s:set name="parentCatID" value="%{#cat.id}" />
									<div class="subcat"><s:include value="op_config_subcat.jsp" /></div>
								</s:if>
							</li>
						</ol>
					</td>
					<td style="padding: 20px;"><div class="catTable" id="catTable_<s:property value="#cat.id" />"></div></td>
				</tr>
			</s:iterator>
		</table>
	</s:if>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Rules to Explicitly Remove PQF Categories</h2>
	<ol>
		<li>
			<div id="excludedCategories"></div>
			<a href="CategoryRuleEditor.action?button=edit&rule.include=false&rule.auditType.id=1&rule.operatorAccount.id=<s:property value="operator.id" />&rule.operatorAccount.name=<s:property value="operator.name" />"
				target="_blank" class="add">Add Rule</a>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Insurance Configuration</h2>
	<ol>
		<li>
			<div id="insurance_13"></div>
		</li>
		<li>
			<div id="insurance_14"></div>
		</li>
		<li>
			<div id="insurance_15"></div>
		</li>
	</ol>
</fieldset>
</body>
</html>