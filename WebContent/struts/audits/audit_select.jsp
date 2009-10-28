<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<ul>
	<li><label>Contractor:</label> <s:property value="conAudit.contractorAccount.name"/></li>
	<li><label>Auditor:</label> <s:property value="conAudit.auditor.name"/></li>
	<li><label>Date:</label> <s:date name="conAudit.scheduledDate" format="MM/dd/yyyy"/></li>
	<li><label>Time:</label> <s:date name="conAudit.scheduledDate" format="hh:mm a"/></li>
</ul>