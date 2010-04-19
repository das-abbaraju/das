<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Compliance By Employee Report</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Compliance By Employee Report</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report" id="matrix">
	<thead>
		<tr>
			<s:if test="permissions.operatorCorporate"><th>Contractor</th></s:if>
			<th>First Name</th>
			<th>Last Name</th>
			<th>Compliance</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="data">
			<tr>
				<s:if test="permissions.operatorCorporate"><td><s:property value="#data.get('name')" /></td></s:if>
				<td><s:property value="#data.get('lastName')" /></td>
				<td><s:property value="#data.get('firstName')" /></td>
				<td><s:property value="#data.get('skilled')" /> / <s:property value="#data.get('required')" /></td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
