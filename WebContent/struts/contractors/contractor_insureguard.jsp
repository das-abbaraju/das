<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>InsureGuard for <s:property value="contractor.name" /></title>
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
<s:push value="#subHeading='InsureGuard'"/>
<s:include value="conHeader.jsp" />

<h3>Requested Insurance Policies</h3>
<table class="report">
	<thead>
	<tr>
		<th>Status</th>
		<th>Type</th>
		<th>Operator</th>
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
		</tr>
	</s:iterator>
	<s:if test="manuallyAddAudit">
		<s:if test="auditTypeName.size > 0">
			<tr>
				<td id="addAudit" colspan="3" class="center">
					<a href="#" onclick="showAddAudit(); return false;">Add New Policy</a>
				</td>
			</tr>
		</s:if>
		<tr id="addAuditManually" style="display: none;">
		<s:form method="post" id="form1" >
			<td>
			<div class="buttons">
				<button class="positive" name="button" type="submit" value="Add">Add</button>
			</div>
			</td>
			<td>
				<s:hidden name="id" value="%{id}"/>
				<s:select list="auditTypeName" name="selectedAudit" cssClass="pics"
					headerKey="" headerValue="- Select Policy Type -" listKey="auditTypeID" listValue="auditName" />
			</td>
			<td>
				<s:if test="permissions.contractor || permissions.admin">
				<s:select list="operators" name="selectedOperator"
					headerKey="" headerValue="- Shared by All Operators -" listKey="operatorAccount.id" listValue="operatorAccount.name" />
				</s:if>
			</td>
			</s:form>
		</tr>
	</s:if>
</table>


<s:if test="currentAudits.size() > 0">
<br/>
<h3>Current Policies</h3>
<table class="report">
	<thead>
	<tr>
		<th>Type</th>
		<th>Effective</th>
		<th>Expires</th>
	</tr>
	</thead>
	<s:iterator value="currentAudits" status="auditStatus">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:date name="creationDate" format="M/d/yy" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
</s:if>
<s:if test="expiredAudits.size() > 0">
<br/>
<h3>Expired Policies</h3>
<table class="report">
	<thead>
	<tr>
		<th>Type</th>
		<th>Effective</th>
	</tr>
	</thead>
	<s:iterator value="expiredAudits" status="auditStatus">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:date name="creationDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
<br/><br/>
</s:if>
</body>
</html>
