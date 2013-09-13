<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Builder</title>
<link rel="stylesheet" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" href="css/rules.css?v=<s:property value="version"/>" />
<script type="text/javascript">
$(function(){
	showRules();
});

function showRules() {
	var data = {
		'conID': <s:property value="id"/>,
		'button':'debugContractor' 
	};
	startThinking({ div: "allTypeRules"});
	$('#allTypeRules').load('AuditTypeRuleTableAjax.action', data);
}

</script>
</head>
<body>
<h1><s:property value="contractor.name" />
<span class="sub">Audit Builder</span>
</h1>
<s:include value="../../actionMessages.jsp"></s:include>

Safety Critical: <s:property value="contractor.safetyRisk" /><br />
Product  Critical: <s:property value="contractor.productRisk" /><br />
<h4>Contractor Type:</h4>
<s:if test="contractor.onsiteServices">Onsite Services</s:if>
<s:if test="contractor.offsiteServices">Offsite Services</s:if>
<s:if test="contractor.materialSupplier">Materials Supplier</s:if>
<br />
Sole Proprietor: <s:if test="contractor.soleProprietor">Yes</s:if><s:else>No</s:else>
<br />
<h4>Tags:</h4>
<s:iterator value="contractor.operatorTags">
	<s:property value="tag.tag" /> | 
</s:iterator>
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
<a href="#" onclick="$('#allTypeRules').toggle(); return false;">Show/Hide</a>
<div id="allTypeRules"></div>

<h2>Audit Types &amp; Rules</h2>
<a href="#" onclick="$('#auditTypeRules').toggle(); return false;">Show/Hide</a>
<table class="report" id="auditTypeRules">
	<thead>
		<tr>
			<th>Audit Type</th>
			<th>Rule</th>
			<th>Operators</th>
		</tr>
	</thead>
	<s:iterator value="auditTypeDetails">
		<tr>
			<td><h4><s:property value="rule.auditType.name" /></h4></td>
			<td><s:property value="rule" /></td>
			<td><s:iterator value="operators"><s:property value="name"/> | </s:iterator></td>
		</tr>
	</s:iterator>
</table>

</body>
</html>
