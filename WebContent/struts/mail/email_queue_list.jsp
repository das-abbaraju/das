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
<h1>Email Queue</h1>

<s:if test="emails.size > 0">
	<table class="report">
		<thead>
		<tr>
			<td></td>
			<td>Priority</td>
			<td>Added</td>
			<td>From</td>
			<td>To</td>
			<td>Subject</td>
		</tr>
		</thead>
		<s:iterator value="emails" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.index + 1" /></td>
				<td><s:property value="priority" /></td>
				<td><s:date name="creationDate" nice="true" /></td>
				<td><s:property value="fromAddress" /></td>
				<td><s:property value="toAddresses" /></td>
				<td><s:property value="subject" /></td>
			</tr>
		</s:iterator>
	</table>
	
	<div id="info">Emails in this list will automatically be sent.</div>
</s:if>
<s:else>
	<div id="info">There are no pending emails to be sent</div>
</s:else>
</body>
</html>