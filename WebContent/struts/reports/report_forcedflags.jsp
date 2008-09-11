<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Forced Flags</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Forced Flags</h1>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Operator Name</td>
		<td>Flag</td>
		<td>Expiration Date</td>
		<td>Contractor Name</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right">
				<s:property value="#stat.index + report.firstRowNumber" />
			</td>
			<td>
			<s:if test="permissions.admin">
				<a href="accounts_edit_operator.jsp?id=<s:property value="[0].get('opId')"/>">
				<s:property value="[0].get('opName')"/></a>
			</s:if>
			<s:else>
				<s:property value="[0].get('opName')"/></a>
			</s:else>
			</td>
			<td class="center">
				<a href="ContractorFlag.action?id=<s:property value="[0].get('forceFlag')"/>" title="Click to view Flag Color details">
				<img src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
			</td>
			<td>
				<s:property value="[0].get('forceend')"/></td>
			<td>
				<a href="ContractorView.action?id=<s:property value="[0].get('id')"/>">
				<s:property value="[0].get('name')" /></a>
			</td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
