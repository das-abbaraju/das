<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Competency By Employee Report</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Competency By Employee Report</h1>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report" id="matrix">
	<thead>
		<tr>
			<th>Last Name</th>
			<th>First Name</th>
			<th>Company</th>
			<th>Competency</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="data">
			<tr>
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="#data.get('id')"/>"><s:property value="#data.get('lastName')" /></a></td>
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="#data.get('id')"/>"><s:property value="#data.get('firstName')" /></a></td>
				<td><s:property value="#data.get('name')" /></td>
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
