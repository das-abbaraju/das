<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="/exception_handler.jsp"%>
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
	$('.datepicker').datepicker({
		showOn: 'both',
		buttonImage: 'images/icon_calendar.gif',
		buttonImageOnly: true
	});
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

	startThinking({div: 'editTest', message: 'Loading assessment test'});
	$('#editTest').load('ManageAssessmentTestsAjax.action', data, function() {
		$(this).dialog({
			title: 'Edit Assessment Test',
			modal: true,
			height: 350,
			width: 450,
			close: function() { $(this).dialog("destroy"); }
		});
	});
	$('#addLink').show();
}
</script>
<s:include value="../reportHeader.jsp" />
</head>
<body>

<s:include value="assessmentHeader.jsp" />

<s:form id="form1">
<s:hidden name="filter.ajax" value="false" />
<s:hidden name="filter.destinationAction" value="ManageAssessmentTests" />
<s:hidden name="showPage" value="1" />
<s:hidden name="orderBy" />
<s:hidden name="id" />
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th><a href="?id=<s:property value="id" />&orderBy=qualificationType,qualificationMethod">Qualification Type</a></th>
			<th><a href="?id=<s:property value="id" />&orderBy=qualificationMethod,qualificationType">Qualification Method</a></th>
			<th>Description</th>
			<th><a href="?id=<s:property value="id" />&orderBy=effectiveDate,qualificationType">Effective Date</a></th>
			<th>Verifiable</th>
			<th>Months To Expire</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat">
			<tr class="clickable" onclick="loadTest(<s:property value="get('id')" />);">
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td><s:property value="get('qualificationType')" /></td>
				<td><s:property value="get('qualificationMethod')" /></td>
				<td><s:property value="get('description')" /></td>
				<td class="center"><s:property value="get('effectiveDate')" /></td>
				<td class="center"><s:if test="get('verifiable') == '1'"><img src="images/okCheck.gif" 
					alt="Verifiable" /></s:if></td>
				<td class="right"><s:property value="get('monthsToExpire')" /></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:form>

<a href="#" onclick="getNew(); $('#assessmentTest').show(); $(this).hide(); return false;" id="addLink"
	class="add">Add New Assessment Test</a>
<div id="assessmentTest"></div>
<div id="editTest" style="display: none"></div>

</body>
</html>