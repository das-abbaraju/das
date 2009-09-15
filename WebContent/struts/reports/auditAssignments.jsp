<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Schedule &amp; Assign Audits</title>
<s:include value="reportHeader.jsp" />
<SCRIPT LANGUAGE="JavaScript">
	var cal1 = new CalendarPopup('caldiv1');
	cal1.offsetY = -110;
	cal1.offsetX = 0;
	cal1.addDisabledDates(null, "<s:property value="yesterday"/>");
	cal1.setDisabledWeekDays(0,6);
	cal1.setCssPrefix("PICS");
</SCRIPT>
<script type="text/javascript">
	function saveAudit(auditId) {
		var auditor = $F($('auditor_' + auditId));
		var scheduleDate = $('scheduled_date_' + auditId + '_date');
		var pars = "contractorAudit.id=" + auditId;
		
		pars = pars + "&auditor.id=" + auditor;

		if (scheduleDate != null) {
			var thisdate = $F($('scheduled_date_' + auditId + '_date'));
			pars = pars + "&contractorAudit.scheduledDate=";	
			if (thisdate != '')	{
				var thisTime = $('scheduled_date_' + auditId + '_time').options[$('scheduled_date_' + auditId + '_time').selectedIndex].text;
				thisdate = thisdate + ' ' + thisTime;
				pars = pars + thisdate;
			}
			
			if ($('auditlocation_' + auditId + '_Onsite').checked == true) {
					pars = pars + "&contractorAudit.auditLocation=Onsite";
			}
			else if($('auditlocation_' + auditId + '_Web').checked == true) {
					pars = pars + "&contractorAudit.auditLocation=Web";
			}
		}

		var assignDateDiv = 'assignDate_'+auditId;
		var divName = 'audit_'+auditId;
		//alert(pars);
		
		var myAjax = new Ajax.Updater(assignDateDiv, 'AuditAssignmentUpdateAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) {
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
	}
	
	function saveAuditor(auditId, auditorId) {
		var pars = "contractorAudit.id=" + auditId + '&auditor.id=' + auditorId;
		var toHighlight = 'audit_'+auditId;
		var assignDateDiv = 'assignDate_'+auditId;
			
		var myAjax = new Ajax.Updater(assignDateDiv, 'AuditAssignmentUpdateAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) { 
				new Effect.Highlight(toHighlight, {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
	}
</script>
</head>
<body>
<h1>Schedule &amp; Assign Audits</h1>

<s:include value="filters.jsp" />
<div class="blueMain"><a href="audit_calendar.jsp" target="_BLANK">Audit Calendar</a></div>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="assignScheduleAuditsForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','atype.auditName');">Type</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','createdDate DESC');">Created</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','current_expiresDate DESC');">Expires</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditorID DESC,name');">Auditor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','assignedDate DESC');">Assigned</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','scheduledDate,a.name');">Scheduled</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditLocation,a.name');">Location</a></td>
				<td></td>
				<td></td>
				<s:if test="showContact">
					<td>Primary Contact</td>
					<td>Phone</td>
					<td>Phone2</td>
					<td>Email</td>
					<td>Office Address</td>
					<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
					<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
					<td>Zip</td>
					<td>Second Contact</td>
					<td>Second Phone</td>
					<td>Second Email</td>
					<td>Web_URL</td>
				</s:if>
				<s:if test="showTrade">
					<td>Trade</td>
					<td>Industry</td>			
				</s:if>
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
				<td class="reportDate"><s:date name="[0].get('current_expiresDate')"
					format="M/d/yy" /></td>
				<td><nobr>
				<s:if test="[0].get('hasAuditor')">
					<s:select onchange="javascript: saveAuditor(%{[0].get('auditID')}, this.value)" cssClass="blueMain" list="auditorList" listKey="id"
						listValue="name" value="%{[0].get('auditorID')}"
						id="%{'auditor_'.concat([0].get('auditID'))}" />
					<s:if test="[0].get('isScheduled') && [0].get('auditorConfirm') == NULL">
						<span class="redMain">*</span>
					</s:if>	
				</s:if></nobr>
				</td>
				<td class="center" id="assignDate_<s:property value="[0].get('auditID')"/>">
					<nobr><s:property
						value="%{getBetterDate( [0].get('assignedDate'), 'MM/dd/yy hh:mm:ss a.000')}" />
					<s:property
						value="%{getBetterTime( [0].get('assignedDate'), 'MM/dd/yy hh:mm:ss a.000')}" />
					</nobr>
				</td>
				<td>
				<s:if test="[0].get('isScheduled')">
					<nobr><input class="blueMain" size="6" type="text"
						name="scheduled_date_<s:property value="[0].get('auditID')"/>_date"
						id="scheduled_date_<s:property value="[0].get('auditID')"/>_date"
						value="<s:property value="getBetterDate( [0].get('scheduledDate'), 'MM/dd/yy hh:mm:ss a.000')"/>" />
						<a href="#"
							onclick="cal1.select($('scheduled_date_<s:property value="[0].get('auditID')"/>_date'),'anchor<s:property value="[0].get('auditID')"/>','MM/dd/yy'); return false;"
							name="anchor<s:property value="[0].get('auditID')"/>" 
							id="anchor<s:property value="[0].get('auditID')"/>"><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
					<s:select list="@com.picsauditing.PICS.DateBean@getBusinessTimes()"
						listKey="key" listValue="value" cssClass="blueMain"
						name="scheduled_date_%{[0].get('auditID')}_time"
						id="scheduled_date_%{[0].get('auditID')}_time"
						value="%{@com.picsauditing.PICS.DateBean@getIndexForTime(getBetterTime( [0].get('scheduledDate'), 'MM/dd/yy hh:mm:ss a.000')) }" />
					</nobr>
				</s:if>
				</td>
				<td>
				<s:if test="[0].get('isScheduled')">
					<s:radio name="auditlocation_%{[0].get('auditID')}" list="#{'Onsite':'On site', 'Web':'Web'}"
						id="auditlocation_%{[0].get('auditID')}_"
						value="%{[0].get('auditLocation')}" theme="pics" />
				</s:if>
				</td>
				<td>
					<input type="button" class="forms" value="Save" onclick="saveAudit('<s:property value="%{[0].get('auditID')}"/>'); return false;"/>
				</td>
				<td>
					<a href="ScheduleAudit.action?auditID=<s:property value="get('auditID')"/>" target="scheduleAudit">Beta</a>
				</td>
				<s:if test="showContact">
					<td><s:property value="get('contact')"/></td>
					<td><s:property value="get('phone')"/></td>
					<td><s:property value="get('phone2')"/></td>
					<td><s:property value="get('email')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/></td>
					<td><s:property value="get('state')"/></td>
					<td><s:property value="get('zip')"/></td>
					<td><s:property value="get('secondContact')"/></td>
					<td><s:property value="get('secondPhone')"/></td>
					<td><s:property value="get('secondEmail')"/></td>
					<td><s:property value="get('web_URL')"/></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
					<td><s:property value="get('industry')"/></td>
				</s:if>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<span class="redMain">* - UnConfirmed Audits</span>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
