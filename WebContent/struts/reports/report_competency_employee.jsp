<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>HSE Competency By Employee Report</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
function orderBy(orderBy) {
	$('#form1').find('input[name=orderBy]').val(orderBy);
	$('#form1').submit();
}
</script>
</head>
<body>
<h1>HSE Competency By Employee Report</h1>
<s:include value="filters_employee.jsp"/>
<div class="right">
	<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all
		<s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportCompetencyByEmployee');"
		title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<s:if test="data.size() > 0">
<table class="report" id="matrix">
	<thead>
		<tr>
			<th><a href="javascript: orderBy('name,lastName');">Company</a></th>
			<th><a href="javascript: orderBy('lastName,firstName');">Employee</a></th>
			<th><a href="javascript: orderBy('title');">Title</a></th>
			<th>Job Roles</th>
			<th>Competency</th>
			<th><a href="javascript: orderBy('percent DESC');">Competency %</a></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="data">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="#data.get('accountID')"/>"><s:property value="#data.get('name')" /></a></td>
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="#data.get('employeeID')"/>"><s:property value="#data.get('lastName')" />, <s:property value="#data.get('firstName')" /></a></td>
				<td><s:property value="get('title')"/></td>
				<td><s:property value="get('roles')"/></td>
				<td class="right"><s:property value="#data.get('skilled')" /> / <s:property value="#data.get('required')" /></td>
				<td class="right"><s:property value="#data.get('percent')" />%</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</s:if>

</body>
</html>
