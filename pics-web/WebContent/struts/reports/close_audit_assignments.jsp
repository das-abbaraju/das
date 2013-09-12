<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>Close Assigned Audits</title>
		<s:include value="reportHeader.jsp" />
		<script type="text/javascript">
			$(function() {
				$('input.picsbutton.positive').live('click', function(e) {
					e.preventDefault();
					
					var auditId = $(this).data('id');
					var auditor = $('#auditor_' + auditId).val();
					var notes = escape($('#notes_' + auditId).val());
		
					var data = {
						'audit': auditId,
						'closingAuditor': auditor,
						'notes': notes
					};
					
					$('#notes_'+auditId).load('ReportCloseAuditAssignments!save.action', data, function(text, status) {
						if (status='success')
							$('#audit_'+auditId).effect('highlight', {color: '#FFFF11'}, 1000);
						}
					);
				});
				$('#selectAll').live('click', function() {
					var checked = $(this).is(":checked");
					$('input.selectable').attr('checked', checked);
				});
				$('#saveAll').live('click', function() {
					$(this).attr('disabled', true);
					$('#closeAuditsForm').submit();
				});
			});
		</script>
	</head>
<body>
	<h1>Close Assigned Audits</h1>
	<s:include value="filters.jsp" />
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	<s:form id="closeAuditsForm" method="post" cssClass="forms">
		<table class="report">
			<thead>
				<tr>
					<td colspan="2"></td>
					<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
					<td align="center">Type</td>
					<td align="center"><a href="javascript: changeOrderBy('form1','createdDate DESC');">Submitted</a></td>
					<td align="center"><a href="javascript: changeOrderBy('form1','auditorID DESC');"><s:text name="global.SafetyProfessional" /></a></td>
					<td align="center"><a href="javascript: changeOrderBy('form1','closingAuditorID DESC,name');"><s:text name="Audit.ClosingAuditor" /></a></td>
					<td align="center"><a href="javascript: changeOrderBy('form1','assignedDate DESC');">Assigned</a></td>
					<td align="center">Notes</td>
					<td></td>
				</tr>
			</thead>
			<s:iterator value="data" status="stat">
				<tr id="audit_<s:property value="[0].get('auditID')"/>">
					<td class="right">
						<s:property value="#stat.index + report.firstRowNumber" />
					</td>
					<td class="center">
						<input type="checkbox" name="auditIDs" value="<s:property value="get('auditID')" />" class="selectable" />
					</td>
					<td>
						<a href="ContractorView.action?id=<s:property value="get('id')"/>">
							<s:property value="get('name')"/>
						</a>
					</td>
					<td>
						<a href="Audit.action?auditID=<s:property value="get('auditID')"/>">
							<s:text name="%{get('atype.name')}" />
						</a>
					</td>
					<td class="reportDate">
						<s:date name="get('completedDate')" />
					</td>
					<td>
						<s:property value="get('auditor_name')"/>
					</td>
					<td>
						<nobr>
							<s:select cssClass="blueMain" list="auditorList" listKey="id"
								listValue="name" value="%{get('closingAuditorID')}"
								id="%{'auditor_'.concat(get('auditID'))}" headerKey="" headerValue="- Assignee -"/>
						</nobr>
					</td>
					<td class="center">
						<nobr>
							<s:text name="dates">
								<s:param value="get('assignedDate')" />
							</s:text>
						</nobr>
					</td>
					<td>
						<s:textarea id="%{'notes_'.concat(get('auditID'))}" rows="3" cols="15"/>
					</td>				
					<td>
						<input type="button" class="forms picsbutton positive" data-id="<s:property value="[0].get('auditID')"/>" value="Save" />
					</td>
				</tr>
			</s:iterator>
			<pics:permission perm="AssignAudits">
				<tr>
					<td colspan="2" class="center">
						<input type="checkbox" id="selectAll" /><label for="selectAll">Select All</label>
					</td>
					<td colspan="8">
						<s:select cssClass="blueMain" list="auditorList" listKey="id" listValue="name" 
							headerKey="0" headerValue="- Assignee -" name="closingAuditor" />
						<s:textarea name="notes" rows="3" cols="15" />
						<input type="hidden" name="method:saveAll" value="Save All" />
						<input type="button" class="picsbutton" value="Save All" id="saveAll" />
					</td>
				</tr>
			</pics:permission>
		</table>
	</s:form>
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</body>
</html>
