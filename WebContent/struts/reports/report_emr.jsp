<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>EMR Report</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>EMR Report</h1>

<s:include value="filters.jsp" />
<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
	    <th></th>
	    <th><a href="?orderBy=a.name" >Contractor</a></th>
	    <th>Year</th>
	    <th><a href="?orderBy=d.answer" >Rate</a></th>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a></td>
			<td><s:property value="get('auditFor')"/></td>
			<td class="right"><s:property value="get('answer')"/></td>
		</tr>
	</s:iterator>
</table>
<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>


</body>
</html>
