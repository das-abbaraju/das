<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>My Audits</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>My Audits</h1>

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
		<td><a href="javascript: changeOrderBy('form1','ca.scheduledDate DESC');">Scheduled</a></td>
		<td><a href="javascript: changeOrderBy('form1','ca.assignedDate DESC');">Assigned</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.completedDate DESC');">Submitted</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.auditStatus DESC');">Status</a></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
			<td><s:date name="[0].get('scheduledDate')" format="M/d/yy" /></td>
			<td><s:date name="[0].get('assignedDate')" format="M/d/yy" /></td>
			<td><s:date name="[0].get('completedDate')" format="M/d/yy" /></td>
			<td><s:property value="[0].get('auditStatus')"/></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
