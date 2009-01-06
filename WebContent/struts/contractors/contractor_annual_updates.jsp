<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Annual Updates for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<s:push value="#subHeading='Contractor Annual Updates'"/>
<s:include value="conHeader.jsp" />

<s:if test="upComingAudits.size() > 0">
<h3>Up Coming Audits</h3>
<table class="report">
	<thead>
	<tr>
		<th>Status</th>
		<th>Type</th>
		<th>For</th>
		<th>Created</th>
		<th>Operator</th>
		<th>Auditor</th>
		<th>Scheduled</th>
		<th>Submitted</th>
		<th>Closed</th>
		<th>View</th>
		<pics:permission perm="AuditCopy">
			<th>Copy</th>
		</pics:permission>	
	</tr>
	</thead>
	<s:iterator value="upComingAudits" status="auditStatus">
		<tr>
			<td><s:property value="auditStatus" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:property value="auditFor"/></td>
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
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
			<pics:permission perm="AuditCopy">
				<td><a href="ConAuditCopy.action?auditID=<s:property value="id" />">Copy</a></td>
			</pics:permission>
		</tr>
	</s:iterator>
</table>
</s:if>
<s:if test="currentAudits.size() > 0">
<br/>
<h3>Current Audits</h3>
<table class="report">
	<thead>
	<tr>
		<th>Status</th>
		<th>Type</th>
		<th>For</th>
		<th>Closed</th>
		<th>Operator</th>
		<th>Auditor</th>
		<th>Expires</th>
		<th>View</th>
		<pics:permission perm="AuditCopy">
			<th>Copy</th>
		</pics:permission>	
	</tr>
	</thead>
	<s:iterator value="currentAudits" status="auditStatus">
		<tr>
			<td><s:property value="auditStatus" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:property value="auditFor"/></td>
			<td><s:date name="closedDate" format="M/d/yy" /></td>
			<td><s:property value="requestingOpAccount.name" /></td>
			<td><s:property value="auditor.name" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
			<pics:permission perm="AuditCopy">
				<td><a href="ConAuditCopy.action?auditID=<s:property value="id" />">Copy</a></td>
			</pics:permission>
		</tr>
	</s:iterator>
</table>
</s:if>
<s:if test="expiredAudits.size() > 0">
<br/>
<h3>Expired Audits</h3>
<table class="report">
	<thead>
	<tr>
		<th>Type</th>
		<th>For</th>
		<th>Operator</th>
		<th>Expired</th>
		<th>View</th>
		<pics:permission perm="AuditCopy">
			<th>Copy</th>
		</pics:permission>	
	</tr>
	</thead>
	<s:iterator value="expiredAudits" status="auditStatus">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:property value="auditFor"/></td>
			<td><s:property value="requestingOpAccount.name" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
			<pics:permission perm="AuditCopy">
				<td><a href="ConAuditCopy.action?auditID=<s:property value="id" />">Copy</a></td>
			</pics:permission>
		</tr>
	</s:iterator>
</table>
</s:if>

</body>
</html>
