<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit List</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Audit List</h1>

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
	    <td><a href="?orderBy=ca.createdDate DESC" >Created</a></td>
	    <td><a href="?orderBy=ca.auditStatus DESC" >Status</a></td>
	    <td><a href="?orderBy=ca.percentComplete" >Comp%</a></td>
	    <td><a href="?orderBy=ca.percentVerified" >Ver%</a></td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
	    <td><a href="?orderBy=auditor.name" >Auditor</a></td>
	    </s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
		<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
		<td class="center"><s:date name="[0].get('createdDate')" format="M/d/yy" /></td>
		<td><s:property value="[0].get('auditStatus')"/></td>
		<td class="right"><s:property value="[0].get('percentComplete')"/>%</td>
		<td class="right"><s:property value="[0].get('percentVerified')"/>%</td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
		<td><s:property value="[0].get('auditor_name')"/></td>
		</s:if>
	</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
