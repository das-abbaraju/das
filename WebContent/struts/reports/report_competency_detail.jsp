<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Competency By Account Report</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Competency By Account Report</h1>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report" id="matrix">
	<thead>
		<tr>
			<th>First Name</th>
			<th>Last Name</th>
			<th>Account</th>
			<th>Label</th>
			<th>Competency</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="data">
			<tr>
				<td><s:property value="#data.get('firstName')" /></td>
				<td><s:property value="#data.get('lastName')" /></td>
				<td><s:property value="#data.get('name')" /></td>
				<td><s:property value="#data.get('label')" /></td>
				<td><s:if test="#data.get('skilledValue') == 1">Skilled</s:if></td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
