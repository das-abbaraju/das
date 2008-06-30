<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Schedule &amp; Assign Audits</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
<script type="text/javascript">
	function saveAudit(auditId) {
		var pars = "contractorAudit.id=" + auditId;
		
		var auditor = $F($('auditor_' + auditId));
		pars = pars + "&auditor.id=" + auditor;

		var scheduleDate = $('scheduled_date_' + auditId + '_date');
		if (scheduleDate != null) {
			var thisdate = $F($('scheduled_date_' + auditId + '_date'));
			if( thisdate != '' )
			{
				var thisTime = $('scheduled_date_' + auditId + '_time').options[$('scheduled_date_' + auditId + '_time').selectedIndex].text;
				thisdate = thisdate + ' ' + thisTime;
				pars = pars + "&contractorAudit.scheduledDate=" + thisdate;	
			}
		
			if( $('auditlocation_' + auditId + '_Onsite').checked == true )
			{
				pars = pars + "&contractorAudit.auditLocation=Onsite";
			}
			else if( $('auditlocation_' + auditId + '_Web').checked == true )
			{
				pars = pars + "&contractorAudit.auditLocation=Web";
			}
		}

		var assignDateDiv = 'assignDate_'+auditId;
		var myAjax = new Ajax.Updater(assignDateDiv,'AuditAssignmentUpdateAjax.action', {method: 'post', parameters: pars});

		var divName = 'audit_'+auditId;
		new Effect.Highlight(divName, {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
	}
	
	function saveAuditor(auditId, auditorId) {
		var pars = "contractorAudit.id=" + auditId + '&auditor.id=' + auditorId;
		
	
		var toHighlight = 'audit_'+auditId;
		var myAjax = new Ajax.Request('AuditorAssignmentUpdateAjax.action', {method: 'post', parameters: pars,
		
			onSuccess: function( transport ) {
				new Effect.Highlight(toHighlight, {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
	}
</script>
</head>
<body>
<h1>Schedule &amp; Assign Audits</h1>

<div id="search">
<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if> ><a href="#">Show Filter Options</a></div>
<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if> ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="%{filtered ? '' : 'display: none'}">
	<table>
		<tr>
			<td style="vertical-align: middle;"><s:textfield
				name="accountName" cssClass="forms" size="8"
				onfocus="clearText(this)" /> <s:select list="auditTypeList"
				cssClass="forms" name="auditTypeID" listKey="auditTypeID"
				listValue="auditName" />
			<s:action name="AuditorsGet" executeResult="true">
				<s:param name="controlName" value="%{'auditorId'}"/>
				<s:param name="presetValue" value="auditorId"/>		
			</s:action>
			<s:submit name="imageField" type="image"
				src="images/button_search.gif" onclick="runSearch( 'form1')" /></td>
		</tr>
		<tr><td>	
		 	<s:select list="operatorList" headerKey="0" headerValue="- Operator -"
			cssClass="forms" name="operator" listKey="id" listValue="name" />
		</td>
		</tr>
		<tr><td>
			<s:checkbox name="unScheduledAudits"/><label>Check to Search on UnConfirmed Audits</label>
		</td></tr>
	</table>
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<span class="redMain">* - UnConfirmed Audits</span>	
<s:form id="assignScheduleAuditsForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','atype.auditName');">Type</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','createdDate DESC');">Created</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditorID DESC,name');">Auditor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','assignedDate DESC');">Assigned</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','scheduledDate,a.name');">Scheduled</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditLocation,a.name');">Location</a></td>
				<td></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="[0].get('auditID')"/>">
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a>
					<s:if test="[0].get('isScheduled') && [0].get('contractorConfirm') == NULL">
						<span class="redMain">*</span>
					</s:if>	
				</td>
				<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
				<td class="reportDate"><s:date name="[0].get('createdDate')"
					format="M/d/yy" /></td>
				<td><nobr><s:if test="[0].get('hasAuditor')">
					<s:select onchange="javascript: saveAuditor(%{[0].get('auditID')}, this.value)" cssClass="blueMain" list="auditorList" listKey="id"
						listValue="name" value="%{[0].get('auditorID')}"
						id="%{'auditor_'.concat([0].get('auditID'))}" />
					<s:if test="[0].get('isScheduled') && [0].get('auditorConfirm') == NULL">
						<span class="redMain">*</span>
					</s:if>	
				</s:if></nobr></td>
				<td class="center" id="assignDate_<s:property value="[0].get('auditID')"/>"><s:if test="[0].get('hasAuditor')">
					<nobr><s:property
						value="%{getBetterDate( [0].get('assignedDate'), 'MM/dd/yy hh:mm:ss a.000')}" />
					<s:property
						value="%{getBetterTime( [0].get('assignedDate'), 'MM/dd/yy hh:mm:ss a.000')}" />
					</nobr>
				</s:if></td>
				<td><s:if test="[0].get('isScheduled')">
					<nobr><input class="blueMain" size="6" type="text"
						name="scheduled_date_<s:property value="[0].get('auditID')"/>_date"
						id="scheduled_date_<s:property value="[0].get('auditID')"/>_date"
						onClick="cal1.select(this,'scheduled_date_<s:property value="[0].get('auditID')"/>_date','M/d/yy'); return false;"
						value="<s:property value="getBetterDate( [0].get('scheduledDate'), 'MM/dd/yy hh:mm:ss a.000')"/>" />
					<s:select list="@com.picsauditing.PICS.DateBean@getBusinessTimes()"
						listKey="key" listValue="value" cssClass="blueMain"
						name="scheduled_date_%{[0].get('auditID')}_time"
						id="scheduled_date_%{[0].get('auditID')}_time"
						value="%{@com.picsauditing.PICS.DateBean@getIndexForTime(getBetterTime( [0].get('scheduledDate'), 'MM/dd/yy hh:mm:ss a.000')) }" />
					</nobr>
				</s:if></td>
				<td><s:if test="[0].get('isScheduled')">
					<s:radio name="auditlocation_%{[0].get('auditID')}" list="#{'Onsite':'On site', 'Web':'Web'}"
						id="auditlocation_%{[0].get('auditID')}_"
						value="%{[0].get('auditLocation')}" />
				</s:if></td>
				<td><input type="button" class="forms" value="Save" onclick="saveAudit('<s:property value="%{[0].get('auditID')}"/>'); return false;"/>
				</td>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
