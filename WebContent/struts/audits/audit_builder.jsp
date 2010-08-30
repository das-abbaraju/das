<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Builder</title>
<s:include value="../jquery.jsp" />
<link rel="stylesheet"
	href="css/reports.css?v=<s:property value="version"/>" />
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
Operators: 
<s:iterator value="contractor.operators">
	<s:property value="operatorAccount" />
</s:iterator>

<table class="report">
	<thead>
		<tr>
			<th>Audit Type</th>
			<th></th>
		</tr>
	</thead>
	<s:iterator value="builder.requiredAuditTypes">
		<tr>
			<td><s:property value="key" /></td>
			<td>
				Rule: <s:property value="value.rule.id" /><br />
				Operators: | <s:iterator value="value.operators"><s:property value="name"/> | </s:iterator>
			</td>
		</tr>
	</s:iterator>
</table>

</body>
</html>
