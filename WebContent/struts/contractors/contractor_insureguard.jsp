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
	</tr>
	</thead>
	<s:iterator value="upComingAudits" status="auditStatus">
		<tr>
			<td><s:property value="auditStatus" /></td>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
		</tr>
	</s:iterator>
	
<s:if test="manuallyAddAudit">
	<tr>
		<td id="addAudit" colspan="2" class="center">
			<a href="#" onclick="showAddAudit(); return false;">Add New Policy</a>
		</td>
	</tr>
	<tr id="addAuditManually" style="display: none;">
		<td>New</td>
		<td>
			<s:form method="post" id="form1" >
				<s:hidden name="id" value="%{id}"/>
				<s:select list="auditTypeName" name="selectedAudit" cssClass="pics"
					headerKey="" headerValue="- Select Policy Type -" 
					listKey="auditTypeID" listValue="auditName" />
				<pics:permission perm="AllOperators">
					<br />
					<s:select list="operators" name="selectedOperator"
						headerKey="" headerValue="- Available to All Operators -"
						listKey="operatorAccount.id" listValue="operatorAccount.name" />
				</pics:permission>
				<div class="buttons">
					<button class="positive" name="button" type="submit" value="Create">Save</button>
				</div>
			</s:form>
		</td>
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
			<td><s:date name="createdDate" format="M/d/yy" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
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
		<th>Expired</th>
	</tr>
	</thead>
	<s:iterator value="expiredAudits" status="auditStatus">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
<br/><br/>
</s:if>
</body>
</html>
