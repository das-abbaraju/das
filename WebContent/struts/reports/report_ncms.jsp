<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>NCMS Data</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>NCMS Data</h1> 
<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td height="30" align="left"><s:property escape="false" value="report.startsWithLinks" /></td>
		<td align="right"><s:property escape="false" value="report.pageLinks" /></td>
	</tr>
</table>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor</td>
		<td>NCMS Name</td>
		<td>PICS Tax ID</td>
		<td>NCMS Tax ID</td>
		<td>NCMS Last Review</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="accounts_edit_contractor.jsp?id=<s:property value="[0].get('id')"/>"
			title="view <s:property value="[0].get('name')" /> details"><s:property value="[0].get('name')"/></a></td>
		<td><a
			href="report_ncmsIndividual.jsp?conID=<s:property value="[0].get('id')"/>&name=<s:property value="[0].get('ContractorsName')" />"><s:property value="[0].get('ContractorsName')"/></a></td>
		<td><s:property value="[0].get('taxID')" /></td>
		<td><s:property value="[0].get('fedTaxID')" /></td>
		<td><s:property value="[0].get('lastReview')" /></td>
	</tr>
	</s:iterator>
</table>
<div align="center"><s:property escape="false" value="report.pageLinks" /></div>
</body>
</html>
