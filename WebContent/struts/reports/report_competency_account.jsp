<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Competency By Account Report</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min"></script>
<script type="text/javascript">
$(function() {
$.mask.definitions['S']='[X0-9]';
$('input.ssn').mask('SSS-SS-SSSS');
});
</script>
</head>
<body>
<h1>Competency By Account Report</h1>

<s:include value="filters_employee.jsp"/>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<s:if test="data.size() > 0">
<table class="report" id="matrix">
	<thead>
		<tr>
			<th>Company</th>
			<th># of Employees</th>
			<th>Competency</th>
			<th>Competency %</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="data">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="#data.get('accountID')"/>"><s:property value="#data.get('name')" /></a></td>
				<td class="right"><a href="ReportCompetencyByEmployee.action?filter.accountName=<s:property value="#data.get('name')"/>"><s:property value="#data.get('employeeCount')"/></a></td>
				<td class="right"><s:property value="#data.get('skilled')" /> / <s:property value="#data.get('required')" /></td>
				<td class="right"><s:property value="getRatio(#data.get('skilled'),#data.get('required'))" />%</td>
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
