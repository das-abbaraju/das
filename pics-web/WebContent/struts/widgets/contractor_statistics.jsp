<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="!contractorCronError && !emailCronError && !contractorCronWarning && !emailCronWarning">
	<div class="widgetinfo">PICS System OK</div>
</s:if>
<s:else>
	<s:if test="contractorCronError">
		<div class="widgeterror">CONTRACTOR System <strong>FAILURE</strong> - <strong>Please Contact IT</strong><br/>
			&nbsp;&nbsp;&nbsp;Contractors Processed in Past Hour: <strong><s:property value="contractorsProcessed"/></strong><br/>
			&nbsp;&nbsp;&nbsp;Contractors Waiting to be Processed: <strong><s:property value="contractorsWaiting"/></strong>
		</div>
	</s:if>
	<s:elseif test="contractorCronWarning">
		<div class="widgetinfo">CONTRACTOR System Working:<br/>
			&nbsp;&nbsp;&nbsp;Contractors Waiting to be Processed: <strong><s:property value="contractorsWaiting"/></strong>
		</div>
	</s:elseif>
	<s:else>
		<div class="widgetinfo">CONTRACTOR System OK</div>
	</s:else>
	<s:if test="emailCronError">
		<div class="widgeterror">EMAIL System <strong>FAILURE</strong> - <strong>Please Contact IT</strong><br/>
			&nbsp;&nbsp;&nbsp;Emails sent in Past Hour: <strong><s:property value="emailsSentInHour"/></strong><br/>
			&nbsp;&nbsp;&nbsp;Emails Waiting to be Sent: <strong><s:property value="emailsPending"/></strong><br/>
			&nbsp;&nbsp;&nbsp;Emails sent in Last Five Minutes: <strong><s:property value="emailsSentInLastFiveMinutes"/></strong><br/>
			&nbsp;&nbsp;&nbsp;Emails Waiting more than Five Minutes to be sent: <strong><s:property value="emailsPendingAndCreatedMoreThanFiveMinutesAgo"/></strong><br/>
			&nbsp;&nbsp;&nbsp;Emails With Errors in Past Week: <strong><s:property value="emailsWithErrorsInLastWeek"/></strong>
		</div>
	</s:if>
	<s:elseif test="emailCronWarning">
		<div class="widgetinfo">EMAIL System Working:<br/>
			&nbsp;&nbsp;&nbsp;Emails Pending In Queue: <strong><s:property value="emailsPending"/></strong><br/>
			&nbsp;&nbsp;&nbsp;Emails With Errors in Past Week: <strong><s:property value="emailsWithErrorsInLastWeek"/></strong>
		</div>
	</s:elseif>
	<s:else>
		<div class="widgetinfo">EMAIL System OK</div>
	</s:else>
</s:else>