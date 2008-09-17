<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Audit/Evaluations for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<s:push value="#subHeading='Contractor Forms, Audits & Evaluations'"/>
<s:include value="conHeader.jsp" />

<table class="report">
	<thead>
	<tr>
		<th>Type</th>
		<th>Status</th>
		<th>Created</th>
		<th>For</th>
		<th>Auditor</th>
		<th>Scheduled</th>
		<th>Submitted</th>
		<th>Closed</th>
		<th>Expires</th>
		<th>View</th>
		<pics:permission perm="AuditCopy">
			<th>Copy</th>
		</pics:permission>	
	</tr>
	</thead>
	<s:iterator value="audits" status="auditStatus">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:property value="auditStatus" /></td>
			<td><s:date name="createdDate" format="M/d/yy" /></td>
			<td><s:property value="requestingOpAccount.name" /></td>
			<td><s:property value="auditor.name" /></td>
			<td><s:date name="scheduledDate" format="M/d/yy HH:mm" /></td>
			<td align="right"><s:if test="auditStatus.toString() == 'Pending'">
				<s:property value="percentComplete" />%</s:if> <s:else>
				<s:date name="completedDate" format="M/d/yy" />
			</s:else></td>
			<td align="right"><s:if test="auditStatus.toString() == 'Submitted'">
				<s:property value="percentVerified" />%</s:if> <s:else>
				<s:date name="closedDate" format="M/d/yy" />
			</s:else></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
			<pics:permission perm="AuditCopy">
				<td><a href="ConAuditCopy.action?auditID=<s:property value="id" />">Copy</a></td>
			</pics:permission>
		</tr>
	</s:iterator>
</table>
<div id="addAuditManually">LOTS OF STUFF</div>
<div id="addAudit">
	<a href="#" onclick="$('addAuditManually').show(); $('addAudit').hide(); return false;">Add Audit Manually</a>
</div>
</body>
</html>
