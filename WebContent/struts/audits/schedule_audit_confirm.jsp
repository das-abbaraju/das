<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
	<title><s:text name="ScheduleAudit.title" /></title>
	
	<meta name="help" content="Scheduling_Audits">
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/audit/schedule_audit.css?v=<s:property value="version"/>" />
    
	<s:include value="../jquery.jsp"/>
    
    <script type="text/javascript" src="js/audit/schedule_audit_confirm.js?v=<s:property value="version"/>"></script>
</head>
	
<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
	<s:include value="../contractors/conHeader.jsp" />
	
	<div class="alert">
		<s:text name="ScheduleAudit.message.NotScheduledYet" />
	</div>
	
	<s:form cssClass="schedule-audit-form schedule-audit-confirm-form">
		<s:hidden name="auditID" />
		<s:hidden name="availabilitySelectedID" />
		
		<fieldset class="form">
			<h2 class="formLegend"><s:text name="ScheduleAudit.label.AuditConfirmation" /></h2>
			
			<ol>
				<li>
					<s:text name="ScheduleAudit.message.ConfirmInformation" />:
				</li>
				<li>
					<label><s:text name="ScheduleAudit.label.AuditDate" />:</label>
					<s:date name="availabilitySelected.startDate" format="EEEE, %{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
				</li>
				<li>
					<label><s:text name="ScheduleAudit.label.AuditTime" />:</label>
					<s:text name="ScheduleAudit.link.DateSelector2">
						<s:param value="%{availabilitySelected.getTimeZoneStartDate(getSelectedTimeZone())}" />
						<s:param value="%{availabilitySelected.getTimeZoneEndDate(getSelectedTimeZone())}" />
					</s:text>
				</li>
		
				<s:if test="conAudit.conductedOnsite">
					<li>
						<label><s:text name="global.Location" />:</label>
						<s:property value="conAudit.fullAddress" />
						<a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id" />"><s:text name="ScheduleAudit.link.Change" /></a>
					</li>
				</s:if>
				<s:else>
					<li>
						<label><s:text name="global.Location" />:</label>
						<s:text name="ScheduleAudit.message.Web" />
						&nbsp;&nbsp;&nbsp;&nbsp;
						<a href="http://help.picsorganizer.com/display/contractors/Implementation+Audit" class="help" style="font-size: 10px" target="_BLANK"><s:text name="ScheduleAudit.help.WebAudit" /></a>
					</li>
				</s:else>
		
				<li>
					<label><s:text name="global.ContactPrimary" />:</label>
					<s:property value="conAudit.contractorContact" />
				</li>
				<li>
					<label><s:text name="User.email" />:</label>
					<s:property value="conAudit.phone2" />
				</li>
				<li>
					<label><s:text name="User.phone" />:</label>
					<s:property value="conAudit.phone" />
				</li>
			</ol>
		</fieldset>
		
		<fieldset class="form submit">
			<div>
				<ol>
					<li>
                        <s:checkbox name="readInstructions" />
                        <s:text name="ScheduleAudit.message.ReadInstructions" />
					</li>
					<li>
                        <s:checkbox name="confirmed" />
						<s:text name="ScheduleAudit.message.ConfirmMessage">
							<s:param><s:date name="lastCancellationTime" format="%{@com.picsauditing.util.PicsDateFormat@Datetime12Hour}" /></s:param>
							<s:param value="%{conAudit.contractorAccount.country.getAmount(rescheduling)}" />
                            <s:param value="%{conAudit.contractorAccount.country.currency.symbol}" />
						</s:text>
					</li>
					<s:if test="!conAudit.conductedOnsite">
						<li>
							<s:checkbox name="webcamConfirm" />
							<s:text name="ScheduleAudit.message.WebcamConfirm" />
						</li>
					</s:if>
				</ol>
				
				<s:submit cssClass="picsbutton positive" method="confirm" value="%{getText('ScheduleAudit.button.ConfirmAudit')}" />
			</div>
		</fieldset>
	</s:form>
</div>