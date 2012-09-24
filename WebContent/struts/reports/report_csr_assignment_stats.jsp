<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ReportCsrAssignmentStats.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:text name="ReportCsrAssignmentStats.title" /></h1>
<table>
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
		<s:iterator id="csrList" value="csrList">
			<tr>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</s:iterator>
	</tbody>
</table>