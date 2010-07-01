<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><s:property value="subHeading" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../reportHeader.jsp" />
<script type="text/javascript">
$().ready(function() {
	$('.datepicker').datepicker();
});

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

function showUpload() {
	url = 'ManageImportDataUploadAjax.action?id=<s:property value="center.id" />';
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=400,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function loadStage(stageID) {
	var data = {
		button: 'Load'
	};
}
</script>
</head>
<body>

<s:include value="assessmentHeader.jsp" />
<div class="info">
	The table below shows imported data that are currently not mapped with a PICS Company or
	an assessment test.
</div>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Result ID</th>
			<th><a href="#" onclick="sortTable('qtype,qmethod'); return false;">Qualification Type</a></th>
			<th><a href="#" onclick="sortTable('qmethod,qtype'); return false;">Qualification Method</a></th>
			<th>Description</th>
			<th><a href="#" onclick="sortTable('#test'); return false;">Test ID</a></th>
			<th colspan="2"><a href="#" onclick="sortTable('name,qtype,qmethod'); return false;">Employee</a></th>
			<th>Employee ID</th>
			<th><a href="#" onclick="sortTable('company,qtype,qmethod'); return false;">Company</a></th>
			<th>Company ID</th>
			<th><a href="#" onclick="sortTable('%date,name'); return false;">Qualification Date</a></th>
		</tr>
	</thead>
	<tbody>	
		<s:iterator value="staged" status="stat">
			<tr>
				<td class="right index"><s:property value="#stat.count" /></td>
				<td><s:property value="resultID" /></td>
				<td class="qtype"><s:property value="qualificationType" /></td>
				<td class="qmethod"><s:property value="qualificationMethod" /></td>
				<td><s:property value="description" /></td>
				<td class="right test"><s:property value="testID" /></td>
				<td><s:property value="firstName" /></td>
				<td class="name"><s:property value="lastName" /></td>
				<td><s:property value="employeeID" /></td>
				<td class="company"><s:property value="companyName" /></td>
				<td class="right"><s:property value="companyID" /></td>
				<td class="center date"><s:date name="qualificationDate" format="MM/dd/yyyy" /></td>
			</tr>
		</s:iterator>
		<s:if test="staged.size() == 0">
			<tr>
				<td colspan="13">No records found</td>
			</tr>
		</s:if>
	</tbody>
</table>

<a href="#" onclick="showUpload(); return false;" class="add">Upload Assessment Results</a><br />
<s:if test="id == 11071 || permissions.accountID == 11071">
	<a href="#" onclick="$('#oqsgImportLink').hide(); $('#oqsgImportDiv').show(); return false;" 
		class="add" id="oqsgImportLink">OQSG Webservice Import</a><br />
	<s:form id="oqsgImportDiv" cssStyle="display: none;">
		<s:hidden name="id" />
		<input type="submit" value="Import New Records" name="button" class="picsbutton" />
		<fieldset class="form" style="margin-top: 10px">
			<legend><span>Import by Date</span></legend>
			<ol>
				<li><label>Start:</label>
					<input type="text" name="start" class="datepicker" value="<s:date name="start" format="MM/dd/yyyy" />" />
				</li>
				<li><label>End:</label>
					<input type="text" name="end" class="datepicker" value="<s:date name="end" format="MM/dd/yyyy" />" />
				</li>
			</ol>
			<div style="margin: 10px;">
				<input type="submit" value="Import By Date" name="button" class="picsbutton" /></div>
		</fieldset>
		<input type="button" value="Cancel" class="picsbutton" 
			onclick="$('#oqsgImportDiv').hide(); $('#oqsgImportLink').show();" />
	</s:form>
</s:if>

</body>
</html>