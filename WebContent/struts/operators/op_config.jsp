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
		orderRules: true,
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

function loadCatRules(catID, divCatID) {
	$('.catTable').empty();

	var data = {
		'operator.id': <s:property value="operator.id" />,
		showPriority: false,
		showWho: false,
		orderRules: true,
		categoryID: catID
	};

	startThinking({ div: "catTable_" + divCatID, message: "Loading Category Rules" });
	$('#catTable_' + divCatID).load("AuditRuleTableAjax.action", data);

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
			<s:if test="excludeTypes.size > 0">
				<table class="report">
					<thead>
						<tr>
							<th>Include</th>
							<th>Audit Type</th>
							<th>Operator</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="excludeTypes" id="type">
							<tr class="clickable" onclick="window.open('AuditTypeRuleEditor.action?id=<s:property value="#type.id" />')">
								<td><s:property value="#type.include ? 'Yes' : 'No'" /></td>
								<td><s:property value="#type.auditType == null ? '*' : #type.auditType.auditName" /></td>
								<td><s:property value="#type.operatorAccount == null ? '*' : #type.operatorAccount.name" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
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
								<a href="#" onclick="return loadCatRules(<s:property value="#cat.id" />, <s:property value="#cat.id" />);"><s:property value="#cat.name" /></a>
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
			<s:if test="excludeCategories.size > 0">
				<table class="report">
					<thead>
						<tr>
							<th>Include</th>
							<th>Category</th>
							<th>Operator</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="excludeCategories" id="cat">
							<tr class="clickable" onclick="window.open('CategoryRuleEditor.action?id=<s:property value="#cat.id" />')">
								<td><s:property value="#cat.include ? 'Yes' : 'No'" /></td>
								<td><s:property value="#cat.auditCategory == null ? '*' : #cat.auditCategory.name" /></td>
								<td><s:property value="#cat.operatorAccount == null ? '*' : #cat.operatorAccount.name" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<a href="CategoryRuleEditor.action?button=edit&rule.include=false&rule.auditType.id=1&rule.operatorAccount.id=<s:property value="operator.id" />&rule.operatorAccount.name=<s:property value="operator.name" />"
				target="_blank" class="add">Add Rule</a>
		</li>
	</ol>
</fieldset>
</body>
</html>