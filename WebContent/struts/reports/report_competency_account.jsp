<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Competency By Account Report</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min.js"></script>
<script type="text/javascript">
$(function() {
$.mask.definitions['S']='[X0-9]';
$('input.ssn').mask('SSS-SS-SSSS');
});
</script>
</head>
<body>
<h1>HSE Competency By Account Report</h1>
<s:if test="data.size() > 0">
<table class="report" id="matrix">
	<thead>
		<tr>
			<th><a href="?orderBy=name">Company</a></th>
			<th># of Employees</th>
			<th># of Job Roles</th>
			<th><a href="?orderBy=ca99statusChangedDate">Job Role Self Assessment</a></th>
			<th><a href="?orderBy=ca100statusChangedDate">Shell Competency Review</a></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="d">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="#d.get('id')"/>"><s:property value="#d.get('name')" /></a></td>
				<td class="right"><a href="ReportCompetencyByEmployee.action?filter.accountName=<s:property value="@java.net.URLEncoder@encode(#d.get('name'))" />"><s:property value="#d.get('employeeCount')" /></a></td>
				<td class="right"><a href="JobCompetencyMatrix.action?id=<s:property value="#d.get('id')"/>"><s:property value="#d.get('jobRoleCount')"/></a></td>
				<td>
					<s:if test="#d.get('ca99ID') != null">
						<a href="Audit.action?auditID=<s:property value="#d.get('ca99ID')"/>">
							<s:if test="#d.get('ca99status') == 'Complete'">
								Completed on <s:date name="#d.get('ca99statusChangedDate')" format="M/d/yyyy"/>
							</s:if>
							<s:elseif test="#d.get('ca99status') == 'Submitted'">
								Submitted on <s:date name="#d.get('ca99statusChangedDate')" format="M/d/yyyy"/>
							</s:elseif>
						</a>
					</s:if>
				</td>
				<td>
					<s:if test="#d.get('ca100ID') != null">
						<a href="Audit.action?auditID=<s:property value="#d.get('ca100ID')"/>">
							<s:if test="#d.get('ca100status') == 'Complete'">
								Completed on <s:date name="#d.get('ca100statusChangedDate')" format="M/d/yyyy"/>
							</s:if>
							<s:elseif test="#d.get('ca100status') == 'Submitted'">
								Submitted on <s:date name="#d.get('ca100statusChangedDate')" format="M/d/yyyy"/>
							</s:elseif>
						</a>
					</s:if>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

</s:if>
<s:else>
	<div class="info">
		No Records Found.
	</div>
</s:else>
</body>
</html>
