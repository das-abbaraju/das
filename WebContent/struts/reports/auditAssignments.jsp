<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Schedule &amp; Assign Audits</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
	function saveAuditor(auditId, auditorId) {
		var data = {
				'contractorAudit.id': auditId,
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
				<td align="center"><a href="javascript: changeOrderBy('form1','auditorID DESC,name');">Safety Professional</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','assignedDate DESC');">Assigned</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','scheduledDate,a.name');">Scheduled</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditLocation,a.name');">Location</a></td>
				<td>Safety Manual</td>
				<td></td>
				<td></td>
				<s:if test="showContact">
					<td>Primary Contact</td>
					<td>Phone</td>
					<td>Email</td>
					<td>Office Address</td>
					<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
					<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
					<td>Zip</td>
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
				<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>" 
						class="contractorQuick" title="<s:property value="[0].get('name')"/>"
						rel="ContractorQuickAjax.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a>
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
				<s:date name="[0].get('scheduledDate')" format="MM/dd/yyyy hh:mm a"/>
				</td>
				<td>
				<s:property value="[0].get('auditLocation')"/>
				</td>
				<td><s:if test="get('manswer') != null">
					<nobr>Size:<s:property value="getFileSize(get('mid').toString())"/></nobr><br/>
						<s:if test="get('mcomment') != null && get('mcomment').toString().length() > 0">
						Comments:<s:property value="get('mcomment')" escape="false"/>
						</s:if>
					</s:if>
				</td>
				<td>
					<input type="button" class="forms" value="Save" onclick="saveAudit('<s:property value="%{[0].get('auditID')}"/>'); return false;"/>
				</td>
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
					<td><s:property value="get('state')"/></td>
					<td><s:property value="get('zip')"/></td>
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

</body>
</html>
