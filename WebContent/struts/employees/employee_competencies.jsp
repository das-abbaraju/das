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

	var sortBys = sortBy.split(',');
	rows.sort(function(a, b) {
		var a1 = $(a).find('.' + sortBys[0]).text().toUpperCase();
		var b1 = $(b).find('.' + sortBys[0]).text().toUpperCase();

		if (sortBys.length > 1) {
			var a2 = $(a).find('.' + sortBys[1]).text().toUpperCase();
			var b2 = $(b).find('.' + sortBys[1]).text().toUpperCase();

			return (a1 < b1) ? -1 : (a1 > b1) ? 1 : (a2 < b2) ? -1 : (a2 > b2) ? 1 : 0;
		}
		
		return (a1 < b1) ? -1 : (a1 > b1) ? 1 : 0;
	});

	$.each(rows, function (index, row) { $(row).find('.id').text(index + 1); $(tbody).append(row); });
}

function saveChange(ecID, checkbox) {
	var data = {
		button: checkbox.checked ? 'AddSkill' : 'RemoveSkill',
		ecID: ecID,
		conID: <s:property value="conID" />
	};

	$.getJSON('EmployeeCompetenciesJson.action', data,
		function(json) {
			checkbox.checked = !checkbox.checked;
		
			$.gritter.add({
				title: json.title,
				text: json.msg
			});
			
			$(checkbox.parentNode).css('background-color', checkbox.checked ? '#AFA' : '#FAA');
		}
	);
}
</script>
</head>
<body>

<h1>Employee Competencies<span class="sub"><s:property value="contractor.name" /></span></h1>
<s:include value="../actionMessages.jsp"/>

<s:if test="employeeID > 0">
	<a href="EmployeeCompetencies.action?conID=<s:property value="conID" />" class="add">View all employees for <s:property value="contractor.name" /></a>
</s:if>

<s:if test="conID > 0 && employees.size() > 0 && employeeID == 0">
	<s:form method="POST">
		<table class="report" id="competenceMatrix">
			<thead>
				<tr>
					<th></th>
					<th><a href="#" onclick="sortTable('employee'); return false;">Employee Name</a></th>
					<s:iterator value="competencies">
						<th><s:property value="label" /></th>
					</s:iterator>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="employees" id="employee" status="stat">
					<tr>
						<td class="id"><s:property value="#stat.count" /></td>
						<td class="employee"><a href="EmployeeCompetencies.action?conID=<s:property value="conID" />&employeeID=<s:property value="#employee.id" />"><s:property value="#employee.displayName" /></a></td>
						<s:iterator value="competencies" id="competency">
							<s:set name="ec" id="ec" value="map.get(#employee, #competency)" />
							<td class="center"
									<s:if test="!#ec.skilled"> style="background-color: #FAA"</s:if>
									<s:if test="#ec.skilled"> style="background-color: #AFA"</s:if>>
								<s:if test="#ec != null">
									<input type="checkbox" <s:if test="#ec.skilled">checked="checked"</s:if> <s:if test="canEdit">onclick="saveChange(<s:property value="#ec.id" />, this); return false;"</s:if><s:else>disabled="disabled"</s:else> />
								</s:if>
							</td>
						</s:iterator>
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
					<th><a href="#" onclick="sortTable('category,label'); return false;">Job Competency Description</a></th>
					<th><a href="#" onclick="sortTable('label,category'); return false;">Label</a></th>
					<th><s:property value="employee.displayName" /></th>
					<s:if test="canEdit"><th></th></s:if>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="competencies" id="competency" status="stat">
					<s:set name="ec" id="ec" value="map.get(employee, #competency)" />
					<tr>
						<td class="id"><s:property value="#stat.count" /></td>
						<td class="category"><s:property value="category" /></td>
						<td class="label"><s:property value="label" /></td>
						<td class="center"
								<s:if test="!#ec.skilled"> style="background-color: #FAA"</s:if>
								<s:elseif test="#ec.skilled"> style="background-color: #AFA"</s:elseif>>
							<s:if test="#ec != null">
								<input type="checkbox" <s:if test="#ec.skilled">checked="checked"</s:if> <s:if test="canEdit">onclick="saveChange(<s:property value="#ec.id" />, this); return false;"</s:if><s:else>disabled="disabled"</s:else> />
							</s:if>
						</td>
						<s:if test="canEdit">
							<td class="center">
								<s:if test="#ec != null">
									<a href="EmployeeCompetencies.action?conID=<s:property value="conID" />&button=RemoveCompetency&employeeID=<s:property value="employeeID" />&ecID=<s:property value="#ec.id" />" class="remove"></a>
								</s:if>
								<s:else>
									<a href="EmployeeCompetencies.action?conID=<s:property value="conID" />&button=AddCompetency&employeeID=<s:property value="employeeID" />&competencyID=<s:property value="#competency.id" />" class="add"></a>
								</s:else>
							</td>
						</s:if>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:form>
	<script type="text/javascript">sortTable('category,label');</script>
</s:elseif>

</body>
</html>