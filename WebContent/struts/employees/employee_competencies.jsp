<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Employee HSE Competencies</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<style type="text/css">
#formTable tr td {
	vertical-align: top;
	padding-right: 10px;
}

#formTable tr td label {
	width: auto;
}
</style>
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
			$(checkbox.parentNode).css('background-color', checkbox.checked ? '#AFA' : '#FAA');
		}
	);
}

function getData() {
	$('#singleView').empty();
	$('#competencyTable').html('<img src="images/ajax_process2.gif" /> finding results');
	$('#competencyTable').load('EmployeeCompetenciesAjax.action', $('#form1').serialize());
}

$.ajaxSetup({
	error : function(XMLHttpRequest, textStatus, errorThrown) {
		$.gritter.add({
			title:'Unexpected Error Occurred',
			text: 'Connection with PICS failed. If this continues, try refreshing or logging out.'
		});
	}
});

function go(type, id) {
	if (id > 0) {
		self.location = 'EmployeeCompetencies.action?conID=' + <s:property value="conID" />
			+ '&' + type + '=' + id;
	}
}
</script>
</head>
<body>

<h1>Employee HSE Competencies<span class="sub"><s:property value="contractor.name" /></span></h1>

<s:form id="form1">
	<s:hidden name="conID" />
	<s:hidden name="button" value="Update List" />
	<fieldset class="form bottom">
		<table id="formTable">
			<tr>
				<td>
					<ol>
						<li><label>HSE Competencies:</label>
							<s:select id="competencyList" list="competencies" listKey="id" listValue="label" 
								multiple="true" name="selectedCompetencies" size="10" />
						</li>
					</ol>
					<input type="button" onclick="getData(); return false;" class="picsbutton positive" value="View"
						style="margin-left: 10px">
				</td>
				<td>
					<ol>
						<li><label>Employees:</label>
							<s:select list="employees" listKey="id" listValue="displayName" headerKey="0"
								headerValue="- Employee -" onchange="go('employeeID', this.value);" />
						</li>
					</ol>
				</td>
				<td>
					<ol>
						<li><label>Job Roles:</label>
							<s:select list="jobRoles" listKey="id" listValue="name" headerKey="0" headerValue="- Job Role -"
								onchange="go('jobRoleID', this.value);" />
						</li>
					</ol>
				</td>
			</tr>
		</table>
	</fieldset>
</s:form>
<div id="competencyTable"></div>
<s:if test="employees.size() == 0">
	<div class="info">This contractor has no employees.</div>
</s:if>
<s:if test="employeeID > 0">
	<div id="singleView">
		<h3>Skills for <s:property value="employee.displayName" /></h3>
		<a href="ManageEmployees.action?employee.id=<s:property value="employee.id" />">View/Edit Employee</a>
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
					<s:set name="ec" id="ec" value="map.get(employee, #competency.competency)" />
					<tr>
						<td class="id"><s:property value="#stat.count" /></td>
						<td class="category"><s:property value="#ec.competency.category" /></td>
						<td class="label"><s:property value="#ec.competency.label" /></td>
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
		<script type="text/javascript">sortTable('category,label');</script>
	</div>
</s:if>
<s:elseif test="jobRoleID > 0">
	<div id="singleView">
		<h3><s:property value="jobRole.name" />s</h3>
		<a href="ManageJobRoles.action?id=<s:property value="conID" />&role.id=<s:property value="jobRole.id" />">View/Edit Role</a>
		<table class="report" id="competenceMatrix">
			<thead>
				<tr>
					<th></th>
					<th><a href="#" onclick="sortTable('lastName,firstName'); return false;">Last Name</a></th>
					<th><a href="#" onclick="sortTable('firstName,lastName'); return false;">First Name</a></th>
					<s:iterator value="competenciesByJobRole">
						<th><s:property value="label" /></th>
					</s:iterator>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="employees" id="employee" status="stat">
					<tr>
						<td class="id"><s:property value="#stat.count" /></td>
						<td class="lastName"><a href="?conID=<s:property value="conID" />&employeeID=<s:property value="#employee.id" />"><s:property value="#employee.lastName" /></a></td>
						<td class="firstName"><a href="?conID=<s:property value="conID" />&employeeID=<s:property value="#employee.id" />"><s:property value="#employee.firstName" /></a></td>
						<s:iterator value="competenciesByJobRole" id="competency">
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
	</div>
</s:elseif>

</body>
</html>