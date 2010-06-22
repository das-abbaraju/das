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

function getNew() {
	var data = {
		button: 'Load',
		id: <s:property value="center.id" />,
		testID: 0
	};

	startThinking({div: 'assessmentTest', message: 'Getting Add New Assessment Test form'});
	$('#assessmentTest').load('ManageAssessmentTestsAjax.action', data);
}

function loadTest(testID) {
	var data = {
		button: 'Load',
		id: <s:property value="center.id" />,
		testID: testID 
	};

	startThinking({div: 'assessmentTest', message: 'Loading assessment test'});
	$('#assessmentTest').load('ManageAssessmentTestsAjax.action', data);
	$('#addLink').show();
}
</script>
</head>
<body>

<s:include value="assessmentHeader.jsp" />

<s:if test="tests.size() > 0">
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Qualification Type</th>
			<th>Qualification Method</th>
			<th>Description</th>
			<th>Effective Date</th>
			<th>Verifiable</th>
			<th>Months To Expire</th>
			<th>Edit</th>
			<th>Remove</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="tests" status="stat">
			<tr>
				<td><s:property value="#stat.count" /></td>
				<td><s:property value="qualificationType" /></td>
				<td><s:property value="qualificationMethod" /></td>
				<td><s:property value="description" /></td>
				<td class="center"><s:date name="effectiveDate" format="MM/dd/yyyy" /></td>
				<td class="center"><s:if test="verifiable"><img src="images/okCheck.gif" alt="Verifiable" /></s:if></td>
				<td class="right"><s:property value="monthsToExpire" /></td>
				<td class="center"><a href="#" onclick="loadTest(<s:property value="id" />); return false;" class="edit"></a></td>
				<td class="center"><a href="ManageAssessmentTests.action?id=<s:property value="center.id" />&button=Remove&testID=<s:property value="id" />"
					class="remove" onclick="return confirm('Are you sure you want to remove this assessment test?');"></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:if>
<a href="#" onclick="getNew(); $(this).hide(); return false;" id="addLink" 
	class="add">Add New Assessment Test</a>
<div id="assessmentTest"></div>

</body>
</html>
