<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="operator.name" /> Configuration</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
function toggleType(ruleID) {
	var div = "#type_" + ruleID;
	var link = "#type_link_" + ruleID;
	
	$(div).toggle();

	if ($(div).is(':hidden'))
		$(link).text("Show Rules");
	else
		$(link).text("Hide Rules");
}
</script>
<style type="text/css">
.auditTypeRule {
	display: none;
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
					<s:iterator value="allParents">
						<tr>
							<td><a href="FacilitiesEdit.action?id=<s:property value="id" />"><s:property value="name" /></a></td>
							<td><a href="#" onclick="return false;" class="remove">Remove</a></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Likely Included Audits</h2>
	<ol>
		<li>
			<table class="report">
				<thead>
					<tr>
						<th>Category</th>
						<th>Audit</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="typeMap.keySet()" id="type">
						<tr class="clickable">
							<td><s:property value="#type.classType" /></td>
							<td><s:property value="#type.auditName" /></td>
							<td>
								<a href="#" onclick="toggleType(<s:property value="#type.id" />); return false;" id="type_link_<s:property value="#type.id" />">Show Rules</a>
								<table id="type_<s:property value="#type.id" />" class="auditTypeRule">
									<s:include value="../audits/rules/audit_rule_header.jsp"/>
									<s:set name="ruleURL" value="'AuditRuleEditor.action'"/>
									<s:set name="categoryRule" value="false"/>
									<s:iterator value="typeMap.get(#type)" id="r">
										<s:include value="../audits/rules/audit_rule_view.jsp"/>
									</s:iterator>
								</table>
							</td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
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
							<tr class="clickable">
								<td><s:property value="#type.include ? 'Yes' : 'No'" /></td>
								<td><s:property value="#type.auditType == null ? '*' : #type.auditType.auditName" /></td>
								<td><s:property value="#type.operatorAccount == null ? '*' : #type.operatorAccount.name" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Likely Included PQF Categories</h2>
	<ol>
		<li><label>Categories</label>
			<s:iterator value="categoryMap.keySet()">
				<s:property value="name" /><br />
			</s:iterator>
		</li>
	</ol>
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
							<tr class="clickable">
								<td><s:property value="#cat.include ? 'Yes' : 'No'" /></td>
								<td><s:property value="#cat.auditCategory == null ? '*' : #cat.auditCategory.name" /></td>
								<td><s:property value="#cat.operatorAccount == null ? '*' : #cat.operatorAccount.name" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
		</li>
	</ol>
</fieldset>
</body>
</html>