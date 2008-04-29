<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Audit/Evaluations for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1><s:property value="contractor.name" />
<span class="sub">Contractor Audit &amp; Evaluations</span></h1>

<s:include value="con_nav.jsp"></s:include>
<s:property value="action"/>

<table class="report">
	<thead>
	<tr>
		<th>Type</td>
		<th>Status</td>
		<th>Created</td>
		<th>For</td>
		<th>Auditor</td>
		<th>Location</th>
		<th>Scheduled</th>
		<th>Submitted</th>
		<th>Closed</th>
		<th>Expires</th>
		<th>View</td>
	</tr>
	</thead>
	<s:iterator value="audits" status="auditStatus">
		<tr>
			<td><s:property value="auditType.auditName" /></td>
			<td><s:property value="auditStatus" /></td>
			<td><s:date name="createdDate" format="M/d/yy" /></td>
			<td><s:property value="requestingOpAccount.name" /></td>
			<td><s:property value="auditor.name" /></td>
			<td><s:property value="auditLocation" /></td>
			<td><s:date name="scheduledDate" format="M/d/yy" /></td>
			<td align="right"><s:if test="percentComplete < 100">
				<s:property value="percentComplete" />%</s:if> <s:else>
				<s:date name="completedDate" format="M/d/yy" />
			</s:else></td>
			<td align="right"><s:if test="percentVerified < 100">
				<s:property value="percentVerified" />%</s:if> <s:else>
				<s:date name="closedDate" format="M/d/yy" />
			</s:else></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="pqf_view.jsp?auditID=<s:property value="id" />">View</a></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
