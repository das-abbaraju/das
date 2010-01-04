<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Audit/Evaluations for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<script type="text/javascript">
	function showAddAudit() {
	$('#addAudit').hide();	
	$('#addAuditManually').show();
	}
</script>
</head>
<body>
<s:push value="#subHeading='Contractor Forms, Audits & Evaluations'"/>
<s:include value="conHeader.jsp" />

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
			<td><s:date name="creationDate" format="M/d/yy" /></td>
			<td>
				<s:if test="requestingOpAccount.name != null">
					<s:property value="requestingOpAccount.name" />
				</s:if>
				<s:else>
					Shared
				</s:else>
			</td>
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
				<td><s:if test="!auditType.annualAddendum && !auditType.classType.toString().equals('Policy')">
						<a href="ConAuditCopy.action?auditID=<s:property value="id" />">Copy</a>
					</s:if>					
				</td>
			</pics:permission>
		</tr>
	</s:iterator>
	<s:if test="manuallyAddAudit">
		<s:if test="auditTypeName.size > 0">
			<tr>
				<td id="addAudit" colspan="11" class="center">
					<a href="#" onclick="showAddAudit(); return false;">Add Audit Manually</a>
				</td>
			</tr>
		</s:if>
		<tr id="addAuditManually" style="display: none;">
		<s:form method="post" id="form1">
			<td>
			<div>
				<button class="picsbutton positive" name="button" type="submit" value="Add">Add</button>
			</div>
			</td>
			<td colspan="3" class="center">
				<s:hidden name="id" value="%{id}"/>
				<s:select list="auditTypeName" listKey="id" listValue="auditName" headerKey="" headerValue="- Select an Audit to be Created -" name="selectedAudit"/>
			</td>
			<td colspan="7" class="center">
				<pics:permission perm="AllOperators">
					<s:select list="operators" listKey="operatorAccount.id" listValue="operatorAccount.name" name="selectedOperator" headerKey="" headerValue="- Shared by All Operators -"/>
				</pics:permission>
			</td>
		</s:form>	
	</s:if>		
</table>

<s:if test="currentAudits.size() > 0">
<br/>
<h3>Current Audits</h3>
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
			<td><s:date name="creationDate" format="M/d/yy" /></td>
			<td>
				<s:if test="requestingOpAccount.name != null">
					<s:property value="requestingOpAccount.name" />
				</s:if>
				<s:else>
					Shared
				</s:else>
			</td>
			<td><s:property value="auditor.name" /></td>
			<td><s:date name="scheduledDate" format="M/d/yy HH:mm"/></td>
			<td><s:date name="completedDate" format="M/d/yy" /></td>
			<td><s:date name="closedDate" format="M/d/yy" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
			<pics:permission perm="AuditCopy">
				<td><s:if test="!auditType.annualAddendum && !auditType.classType.toString().equals('Policy')">
					<a href="ConAuditCopy.action?auditID=<s:property value="id" />">Copy</a>
				</s:if></td>
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
		<th>Created</th>
		<th>Operator</th>
		<th>Scheduled</th>
		<th>Submitted</th>
		<th>Closed</th>
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
			<td><s:date name="creationDate" format="M/d/yy" /></td>			
			<td><s:property value="requestingOpAccount.name" /></td>
			<td><s:date name="scheduledDate" format="M/d/yy HH:mm"/></td>
			<td><s:date name="completedDate" format="M/d/yy" /></td>
			<td><s:date name="closedDate" format="M/d/yy" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
			<pics:permission perm="AuditCopy">
				<td><s:if test="!auditType.annualAddendum && !auditType.classType.toString().equals('Policy')">
					<a href="ConAuditCopy.action?auditID=<s:property value="id" />">Copy</a>
				</s:if></td>
			</pics:permission>
		</tr>
	</s:iterator>
</table>
</s:if>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>
