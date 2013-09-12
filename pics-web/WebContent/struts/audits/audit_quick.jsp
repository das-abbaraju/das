<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<ul>
	<li><label>Contractor:</label> <a href="ContractorView.action?id=<s:property value="conAudit.contractorAccount.id"/>" target="_blank"><s:property value="conAudit.contractorAccount.name"/></a></li>
	<li><label>Audit Type:</label> <a href="Audit.action?auditID=<s:property value="conAudit.id"/>" target="_blank"><s:property value="conAudit.auditType.name"/></a></li>
	<li><label>Safety Professional:</label> <s:property value="conAudit.auditor.name"/></li>
	<li><label>Date:</label> <s:property value="formatDate(conAudit.scheduledDate, 'MM/dd/yyyy')"/></li>
	<li><label>Time:</label> <s:property value="formatDate(conAudit.scheduledDate, 'hh:mm a z')"/></li>
	<s:if test="conAudit.auditType.scheduled && !permissions.operatorCorporate">
		<li><label></label>
			<a href="ScheduleAudit!edit.action?auditID=<s:property value="conAudit.id"/>" target="_blank">Edit Schedule</a></li>
	</s:if>
</ul>