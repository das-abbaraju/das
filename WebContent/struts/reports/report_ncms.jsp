<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>NCMS Data</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>NCMS Data</h1> 
<s:form id="form1">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td height="30" align="left"><s:property
			value="report.startsWithLinksWithDynamicForm" escape="false" />
		</td>
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
		<td><a href="ContractorEdit.action?id=<s:property value="[0].get('id')"/>"
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
