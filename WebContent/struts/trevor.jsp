<%@ taglib prefix="s" uri="/struts-tags"%>
<% // This is a blank jsp page created for struts ajax calls %>
<a href="?button=fill">Fill</a>
<a href="?conID=3">Ancon Marine</a>
<a href="?button=print">Print</a>

<table>
<s:iterator value="applicable">
	<s:include value="audits/rules/audit_rule_view.jsp" />
</s:iterator>
</table>
