<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Email Queue List</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<style>
</style>
</head>
<body>
<h1>Email Queue List</h1>
<table class="report">
	<thead>
	<tr>
		<td></td>
		<td>Priority</td>
		<td>Status</td>
		<td>Date</td>
		<td>From</td>
		<td>To</td>
		<td>Subject</td>
		<td>Date Sent</td>
	</tr>
	</thead>
	<s:iterator value="emails" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + 1" /></td>
			<td><s:property value="priority" /></td>
			<td><s:property value="status" /></td>
			<td><s:property value="creationDate" /></td>
			<td><s:property value="fromAddress" /></td>
			<td><s:property value="toAddresses" /></td>
			<td><s:property value="subject" /></td>
			<td><s:property value="sentDate" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>