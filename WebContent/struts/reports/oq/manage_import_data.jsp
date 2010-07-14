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

<s:form id="form1">
<s:hidden name="filter.ajax" value="false" />
<s:hidden name="filter.destinationAction" value="ManageImportData" />
<s:hidden name="showPage" value="1" />
<s:hidden name="orderBy" />
<s:hidden name="id" />
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Result ID</th>
			<th><a href="?id=<s:property value="id" />&orderBy=qualificationType,qualificationMethod">Qualification Type</a></th>
			<th><a href="?id=<s:property value="id" />&orderBy=qualificationMethod,qualificationType">Qualification Method</a></th>
			<th>Description</th>
			<th><a href="?id=<s:property value="id" />&orderBy=testID">Test ID</a></th>
			<th colspan="2"><a href="?orderBy=firstName,lastName">Employee</a></th>
			<th>Employee ID</th>
			<th><a href="?id=<s:property value="id" />&orderBy=companyName">Company</a></th>
			<th>Company ID</th>
			<th><a href="?id=<s:property value="id" />&orderBy=s.qualificationDate">Qualification Date</a></th>
		</tr>
	</thead>
	<tbody>	
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td><s:property value="get('resultID')" /></td>
				<td><s:property value="get('qualificationType')" /></td>
				<td><s:property value="get('qualificationMethod')" /></td>
				<td><s:property value="get('description')" /></td>
				<td class="right"><s:property value="get('testID')" /></td>
				<td><s:property value="get('firstName')" /></td>
				<td><s:property value="get('lastName')" /></td>
				<td><s:property value="get('employeeID')" /></td>
				<td><s:property value="get('companyName')" /></td>
				<td class="right"><s:property value="get('companyID')" /></td>
				<td class="center"><s:property value="get('qualificationDate')" /></td>
			</tr>
		</s:iterator>
		<s:if test="data.size() == 0">
			<tr>
				<td colspan="13">No records found</td>
			</tr>
		</s:if>
	</tbody>
</table>
<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:form>

<a href="#" onclick="showUpload(); return false;" class="add">Upload Assessment Results</a><br />
<s:if test="id == 11071 || permissions.accountID == 11071">
	<a href="#" onclick="$('#oqsgImportLink').hide(); $('#oqsgImportDiv').show(); return false;" 
		class="add" id="oqsgImportLink">OQSG Webservice Import</a><br />
	<s:form id="oqsgImportDiv" cssStyle="display: none;">
		<s:hidden name="id" />
		<fieldset class="form" style="margin-top: 10px">
			<h2 class="formLegend">Import by Date</h2>
			<ol>
				<li><label>Start:</label>
					<input type="text" name="start" class="datepicker" value="<s:date name="start" format="MM/dd/yyyy" />" />
				</li>
				<li><label>End:</label>
					<input type="text" name="end" class="datepicker" value="<s:date name="end" format="MM/dd/yyyy" />" />
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<input type="submit" value="Import By Date" name="button" class="picsbutton positive" />
			<input type="submit" value="Import New Records" name="button" class="picsbutton" />
			<input type="button" value="Cancel" class="picsbutton" 
				onclick="$('#oqsgImportDiv').hide(); $('#oqsgImportLink').show();" />
		</fieldset>
	</s:form>
</s:if>

</body>
</html>