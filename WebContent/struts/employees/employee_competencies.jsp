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

	$.getJSON('EmployeeCompetenciesAjax.action', data,
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

function getData() {
	$('#competencyTable').html('<img src="images/ajax_process2.gif" /> finding results');
	$('#competencyTable').load('EmployeeCompetenciesAjax.action', $('#form1').serialize());
}
</script>
</head>
<body>

<h1>Employee Competencies<span class="sub"><s:property value="contractor.name" /></span></h1>

<s:if test="conID > 0 && employees.size() > 0 && employeeID == 0">
	<s:form id="form1">
		<s:hidden name="conID" />
		<s:hidden name="button" value="Update List" />
		<fieldset class="form bottom">
			<ol>
				<li><label style="width: auto;">Competencies:</label>
					<s:select id="competencyList" list="competencies" listKey="id" listValue="label" 
						multiple="true" name="selectedCompetencies" size="10" />
				</li>
			</ol>
			<button onclick="getData(); return false;" class="picsbutton positive submit">View</button>
		</fieldset>
	</s:form>
	<div id="competencyTable"></div>
</s:if>
<s:elseif test="employees.size() == 0">
	<div class="info">This contractor has no employees.</div>
</s:elseif>
<s:elseif test="employeeID > 0">
	<h3>Job Competency for <s:property value="employee.displayName" /></h3>
	<s:form method="POST">
		<table class="report" id="competenceMatrix">
			<thead>
				<tr>
					<th></th>
					<th><a href="#" onclick="sortTable('category,label'); return false;">Category</a></th>
					<th><a href="#" onclick="sortTable('label,category'); return false;">Label</a></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="getCompetencies(employee)" id="competency" status="stat">
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
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:form>
	<script type="text/javascript">sortTable('category,label');</script>
</s:elseif>

<s:if test="employeeID > 0">
	<a href="EmployeeCompetencies.action?conID=<s:property value="conID" />">View all employees for <s:property value="contractor.name" /></a>
</s:if>

</body>
</html>