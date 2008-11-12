<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Audit/Evaluations for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript">
	function showAddAudit() {
	$('addAudit').hide();	
	$('addAuditManually').show();
	}
</script>
</head>
<body>
<s:push value="#subHeading='Contractor Forms, Audits & Evaluations'"/>
<s:include value="conHeader.jsp" />
<s:if test="upComingAudits.size() > 0">
<h3>UpComing Audits</h3>
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
		<th>View</th>
		<pics:permission perm="AuditCopy">
			<th>Copy</th>
		</pics:permission>	
	</tr>
	</thead>
	<s:iterator value="upComingAudits" status="auditStatus">
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
		<th>Type</th>
		<th>Status</th>
		<th>Closed</th>
		<th>For</th>
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
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:property value="auditStatus" /></td>
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
			<td><s:property value="requestingOpAccount.name" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
			<pics:permission perm="AuditCopy">
				<td><a href="ConAuditCopy.action?auditID=<s:property value="id" />">Copy</a></td>
			</pics:permission>
		</tr>
	</s:iterator>
</table>
<br/><br/>
</s:if>
<s:if test="manuallyAddAudit">
<div id="addAudit">
	<a href="#" onclick="showAddAudit(); return false;">Add Audit Manually</a>
</div>
<div id="addAuditManually" style="display: none;">
<s:form method="post" id="form1" >
	<s:hidden name="id" value="%{id}"/>
	<label>Select a Audit to be Created</label>
	<s:select list="auditTypeName" listKey="auditTypeID" listValue="auditName" name="selectedAudit"/>
	<pics:permission perm="AllOperators">
		<s:select list="operators" listKey="operatorAccount.id" listValue="operatorAccount.name" name="selectedOperator" headerKey="" headerValue="- No Operator -"/>
	</pics:permission>
	<div class="buttons">
		<button class="positive" name="button" type="submit" value="Create">Add</button>
	</div>
</s:form>	
</div>
</s:if>
</body>
</html>
