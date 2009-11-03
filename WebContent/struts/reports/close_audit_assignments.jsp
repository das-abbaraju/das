<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Close Assigned Audits</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
	function saveAudit(auditId) {
		var auditor = $('#auditor_' + auditId).val();
		var notes = escape($('#notes_' + auditId).val());

		var data = {
				'auditID': auditId,
				'closeAuditor': auditor,
				'notes': notes
		};
		
		$('#notes_'+auditId).load('AuditCloseUpdateAjax.action', data, function(text, status) {
			if (status='success')
				$('#audit_'+auditId).effect('highlight', {color: '#FFFF11'}, 1000);
		}
		);
	}
</script>
</head>
<body>
<h1>Close Assigned Audits</h1>

<s:include value="filters.jsp" />

<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<s:form id="closeAuditsForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','atype.auditName');">Type</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','createdDate DESC');">Submitted</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditorID DESC');">Auditor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','closingAuditorID DESC,name');">Closing Auditor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','assignedDate DESC');">Assigned</a></td>
				<td align="center">Notes</td>
				<td></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="[0].get('auditID')"/>">
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
				<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('auditName')"/></a></td>
				<td class="reportDate"><s:date name="get('completedDate')"format="M/d/yy" /></td>
				<td><s:property value="get('auditor_name')"/></td>
				<td><nobr>
					<s:select cssClass="blueMain" list="auditorList" listKey="id"
						listValue="name" value="%{get('closingAuditorID')}"
						id="%{'auditor_'.concat(get('auditID'))}" headerKey="" headerValue="- Auditor -"/>
				</nobr></td>
				<td class="center">
					<nobr><s:property value="%{getBetterDate( get('assignedDate'), 'MM/dd/yy hh:mm:ss a.000')}" /></nobr>
				</td>
				<td>
					<s:textarea id="%{'notes_'.concat(get('auditID'))}" name="notes" rows="3" cols="15"/>
				</td>				
				<td>
					<input type="button" class="forms" value="Save" onclick="saveAudit('<s:property value="%{get('auditID')}"/>'); return false;"/>
				</td>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
