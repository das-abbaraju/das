<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>My Audits</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Close Open Requirements</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <td>
	    	<a href="javascript: changeOrderBy('form1','a.name');">Contractor</a>
	    </td>
	    <td><a href="javascript: changeOrderBy('form1','atype.auditName');">Type</a></td>
		<td>File</td>
	    <td><a href="javascript: changeOrderBy('form1','ca.completedDate DESC');">Submitted</a></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="get('name')"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="get('auditName')"/></a></td>
			<td><a href="ContractorAuditFileUpload.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="get('description')"/></a></td>
			<td><s:date name="get('uploadDate')" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
