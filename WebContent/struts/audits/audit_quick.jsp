<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<ul>
	<li><label>Contractor:</label> <a href="ContractorView.action?id=<s:property value="conAudit.contractorAccount.id"/>" target="_blank"><s:property value="conAudit.contractorAccount.name"/></a></li>
	<li><label>Audit Type:</label> <a href="Audit.action?auditID=<s:property value="conAudit.id"/>" target="_blank"><s:property value="conAudit.auditType.auditName"/></a></li>
	<li><label>Auditor:</label> <s:property value="conAudit.auditor.name"/></li>
	<li><label>Audit Status:</label> <s:property value="conAudit.auditStatus"/></li>
	<li><label>Date:</label> <s:property value="formatDate(conAudit.scheduledDate, 'MM/dd/yyyy')"/></li>
	<li><label>Time:</label> <s:property value="formatDate(conAudit.scheduledDate, 'hh:mm a z')"/></li>
	<s:if test="conAudit.auditType.scheduled && !permissions.operatorCorporate">
		<li><label></label>
			<a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>" target="_blank">Audit Schedule</a></li>
	</s:if>
</ul>