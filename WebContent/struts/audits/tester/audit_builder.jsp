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
<h1><s:property value="contractor.name" />
<span class="sub">Audit Builder</span>
</h1>
<s:include value="../actionMessages.jsp"></s:include>

Safety Critical: <s:property value="contractor.safetyRisk" /><br />
Product  Critical: <s:property value="contractor.productRisk" /><br />
<h4>Contractor Type:</h4>
<s:if test="contractor.onsiteServices">Onsite Services</s:if>
<s:if test="contractor.offsiteServices">Offsite Services</s:if>
<s:if test="contractor.materialSupplier">Materials Supplier</s:if>
<br />
<h4>Tags:</h4>
<br />
<h4>Trades:</h4>
<s:iterator value="contractor.trades">
	<s:property value="trade.nodeDisplay" /> | 
</s:iterator>
<br />
<h4>Operators/Corporates:</h4>
<s:iterator value="contractor.operators">
	<s:property value="operatorAccount" /> | 
</s:iterator>

<h2>Audit Type Rules</h2>
<a href="#" onclick="$('#auditCatRules').toggle(); return false;">Show/Hide</a>
<table class="report" id="auditCatRules">
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
<table class="report" id="auditTypeRules">
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

</body>
</html>
