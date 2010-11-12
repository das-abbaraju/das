<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Rule History</title>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
</head>
<body>
<h1>Audit Rule History</h1>
<table class="report">
	<thead>
		<tr>
			<th>Type</th>
			<th>Status</th>
			<th>Date</th>
			<th>Changed By</th>
			<th>View</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data">
			<tr>
				<td><s:property value="get('rType')"/></td>
				<td><s:property value="get('status')"/></td>
				<td><s:date name="get('sDate')" format="MM/dd/yyyy -  HH:MM" /></td>
				<td><s:property value="get('who')"/></td>
				<td><a class="go" href="<s:property value="get('rType')"/>Editor.action?id=<s:property value="get('id')"/>">Go</a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>

</body>
</html>