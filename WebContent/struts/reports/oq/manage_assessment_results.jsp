<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="subHeading" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
<style type="text/css">
#newAssessmentTest {
	display: none;
}
</style>
<s:include value="../../jquery.jsp" />
<script type="text/javascript">
$().ready(function() {
	$('.datepicker').datepicker();
});

function loadResult(resultID, companyID) {
	var data = {
		button: 'Load',
		id: <s:property value="center.id" />,
		resultID: resultID,
		companyID: companyID
	};

	startThinking({div: 'assessmentResult', message: 'Loading assessment result'});
	$('#assessmentResult').load('ManageAssessmentResultsAjax.action', data);
	$('#addLink').show();
}

function getEmployee(companyID) {
	var data = {
		button: 'Employee',
		id: <s:property value="center.id" />,
		companyID: companyID
	};

	startThinking({div: 'employeeList', message: 'Loading employee list'});
	$('#employeeList').load('ManageAssessmentResultsAjax.action', data);
}
</script>
</head>
<body>

<s:include value="assessmentHeader.jsp" />

<s:if test="results.size() > 0">
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Qualification Type</th>
			<th>Qualification Method</th>
			<th>Employee</th>
			<th>Effective Date</th>
			<th>Expiration Date</th>
			<th>Edit</th>
			<th>Remove</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="results" status="stat">
			<tr>
				<td><s:property value="#stat.count" /></td>
				<td><s:property value="assessmentTest.qualificationType" /></td>
				<td><s:property value="assessmentTest.qualificationMethod" /></td>
				<td><s:property value="employee.displayName" /></td>
				<td class="center"><s:date name="effectiveDate" format="MM/dd/yyyy" /></td>
				<td class="center"><s:date name="expirationDate" format="MM/dd/yyyy" /></td>
				<td class="center"><a href="#" onclick="loadResult(<s:property value="id" />, <s:property value="employee.account.id" />); return false;" class="edit"></a></td>
				<td class="center"><a href="ManageAssessmentResults.action?id=<s:property value="center.id" />&button=Remove&resultID=<s:property value="id" />"
					class="remove" onclick="return confirm('Are you sure you want to remove this assessment result?');"></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:if>
<a href="#" onclick="loadResult(0, 0); $(this).hide(); return false;" id="addLink" 
	class="add">Add New Assessment Result</a>
<div id="assessmentResult"></div>

</body>
</html>
