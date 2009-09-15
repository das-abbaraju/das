<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<div class="noprint">
	<button class="picsbutton" type="button" onclick="window.print();">Print</button>
</div>
<fieldset class="form"><legend><span>Audit Time &amp; Location</span></legend>
<ol>
	<li><label>Audit Date:</label> <s:property value="formatDate(conAudit.scheduledDate, 'EEEE, MMM d, yyyy')" /></li>
	<li><label>Audit Time:</label> <s:property value="formatDate(conAudit.scheduledDate, 'h:mm a z')" /></li>

	<s:if test="conAudit.conductedOnsite">
		<li><label>Location:</label> <s:property value="conAudit.fullAddress" /></li>
	</s:if>
	<s:else>
		<li><label>Location:</label> Internet <a href="http://help.picsauditing.com/wiki/Office_Audit" class="help">What
		is this?</a></li>
		<li><label>Video Camera:</label> <s:radio name="conAudit.needsCamera" theme="pics"
			list="#{false: 'I have my own webcam that I can use for this audit', true: 'Please mail me a webcam for my computer. Use this address: ' + conAudit.fullAddress}" />
		</li>
	</s:else>
	<li>Any changes to the above schedule must be done 2 business days before this audit begins (<s:property value="formatDate(lastCancellationTime)" />) to avoid a $150 rescheduling fee.</li>
</ol>
</fieldset>
<fieldset class="form"><legend><span>PICS Auditor</span></legend>
<ol>
		<li><label>Name:</label> <s:property value="conAudit.auditor.name" /></li>
		<li><label>Email:</label> <s:property value="conAudit.auditor.email" /></li>
		<li><label>Phone:</label> <s:property value="conAudit.auditor.phone" /></li>
		<li><label>Fax:</label> <s:property value="conAudit.auditor.fax" /></li>
		<li>If you have any questions or concerns about your up coming audit, feel free to contact <s:property value="conAudit.auditor.name" /> directly.</li>
</ol>
</fieldset>
<fieldset class="form bottom"><legend><span>Primary Contact</span></legend>
<ol>
	<li><label>Name:</label> <s:property value="conAudit.contractorContact" /></li>
	<li><label>Email:</label> <s:property value="conAudit.phone2" /></li>
	<li><label>Phone:</label> <s:property value="conAudit.phone" /></li>
</ol>
</fieldset>

</body>
</html>
