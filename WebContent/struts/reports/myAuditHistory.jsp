<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>My Audit History</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>My Audit History</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="?orderBy=a.name" >Contractor</a></th>
	    <td><a href="?orderBy=atype.auditName" >Type</a></td>
	    <td><a href="?orderBy=ca.closedDate DESC" >Created</a></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><s:property value="[0].get('name')"/></td>
		<td><s:property value="[0].get('auditName')"/></td>
		<td class="center"><s:date name="[0].get('closedDate')" format="M/d/yy" /></td>
	</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
