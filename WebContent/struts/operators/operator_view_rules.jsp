<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Operator Rules</title>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<script type="text/javascript">
</script>
</head>
<body>
<s:include value="../actionMessages.jsp"/>
<h2>Related Operator Rules</h2>
<s:include value="opHeader.jsp"></s:include>
<s:if test="relatedRules.size() >= 50">
	<div class="alert">
		There are too many rules to display here.
		<s:if test="categoryRule">			
			<s:set name="searchURL" value="'CategoryRuleSearch.action'"/>	
		</s:if> 
		<s:else>
			<s:set name="searchURL" value="'AuditTypeRuleSearch.action'"/>
		</s:else>
		<a href="<s:property value="#searchURL"/>?filter.opID=<s:property value="operator.id"/>">Click here to view all rules for <s:property value="operator.name"/>.</a>
	</div>
</s:if>
<s:if test="relatedRules.size()>0">
	<table class="report">
		<s:include value="../audits/rules/audit_rule_header.jsp"/>
		<tbody>
		<s:if test="categoryRule">
				<s:set name="ruleURL" value="'CategoryRuleEditor.action'"/>
		</s:if>
		<s:else>
				<s:set name="ruleURL" value="'AuditTypeRuleEditor.action'"/>	
		</s:else>
		<s:iterator value="relatedRules" id="r">
			<s:include value="../audits/rules/audit_rule_view.jsp"/>
		</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
<div id="alert">
No rules for this operator
</div>
</s:else>
	
</body>
</html>


