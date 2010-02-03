<%@ taglib prefix="s" uri="/struts-tags"%>
<style>
#content div.panel_content {
	background:#FFFFFF none repeat scroll 0%;
	border-bottom:1px solid #B3B3B3;
	font-size:10pt;
	font-size-adjust:none;
	font-stretch:normal;
	font-style:normal;
	font-variant:normal;
	font-weight:normal;
	line-height:150%;
	overflow:hidden;
	padding: 0px;
	margin: 0px;
	min-height: 0px;
}
div.widgetinfo, div.widgeterror
{
padding-top: 1em;
padding-right: 1em;
padding-bottom: 1em;
padding-left: 4em;
margin:1px 0 1px 0;
width: 100%;
clear: left;
}

div#widgetinfo, div.widgetinfo
{
border-top-width: 2px;
border-top-style: solid;
border-top-color: #b7d2f2;
border-bottom-width: 2px;
border-bottom-style: solid;
border-bottom-color: #b7d2f2;
background-color: #dbe7f8;
background-image: url(images/icon-info.gif);
background-repeat: no-repeat;
background-attachment: scroll;
background-position: 10px 10px;
}

div#widgeterror, div.widgeterror
{
border-top-width: 2px;
border-top-style: solid;
border-top-color: #feabab;
border-bottom-width: 2px;
border-bottom-style: solid;
border-bottom-color: #feabab;
background-color: #ffdfdf;
background-image: url(images/icon-error.gif);
background-repeat: no-repeat;
background-attachment: scroll;
background-position: 10px 10px;
}

</style>

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
			&nbsp;&nbsp;&nbsp;Emails Created in Last Five Minutes Waiting: <strong><s:property value="emailsPendingAndCreatedMoreThanFiveMinutesAgo"/></strong>
		</div>
	</s:if>
	<s:elseif test="emailCronWarning">
		<div class="widgetinfo">EMAIL System Working:<br/>
			&nbsp;&nbsp;&nbsp;Emails Pending In Queue: <strong><s:property value="emailsPending"/></strong>
		</div>
	</s:elseif>
	<s:else>
		<div class="widgetinfo">EMAIL System OK</div>
	</s:else>
</s:else>