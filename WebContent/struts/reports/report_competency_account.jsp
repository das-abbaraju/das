<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>HSE Competency By Account Report</title>
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
			<th><a href="?orderBy=ca99date">Job Role Self Assessment</a></th>
			<th><a href="?orderBy=ca100date">Shell Competency Review</a></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat" id="d">
			<tr>
				<td><a href="ContractorView.action?id=<s:property value="#d.get('accountID')"/>"><s:property value="#d.get('name')" /></a></td>
				<td class="right"><a href="ReportCompetencyByEmployee.action?filter.accountName=<s:property value="@java.net.URLEncoder@encode(#d.get('name'))" />"><s:property value="#d.get('eCount')" /></a></td>
				<td class="right"><a href="JobCompetencyMatrix.action?id=<s:property value="#d.get('accountID')"/>"><s:property value="#d.get('jCount')"/></a></td>
				<td>
					<s:if test="#d.get('ca99ID') != null">
						<s:if test="#d.get('ca99status') == 'Complete'">
							Completed on <s:date name="#d.get('ca99date')" format="M/d/yyyy"/>
						</s:if>
					</s:if>
				</td>
				<td>
					<s:if test="#d.get('ca100ID') != null">
						<a href="Audit.action?auditID=<s:property value="#d.get('ca100ID')"/>">
							<s:if test="#d.get('ca100status') == 'Complete'">
								Completed on <s:date name="#d.get('ca100date')" format="M/d/yyyy"/>
							</s:if>
							<s:elseif test="#d.get('ca100status') == 'Submitted'">
								Submitted on <s:date name="#d.get('ca100date')" format="M/d/yyyy"/>
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
