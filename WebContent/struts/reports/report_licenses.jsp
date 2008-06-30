<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Contractor Licenses</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Contractor Licenses</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td>PQF</td>
		<td colspan="2">CA License</td>
		<td colspan="2">Expiration</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				><s:property value="[0].get('name')" /></a></td>
			<td><a
				href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditStatus')"/></a></td>
		<s:if test="[0].get('isCorrect401').equals('Yes')">
			<td><s:property value="[0].get('verifiedAnswer401')"/></td>
			<td><img src="images/okCheck.gif" width="19" height="15" /></td>
		</s:if>
		<s:else>
			<td colspan="2"><s:property value="[0].get('answer401')"/></td>
		</s:else>
		<s:if test="[0].get('isCorrect401').equals('Yes')">
			<td><s:property value="[0].get('verifiedAnswer755')"/></td>
			<td><img src="images/okCheck.gif" width="19" height="15" /></td>
		</s:if>
		<s:else>
			<td colspan="2"><s:property value="[0].get('answer755')"/></td>
		</s:else>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
