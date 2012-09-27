<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>CSR Assignment Stats</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>CSR Assignment Stats</h1>
<table class="report">
	<thead>
		<tr>
			<td>CSR ID</td>
			<td>CSR Name</td>
			<td># Assigned</td>
			<td># Requested</td>
			<td># w/ AuditGUARD</td>
			<td># w/ InsureGUARD</td>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" var="csr">
			<tr>
				<td><a href="UsersManage.action?account=1100&user=<s:property value="get('csrId')"/>" ><s:property value="get('csrId')"/></a></td>
				<td><a href="UsersManage.action?account=1100&user=<s:property value="get('csrId')"/>" ><s:property value="get('csrName')"/></a></td>
				<td><s:property value="get('numActive')"/></td>
				<td><s:property value="get('numRequested')"/></td>
				<td><s:property value="get('numWithAuditGuard')"/></td>
				<td><s:property value="get('numWithInsureGuard')"/></td>
			</tr>
		</s:iterator>
	</tbody>
</table>