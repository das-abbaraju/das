<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Employee HSE Competencies</title>
<s:include value="../reports/reportHeader.jsp" />
<style type="text/css">
.red, .green {
	text-align: center;
}
.red {
	background-color: #FAA;
}
.green {
	background-color: #AFA;
}
table.legend {
	clear: both;
	margin: 20px 0px;
}
table.legend td {
	padding: 3px;
	vertical-align: middle;
}
div.box {
	width: 16px;
	height: 16px;
	border: 1px solid #012142;
}
</style>
<script type="text/javascript">
function changeCompetency(employeeID, competencyID, checkbox) {
	var checked = $(checkbox).is(":checked");
	var data = {
		employeeID: employeeID,
		competencyID: competencyID,
		skilled: checked,
		button: "ChangeCompetency"
	};

	$("#messages").load('EmployeeCompetenciesAjax.action?' + $('#form1').serialize(), data, function(r, status, xhr) {
		if (status == "success" && r.indexOf("Success") > 0)
			$(checkbox).parent().removeClass('green').removeClass('red').addClass(checked ? 'green' : 'red');
		else
			$(checkbox).attr('checked', !checked);
	});
}
</script>
</head>
<body>
<s:if test="auditID > 0">
	<div class="info"><a href="Audit.action?auditID=<s:property value="auditID" />">Return to Job Roles Self Assessment</a></div>
</s:if>
<h1><s:property value="account.name" /><span class="sub">Employee HSE Competencies</span></h1>

<s:include value="../reports/filters_employee.jsp" />
<div class="right"><a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
	href="javascript: download('EmployeeCompetencies');" title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a></div>

<table class="legend">
	<tr>
		<td><div class="box green"></div></td>
		<td>If checked, this employee is SKILLED in the given competency.</td>
	</tr>
	<tr>
		<td><div class="box red"></div></td>
		<td>If unchecked, this employee is not yet verified as SKILLED in the given competency.</td>
	</tr>
	<tr>
		<td><div class="box" style="background-color: #F9F9F9"></div></td>
		<td>If blank, this employee does not require the given competency. Change this by either updating the <a href="ManageEmployees.action">employee's job roles</a> or by editing the <a href="ManageJobRoles.action">required competencies for this company's job roles</a>.</td>
	</tr>
</table>

<div id="report_data">
	<s:include value="employee_competencies_data.jsp" />
</div>

</body>
</html>