<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>OQ Change List</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>OQ Change List</h1>
<table class="report">
	<thead>
		<tr>
			<th>Type</th>
			<th>Task</th>
			<th>Criteria</th>
			<th>Action</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="dataCriteria">
			<tr>
				<td><s:property value="get('taskType')" /></td>
				<td><s:property value="get('task')" /></td>
				<td><s:property value="get('criteria')" /></td>
				<td><s:if test="get('daysFromExpiration') > 0">Added <s:date name="get('effectiveDate')" format="MMM d" /></s:if>
					<s:else>Removed <s:date name="get('expirationDate')" format="MMM d" /></s:else></td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<table class="report">
	<thead>
		<tr>
			<th>Type</th>
			<th>Task</th>
			<th>Site</th>
			<th>Action</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="dataSites">
			<tr>
				<td><s:property value="get('taskType')" /></td>
				<td><s:property value="get('task')" /></td>
				<td><s:if test="!permissions.operator"><s:property value="get('opName')" />: </s:if><s:property value="get('name')" /></td>
				<td><s:if test="get('daysFromExpiration') > 0">Added <s:date name="get('effectiveDate')" format="MMM d" /></s:if>
					<s:else>Removed <s:date name="get('expirationDate')" format="MMM d" /></s:else></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>
