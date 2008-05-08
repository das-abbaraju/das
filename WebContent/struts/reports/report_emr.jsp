<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>EMR Report</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>EMR Report</h1>

<div id="search">
<s:form id="form1">
Year: <s:select list="{2004,2005,2006,2007}" name="year" />
Min: <s:textfield name="minRate" size="5" />
Max: <s:textfield name="maxRate" size="5" />
<s:submit />
	<s:hidden name="showPage" value="1"/>
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
</s:form>
</div>

<table class="report">
	<thead>
	<tr>
	    <th></th>
	    <th><a href="?orderBy=a.name" >Contractor</a></th>
	    <th>Year</th>
	    <th><a href="?orderBy=d.verifiedAnswer" >Rate</a></th>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
			<td><s:property value="year"/></td>
			<td class="right"><s:property value="[0].get('verifiedAnswer')"/></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>


</body>
</html>
