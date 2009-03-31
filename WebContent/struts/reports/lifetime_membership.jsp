<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit List</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Audit List</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<div id="report_data">
	<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th>ID</th>
	    <td>Name</td>
	    <td>Active</td>
	    <td>Paying Facilities</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><s:property value="get('id')"/></td>
		<td><s:property value="get('name')"/></td>
		<td><s:property value="get('active')"/></td>
		<td><s:property value="get('payingFacilities')"/></td>
	</tr>
	</s:iterator>
</table>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
