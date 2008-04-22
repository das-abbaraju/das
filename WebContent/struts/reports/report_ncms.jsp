<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>NCMS Data</title>
</head>
<body>
<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td height="30" align="left"><s:property value="report.startsWithLinks" /></td>
		<td align="right"><s:property value="report.pageLinks" /></td>
	</tr>
</table>
<table width="657" border="0" cellpadding="1" cellspacing="1"
	align="center">
	<tr bgcolor="#003366" class="whiteTitle">
		<td width="150" colspan="2">Contractor</td>
		<td width="150">NCMS Name</td>
		<td align="center">PICS Tax ID</td>
		<td align="center">NCMS Tax ID</td>
		<td align="center">NCMS Last Review</td>
	</tr>
	<s:iterator value="data">
	<tr class="blueMain" <s:property value="color.nextBgColor" />>
		<td align="right"><s:property value="color.counter" /></td>
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
<p align="center"><s:property value="report.pageLinks" /></p>
</body>
</html>
