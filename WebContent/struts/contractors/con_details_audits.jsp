<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>

<s:if test="audits.size() == 0">
	<div class="alert">No audits found</div>
</s:if>
<s:else>
<table class="report">
	<thead>
	<tr>
		<th>Type</th>
		<th>Created</th>
		<th>Safety Professional</th>
		<th>Scheduled</th>
		<th>Submitted</th>
		<th>Closed</th>
		<th>Expires</th>
	</tr>
	</thead>

	<s:iterator value="auditList">
	<tr>
		<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
		<td><s:date name="creationDate" format="M/d/yy" /></td>
		<td><s:property value="auditor.name" /></td>
		<td><s:date name="scheduledDate" format="M/d/yy HH:mm"/></td>
		<td><s:date name="completedDate" format="M/d/yy" /></td>
		<td><s:date name="closedDate" format="M/d/yy" /></td>
		<td><s:date name="expiresDate" format="M/d/yy" /></td>
	</tr>
	</s:iterator>
</table>
</s:else>