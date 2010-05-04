<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Policy Verification</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Policy Verification</h1>
<s:include value="filters.jsp" />
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report">
	<thead>
	<tr>
		<td></td>
		<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
		<td>Policy</td>
		<td>Submitted</td>
		<td>Operators</td>
		<td></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:property value="get('name')"/></td>
			<td><a href="AuditCat.action?auditID=<s:property value="get('auditID')"/>&catDataID=<s:property value="get('catdataID')" />"><s:property value="get('auditName')"/></a></td>
			<td><s:date name="get('caoUpdateDate')" format="M/d/yy"/></td>
			<td><s:property value="get('operatorCount')"/></td>
			<td><a href="AuditCat.action?auditID=<s:property value="get('auditID')"/>&catDataID=<s:property value="get('catdataID')"/>" target="VERIFY">Open</a></td>
		</tr>
	</s:iterator>	
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>