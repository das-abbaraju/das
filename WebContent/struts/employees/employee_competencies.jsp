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
</style>
<script type="text/javascript">
function changeCompetency(employeeID, competencyID, checked) {
	var data = {
		employeeID: employeeID,
		competencyID: competencyID,
		skilled: checked,
		button: "ChangeCompetency"
	};

	startThinking({ div: "report_data", type: "large" });
	$("#report_data").load('EmployeeCompetenciesAjax.action?' + $('#form1').serialize(), data);
}
</script>
</head>
<body>

<h1><s:property value="account.name" /><span class="sub">Employee HSE Competencies</span></h1>

<s:include value="../reports/filters_employee.jsp" />

<div id="report_data">
	<s:include value="employee_competencies_data.jsp" />
</div>

</body>
</html>