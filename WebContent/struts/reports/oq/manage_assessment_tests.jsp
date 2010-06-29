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

function sortTable(sortBy) {
	var tbody = $('table.report').find('tbody');
	var rows = $(tbody).children();
	$(tbody).empty();

	var sortBys = sortBy.split(',');
	rows.sort(function(a, b) {
		var sort1 = sort(a, b, sortBys[0]);
		var count = 0;

		while (sort1 == 0 && count < sortBys.length) {
			sort1 = sort(a, b, sortBys[count]);
			count++;
		}

		return sort1;
	});

	$.each(rows, function (index, row) { $(row).find('.index').text(index + 1); $(tbody).append(row); });
}

function sort(a, b, sortBy) {
	var a1;
	var b1;
	
	if (sortBy.indexOf("#") >= 0) {
		a1 = new Number($(a).find('.' + sortBy.substr(1)).text());
		b1 = new Number($(b).find('.' + sortBy.substr(1)).text());
	} else if (sortBy.indexOf("%") >= 0) {
		a1 = new Date($(a).find('.' + sortBy.substr(1)).text());
		b1 = new Date($(b).find('.' + sortBy.substr(1)).text());
	} else {
		a1 = $(a).find('.' + sortBy).text().toUpperCase();
		b1 = $(b).find('.' + sortBy).text().toUpperCase();
	}

	return a1 > b1 ? 1 : a1 < b1 ? -1 : 0;
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
			<th><a href="#" onclick="sortTable('qtype,qmethod,%date'); return false;">Qualification Type</a></th>
			<th><a href="#" onclick="sortTable('qmethod,qtype,%date'); return false;">Qualification Method</a></th>
			<th>Description</th>
			<th><a href="#" onclick="sortTable('%date,qtype,qmethod'); return false;">Effective Date</a></th>
			<th>Verifiable</th>
			<th>Months To Expire</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="tests" status="stat">
			<tr class="clickable" onclick="loadTest(<s:property value="id" />);">
				<td class="index"><s:property value="#stat.count" /></td>
				<td class="qtype"><s:property value="qualificationType" /></td>
				<td class="qmethod"><s:property value="qualificationMethod" /></td>
				<td><s:property value="description" /></td>
				<td class="center date"><s:date name="effectiveDate" format="MM/dd/yyyy" /></td>
				<td class="center"><s:if test="verifiable"><img src="images/okCheck.gif" 
					alt="Verifiable" /></s:if></td>
				<td class="right"><s:property value="monthsToExpire" /></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:if>
<a href="#" onclick="getNew(); $('#assessmentTest').show(); $(this).hide(); return false;" id="addLink"
	class="add">Add New Assessment Test</a>
<div id="assessmentTest"></div>
<div id="editTest" style="display: none"></div>

</body>
</html>