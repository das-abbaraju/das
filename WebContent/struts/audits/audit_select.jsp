<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<ul>
	<li><label>Contractor:</label> <s:property value="conAudit.contractorAccount.name"/></li>
	<li><label>Auditor:</label> <s:property value="conAudit.auditor.name"/></li>
	<li><label>Audit Type:</label> <s:property value="conAudit.auditType.auditName"/></li>
	<li><label>Audit Status:</label> <s:property value="conAudit.auditStatus"/></li>
	<li><label>Date:</label> <s:date name="conAudit.scheduledDate" format="MM/dd/yyyy"/></li>
	<li><label>Time:</label> <s:date name="conAudit.scheduledDate" format="hh:mm a z"/></li>
	<s:if test="conAudit.auditType.scheduled && !permissions.operatorCorporate">
		<li><a href="ScheduleAudit.action?button=summary&auditID=<s:property value="conAudit.id"/>">View Audit Schedule Summary</a></li>
	</s:if>
	<li><a href="Audit.action?auditID=<s:property value="conAudit.id"/>">View Audit</a></li>
</ul>