<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}
</style>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<div class="alert"><s:text name="%{scope}.message.NotScheduledYet" /></div>
<s:form>
	<s:hidden name="auditID" />
	<s:hidden name="availabilitySelectedID" />
	<fieldset class="form">
	<h2 class="formLegend"><s:text name="%{scope}.label.AuditConfirmation" /></h2>
	<ol>
		<li><s:text name="%{scope}.message.ConfirmInformation" />:</li>
		<li><label><s:text name="%{scope}.label.AuditDate" />:</label> <s:date name="availabilitySelected.startDate" format="EEEE, MMM d, yyyy" /></li>
		<li><label><s:text name="%{scope}.label.AuditTime" />:</label> <s:date name="availabilitySelected.startDate" format="h:mm a z" /></li>

		<s:if test="conAudit.conductedOnsite">
			<li><label><s:text name="%{scope}.label.Location" />:</label> <s:property value="conAudit.fullAddress" /> <a
				href="ScheduleAudit.action?auditID=<s:property value="conAudit.id" />"><s:text name="%{scope}.link.Change" /></a></li>
		</s:if>
		<s:else>
			<li><label><s:text name="%{scope}.label.Location" />:</label> <s:text name="%{scope}.message.Web" /> &nbsp;&nbsp;&nbsp;&nbsp;<a
				href="http://help.picsauditing.com/wiki/Office_Audit" class="help" style="font-size: 10px" target="_BLANK"><s:text name="%{scope}.help.WebAudit" /></a></li>
			<li><label><s:text name="%{scope}.label.VideoCamera" />:</label> <s:radio name="conAudit.needsCamera" theme="pics"
				list="#{false: getText('ScheduleAudit.message.HasWebcam'), true: getText('ScheduleAudit.message.MailWebcam') + ': ' + conAudit.fullAddress}" />
				<a class="edit" href="ScheduleAudit.action?auditID=<s:property value="conAudit.id" />"><s:text name="%{scope}.link.ChangeAddress" /></a>
			</li>
		</s:else>

		<li><label><s:text name="global.ContactPrimary" />:</label> <s:property value="conAudit.contractorContact" /></li>
		<li><label><s:text name="User.email" />:</label> <s:property value="conAudit.phone2" /></li>
		<li><label><s:text name="User.phone" />:</label> <s:property value="conAudit.phone" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<ol>
		<li><s:checkbox name="confirmed" />
			<s:text name="%{scope}.message.ConfirmMessage">
				<s:param><s:date name="lastCancellationTime" format="MMMMM d, yyyy, h:mm a" /></s:param>
				<s:param value="%{rescheduling.amount}" />
			</s:text>
		</li>
	</ol>
	<s:submit cssClass="picsbutton positive" method="confirm" value="%{getText(scope + '.button.ConfirmAudit')}" />
	</div>
	</fieldset>
</s:form>

</body>
</html>