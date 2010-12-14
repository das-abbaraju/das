<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>OQ by Company/Site</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>OQ by Company/Site</h1>
<s:include value="filters_employee.jsp" />
<a class="excel" href="javascript: download('ReportOQ');" target="_blank"
		title="Download <s:property value="data.size" /> results to a CSV file">Download</a>
<table class="report">
	<thead>
		<tr>
			<th>Company</th>
			<th>Site</th>
			<th>Employees</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data">
			<tr>
				<td><s:property value="get('name')" /></td>
				<td><s:property value="get('label')" /></td>
				<td class="right"><a href="ReportOQEmployees.action?filter.accountName=<s:property value="get('accountID')" />&filter.projects=<s:property value="get('jobSiteID')" />"><s:property value="get('employeeCount')" /></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>
