<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Employee Competencies</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
//Sort by table header
function sortTable(sortBy) {
	var tbody = $('#competenceMatrix').find('tbody');
	var rows = $(tbody).children();
	$(tbody).empty();

	rows.sort(function(a, b) {
		var a1 = $(a).find('.' + sortBy).text().toUpperCase();
		var b1 = $(b).find('.' + sortBy).text().toUpperCase();
		
		return (a1 < b1) ? -1 : (a1 > b1) ? 1 : 0;
	});

	$.each(rows, function (index, row) { $(row).find('.id').text(index + 1); $(tbody).append(row); });
}
</script>
</head>
<body>

<h1>Employee Competencies<span class="sub">
	<s:if test="employeeID > 0"><s:property value="employee.displayName" /></s:if>
	<s:if test="competencyID > 0"><s:property value="competency.label" /></s:if>
</span></h1>
<s:include value="../actionMessages.jsp"/>

<s:if test="conID > 0 && employees.size() > 0 && employeeID == 0">
	<s:form method="POST">
		<table class="report" id="competenceMatrix">
			<thead>
				<tr>
					<th></th>
					<th><a href="#" onclick="sortTable('employee'); return false;">Employee Name</a></th>
					<s:if test="competencyID == 0">
						<s:iterator value="competencies" id="competency">
							<th><a href="EmployeeCompetencies.action?conID=<s:property value="conID" />&competencyID=<s:property value="#competency.id" />"><s:property value="label" /></a></th>
						</s:iterator>
					</s:if>
					<s:else>
						<th></th>
					</s:else>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="employees" id="employee" status="stat">
					<tr>
						<td class="id"><s:property value="#stat.count" /></td>
						<td class="employee"><a href="EmployeeCompetencies.action?conID=<s:property value="conID" />&employeeID=<s:property value="#employee.id" />"><s:property value="#employee.displayName" /></a></td>
						<s:if test="competencyID == 0">
							<s:iterator value="competencies" id="competency">
								<td class="center"
										<s:if test="!map.get(#employee, #competency).skilled"> style="background-color: red"</s:if>
										<s:if test="map.get(#employee, #competency).skilled"> style="background-color: green"</s:if>>
									<s:if test="map.get(#employee, #competency) != null">
										<s:checkbox name="map.get(#employee, #competency).skilled" ></s:checkbox>
									</s:if>
								</td>
							</s:iterator>
						</s:if>
						<s:else>
							<td class="center"
									<s:if test="!map.get(#employee, competency).skilled"> style="background-color: red"</s:if>
									<s:elseif test="map.get(#employee, competency).skilled"> style="background-color: green"</s:elseif>>
								<s:if test="map.get(#employee, competency) != null">
									<s:checkbox name="map.get(#employee, competency).skilled" ></s:checkbox>
								</s:if>
							</td>
						</s:else>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:form>
</s:if>
<s:elseif test="employees.size() == 0">
	<div class="info">This contractor has no employees.</div>
</s:elseif>
<s:elseif test="employeeID > 0">
	<s:form method="POST">
		<table class="report" id="competenceMatrix">
			<thead>
				<tr>
					<th></th>
					<th><a href="#" onclick="sortTable('competency'); return false;">Job Competencies</a></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="competencies" id="competency" status="stat">
					<tr>
						<td class="id"><s:property value="#stat.count" /></td>
						<td class="competency"><a href="EmployeeCompetencies.action?conID=<s:property value="conID" />&competencyID=<s:property value="#competency.id" />"><s:property value="label" /></a></td>
						<td class="center"
								<s:if test="!map.get(employee, #competency).skilled"> style="background-color: red"</s:if>
								<s:elseif test="map.get(employee, #competency).skilled"> style="background-color: green"</s:elseif>>
							<s:if test="map.get(employee, #competency) != null">
								<s:checkbox name="map.get(employee, #competency).skilled" ></s:checkbox>
							</s:if>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:form>
</s:elseif>

<s:if test="employeeID > 0 || competencyID > 0">
<a href="EmployeeCompetencies.action?conID=<s:property value="conID" />" class="add">View all employees and job competencies.</a>
</s:if>

</body>
</html>