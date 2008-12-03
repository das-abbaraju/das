<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Fatalities</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Fatalities Report</h1>
<s:include value="filters.jsp" />
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<td></td>
			<th><a href="?orderBy=a.name">Contractor</a></th>
			<s:if test="permissions.operator">
				<td><a href="?orderBy=flag DESC">Flag</a></td>
			</s:if>
			<td>For</td>
			<td>Fatalities</td>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property
				value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property
				value="[0].get('name')" /></a></td>
			<s:if test="permissions.operator">
				<td class="center"><a
					href="ContractorFlag.action?id=<s:property value="[0].get('id')"/>"
					title="<s:property value="[0].get('flag')"/> - Click to view details"><img
					src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif"
					width="12" height="15" border="0"></a></td>
			</s:if>
			<td class="center"><s:property value="get('auditFor')" /></td>
			<td class="center"><s:property value="get('fatalities')" /></td>
		</tr>
	</s:iterator>
</table>
<br>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
</body>
</html>
