<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<s:include value="../reports/reportHeader.jsp" />
<style type="text/css">
.box {
	background-color: #F9F9F9;
}
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
		employee: employeeID,
		competency: competencyID,
		skilled: checked
	};

	$("#messages").load('EmployeeCompetencies!changeCompetency.action?' + $('#form1').serialize(), data, function(r, status, xhr) {
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
	<div class="info"><a href="Audit.action?auditID=<s:property value="auditID" />"><s:text name="Audit.link.ReturnToHSESAAudit" /></a></div>
</s:if>
<h1><s:property value="account.name" /><span class="sub"><s:text name="%{scope}.title" /></span></h1>

<s:include value="../reports/filters_employee.jsp" />
<div class="right"><a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="javascript.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
	href="javascript: download('EmployeeCompetencies');" title="<s:text name="javascript.DownloadAllRows"><s:param value="report.allRows" /></s:text>"><s:text name="global.Download" /></a></div>

<table class="legend">
	<tr>
		<td><div class="box green"></div></td>
		<td><s:text name="%{scope}.help.Green" /></td>
	</tr>
	<tr>
		<td><div class="box red"></div></td>
		<td><s:text name="%{scope}.help.Red" /></td>
	</tr>
	<tr>
		<td><div class="box"></div></td>
		<td>
			<s:text name="%{scope}.help.Blank" />
			<s:if test="permissions.contractor">
				<s:text name="%{scope}.help.ContractorLinks" />
			</s:if>
		</td>
	</tr>
</table>

<div id="report_data">
	<s:include value="employee_competencies_data.jsp" />
</div>

</body>
</html>