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
<s:include value="actionMessages.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <td><a href="javascript: changeOrderBy('form1','atype.auditName');" >Type</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.createdDate DESC');" >Created</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.completedDate DESC');" >Submitted</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.closedDate DESC');" >Closed</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.expiresDate DESC');" >Expired</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.auditStatus DESC');" >Status</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.percentComplete');" >Comp%</a></td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
	    <td><a href="javascript: changeOrderBy('form1','auditor.name');" >Auditor</a></td>
	    </s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
		<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
		<td class="center"><s:date name="[0].get('createdDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="[0].get('completedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="[0].get('closedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="[0].get('expiresDate')" format="M/d/yy" /></td>
		<td><s:property value="[0].get('auditStatus')"/></td>
		<td class="right"><s:property value="[0].get('percentComplete')"/>%</td>
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
