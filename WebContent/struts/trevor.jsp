<%@ taglib prefix="s" uri="/struts-tags"%>
<% // This is a blank jsp page created for struts ajax calls %>
<link rel="stylesheet" href="css/rules.css"/>
<link rel="stylesheet" href="css/reports.css"/>
<a href="?button=fill">Fill</a>
<a href="?conID=3">Ancon Marine</a>
<a href="?button=print">Print</a>

<s:if test="applicable.size() > 0">
<table class="report">
<s:set name="categoryRule" value="true"/>
<s:set name="ruleURL" value="CategoryRuleEditor.action"/>
<s:include value="audits/rules/audit_rule_header.jsp"/>
<s:iterator value="applicable">
	<s:include value="audits/rules/audit_rule_view.jsp" />
</s:iterator>
</table>
</s:if>
