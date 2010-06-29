<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="subHeading" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<s:include value="../../jquery.jsp" />
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

<s:if test="results.size() > 0">
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th><a href="#" onclick="sortTable('company,employee,test,%date'); return false;">Company</a></th>
			<th><a href="#" onclick="sortTable('employee,company,test,%date'); return false;">Employee</a></th>
			<th><a href="#" onclick="sortTable('%date,company,company,test'); return false;">Assessment Date</a></th>
			<th><a href="#" onclick="sortTable('test,company,employee,%date'); return false;">Assessment Test</a></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="results" status="stat">
			<tr class="clickable" onclick="edit(<s:property value="id" />, <s:property value="employee.account.id" />); return false;">
				<td class="index"><s:property value="#stat.count" /></td>
				<td class="company"><s:property value="employee.account.name" /></td>
				<td class="employee"><s:property value="employee.displayName" /></td>
				<td class="center date"><s:date name="effectiveDate" format="MM/dd/yyyy" /></td>
				<td class="test"><s:property value="assessmentTest.qualificationMethod" /> - <s:property value="assessmentTest.qualificationType" /></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:if>
<a href="#" onclick="add(); $(this).hide(); return false;" id="addLink" 
	class="add">Add New Assessment Result</a>
<div id="assessmentResult"></div>
<div id="editResult"></div>

</body>
</html>
