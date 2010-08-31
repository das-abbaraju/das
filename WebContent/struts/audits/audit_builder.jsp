<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Builder</title>
<s:include value="../jquery.jsp" />
<link rel="stylesheet" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" href="css/rules.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>Audit Builder</h1>
<s:include value="../actionMessages.jsp"></s:include>

Name:
<s:property value="contractor.name" />
Risk:
<s:property value="contractor.riskLevel" />
Contractor Type:
<s:if test="contractor.onsiteServices">Onsite Services</s:if>
<s:if test="contractor.offsiteServices">Offsite Services</s:if>
<s:if test="contractor.materialSupplier">Materials Supplier</s:if>
Tags:
<br />
Operators/Corporates: 
<s:iterator value="contractor.operators">
	<s:if test="operatorAccount.operator">
		<s:property value="operatorAccount" /> | 
	</s:if>
</s:iterator>

<h2>Audit Type Rules</h2>
<a href="#" onclick="$('#auditCatRules').toggle(); return false;">Show/Hide</a>
<table class="report" id="auditCatRules" style="display: none;">
	<thead>
		<tr>
			<th>Inc</th>
			<th>Audit Type</th>
			<th>Risk</th>
			<th>Operator</th>
			<th>Tag</th>
			<th>Type</th>
			<th colspan="3">Question</th>
			<th>Bid</th>
		</tr>
	</thead>
	<s:iterator value="builder.auditTypeRules">
		<tr>
			<td><s:property value="(include ? 'Yes' : 'No')" /></td>
			<td><s:property value="auditTypeLabel" /></td>
			<td><s:property value="riskLabel" /></td>
			<td><s:property value="operatorAccountLabel" /></td>
			<td><s:property value="contractorTypeLabel" /></td>
			<td><s:property value="tagLabel" /></td>
			<td><s:property value="questionLabel" /></td>
			<td><s:property value="questionComparatorLabel" /></td>
			<td><s:property value="questionAnswerLabel" /></td>
			<td><s:property value="acceptsBidsLabel" /></td>
		</tr>
	</s:iterator>
</table>

<h2>Audit Types &amp; Rules</h2>
<a href="#" onclick="$('#auditTypeRules').toggle(); return false;">Show/Hide</a>
<table class="report" id="auditTypeRules" style="display: none;">
	<thead>
		<tr>
			<th>Audit Type</th>
			<th></th>
		</tr>
	</thead>
	<s:set name="ruleURL" value="'AuditTypeRuleEditor.action'"/><s:set name="categoryRule" value="false"/>
	<s:iterator value="builder.requiredAuditTypes">
		<tr>
			<td><s:property value="key" /></td>
			<td>
				<b>Rule:</b><table><s:include value="rules/audit_rule_header.jsp"/><s:iterator value="value.rule" id="r"><s:include value="rules/audit_rule_view.jsp" /></s:iterator></table>
				<b>Operators:</b> | <s:iterator value="value.operators"><s:property value="name"/> | </s:iterator>
			</td>
		</tr>
	</s:iterator>
</table>


<h2>Current Contractor Audits</h2>
<a href="#" onclick="$('#currentAudits').toggle(); return false;">Show/Hide</a>
<table class="report" id="currentAudits" style="display: block;">
	<thead>
		<tr>
			<th>Contractor Audit</th>
			<th>Categories and Details</th>
		</tr>
	</thead>
	<tbody>
	<s:set name="ruleURL" value="'CategoryRuleEditor.action'"/><s:set name="categoryRule" value="true"/>
	<s:iterator value="auditCategoriesDetail">
		<tr>
			<td><s:property value="key.id" /> <s:property value="key.auditType" /> <s:date name="key.effectiveDate" format="yyyy" /></td>
			<td>
				<b>Operators:</b> <s:iterator value="value.operators"> <br /><s:property value="key"/> RuleID=<s:property value="value.id"/></s:iterator><br />
				<b>Categories:</b> <s:iterator value="value.categories"><s:property value="name"/> | </s:iterator><br />
				<b>Rules:</b> <table><s:include value="rules/audit_rule_header.jsp"/><s:iterator value="value.rules" id="r"><s:include value="rules/audit_rule_view.jsp" /></s:iterator></table>
				<b>Governing Bodies:</b> <s:iterator value="value.governingBodies"><s:property value="name"/> | </s:iterator><br />
			</td>
		</tr>
	</s:iterator>
	</tbody>
</table>

</body>
</html>
