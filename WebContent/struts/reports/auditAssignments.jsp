<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Schedule &amp; Assign Audits</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
	function saveAuditor(auditId, auditorId) {
		var data = {
				'contractorAudit': auditId,
				'auditor.id': auditorId
		};

		$('#assignDate_'+auditId).load('AuditAssignmentUpdateAjax.action', data, function(text, status) {
				if (status='success')
					$('#audit_'+auditId).effect('highlight', {color: '#FFFF11'}, 1000);
			}
		);
	}
</script>
</head>
<body>
<h1>Schedule &amp; Assign Audits</h1>

<s:include value="filters.jsp" />
<div class="blueMain"><a href="AuditCalendar.action" target="_BLANK">Audit Calendar</a></div>

<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<s:form id="assignScheduleAuditsForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td align="center"><a>Type</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','createdDate DESC');">Created</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','current_expiresDate DESC');">Expires</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditorID DESC,name');">Safety Professional</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','assignedDate DESC');">Assigned</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','scheduledDate DESC,a.name');">Scheduled</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditLocation,a.name');">Location</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','pqfCompletionDate ASC, a.name');">PQF Completed</td>
				<td>Safety Manual</td>
				<td align="center"><a href="javascript: changeOrderBy('form1','dateVerified ASC,a.name');">Safety Manual Verified</td>
				<td></td>
				<s:if test="showContact">
					<td><s:text name="global.ContactPrimary" /></td>
					<td><s:text name="User.phone" /></td>
					<td><s:text name="User.email" /></td>
					<td><s:text name="global.OfficeAddress" /></td>
					<td><a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a></td>
					<td><a href="javascript: changeOrderBy('form1','a.countrySubdivision,a.name');"><s:text name="CountrySubdivision" /></a></td>
					<td><s:text name="global.ZipPostalCode" /></td>
					<td><s:text name="ContractorAccount.webUrl" /></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:text name="Trade" /></td>
				</s:if>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="[0].get('auditID')"/>">
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>" 
						class="contractorQuick" title="<s:property value="[0].get('name')"/>"
						rel="ContractorQuick.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a>
					<s:if test="[0].get('isScheduled') && [0].get('contractorConfirm') == NULL">
						<span class="redMain">*</span>
					</s:if>	
				</td>
				<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:text name="%{[0].get('atype.name')}" /></a></td>
				<td class="reportDate"><s:date name="[0].get('createdDate')"
					format="%{getText('date.short')}" /></td>
				<td class="reportDate"><s:date name="[0].get('current_expiresDate')"
					format="%{getText('date.short')}" /></td>
				<td><nobr>
				<s:if test="[0].get('hasAuditor')">
					<s:select onchange="saveAuditor(%{[0].get('auditID')}, this.value)" cssClass="blueMain" list="safetyList" listKey="id"
						listValue="name" value="%{[0].get('auditorID')}"
						id="%{'auditor_'.concat([0].get('auditID'))}" headerKey="" headerValue="- Safety Professional -" />
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
				<s:date name="[0].get('scheduledDate')" format="%{getText('date.shorttime')}"/>
				</td>
				<td>
				<s:property value="[0].get('auditLocation')"/>
				</td>
				<td><s:date name="[0].get('pqfCompletionDate')" format="%{getText('date.short')}"/></td>
				<td><s:if test="get('manswer') != null">
					<nobr>Size:<s:property value="getFileSize(get('mid').toString())"/></nobr><br/>
						<s:if test="get('mcomment') != null && get('mcomment').toString().length() > 0">
						Comments:<s:property value="get('mcomment')" escape="false"/>
						</s:if>
					</s:if>
				</td>
				<td><s:date name="[0].get('dateVerified')" format="%{getText('date.short')}"/></td>
				<td>
					<s:if test="[0].get('isScheduled')">
						<a href="ScheduleAudit.action?auditID=<s:property value="get('auditID')"/>" target="scheduleAudit">Schedule</a>
					</s:if>
				</td>
				<s:if test="showContact">
					<td><s:property value="get('contactname')"/></td>
					<td><s:property value="get('contactphone')"/></td>
					<td><s:property value="get('contactemail')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/></td>
					<td><s:property value="get('countrySubdivision')"/></td>
					<td><s:property value="get('zip')"/></td>
					<td><s:property value="get('web_URL')"/></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
				</s:if>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<span class="redMain">* - UnConfirmed Audits</span>

</body>
</html>
