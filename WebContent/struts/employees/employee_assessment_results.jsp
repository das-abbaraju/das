<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
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
</script>
</head>
<body>
<h1>Assessment Results <s:if test="employeeID > 0"><span class="sub"><s:property value="employee.displayName"/></span></s:if></h1>

<s:if test="employeeID > 0">
	<div id="internalnavcontainer">
		<ul id="navlist">
			<li><a href="EmployeeDetail.action?employee.id=<s:property value="employee.id" />">Edit</a></li>
			<li><a href="EmployeeAssessmentResults.action?employee.id=<s:property value="employee.id" />">Assessments</a></li>
			<li><a href="EmployeeDataPartnerIDs.action?employee.id=<s:property value="employee.id" />">Data Partner IDs</a></li>
		</ul>
	</div>
</s:if>
	
<s:include value="../actionMessages.jsp"/>

<s:if test="employeeID == 0">
	<s:form method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
		<input type="submit" value="Generate Assessment Results" name="button" class="picsbutton positive" />
	</s:form>
	<br />
</s:if>

<s:if test="effective.size() > 0">
	<table class="report" id="effective"><thead>
		<tr>
			<th></th>
			<th><a href="#" onclick="sortTable('effective', 'center'); return false;">Assessment Center</a></th>
			<th><a href="#" onclick="sortTable('effective', 'type'); return false;">Qualification Type</a></th>
			<th><a href="#" onclick="sortTable('effective', 'method'); return false;">Qualification Method</a></th>
			<th><a href="#" onclick="sortTable('effective', 'description'); return false;">Test Description</a></th>
			<s:if test="employee == null">
				<th><a href="#" onclick="sortTable('effective', 'employee'); return false;">Employee</a></th>
			</s:if>
			<pics:permission perm="ManageJobSites" type="Edit">
				<th>Remove</th>
			</pics:permission>
		</tr>
	</thead><tbody>
		<s:iterator value="effective" id="result" status="stat">
			<tr>
				<td class="id"><s:property value="#stat.count" /></td>
				<td class="assessmentCenter"><s:property value="#result.assessmentTest.assessmentCenter.name" /></td>
				<td class="type"><s:property value="#result.assessmentTest.qualificationType" /></td>
				<td class="method"><s:property value="#result.assessmentTest.qualificationMethod" /></td>
				<td class="description"><s:property value="#result.assessmentTest.description" /></td>
				<s:if test="employeeID == 0">
					<td class="employee">
						<a href="EmployeeDetail.action?employee.id=<s:property value="#result.employee.id" />">
							<s:property value="#result.employee.displayName" />
						</a>
					</td>
				</s:if>
				<pics:permission perm="ManageJobSites" type="Edit">
					<td class="center">
						<a href="ManageAssessmentResults.action?button=Remove&resultID=<s:property value="#result.id" />" class="remove"></a>
					</td>
				</pics:permission>
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
			<th>Assessment Center</th>
			<th>Qualification Type</th>
			<th>Qualification Method</th>
			<th>Test Description</th>
			<s:if test="employee == null">
				<th>Employee</th>
			</s:if>
			<th>Expiration Date</th>
		</tr>
	</thead><tbody>
		<s:iterator value="expired" id="result" status="stat">
			<tr>
				<td><s:property value="#result.assessmentTest.assessmentCenter.name" /></td>
				<td><s:property value="#result.assessmentTest.qualificationType" /></td>
				<td><s:property value="#result.assessmentTest.qualificationMethod" /></td>
				<td><s:property value="#result.assessmentTest.description" /></td>
				<td>
					<a href="ManageEmployees.action?employee.id=<s:property value="#result.employee.id" />">
						<s:property value="#result.employee.lastName" />, <s:property value="#result.employee.firstName" />
					</a>
				</td>
				<td><s:date name="#result.expirationDate" format="MM/dd/yyyy" /></td>
			</tr>
		</s:iterator>
	</tbody></table>
</s:if>
</body>
</html>
