<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>OQ by Company/Site</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>OQ by Company/Site</h1>
<s:include value="filters_employee.jsp" />
<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
<div class="right">
	<a class="excel" href="#" onclick="download('ReportOQ'); return false;" target="_blank" title="Download <s:property value="data.size" /> results to a CSV file">Download</a>
</div>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<th>Company</th>
			<th>Project</th>
			<th>Employees</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data">
			<tr>
				<td><s:property value="get('name')" /></td>
				<td><s:property value="get('jsName')" /></td>
				<td class="right"><a href="ReportOQEmployees.action?filter.accountName=<s:property value="get('accountID')" />&filter.projects=<s:property value="get('jsID')" />"><s:property value="get('employeeCount')" /></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:else>
</body>
</html>
