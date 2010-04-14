<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Data Partner IDs</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
// Sort by table header
function sortTable(sortBy) {
	previousSort = sortBy;
	var tbody = $('#employee_auths').find('tbody');
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
<h1>Data Partner IDs <s:if test="employeeID > 0"><span class="sub"><s:property value="employee.displayName"/></span></s:if></h1>

<s:if test="employeeID > 0">
	<div id="internalnavcontainer">
		<ul id="navlist">
			<li><a href="EmployeeDetail.action?employee.id=<s:property value="employeeID" />">Edit</a></li>
			<li><a href="EmployeeAssessmentResults.action?employee.id=<s:property value="employeeID" />">Assessments</a></li>
			<li><a href="EmployeeDataPartnerIDs.action?employee.id=<s:property value="employeeID" />">Data Partner IDs</a></li>
		</ul>
	</div>
</s:if>
	
<s:include value="../actionMessages.jsp"/>

<s:if test="employeeID == 0">
	<s:form method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
		<input type="submit" value="Generate Data Partner IDs" name="button" class="picsbutton positive" />
	</s:form>
	<br />
</s:if>

<s:if test="dataPartnerIDs.size() > 0">
	<table class="report" id="employee_auths">
		<thead>
			<tr>
				<th></th>
				<th><a href="#" onclick="sortTable('assessmentCenter'); return false;">Assessment Center</a></th>
				<s:if test="employeeID == 0"><th>
					<a href="#" onclick="sortTable('employee'); return false;">Employee Name</a>
				</th></s:if>
				<th><a href="#" onclick="sortTable('membership'); return false;">Membership ID</a></th>
				<th><a href="#" onclick="sortTable('authorization'); return false;">Authorization Key</a></th>
				<s:if test="canEdit"><th>Remove</th></s:if>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="dataPartnerIDs" status="stat" id="eaa">
				<tr>
					<td class="id"><s:property value="#stat.count" /></td>
					<td class="assessmentCenter"><s:property value="#eaa.assessmentCenter.name" /></td>
					<s:if test="employeeID == 0">
						<td class="employee">
							<a href="EmployeeDetail.action?employee.id=<s:property value="#eaa.employee.id" />">
								<s:property value="#eaa.employee.displayName" />
							</a>
						</td>
					</s:if>
					<td class="membership"><s:property value="#eaa.membershipID" /></td>
					<td class="authorization"><s:property value="#eaa.authorizationKey" /></td>
					<td class="center"><a href="EmployeeDataPartnerIDs.action?button='Remove'&eaaID=<s:property value="#eaa.id" />" class="remove"></a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<s:if test="employeeID > 0">
		<div class="info">This employee has no data partner IDs associated.</div>
	</s:if>
</s:else>

<s:if test="canEdit">
	<a href="#" onclick="$('#newAuthDiv').show(); $('#newAuthLink').hide(); return false;" class="add" id="newAuthLink">Add New Data Partner ID</a>
	<div id="newAuthDiv" style="display: none;">
		<s:form id="newAuthForm" method="POST" enctype="multipart/form-data">
			<s:hidden name="employee.id" />
			<fieldset class="form bottom" >
				<legend><span>Add New Data Partner ID</span></legend>
				<ol>
					<li><label>Assessment Center:</label>
						<s:select list="assessmentCenters" headerValue="- Assessment Center -" headerKey="0"
							listValue="name" listKey="id" name="centerID" ></s:select>
					</li>
					<s:if test="employeeID == 0">
						<li><label>Employee:</label>
							<s:select list="employees" headerValue="- Employees -" headerKey="0"
								listKey="id" listValue="displayName" name="employeeID" ></s:select>
						</li>
					</s:if>
					<li><label>Membership ID:</label>
						<s:textfield name="membershipID" size="20" />
					</li>
					<li><label>Authorization Key:</label>
						<s:textfield name="authorizationKey" size="20" />
					</li>
				</ol>
				<div style="margin-left: 5em;">
					<input type="submit" value="Save" class="picsbutton positive" name="button" />
					<button onclick="$('#newAuthLink').show(); $('#newAuthDiv').hide(); return false;"
						class="picsbutton negative">Cancel</button>
				</div>
			</fieldset>
		</s:form>
	</div>
</s:if>

</body>
</html>
