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
			<th>Qualified Employees</th>
			<th>Total Employees</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data">
			<tr>
				<td>
					<s:if test="get('isContractor') == 1">
						<a href="ContractorView.action?id=<s:property value="get('accountID')" />"><s:property value="get('name')" /></a>
					</s:if>
					<s:else>
						<s:property value="get('name')" />
					</s:else>
				</td>
				<td><s:property value="get('jsName')" /></td>
				<td class="right"><a href="ReportOQEmployees.action?filter.accountName=<s:property value="get('accountID')" />&filter.projects=<s:property value="get('jsID')" />"><s:property value="get('employeeCount')" /></a></td>
				<td class="right"><s:property value="get('employeeTotals')" /></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:else>
</body>
</html>
