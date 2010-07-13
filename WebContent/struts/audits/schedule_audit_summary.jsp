<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<div class="noprint">
	<button class="picsbutton" type="button" onclick="window.print();">Print</button>
</div>
<fieldset class="form">
<h2 class="formLegend">Audit Time &amp; Location</h2>
<ol>
	<s:if test="permissions.admin">
		<li><a class="picsbutton" href="?button=edit&auditID=<s:property value="auditID"/>">Edit Schedule Manually</a></li>
	</s:if>
	<li><label>Audit Date:</label> <s:property value="formatDate(conAudit.scheduledDate, 'EEEE, MMM d, yyyy')" /></li>
	<li><label>Audit Time:</label> <s:property value="formatDate(conAudit.scheduledDate, 'h:mm a z')" /></li>

	<s:if test="conAudit.conductedOnsite">
		<li><label>Location:</label> <s:property value="conAudit.fullAddress" /></li>
	</s:if>
	<s:else>
		<li><label>Location:</label> Internet <a href="http://help.picsauditing.com/wiki/Office_Audit" class="help">What
		is this?</a></li>
		<li><label>Video Camera:</label>
			<s:if test="conAudit.needsCamera">Please mail me a webcam for my computer to: <s:property value="conAudit.fullAddress"/></s:if>
			<s:else>I have my own webcam that I can use for this audit</s:else>
		</li>
	</s:else>
	<li><div class="alert">Any changes to the above schedule must be done 2 business days before this audit begins (<s:property value="formatDate(lastCancellationTime)" />) to avoid a $150 rescheduling fee.</div></li>
</ol>
</fieldset>
<fieldset class="form">
<h2 class="formLegend">PICS Safety Professional</h2>
<ol>
		<li><label>Name:</label> <s:property value="conAudit.auditor.name" /></li>
		<li><label>Email:</label> <s:property value="conAudit.auditor.email" /></li>
		<li><label>Phone:</label> <s:property value="conAudit.auditor.phone" /></li>
		<li><label>Fax:</label> <s:property value="conAudit.auditor.fax" /></li>
		<li>If you have any questions or concerns about your up coming audit, feel free to contact <s:property value="conAudit.auditor.name" /> directly.</li>
</ol>
</fieldset>
<fieldset class="form bottom">
<h2 class="formLegend">Primary Contact</h2>
<ol>
	<li><label>Name:</label> <s:property value="conAudit.contractorContact" /></li>
	<li><label>Email:</label> <s:property value="conAudit.phone2" /></li>
	<li><label>Phone:</label> <s:property value="conAudit.phone" /></li>
</ol>
</fieldset>

</body>
</html>
