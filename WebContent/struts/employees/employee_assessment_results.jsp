<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<html>
<head>
<title>Assessments</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
// Sort by table header
function sortTable(tableName, sortBy) {
	previousSort = sortBy;
	var tbody = $('#' + tableName).find('tbody');
	var rows = $(tbody).children();
	$(tbody).empty();

	rows.sort(function(a, b) {
		var a1 = $(a).find('.' + sortBy).text().toUpperCase();
		var b1 = $(b).find('.' + sortBy).text().toUpperCase();

		if (sortBy != 'employee') {
			var a2 = $(a).find('.employee').text().toUpperCase();
			var b2 = $(b).find('.employee').text().toUpperCase();

			if (a1 == b1)
				return (a2 < b2) ? -1 : (a2 > b2) ? 1 : 0;
		}
			
		return (a1 < b1) ? -1 : (a1 > b1) ? 1 : 0;
	});

	$.each(rows, function (index, row) { $(row).find('.id').text(index + 1); $(tbody).append(row); });
}

function getHistory(date) {
	self.location = "EmployeeAssessmentResults.action?id=" + <s:property value="contractor.id" /> + "&employeeID="
		+ <s:property value="employeeID" /> + "&date=" + date;
}
</script>
</head>
<body>
<s:include value="../contractors/conHeader.jsp"/>

<s:if test="permissions.admin">
	<s:form method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
		<input type="submit" value="Generate Assessment Results" name="button" class="picsbutton positive" />
	</s:form>
	<br />
</s:if>

<s:if test="history.size() > 1">
	Effective on: <s:select list="history" name="date" onchange="getHistory(this.value);"></s:select><br />
	<a href="EmployeeAssessmentResults.action?id=<s:property value="contractor.id" />&employeeID=<s:property value="employeeID" />">View Today</a>
</s:if>

<s:if test="employeeID > 0">
	<a href="EmployeeAssessmentResults.action?id=<s:property value="contractor.id" />">View All Employees</a>
</s:if>

<s:if test="effective.size() > 0">
	<table class="report" id="effective"><thead>
		<tr>
			<th></th>
			<th><a href="#" onclick="sortTable('effective', 'results'); return false;">Assessment Results</a></th>
			<th><a href="#" onclick="sortTable('effective', 'description'); return false;">Test Description</a></th>
			<s:if test="employee == null">
				<th><a href="#" onclick="sortTable('effective', 'employee'); return false;">Employee</a></th>
			</s:if>
			<s:if test="canEdit">
				<th>Remove</th>
			</s:if>
		</tr>
	</thead><tbody>
		<s:iterator value="effective" id="result" status="stat">
			<tr>
				<td class="id"><s:property value="#stat.count" /></td>
				<td class="results">
					<s:property value="#result.assessmentTest.assessmentCenter.name" />:
					<s:property value="#result.assessmentTest.qualificationType" /> -
					<s:property value="#result.assessmentTest.qualificationMethod" />
				</td>
				<td class="description"><s:property value="#result.assessmentTest.description" /></td>
				<s:if test="employeeID == 0">
					<td class="employee">
						<a href="EmployeeAssessmentResults.action?id=<s:property value="contractor.id" />&employee.id=<s:property value="#result.employee.id" />">
							<s:property value="#result.employee.displayName" />
						</a>
					</td>
				</s:if>
				<s:if test="canEdit">
					<td class="center">
						<a href="EmployeeAssessmentResults.action?id=<s:property value="id" />&button=Remove&resultID=<s:property value="#result.id" />" class="remove"></a>
					</td>
				</s:if>
			</tr>
		</s:iterator>
	</tbody></table>
	<br />
</s:if>
<s:else>
	<s:if test="employeeID > 0">
		<div class="info">This employee has no assessments associated or in effect.</div>
	</s:if>
</s:else>

<s:if test="expired.size() > 0">
	<h3>Expired</h3>
	<table class="report"><thead>
		<tr>
			<th>Assessment Results</th>
			<th>Test Description</th>
			<s:if test="employeeID == 0">
				<th>Employee</th>
			</s:if>
			<th>Expiration Date</th>
		</tr>
	</thead><tbody>
		<s:iterator value="expired" id="result" status="stat">
			<tr>
				<td>
					<s:property value="#result.assessmentTest.assessmentCenter.name" />:
					<s:property value="#result.assessmentTest.qualificationType" /> -
					<s:property value="#result.assessmentTest.qualificationMethod" />
				</td>
				<td><s:property value="#result.assessmentTest.description" /></td>
				<s:if test="employeeID == 0">
					<td>
						<a href="ManageEmployees.action?employee.id=<s:property value="#result.employee.id" />">
							<s:property value="#result.employee.lastName" />, <s:property value="#result.employee.firstName" />
						</a>
					</td>
				</s:if>
				<td class="center"><s:date name="#result.expirationDate" format="MM/dd/yyyy" /></td>
			</tr>
		</s:iterator>
	</tbody></table>
</s:if>
</body>
</html>
