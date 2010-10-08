<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="subHeading" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<s:include value="../reportHeader.jsp" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
function add() {	
	$('#assessmentResult').show();
	
	var data = {
		button: 'Load',
		id: <s:property value="center.id" />,
		resultID: 0,
		companyID: 0
	};

	startThinking({div: 'assessmentResult', message: 'Loading assessment result'});
	$('#assessmentResult').load('ManageAssessmentResultsAjax.action', data);
}

function edit(resultID, companyID) {	
	var data = {
		button: 'Load',
		id: <s:property value="center.id" />,
		resultID: resultID,
		companyID: companyID
	};

	$('#editResult').load('ManageAssessmentResultsAjax.action', data, function() {
		$(this).dialog({
			title: 'Edit Assessment result',
			modal: true,
			width: 500,
			open: function() {
				$('#company_edit').autocomplete('ContractorSelectAjax.action', 
					{
						minChars: 3,
						extraParams: {'filter.accountName': function() {return $('#company_edit').val();} },
						formatResult: function(data,i,count) { return data[0]; }
					}
				).result(function(event, data){
					$('input#companyID').val(data[1]);
					getEmployee('employee_edit', $('input#companyID').val(), $('input#resultID').val());
				});
			},
			close: function() { $(this).dialog("destroy"); $('#editResult').empty(); }
		});
	});

	$('#addLink').show();
}

function getEmployee(div, companyID, resultID) {
	var data = {
		button: 'Employee',
		id: <s:property value="center.id" />,
		resultID: resultID,
		companyID: companyID
	};

	startThinking({div: div, message: 'Loading employee list'});
	$('#' + div).load('ManageAssessmentResultsAjax.action', data);
}
</script>
</head>
<body>

<s:include value="assessmentHeader.jsp" />

<div class="info">
	The table below shows assessment results mapped with an employee. If results are missing,
	the record may need to be mapped with a company, an assessment test, or an employee.
</div>

<s:form id="form1">
<s:hidden name="filter.ajax" value="false" />
<s:hidden name="filter.destinationAction" value="ManageAssessmentResults" />
<s:hidden name="showPage" value="1" />
<s:hidden name="orderBy" />
<s:hidden name="id" />
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th><a href="?id=<s:property value="id" />&orderBy=a.name">Company</a></th>
			<th><a href="?id=<s:property value="id" />&orderBy=e.firstName,e.lastName">Employee</a></th>
			<th><a href="?id=<s:property value="id" />&orderBy=r.effectiveDate">Assessment Date</a></th>
			<th><a href="?id=<s:property value="id" />&orderBy=qualificationMethod,qualificationType">Assessment Test</a></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat">
			<tr class="clickable" onclick="edit(<s:property value="get('id')" />, <s:property value="get('accountID')" />); return false;">
				<td><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td><s:property value="get('name')" /></td>
				<td><s:property value="get('firstName')" /> <s:property value="get('lastName')" /></td>
				<td class="center"><s:property value="get('effectiveDate')" /></td>
				<td><s:property value="get('qualificationMethod')" /> - <s:property value="get('qualificationType')" /></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:form>

<a href="#" onclick="add(); $(this).hide(); return false;" id="addLink" 
	class="add">Add New Assessment Result</a>
<div id="assessmentResult"></div>
<div id="editResult"></div>

</body>
</html>
