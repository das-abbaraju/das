<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Integrity Management for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function showAddAudit() {
	$('#addAudit').hide();	
	$('#addAuditManually').show();
}
</script>
</head>
<body>
<s:push value="#subHeading='Integrity Management'"/>
<s:include value="conHeader.jsp" />


<h3>Integrity Management Scores For This Contractor</h3>
<table class="report">
	<thead>
	<tr>
		<th>Audit Name</th>
		<th>Overall Score</th>
	</tr>
	</thead>
	<s:iterator value="imScores.keySet()" status="auditStatus">
		<s:set name="auditName" value="top"/>
		<tr>
			<td><s:property value="#attr.auditName"/></td>
			<td><s:property value="imScores.get(#attr.auditName)"/></td>
		</tr>
	</s:iterator>
</table>
<br/><br/>
<h3>Integrity Management Audits</h3>
<table class="report">
	<thead>
	<tr>
		<th>Status</th>
		<th>Type</th>
		<th>Operator</th>
		<th>For</th>
		<th>Score</th>
	</tr>
	</thead>
	<s:iterator value="upComingAudits" status="auditStatus">
		<tr>
			<td><s:property value="auditStatus" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<s:if test="requestingOpAccount.name != null">
				<td><s:property value="requestingOpAccount.name" /></td>
			</s:if>
			<s:else>
				<td>Shared</td>
			</s:else>
			<td><s:property value="auditFor"/></td>
			<td><s:property value="printableScore"/></td>
		</tr>
	</s:iterator>
	<s:if test="manuallyAddAudit">
		<s:if test="auditTypeName.size > 0">
			<tr>
				<td id="addAudit" colspan="5" class="center">
					<a href="#" onclick="showAddAudit(); return false;">Add New Integrity Management Audit</a>
				</td>
			</tr>
		</s:if>
		<tr id="addAuditManually" style="display: none;">
		<s:form method="post" id="form1" >
			<td>
			<div>
				<button class="picsbutton positive" name="button" type="submit" value="Add">Add</button>
			</div>
			</td>
			<td>
				<s:hidden name="id" value="%{id}"/>
				<s:select list="auditTypeName" name="selectedAudit" cssClass="pics"
					headerKey="" headerValue="- Select Audit Type -" listKey="id" listValue="auditName" />
			</td>
			<td>
				<s:if test="permissions.contractor || permissions.admin">
				<s:select list="operators" name="selectedOperator"
					headerKey="" headerValue="- Shared by All Operators -" listKey="operatorAccount.id" listValue="operatorAccount.name" />
				</s:if>
			</td>
			<td colspan="2"><s:textfield size="40" name="auditFor"/></td>
			</s:form>
		</tr>
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
		<th>Closed</th>
		<th>Operator</th>
		<th>Auditor</th>
		<th>Expires</th>
		<th>View</th>
	</tr>
	</thead>
	<s:iterator value="currentAudits" status="auditStatus">
		<tr>
			<td><s:property value="auditStatus" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:property value="auditFor"/></td>
			<td><s:date name="closedDate" format="M/d/yy" /></td>
			<td>
				<s:if test="requestingOpAccount.name != null">
					<s:property value="requestingOpAccount.name" />
				</s:if>
				<s:else>
					Shared
				</s:else>
			</td>
			<td><s:property value="auditor.name" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
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
	</tr>
	</thead>
	<s:iterator value="expiredAudits" status="auditStatus">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:property value="auditFor"/></td>
			<td><s:property value="requestingOpAccount.name" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />">View</a></td>
		</tr>
	</s:iterator>
</table>
<br/><br/>
</s:if>
</body>
</html>
