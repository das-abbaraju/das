<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Competency Detail Report</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min.js"></script>
<script type="text/javascript">
$(function() {
$.mask.definitions['S']='[X0-9]';
$('input.ssn').mask('SSS-SS-SSSS');
});
</script>
</head>
<body>
<h1>Competency Detail Report</h1>

<s:include value="filters_employee.jsp"/>
<div class="right">
	<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all
		<s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportCompetencyDetail');"
		title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<s:if test="data.size() > 0">
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
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="#data.get('id')"/>"><s:property value="#data.get('firstName')" /></a></td>
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="#data.get('id')"/>"><s:property value="#data.get('lastName')" /></a></td>
				<td><a href="ContractorView.action?id=<s:property value="#data.get('accountID')"/>"><s:property value="#data.get('name')" /></a></td>
				<td><s:property value="#data.get('label')" /></td>
				<td><s:property value="#data.get('skilledValue')"/></td>
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
