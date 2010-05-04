<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>My Audit History</title>
<s:include value="reportHeader.jsp" />
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
	    <td><a href="?orderBy=ca.completedDate DESC" >Submitted</a></td>
	    <td><a href="?orderBy=ca.closedDate DESC" >Closed</a></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><s:property value="[0].get('name')"/></td>
		<td><s:property value="[0].get('auditName')"/></td>
		<td class="center"><s:date name="[0].get('completedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="[0].get('closedDate')" format="M/d/yy" /></td>
	</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
