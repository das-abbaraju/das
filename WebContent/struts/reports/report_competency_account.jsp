<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Competency By Account Report</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min"></script>
<script type="text/javascript">
$(function() {
$.mask.definitions['S']='[X0-9]';
$('input.ssn').mask('SSS-SS-SSSS');
});
</script>
</head>
<body>
<h1>Competency By Account Report</h1>
<s:if test="data.size() > 0">
<table class="report" id="matrix">
	<thead>
		<tr>
			<th>Company</th>
			<th># of Employees</th>
			<th># of Job Roles</th>
			<th>Competency</th>
			<th>Competency %</th>
			<th>HSE Competencies by Job Role</th>
			<th>Shell HSE Competency Review</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="data">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="#data.get('id')"/>"><s:property value="#data.get('name')" /></a></td>
				<td class="right"><a href="ReportCompetencyByEmployee.action?filter.accountName=<s:property value="@java.net.URLEncoder@encode(#data.get('name'))" />"><s:property value="#data.get('employeeCount')" /></a></td>
				<td class="right"><a href="JobCompetencyMatrix.action?id=<s:property value="#data.get('id')"/>"><s:property value="#data.get('jobRoleCount')"/></a></td>
				<td class="right"><s:property value="#data.get('skilled')" /> / <s:property value="#data.get('required')" /></td>
				<td class="right"><s:property value="getRatio(#data.get('skilled'),#data.get('required'))" />%</td>
				<td>
					<s:if test="#data.get('ca99ID') != null">
						<a href="Audit.action?auditID=<s:property value="#data.get('ca99ID')"/>">
							<s:if test="#data.get('ca99status') == 'Active'">
								Expires on <s:date name="#data.get('ca99expiresDate')" format="M/d/yyyy"/>
							</s:if>
							<s:elseif test="#data.get('ca99status') == 'Submitted'">
								Completed on <s:date name="#data.get('ca99completedDate')" format="M/d/yyyy"/>
							</s:elseif>
							<s:else>
								Created on <s:date name="#data.get('ca99creationDate')" format="M/d/yyyy"/>
							</s:else>
						</a>
					</s:if>
				</td>
				<td>
					<s:if test="#data.get('ca100ID') != null">
						<a href="Audit.action?auditID=<s:property value="#data.get('ca100ID')"/>">
							<s:if test="#data.get('ca100status') == 'Active'">
								Expires on <s:date name="#data.get('ca100expiresDate')" format="M/d/yyyy"/>
							</s:if>
							<s:elseif test="#data.get('ca100status') == 'Submitted'">
								Completed on <s:date name="#data.get('ca100completedDate')" format="M/d/yyyy"/>
							</s:elseif>
							<s:else>
								Created on <s:date name="#data.get('ca100creationDate')" format="M/d/yyyy"/>
							</s:else>
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
